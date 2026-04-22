package com.jel.spys.service;

import com.jel.spys.entity.*;
import com.jel.spys.exception.BulkLoadException;
import com.jel.spys.exception.ResourceNotFoundException;
import com.jel.spys.model.*;
import com.jel.spys.repository.*;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
public class FinanceService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private TransactionAuditRepository transactionAuditRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private UserService userService;
    
    @Autowired
    private ClockService clockService;

    @Autowired
    private TransactionAuditService transactionAuditService;

    @Transactional(readOnly = true)
    public List<CompactUserDTO> searchUsersPaginated(String search, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("lastName", "firstName"));
        Page<UserEntity> userPage;

        if (search == null || search.trim().isEmpty()) {
            userPage = userRepository.findAll(pageable);
        } else {
            userPage = userRepository
                    .findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                            search, search, search, pageable);
        }

        return userPage.getContent().stream()
                .map(CompactUserDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public FinanceUserDTO getUserDetail(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        return convertToFinanceUserDTO(user);
    }

    @Transactional()
    public AdminFinanceLoadResponse bulkLoadCredit(@Valid AdminBulkLoadRequest request) {
        UserEntity currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            throw new IllegalStateException("No authenticated user found");
        }

        List<AdminLoadCreditResult> results = new ArrayList<>();
        List<TransactionEntity> createdTransactions = new ArrayList<>();
        BigDecimal totalAmountLoaded = BigDecimal.ZERO;
        int successfulLoads = 0;
        int failedLoads = 0;
        int totalRequests = 0;

        try {
            MultipartFile csvFile = request.getCsvFile();
            Reader reader = new InputStreamReader(csvFile.getInputStream());
            
            // Configure CSV format - auto-detect header, trim values, handle quotes
            CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
                    .setHeader()
                    .setSkipHeaderRecord(true)
                    .setTrim(true)
                    .setIgnoreEmptyLines(true)
                    .setIgnoreSurroundingSpaces(true)
                    .build();
            
            CSVParser csvParser = new CSVParser(reader, csvFormat);
            
            for (CSVRecord record : csvParser) {
                totalRequests++;
                
                try {
                    // Get values by column index (works with or without headers)
                    String identifier = record.get(0);
                    String amountStr = record.get(1);
                    String description = record.size() > 2 ? record.get(2) : "Bulk laai";
                    
                    // Validate identifier
                    if (identifier == null || identifier.trim().isEmpty()) {
                        results.add(AdminLoadCreditResult.builder()
                                .identifier("Ry " + record.getRecordNumber())
                                .success(false)
                                .errorMessage("Identifiseerder is nie geldig nie")
                                .build());
                        failedLoads++;
                        continue;
                    }
                    
                    // Parse and validate amount
                    BigDecimal amount;
                    try {
                        amount = new BigDecimal(amountStr);
                        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                            results.add(AdminLoadCreditResult.builder()
                                    .identifier(identifier)
                                    .success(false)
                                    .errorMessage("Bedrag moet positief wees!")
                                    .build());
                            failedLoads++;
                            continue;
                        }
                    } catch (NumberFormatException | NullPointerException e) {
                        results.add(AdminLoadCreditResult.builder()
                                .identifier(identifier)
                                .success(false)
                                .errorMessage("Formaat van bedrag is verkeerd: " + amountStr)
                                .build());
                        failedLoads++;
                        continue;
                    }
                    
                    // Find user and load credit
                    UserEntity user = findUserByIdentifier(identifier, identifier);
                    TransactionEntity transaction = creditUserAccount(user.getId(), amount, description);
                    
                    // Track created transaction for audit
                    createdTransactions.add(transaction);
                    
                    results.add(AdminLoadCreditResult.builder()
                            .identifier(identifier)
                            .success(true)
                            .amountLoaded(amount)
                            .transactionId(transaction.getId())
                            .build());
                    
                    successfulLoads++;
                    totalAmountLoaded = totalAmountLoaded.add(amount);
                    
                } catch (ResourceNotFoundException e) {
                    results.add(AdminLoadCreditResult.builder()
                            .identifier(record.get(0))
                            .success(false)
                            .errorMessage("Gebruiker nie gevind nie: " + e.getMessage())
                            .build());
                    failedLoads++;
                } catch (Exception e) {
                    log.error("Fout met die hanetering van gebruiker rekord {}: {}", record.getRecordNumber(), e.getMessage());
                    results.add(AdminLoadCreditResult.builder()
                            .identifier(record.size() > 0 ? record.get(0) : "Ry " + record.getRecordNumber())
                            .success(false)
                            .errorMessage("Verwerking fout: " + e.getMessage())
                            .build());
                    failedLoads++;
                }
            }
            
            csvParser.close();

        } catch (IOException e) {
            log.error("Error processing CSV file: {}", e.getMessage());
            throw new RuntimeException("Error processing CSV file: " + e.getMessage());
        }

        // Create audit record with linked transactions
        String auditContent = String.format("Processed %d records: %d successful, %d failed, total amount: %s",
                totalRequests, successfulLoads, failedLoads, totalAmountLoaded);
        TransactionAuditEntity auditRecord = transactionAuditService.createAuditRecord(
                currentUser,
                TransactionAuditType.BULK_CREDIT_LOAD,
                auditContent,
                createdTransactions);

        log.info("Bulk credit load completed: {} requests, {} successful, {} failed, total amount: {}, transactions linked: {}",
                totalRequests, successfulLoads, failedLoads, totalAmountLoaded, createdTransactions.size());
        var result =  AdminFinanceLoadResponse.builder()
                .auditId(auditRecord.getId())
                .totalRequests(totalRequests)
                .successfulLoads(successfulLoads)
                .failedLoads(failedLoads)
                .totalAmountLoaded(totalAmountLoaded)
                .results(results)
                .processedAt(clockService.now())
                .processedBy(CompactUserDTO.builder()
                        .id(currentUser.getId())
                        .firstName(currentUser.getFirstName())
                        .lastName(currentUser.getLastName())
                        .email(currentUser.getEmail())
                        .isAdmin(currentUser.getRoles() != null &&
                                currentUser.getRoles().contains(Role.ADMIN))
                        .build())
                .build();
        if (failedLoads > 0) {
            throw new BulkLoadException(result);
        }

        return result;
    }

    @Transactional(readOnly = true)
    public List<TransactionDTO> getTransactionsPaginated(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("transactionDate").descending());
        Page<TransactionEntity> transactionPage = transactionRepository.findAll(pageable);

        return transactionPage.getContent().stream()
                .map(TransactionDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AccountDTO getUserAccount(UserEntity user) {
        Optional<UserProfileEntity> profileOpt = userProfileRepository.findByUser(user);
        if (profileOpt.isEmpty()) {
            throw new IllegalStateException("User profile not found for user: " + user.getEmail());
        }

        AccountEntity account = profileOpt.get().getAccount();
        if (account == null) {
            throw new IllegalStateException("Account not found for user: " + user.getEmail());
        }

        return convertToAccountDTO(account);
    }

    /**
     * Load credit for a specific user
     */
    public TransactionEntity creditUserAccount(Long userId, BigDecimal amount, String description) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        UserProfileEntity profile = userProfileRepository.findByUser(user)
                .orElseThrow(() -> new IllegalStateException("User profile not found"));

        AccountEntity account = profile.getAccount();
        BigDecimal currentBalance = calculateCurrentBalance(account);
        BigDecimal newBalance = currentBalance.add(amount);

        // Create credit transaction
        TransactionEntity transaction = new TransactionEntity(
                BigDecimal.ZERO, // debit
                amount, // credit
                newBalance, // running balance
                clockService.now(),
                description != null ? description : "Credit load");

        transaction.setAccount(account);

        transaction = transactionRepository.save(transaction);

        // Link transaction to account
        if (account.getTransactions() == null) {
            account.setTransactions(new ArrayList<>());
        }
        account.getTransactions().add(transaction);
        accountRepository.save(account);

        return transaction;
    }


    public TransactionEntity debitUserAccount(Long userId, BigDecimal amount, String description) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        UserProfileEntity profile = userProfileRepository.findByUser(user)
                .orElseThrow(() -> new IllegalStateException("User profile not found"));

        AccountEntity account = profile.getAccount();
        BigDecimal currentBalance = calculateCurrentBalance(account);
        BigDecimal newBalance = currentBalance.subtract(amount);

        // Create credit transaction
        TransactionEntity transaction = new TransactionEntity(
                amount, // debit
                BigDecimal.ZERO, // credit
                newBalance, // running balance
                clockService.now(),
                description != null ? description : "Credit load");

        transaction.setAccount(account);

        transaction = transactionRepository.save(transaction);

        // Link transaction to account
        if (account.getTransactions() == null) {
            account.setTransactions(new ArrayList<>());
        }
        account.getTransactions().add(transaction);
        accountRepository.save(account);

        return transaction;
    }

    /**
     * Get financial statistics for admin dashboard
     */
    @Transactional(readOnly = true)
    public AdminFinanceLoadResponse getFinanceStatistics() {
        Instant now = clockService.now();
        Instant startOfMonth = now.minus(30, ChronoUnit.DAYS);

        BigDecimal totalCreditsMonth = transactionRepository.sumCreditAmountInDateRange(startOfMonth, now);

        return AdminFinanceLoadResponse.builder()
                .totalAmountLoaded(totalCreditsMonth != null ? totalCreditsMonth : BigDecimal.ZERO)
                .processedAt(now)
                .build();
    }

    /**
     * Calculate current balance from transactions
     */
    private BigDecimal calculateCurrentBalance(AccountEntity account) {
        return accountRepository.getAccountBalance(account).orElse(BigDecimal.ZERO);
    }

    /**
     * Calculate movement amounts for different periods
     */
    private BigDecimal calculateMovement(List<TransactionEntity> transactions, Instant fromDate) {
        return transactions.stream()
                .filter(t -> t.getTransactionDate().isAfter(fromDate))
                .map(t -> t.getCreditAmount().subtract(t.getDebitAmount()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Convert UserEntity to FinanceUserDTO
     */
    private FinanceUserDTO convertToFinanceUserDTO(UserEntity user) {
        Optional<UserProfileEntity> profileOpt = userProfileRepository.findByUser(user);

        BigDecimal balance = BigDecimal.ZERO;
        BigDecimal movementDay = BigDecimal.ZERO;
        BigDecimal movementWeek = BigDecimal.ZERO;
        BigDecimal movementMonth = BigDecimal.ZERO;
        BigDecimal movementYTD = BigDecimal.ZERO;
        String credentialNumber = null;

        if (profileOpt.isPresent() && profileOpt.get().getAccount() != null) {
            AccountEntity account = profileOpt.get().getAccount();
            balance = calculateCurrentBalance(account);

            if (account.getTransactions() != null) {
                Instant now = clockService.now();
                Instant startOfDay = now.truncatedTo(ChronoUnit.DAYS);
                Instant startOfWeek = startOfDay.minus(7, ChronoUnit.DAYS);
                Instant startOfMonth = startOfDay.minus(30, ChronoUnit.DAYS);
                Instant startOfYear = LocalDate.now().withDayOfYear(1).atStartOfDay().toInstant(ZoneOffset.UTC);

                movementDay = calculateMovement(account.getTransactions(), startOfDay);
                movementWeek = calculateMovement(account.getTransactions(), startOfWeek);
                movementMonth = calculateMovement(account.getTransactions(), startOfMonth);
                movementYTD = calculateMovement(account.getTransactions(), startOfYear);
            }

            credentialNumber = profileOpt.get().getCredentialNumber();
        }


        return FinanceUserDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .credentialNumber(credentialNumber)
                .balance(balance)
                .movementDay(movementDay)
                .movementWeek(movementWeek)
                .movementMonth(movementMonth)
                .movementYTD(movementYTD)
                .build();
    }

    /**
     * Convert AccountEntity to AccountDTO
     */
    private AccountDTO convertToAccountDTO(AccountEntity account) {
        BigDecimal currentBalance = calculateCurrentBalance(account);

        Instant lastTransactionDate = null;
        if (account.getTransactions() != null && !account.getTransactions().isEmpty()) {
            lastTransactionDate = account.getTransactions().stream()
                    .map(TransactionEntity::getTransactionDate)
                    .max(Instant::compareTo)
                    .orElse(null);
        }

        return AccountDTO.builder()
                .id(account.getId())
                .currentBalance(currentBalance)
                .lastTransactionDate(lastTransactionDate)
                .createdAt(account.getCreatedAt())
                .updatedAt(account.getUpdatedAt())
                .build();
    }

    public List<TransactionDTO> getUserTransactionsPaginated(UserEntity currentUser, int page, int size) {
        AccountDTO account = getUserAccount(currentUser);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "transactionDate").and(Sort.by(Sort.Direction.DESC, "id")));
        List<TransactionEntity> transactions = transactionRepository.getByAccountId(account.getId(), pageable);

        return transactions.stream().map(TransactionDTO::new).collect(Collectors.toList());
    }

    public UserEntity findUserByIdentifier(String email, String credentialNumber) {
        Optional<UserProfileEntity> profileOpt = userProfileRepository.findByCredentialNumber(credentialNumber);
        if (profileOpt.isPresent()) {
            return profileOpt.get().getUser();
        } else {
            Optional<UserEntity> userOpt = userRepository.findByEmail(email);
            return userOpt.orElseThrow(() -> new ResourceNotFoundException("Could not find user!"));
        }
    }

    public AdminLoadCreditResult loadCredit(AdminLoadCreditRequest request) {
        UserEntity currentUser = userService.getCurrentUser();
        UserEntity targetUser = findUserByIdentifier(request.getEmail(), request.getCredentialNumber());
        TransactionEntity transaction = creditUserAccount(targetUser.getId(), request.getAmount(), request.getDescription());
        
        // Create audit record with linked transaction
        String auditContent = String.format("Single credit load: %s to user %s (%s), amount: %s",
                request.getDescription() != null ? request.getDescription() : "Credit load",
                targetUser.getEmail(),
                request.getCredentialNumber(),
                request.getAmount());
        transactionAuditService.createAuditRecord(
                currentUser,
                TransactionAuditType.LOAD_SINGLE,
                auditContent,
                transaction);
        
        return AdminLoadCreditResult.builder()
                .amountLoaded(request.getAmount())
                .success(true)
                .transactionId(transaction.getId())
                .identifier(request.getCredentialNumber()).build();
    }

    public FinancialOverview getFinancialOverview() {
        List<FinancialPeriodStatistic> stats = new ArrayList<>();

        Instant now = clockService.now();
        Instant startOfDay = now.truncatedTo(ChronoUnit.DAYS);
        Instant startOfWeek = clockService.today().with(java.time.DayOfWeek.MONDAY).atStartOfDay().toInstant(ZoneOffset.UTC);
        Instant startOfMonth = clockService.today().withDayOfMonth(1).atStartOfDay().toInstant(ZoneOffset.UTC);
        Instant startOfYear = clockService.today().withDayOfYear(1).atStartOfDay().toInstant(ZoneOffset.UTC);
        
        stats.add(computePeriodStatistic(startOfDay, now));
        stats.add(computePeriodStatistic(startOfWeek, now));
        stats.add(computePeriodStatistic(startOfMonth, now));
        stats.add(computePeriodStatistic(startOfYear, now));

        return FinancialOverview.builder()
                .periodStatistics(stats)
                .build();
    }

    public FinancialPeriodStatistic getPeriodStatistic(Instant start, Instant end) {
        if(end == null)
            end = clockService.now();
        return computePeriodStatistic(start, end);
    }

    private FinancialPeriodStatistic computePeriodStatistic(Instant startOfDay, Instant now) {
        // Calculate total credit transactions (money added to accounts)
        BigDecimal totalCreditTransactions = transactionRepository.sumCreditAmountInDateRange(startOfDay, now);
        if (totalCreditTransactions == null) {
            totalCreditTransactions = BigDecimal.ZERO;
        }
        
        // Calculate total debit transactions (money spent from accounts)
        BigDecimal totalDebits = transactionRepository.sumDebitAmountInDateRange(startOfDay, now);
        if (totalDebits == null) {
            totalDebits = BigDecimal.ZERO;
        }
        
        // Calculate total refunds (money refunded to users)
        BigDecimal totalRefunds = transactionRepository.sumRefundAmountInDateRange(startOfDay, now);
        if (totalRefunds == null) {
            totalRefunds = BigDecimal.ZERO;
        }
        
        // Calculate pending revenue (orders that can still be cancelled - before editBy date)
        BigDecimal totalRevenuePending = orderItemRepository.sumPendingRevenueInDateRange(startOfDay, now, clockService.now());
        if (totalRevenuePending == null) {
            totalRevenuePending = BigDecimal.ZERO;
        }
        
        // Build and return the statistics object
        return FinancialPeriodStatistic.builder()
                .periodStart(startOfDay)
                .periodEnd(now)
                .totalRevenue(totalDebits) // Revenue is money spent by users (debits)
                .totalCreditTransactions(totalCreditTransactions.subtract(totalRefunds)) // Credits are money added to accounts
                .totalRevenuePending(totalRevenuePending) // Revenue from cancellable orders
                .totalRefunds(totalRefunds) // Money refunded to users
                .build();
    }

    public List<FinanceUserDTO> findAllUsers() {
        List<UserEntity> users = userRepository.findAll();
        return users.stream().map(this::convertToFinanceUserDTO).filter(v -> v.getCredentialNumber() != null && !v.getCredentialNumber().isEmpty()).toList();
    }

    public List<AdminTransactionDTO> getAllTransactions() {
        return transactionRepository.findAll().stream()
                .sorted(Comparator.comparing(TransactionEntity::getTransactionDate).reversed())
                .map(AdminTransactionDTO::new).toList();
    }
}
