package com.jel.spys.demo;

import com.jel.spys.entity.AccountEntity;
import com.jel.spys.entity.AllergyEntity;
import com.jel.spys.entity.CampusEntity;
import com.jel.spys.entity.MenuItemEntity;
import com.jel.spys.entity.MenuTemplateEntity;
import com.jel.spys.entity.NotificationEntity;
import com.jel.spys.entity.NotificationPreferenceEntity;
import com.jel.spys.entity.NotificationStatus;
import com.jel.spys.entity.NotificationType;
import com.jel.spys.entity.OrderEntity;
import com.jel.spys.entity.OrderItemEntity;
import com.jel.spys.entity.OrderItemFeedbackEntity;
import com.jel.spys.entity.OrderItemStatus;
import com.jel.spys.entity.OrderStatus;
import com.jel.spys.entity.RefreshTokenEntity;
import com.jel.spys.entity.ReportEntity;
import com.jel.spys.entity.ReportStatus;
import com.jel.spys.entity.ReportType;
import com.jel.spys.entity.ResidenceEntity;
import com.jel.spys.entity.Role;
import com.jel.spys.entity.TransactionAuditType;
import com.jel.spys.entity.TransactionEntity;
import com.jel.spys.entity.UserDeviceEntity;
import com.jel.spys.entity.UserEntity;
import com.jel.spys.entity.UserEventEntity;
import com.jel.spys.entity.UserEventType;
import com.jel.spys.entity.UserProfileEntity;
import com.jel.spys.model.CreateUserProfileRequest;
import com.jel.spys.model.FeedbackRequest;
import com.jel.spys.model.MenuItemDTO;
import com.jel.spys.model.MenuItemQueueRequest;
import com.jel.spys.model.OrderItemRequest;
import com.jel.spys.model.PlaceOrderRequest;
import com.jel.spys.repository.AccountRepository;
import com.jel.spys.repository.AllergyRepository;
import com.jel.spys.repository.CampusRepository;
import com.jel.spys.repository.MenuItemRepository;
import com.jel.spys.repository.MenuTemplateRepository;
import com.jel.spys.repository.NotificationPreferenceRepository;
import com.jel.spys.repository.NotificationRepository;
import com.jel.spys.repository.OrderItemFeedbackRepository;
import com.jel.spys.repository.OrderItemRepository;
import com.jel.spys.repository.OrderRepository;
import com.jel.spys.repository.RefreshTokenRepository;
import com.jel.spys.repository.ReportRepository;
import com.jel.spys.repository.ResidenceRepository;
import com.jel.spys.repository.TransactionAuditRepository;
import com.jel.spys.repository.TransactionRepository;
import com.jel.spys.repository.UserDeviceRepository;
import com.jel.spys.repository.UserEventRepository;
import com.jel.spys.repository.UserProfileRepository;
import com.jel.spys.repository.UserRepository;
import com.jel.spys.service.ClockService;
import com.jel.spys.service.FeedbackService;
import com.jel.spys.service.FinanceService;
import com.jel.spys.service.MenuService;
import com.jel.spys.service.OrderService;
import com.jel.spys.service.TransactionAuditService;
import com.jel.spys.service.UserProfileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
public class DemoDataLoader implements CommandLineRunner {

    private static final String FINANCE_ADMIN_EMAIL = "felix.finance@portfolio.demo";
    private static final String AUDIT_ADMIN_EMAIL = "audrey.audit@portfolio.demo";
    private static final String MENU_ADMIN_EMAIL = "mia.menu@portfolio.demo";
    private static final String SUPER_ADMIN_EMAIL = "sam.super@portfolio.demo";
    private static final String DEFAULT_PASSWORD = "demo123";
    private static final ZoneId APP_ZONE = ZoneId.systemDefault();

    private static final List<String> ALLERGY_NAMES = List.of(
            "Gluten",
            "Dairy",
            "Tree Nuts",
            "Fish",
            "Shellfish",
            "Eggs",
            "Soy"
    );

    private static final List<String> CAMPUS_NAMES = List.of(
            "Hatfield Campus",
            "Lynnwood Campus",
            "Centurion Campus",
            "West City Campus"
    );

    private static final List<String> RESIDENCE_NAMES = List.of(
            "Maple House",
            "Oak Court",
            "Riverside Residence",
            "Summit Hall",
            "Cedar Commons",
            "Parkview Lofts"
    );

    private static final List<DemoUserSeed> DEMO_USERS = List.of(
            new DemoUserSeed("alex.carter@portfolio.demo", "Alex", "Carter", Set.of(Role.USER), DEFAULT_PASSWORD, true, true, true, true, "Hatfield Campus", "Maple House", List.of("Dairy")),
            new DemoUserSeed("maya.singh@portfolio.demo", "Maya", "Singh", Set.of(Role.USER), DEFAULT_PASSWORD, true, true, true, true, "Lynnwood Campus", "Oak Court", List.of("Gluten")),
            new DemoUserSeed("daniel.brooks@portfolio.demo", "Daniel", "Brooks", Set.of(Role.USER), DEFAULT_PASSWORD, true, true, true, true, "Centurion Campus", "Riverside Residence", List.of()),
            new DemoUserSeed("chloe.adams@portfolio.demo", "Chloe", "Adams", Set.of(Role.USER), DEFAULT_PASSWORD, true, true, true, true, "West City Campus", "Summit Hall", List.of("Tree Nuts")),
            new DemoUserSeed("ethan.patel@portfolio.demo", "Ethan", "Patel", Set.of(Role.USER), DEFAULT_PASSWORD, true, true, true, true, "Hatfield Campus", "Cedar Commons", List.of("Soy")),
            new DemoUserSeed("grace.johnson@portfolio.demo", "Grace", "Johnson", Set.of(Role.USER), DEFAULT_PASSWORD, true, true, true, true, "Lynnwood Campus", "Maple House", List.of()),
            new DemoUserSeed("liam.ndlovu@portfolio.demo", "Liam", "Ndlovu", Set.of(Role.USER), DEFAULT_PASSWORD, true, true, true, true, "Centurion Campus", "Parkview Lofts", List.of("Eggs")),
            new DemoUserSeed("sophie.williams@portfolio.demo", "Sophie", "Williams", Set.of(Role.USER), DEFAULT_PASSWORD, true, true, true, true, "West City Campus", "Oak Court", List.of("Dairy", "Gluten")),
            new DemoUserSeed("noah.kim@portfolio.demo", "Noah", "Kim", Set.of(Role.USER), DEFAULT_PASSWORD, true, true, true, true, "Hatfield Campus", null, List.of("Fish")),
            new DemoUserSeed("aisha.daniels@portfolio.demo", "Aisha", "Daniels", Set.of(Role.USER), DEFAULT_PASSWORD, true, true, true, true, "Lynnwood Campus", "Riverside Residence", List.of()),
            new DemoUserSeed("benjamin.cooper@portfolio.demo", "Benjamin", "Cooper", Set.of(Role.USER), DEFAULT_PASSWORD, true, true, true, true, "Centurion Campus", "Summit Hall", List.of("Shellfish")),
            new DemoUserSeed("olivia.reed@portfolio.demo", "Olivia", "Reed", Set.of(Role.USER), DEFAULT_PASSWORD, true, true, true, true, "West City Campus", null, List.of("Tree Nuts")),
            new DemoUserSeed("marcus.bell@portfolio.demo", "Marcus", "Bell", Set.of(Role.USER), DEFAULT_PASSWORD, true, true, true, true, "Hatfield Campus", "Cedar Commons", List.of()),
            new DemoUserSeed("priya.naidoo@portfolio.demo", "Priya", "Naidoo", Set.of(Role.USER), DEFAULT_PASSWORD, true, true, true, true, "Lynnwood Campus", "Parkview Lofts", List.of("Soy")),
            new DemoUserSeed("emma.jacobs@portfolio.demo", "Emma", "Jacobs", Set.of(Role.USER), DEFAULT_PASSWORD, true, true, true, true, "Centurion Campus", "Maple House", List.of()),
            new DemoUserSeed("zoe.bennett@portfolio.demo", "Zoe", "Bennett", Set.of(Role.USER), DEFAULT_PASSWORD, true, true, true, true, "West City Campus", "Riverside Residence", List.of("Gluten")),
            new DemoUserSeed("caleb.morris@portfolio.demo", "Caleb", "Morris", Set.of(Role.USER), DEFAULT_PASSWORD, true, true, true, true, "Hatfield Campus", null, List.of("Eggs")),
            new DemoUserSeed("lucy.foster@portfolio.demo", "Lucy", "Foster", Set.of(Role.USER), DEFAULT_PASSWORD, true, true, true, true, "Lynnwood Campus", "Summit Hall", List.of("Dairy")),
            new DemoUserSeed("jordan.price@portfolio.demo", "Jordan", "Price", Set.of(Role.USER), DEFAULT_PASSWORD, true, true, false, true, "Centurion Campus", "Oak Court", List.of()),
            new DemoUserSeed("nina.alvarez@portfolio.demo", "Nina", "Alvarez", Set.of(Role.USER), DEFAULT_PASSWORD, false, true, true, true, "West City Campus", "Cedar Commons", List.of("Shellfish")),
            new DemoUserSeed("ryan.owens@portfolio.demo", "Ryan", "Owens", Set.of(Role.USER), DEFAULT_PASSWORD, true, false, true, true, "Hatfield Campus", "Parkview Lofts", List.of()),
            new DemoUserSeed("tessa.green@portfolio.demo", "Tessa", "Green", Set.of(Role.USER), DEFAULT_PASSWORD, true, true, true, false, "Lynnwood Campus", "Maple House", List.of("Tree Nuts")),
            new DemoUserSeed("audrey.audit@portfolio.demo", "Audrey", "Audit", Set.of(Role.ADMIN, Role.AUDIT_ADMIN, Role.REPORTING_ADMIN, Role.USER_ADMIN, Role.SYSTEM_ADMIN), DEFAULT_PASSWORD, true, true, true, true, null, null, List.of()),
            new DemoUserSeed("felix.finance@portfolio.demo", "Felix", "Finance", Set.of(Role.ADMIN, Role.FINANCIAL_ADMIN, Role.REPORTING_ADMIN), DEFAULT_PASSWORD, true, true, true, true, null, null, List.of()),
            new DemoUserSeed("mia.menu@portfolio.demo", "Mia", "Menu", Set.of(Role.ADMIN, Role.MENU_ADMIN, Role.FEEDBACK_ADMIN), DEFAULT_PASSWORD, true, true, true, true, null, null, List.of()),
            new DemoUserSeed("dylan.delivery@portfolio.demo", "Dylan", "Delivery", Set.of(Role.ADMIN, Role.ORDER_ADMIN, Role.DELIVERY_ADMIN), DEFAULT_PASSWORD, true, true, true, true, null, null, List.of()),
            new DemoUserSeed("sofia.support@portfolio.demo", "Sofia", "Support", Set.of(Role.ADMIN, Role.USER_ADMIN, Role.FEEDBACK_ADMIN), DEFAULT_PASSWORD, true, true, true, true, null, null, List.of()),
            new DemoUserSeed("sam.super@portfolio.demo", "Sam", "Super", Set.of(Role.ADMIN, Role.SYSTEM_ADMIN, Role.FINANCIAL_ADMIN, Role.MENU_ADMIN, Role.USER_ADMIN, Role.AUDIT_ADMIN, Role.REPORTING_ADMIN, Role.ORDER_ADMIN, Role.DELIVERY_ADMIN, Role.FEEDBACK_ADMIN), DEFAULT_PASSWORD, true, true, true, true, null, null, List.of())
    );

    private static final List<MenuSeed> MENU_CATALOG = List.of(
            new MenuSeed("Lemon Herb Chicken Bowl", "Grilled chicken, roasted vegetables, herbed rice, and a light yogurt drizzle.", 56.00, 740L, List.of("Dairy")),
            new MenuSeed("Beef Lasagne", "Layered beef ragout, rich tomato sauce, and bubbling mozzarella.", 62.00, 860L, List.of("Gluten", "Dairy")),
            new MenuSeed("Roast Vegetable Wrap", "Charred peppers, hummus, baby spinach, and feta in a toasted wrap.", 48.00, 590L, List.of("Gluten", "Dairy")),
            new MenuSeed("Butter Chicken with Basmati Rice", "Tender chicken in a tomato-butter sauce with fragrant basmati rice.", 64.00, 820L, List.of("Dairy")),
            new MenuSeed("Grilled Hake and Potato Wedges", "Lemon pepper hake, crisp potato wedges, and tartar sauce.", 68.00, 700L, List.of("Fish", "Eggs")),
            new MenuSeed("BBQ Chicken Burger and Chips", "Smoky chicken burger with cheddar, slaw, and golden fries.", 58.00, 910L, List.of("Gluten", "Dairy", "Eggs")),
            new MenuSeed("Mediterranean Pasta Salad", "Penne pasta with olives, cherry tomatoes, basil pesto, and parmesan.", 46.00, 610L, List.of("Gluten", "Dairy", "Tree Nuts")),
            new MenuSeed("Thai Green Curry Tofu Bowl", "Green curry tofu with jasmine rice, snap peas, and fresh herbs.", 52.00, 640L, List.of("Soy")),
            new MenuSeed("Creamy Mushroom Penne", "Penne pasta tossed in a garlic mushroom cream sauce with parsley.", 50.00, 730L, List.of("Gluten", "Dairy")),
            new MenuSeed("Peri-Peri Chicken Wrap", "Spicy chicken strips, crunchy slaw, and chili mayo in a grilled wrap.", 54.00, 670L, List.of("Gluten", "Eggs")),
            new MenuSeed("Ginger Sesame Stir-Fry", "Wok-fried vegetables and noodles with ginger, sesame, and cashews.", 49.00, 620L, List.of("Gluten", "Soy", "Tree Nuts")),
            new MenuSeed("Cottage Pie", "Savory beef mince topped with creamy mashed potato and oven-baked until golden.", 60.00, 790L, List.of("Dairy")),
            new MenuSeed("Falafel Mezze Plate", "Crispy falafel with couscous salad, tzatziki, and pickled vegetables.", 47.00, 580L, List.of("Gluten", "Dairy")),
            new MenuSeed("Sweet Chili Beef Noodles", "Tender beef strips with noodles, peppers, and sticky sweet chili sauce.", 61.00, 810L, List.of("Gluten", "Soy"))
    );

    private final Random random = new Random(20260626L);

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private AllergyRepository allergyRepository;

    @Autowired
    private CampusRepository campusRepository;

    @Autowired
    private ResidenceRepository residenceRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private OrderItemFeedbackRepository orderItemFeedbackRepository;

    @Autowired
    private NotificationPreferenceRepository notificationPreferenceRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private MenuTemplateRepository menuTemplateRepository;

    @Autowired
    private UserDeviceRepository userDeviceRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private TransactionAuditRepository transactionAuditRepository;

    @Autowired
    private UserEventRepository userEventRepository;

    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    private ClockService clockService;

    @Autowired
    private MenuService menuService;

    @Autowired
    private FinanceService financeService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private FeedbackService feedbackService;

    @Autowired
    private TransactionAuditService transactionAuditService;

    @Value("${spys.demoData.enabled}")
    private Boolean demoData;

    @Value("${spys.demoData.seedWeeks}")
    private Integer seedWeeks;

    @Override
    public void run(String... args) {
        if (Boolean.TRUE.equals(demoData)) {
            seedDatabase();
        }
    }

    private void seedDatabase() {
        try {
            Map<String, AllergyEntity> allergies = seedAllergies();
            Map<String, CampusEntity> campuses = seedCampuses();
            Map<String, ResidenceEntity> residences = seedResidences();
            Map<String, UserEntity> users = seedUsers();
            seedProfiles(users, campuses, residences, allergies);
            Map<String, MenuItemEntity> sourceMenuItems = seedMenuCatalog(allergies);
            List<UserEntity> operationalUsers = getOperationalUsers(users);

            simulateHistoricalData(operationalUsers, users, sourceMenuItems);
            seedCurrentAndUpcomingMenu(operationalUsers, sourceMenuItems);
            enrichFeedbackMetadata();
            seedNotificationPreferences(operationalUsers);
            seedPortfolioNotifications(operationalUsers);
            normalizeNotifications();
            seedMenuTemplates();
            seedReports(users);
            seedDevicesAndTokens(operationalUsers);
            seedUserEvents(users);
            seedAuditSamples(users, operationalUsers);

            log.info(
                    "Demo seed complete: users={}, profiles={}, menuItems={}, orders={}, notifications={}, reports={}",
                    userRepository.count(),
                    userProfileRepository.count(),
                    menuItemRepository.count(),
                    orderRepository.count(),
                    notificationRepository.count(),
                    reportRepository.count());
        } finally {
            clockService.reset();
        }
    }

    private Map<String, AllergyEntity> seedAllergies() {
        Map<String, AllergyEntity> existing = allergyRepository.findAll().stream()
                .collect(Collectors.toMap(AllergyEntity::getAllergy, allergy -> allergy, (left, right) -> left, LinkedHashMap::new));

        for (String allergyName : ALLERGY_NAMES) {
            existing.computeIfAbsent(allergyName, name -> allergyRepository.save(new AllergyEntity(name)));
        }

        return existing;
    }

    private Map<String, CampusEntity> seedCampuses() {
        Map<String, CampusEntity> existing = campusRepository.findAll().stream()
                .collect(Collectors.toMap(CampusEntity::getCampus, campus -> campus, (left, right) -> left, LinkedHashMap::new));

        for (String campusName : CAMPUS_NAMES) {
            existing.computeIfAbsent(campusName, name -> campusRepository.save(new CampusEntity(name)));
        }

        return existing;
    }

    private Map<String, ResidenceEntity> seedResidences() {
        Map<String, ResidenceEntity> existing = residenceRepository.findAll().stream()
                .collect(Collectors.toMap(ResidenceEntity::getResidence, residence -> residence, (left, right) -> left, LinkedHashMap::new));

        for (String residenceName : RESIDENCE_NAMES) {
            existing.computeIfAbsent(residenceName, name -> residenceRepository.save(new ResidenceEntity(name)));
        }

        return existing;
    }

    private Map<String, UserEntity> seedUsers() {
        Map<String, UserEntity> users = new LinkedHashMap<>();

        for (DemoUserSeed seed : DEMO_USERS) {
            Optional<UserEntity> existingUser = userRepository.findByEmail(seed.email());
            UserEntity user = existingUser.orElseGet(UserEntity::new);

            user.setFirstName(seed.firstName());
            user.setLastName(seed.lastName());
            user.setEmail(seed.email());
            user.setPassword(passwordEncoder.encode(seed.password()));
            user.setRoles(seed.roles());
            user.setEnabled(seed.enabled());
            user.setAccountNonExpired(seed.accountNonExpired());
            user.setAccountNonLocked(seed.accountNonLocked());
            user.setCredentialsNonExpired(seed.credentialsNonExpired());

            users.put(seed.email(), userRepository.save(user));
        }

        return users;
    }

    private void seedProfiles(Map<String, UserEntity> users,
                              Map<String, CampusEntity> campuses,
                              Map<String, ResidenceEntity> residences,
                              Map<String, AllergyEntity> allergies) {
        int credentialNumber = 24000000;

        for (DemoUserSeed seed : DEMO_USERS) {
            if (!seed.shouldHaveProfile()) {
                continue;
            }

            UserEntity user = users.get(seed.email());
            UserProfileEntity profile = userProfileRepository.findByUser(user).orElse(null);

            if (profile == null) {
                CreateUserProfileRequest.CreateUserProfileRequestBuilder builder = CreateUserProfileRequest.builder()
                        .credentialNumber(nextAvailableCredentialNumber(credentialNumber))
                        .campusId(campuses.get(seed.campus()).getId())
                        .allergyIds(seed.allergies().stream().map(allergies::get).filter(Objects::nonNull).map(AllergyEntity::getId).toList());

                if (seed.residence() != null) {
                    builder.residenceId(residences.get(seed.residence()).getId());
                }

                userProfileService.createUserProfile(user, builder.build());
                profile = userProfileRepository.findByUser(user).orElseThrow();
            }

            profile.setCampus(campuses.get(seed.campus()));
            profile.setResidence(seed.residence() != null ? residences.get(seed.residence()) : null);
            profile.setAllergy(seed.allergies().stream()
                    .map(allergies::get)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toCollection(ArrayList::new)));

            if (profile.getAccount() == null) {
                profile.setAccount(accountRepository.save(new AccountEntity()));
            }

            userProfileRepository.save(profile);
            credentialNumber++;
        }
    }

    private Map<String, MenuItemEntity> seedMenuCatalog(Map<String, AllergyEntity> allergies) {
        Map<String, MenuItemEntity> draftItems = menuItemRepository.findDraftItems().stream()
                .collect(Collectors.toMap(MenuItemEntity::getName, item -> item, (left, right) -> left, LinkedHashMap::new));
        Map<String, MenuItemEntity> seededItems = new LinkedHashMap<>();

        for (MenuSeed seed : MENU_CATALOG) {
            MenuItemEntity item = draftItems.getOrDefault(seed.name(), new MenuItemEntity());
            item.setName(seed.name());
            item.setDescription(seed.description());
            item.setPrice(seed.price());
            item.setKcal(seed.kcal());
            item.setDeliveryDate(null);
            item.setReleaseDate(null);
            item.setEditBy(null);
            item.setOrderBy(null);
            item.setImageHero(null);
            item.setImageDetail(null);
            item.setAllergies(seed.allergies().stream()
                    .map(allergies::get)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toCollection(ArrayList::new)));

            seededItems.put(seed.name(), menuItemRepository.save(item));
        }

        return seededItems;
    }

    private void simulateHistoricalData(List<UserEntity> operationalUsers,
                                        Map<String, UserEntity> allUsers,
                                        Map<String, MenuItemEntity> sourceMenuItems) {
        if (orderRepository.count() > 0 || seedWeeks == null || seedWeeks <= 0) {
            return;
        }

        UserEntity financeAdmin = allUsers.get(FINANCE_ADMIN_EMAIL);
        LocalDate today = clockService.today();
        LocalDate firstWeek = today.minusWeeks(seedWeeks).with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
        LocalDate yesterday = today.minusDays(1);

        seedInitialBalances(financeAdmin, operationalUsers, firstWeek);

        for (int weekIndex = 0; weekIndex < seedWeeks; weekIndex++) {
            LocalDate weekStart = firstWeek.plusWeeks(weekIndex);
            if (weekStart.isAfter(yesterday)) {
                break;
            }

            if (weekStart.getDayOfMonth() <= 7) {
                seedMonthlyAllowance(financeAdmin, operationalUsers, weekStart);
            }

            List<Instant> deliverySlots = queueHistoricalWeek(sourceMenuItems, weekStart, yesterday, weekIndex);
            placeHistoricalOrders(operationalUsers, weekStart, deliverySlots);
            settleHistoricalOrders(deliverySlots, weekIndex);
            leaveHistoricalFeedback(operationalUsers, weekStart, yesterday);
        }

        markHistoricOrders(today);
    }

    private void seedInitialBalances(UserEntity financeAdmin, List<UserEntity> operationalUsers, LocalDate firstWeek) {
        if (!transactionAuditRepository.findByLoadedContentContaining("demo seed opening balance").isEmpty()) {
            return;
        }

        Instant creditTime = firstWeek.atTime(7, 30).atZone(APP_ZONE).toInstant();
        clockService.setFixedTime(creditTime);

        List<TransactionEntity> transactions = new ArrayList<>();
        for (UserEntity user : operationalUsers) {
            transactions.add(financeService.creditUserAccount(
                    user.getId(),
                    BigDecimal.valueOf(200),
                    "Opening balance for portfolio demo"));
        }

        transactionAuditService.createAuditRecord(
                financeAdmin,
                TransactionAuditType.BULK_CREDIT_LOAD,
                "demo seed opening balance distribution",
                transactions);
    }

    private void seedMonthlyAllowance(UserEntity financeAdmin, List<UserEntity> operationalUsers, LocalDate weekStart) {
        Instant creditTime = weekStart.atTime(8, 15).atZone(APP_ZONE).toInstant();
        clockService.setFixedTime(creditTime);

        List<TransactionEntity> transactions = new ArrayList<>();
        for (int i = 0; i < operationalUsers.size(); i++) {
            UserEntity user = operationalUsers.get(i);
            BigDecimal amount = BigDecimal.valueOf(620 + ((i % 5) * 25L));
            transactions.add(financeService.creditUserAccount(
                    user.getId(),
                    amount,
                    "Monthly meal allowance for " + weekStart.getMonth() + " " + weekStart.getYear()));
        }

        transactionAuditService.createAuditRecord(
                financeAdmin,
                TransactionAuditType.BULK_CREDIT_LOAD,
                "Monthly meal allowance batch for " + weekStart.getMonth() + " " + weekStart.getYear(),
                transactions);
    }

    private List<Instant> queueHistoricalWeek(Map<String, MenuItemEntity> sourceMenuItems,
                                              LocalDate weekStart,
                                              LocalDate yesterday,
                                              int weekIndex) {
        List<Instant> deliverySlots = new ArrayList<>();
        List<MenuItemEntity> catalog = new ArrayList<>(sourceMenuItems.values());

        for (int dayOffset = 0; dayOffset < 5; dayOffset++) {
            LocalDate deliveryDay = weekStart.plusDays(dayOffset);
            if (deliveryDay.isAfter(yesterday)) {
                break;
            }

            Instant lunchSlot = deliveryDay.atTime(12, 0).atZone(APP_ZONE).toInstant();
            Instant dinnerSlot = deliveryDay.atTime(17, 30).atZone(APP_ZONE).toInstant();

            MenuItemEntity lunchItem = catalog.get((weekIndex + dayOffset) % catalog.size());
            MenuItemEntity dinnerItem = catalog.get((weekIndex + dayOffset + 5) % catalog.size());

            queueMenuSlotIfMissing(lunchItem, lunchSlot, 5, 20);
            queueMenuSlotIfMissing(dinnerItem, dinnerSlot, 5, 22);

            deliverySlots.add(lunchSlot);
            deliverySlots.add(dinnerSlot);
        }

        return deliverySlots;
    }

    private void placeHistoricalOrders(List<UserEntity> operationalUsers, LocalDate weekStart, List<Instant> deliverySlots) {
        if (deliverySlots.isEmpty()) {
            return;
        }

        for (UserEntity user : operationalUsers) {
            int orderAttempts = isFrequentBuyer(user) ? 2 : 1;

            for (int attempt = 0; attempt < orderAttempts; attempt++) {
                Instant orderTime = weekStart.minusDays(2)
                        .atTime(10 + random.nextInt(6), random.nextInt(60))
                        .atZone(APP_ZONE)
                        .toInstant()
                        .plus(attempt, ChronoUnit.DAYS);

                if (orderTime.isAfter(clockService.today().minusDays(1).plusDays(1).atStartOfDay(APP_ZONE).toInstant())) {
                    continue;
                }

                clockService.setFixedTime(orderTime);

                List<MenuItemDTO> availableMenu = menuService.getMenu().stream()
                        .filter(item -> item.getDeliveryDate() != null)
                        .filter(item -> !item.getDeliveryDate().isBefore(weekStart.atStartOfDay(APP_ZONE).toInstant()))
                        .filter(item -> item.getDeliveryDate().isBefore(weekStart.plusDays(7).atStartOfDay(APP_ZONE).toInstant()))
                        .sorted(Comparator.comparing(MenuItemDTO::getDeliveryDate))
                        .toList();

                if (availableMenu.isEmpty()) {
                    continue;
                }

                List<MenuItemDTO> shuffled = new ArrayList<>(availableMenu);
                Collections.shuffle(shuffled, random);

                int itemCount = Math.min(shuffled.size(), 1 + random.nextInt(Math.min(3, shuffled.size())));
                List<OrderItemRequest> items = new ArrayList<>();
                for (int i = 0; i < itemCount; i++) {
                    MenuItemDTO item = shuffled.get(i);
                    int quantity = random.nextDouble() < 0.25 ? 2 : 1;
                    items.add(new OrderItemRequest(item.getId(), quantity));
                }

                try {
                    orderService.placeOrder(user, PlaceOrderRequest.builder().items(items).build());
                } catch (Exception ex) {
                    log.debug("Skipped demo order for {} at {}: {}", user.getEmail(), orderTime, ex.getMessage());
                }
            }
        }
    }

    private void settleHistoricalOrders(List<Instant> deliverySlots, int weekIndex) {
        for (Instant deliverySlot : deliverySlots) {
            List<OrderItemEntity> paidItems = orderItemRepository.findByDeliveryDateAndStatus(deliverySlot, OrderItemStatus.PAID);
            if (paidItems.isEmpty()) {
                continue;
            }

            Instant reviewTime = deliverySlot.minus(5, ChronoUnit.HOURS);
            clockService.setFixedTime(reviewTime);

            if ((weekIndex + deliverySlot.hashCode()) % 7 == 0 && !paidItems.isEmpty()) {
                orderService.updateOrderItemStatus(paidItems.get(0).getId(), OrderItemStatus.CANCELLED);
            }

            if ((weekIndex + deliverySlot.hashCode()) % 9 == 0 && paidItems.size() > 1) {
                orderService.updateOrderItemStatus(paidItems.get(1).getId(), OrderItemStatus.REFUNDED);
            }

            advanceStatuses(deliverySlot, OrderItemStatus.PAID, OrderItemStatus.IN_PROGRESS, deliverySlot.minus(3, ChronoUnit.HOURS));
            advanceStatuses(deliverySlot, OrderItemStatus.IN_PROGRESS, OrderItemStatus.IN_DELIVERY, deliverySlot.minus(90, ChronoUnit.MINUTES));
            advanceStatuses(deliverySlot, OrderItemStatus.IN_DELIVERY, OrderItemStatus.DELIVERED, deliverySlot.plus(30, ChronoUnit.MINUTES));
        }
    }

    private void leaveHistoricalFeedback(List<UserEntity> operationalUsers, LocalDate weekStart, LocalDate yesterday) {
        LocalDate feedbackDay = weekStart.plusDays(5);
        if (feedbackDay.isAfter(yesterday)) {
            return;
        }

        clockService.setFixedTime(feedbackDay.atTime(14, 0).atZone(APP_ZONE).toInstant());

        for (UserEntity user : operationalUsers) {
            List<com.jel.spys.model.OrderItemDTO> deliveredItems = orderService.getUserOrderItemsByStatus(user, List.of(OrderItemStatus.DELIVERED));
            for (com.jel.spys.model.OrderItemDTO item : deliveredItems) {
                if (item.getFeedback() != null || random.nextDouble() > 0.35) {
                    continue;
                }

                CanidateFeedback.FeedbackEntry entry = CanidateFeedback.getRandomFeedback(item.getName());
                try {
                    feedbackService.placeUserFeedback(user, new FeedbackRequest(item.getId(), entry.getRating(), entry.getFeedback()));
                } catch (Exception ex) {
                    log.debug("Skipped demo feedback for {} and item {}: {}", user.getEmail(), item.getId(), ex.getMessage());
                }
            }
        }
    }

    private void markHistoricOrders(LocalDate today) {
        Instant startOfToday = today.atStartOfDay(APP_ZONE).toInstant();

        for (OrderEntity order : orderRepository.findAll()) {
            if (order.getOrderItems() == null || order.getOrderItems().isEmpty()) {
                continue;
            }

            boolean fullyPast = order.getOrderItems().stream()
                    .allMatch(item -> item.getDeliveryDate() != null && item.getDeliveryDate().isBefore(startOfToday));
            boolean terminal = order.getOrderItems().stream()
                    .allMatch(item -> item.getStatus() == OrderItemStatus.DELIVERED
                            || item.getStatus() == OrderItemStatus.CANCELLED
                            || item.getStatus() == OrderItemStatus.REFUNDED);

            if (fullyPast && terminal) {
                order.setStatus(OrderStatus.HISTORIC);
                orderRepository.save(order);
            }
        }
    }

    private void seedCurrentAndUpcomingMenu(List<UserEntity> operationalUsers, Map<String, MenuItemEntity> sourceMenuItems) {
        List<Instant> futureSlots = nextBusinessLunchSlots(10);
        List<MenuItemEntity> catalog = new ArrayList<>(sourceMenuItems.values());

        for (int i = 0; i < futureSlots.size(); i++) {
            queueMenuSlotIfMissing(catalog.get((i + 2) % catalog.size()), futureSlots.get(i), 4, 20);
        }

        boolean hasFutureOrders = orderItemRepository.findByStatus(OrderItemStatus.PAID).stream()
                .anyMatch(item -> item.getDeliveryDate() != null && item.getDeliveryDate().isAfter(clockService.now()));
        if (hasFutureOrders || futureSlots.isEmpty()) {
            return;
        }

        List<UserEntity> featuredUsers = operationalUsers.stream().limit(8).toList();
        for (int index = 0; index < featuredUsers.size(); index++) {
            UserEntity user = featuredUsers.get(index);
            Instant targetSlot = futureSlots.get(index % Math.min(4, futureSlots.size()));
            Instant orderTime = targetSlot.minus(2, ChronoUnit.DAYS).minus(index % 3, ChronoUnit.HOURS);
            clockService.setFixedTime(orderTime);

            List<MenuItemDTO> options = menuService.getMenu().stream()
                    .filter(item -> item.getDeliveryDate() != null)
                    .filter(item -> !item.getDeliveryDate().isBefore(targetSlot))
                    .filter(item -> item.getDeliveryDate().isBefore(targetSlot.plus(3, ChronoUnit.DAYS)))
                    .sorted(Comparator.comparing(MenuItemDTO::getDeliveryDate))
                    .toList();

            if (options.isEmpty()) {
                continue;
            }

            List<OrderItemRequest> requests = new ArrayList<>();
            requests.add(new OrderItemRequest(options.get(0).getId(), 1));
            if (options.size() > 1 && index % 2 == 0) {
                requests.add(new OrderItemRequest(options.get(1).getId(), 1));
            }

            try {
                orderService.placeOrder(user, PlaceOrderRequest.builder().items(requests).build());
            } catch (Exception ex) {
                log.debug("Skipped future demo order for {}: {}", user.getEmail(), ex.getMessage());
            }
        }
    }

    private void enrichFeedbackMetadata() {
        List<OrderItemFeedbackEntity> updates = new ArrayList<>();

        for (OrderItemFeedbackEntity feedback : orderItemFeedbackRepository.findAll()) {
            boolean changed = false;

            if (feedback.getSentiment() == null) {
                feedback.setSentiment(feedback.getRating() >= 4 ? 1L : feedback.getRating() == 3 ? 0L : -1L);
                changed = true;
            }

            if (feedback.getCategory() == null || feedback.getCategory().isBlank()) {
                feedback.setCategory(resolveFeedbackCategory(feedback.getFeedback(), feedback.getRating()));
                changed = true;
            }

            if (changed) {
                updates.add(feedback);
            }
        }

        if (!updates.isEmpty()) {
            orderItemFeedbackRepository.saveAll(updates);
        }
    }

    private void seedNotificationPreferences(List<UserEntity> operationalUsers) {
        for (int i = 0; i < operationalUsers.size(); i++) {
            UserEntity user = operationalUsers.get(i);
            NotificationPreferenceEntity preference = notificationPreferenceRepository.findByUser(user)
                    .orElseGet(NotificationPreferenceEntity::new);

            preference.setUser(user);
            preference.setPushEnabled(i % 6 != 0);
            preference.setEmailEnabled(i % 5 != 0);
            preference.setOrderUpdates(true);
            preference.setMenuUpdates(i % 4 != 0);
            preference.setAccountUpdates(true);
            preference.setPromotional(i % 3 == 0);
            preference.setSystemAnnouncements(true);
            preference.setQuietHoursStart(i % 2 == 0 ? "22:00" : null);
            preference.setQuietHoursEnd(i % 2 == 0 ? "06:30" : null);

            notificationPreferenceRepository.save(preference);
        }
    }

    private void seedPortfolioNotifications(List<UserEntity> operationalUsers) {
        boolean alreadySeeded = notificationRepository.findAll().stream()
                .anyMatch(notification -> "Welcome to Meal Orders".equals(notification.getTitle()));
        if (alreadySeeded || operationalUsers.isEmpty()) {
            return;
        }

        UserEntity alex = findUserByEmail(operationalUsers, "alex.carter@portfolio.demo");
        UserEntity maya = findUserByEmail(operationalUsers, "maya.singh@portfolio.demo");
        UserEntity daniel = findUserByEmail(operationalUsers, "daniel.brooks@portfolio.demo");
        UserEntity priya = findUserByEmail(operationalUsers, "priya.naidoo@portfolio.demo");

        List<NotificationEntity> notifications = new ArrayList<>();
        notifications.add(buildNotification(alex, "Welcome to Meal Orders",
                "Your profile is ready and your meal allowance has been loaded for the month.",
                NotificationType.GENERAL_ANNOUNCEMENT, NotificationStatus.READ, 1,
                "/user/menu", null, clockService.now().minus(30, ChronoUnit.DAYS), null));
        notifications.add(buildNotification(maya, "Weekly Menu Refresh",
                "Fresh lunch options for next week are now live. Save your favourites before Friday afternoon.",
                NotificationType.MENU_UPDATE, NotificationStatus.READ, 0,
                "/user/menu/all", null, clockService.now().minus(10, ChronoUnit.DAYS), null));
        notifications.add(buildNotification(daniel, "Low Balance Reminder",
                "Your current balance is running low. Top up before your next order window closes.",
                NotificationType.ACCOUNT_BALANCE_LOW, NotificationStatus.SENT, 1,
                "/user/account", null, clockService.now().minus(2, ChronoUnit.DAYS), null));
        notifications.add(buildNotification(priya, "Courtesy Credit Added",
                "A courtesy credit of R25.00 has been added after your delayed delivery last week.",
                NotificationType.ACCOUNT_CREDITED, NotificationStatus.READ, 1,
                "/user/account", null, clockService.now().minus(5, ChronoUnit.DAYS), null));
        notifications.add(buildNotification(alex, "System Maintenance Window",
                "Scheduled maintenance is planned for Sunday from 22:00 to 23:30. Ordering will be temporarily unavailable.",
                NotificationType.SYSTEM_MAINTENANCE, NotificationStatus.DELIVERED, 2,
                "/user/notifications", clockService.now().minus(1, ChronoUnit.DAYS), clockService.now().minus(1, ChronoUnit.DAYS), clockService.now().plus(6, ChronoUnit.DAYS)));
        notifications.add(buildNotification(maya, "Student Winter Special",
                "Add any wrap and iced tea combo next week for R69.00 while the winter showcase menu is live.",
                NotificationType.PROMOTIONAL, NotificationStatus.SENT, 0,
                "/user/menu/favourites", clockService.now().minus(12, ChronoUnit.HOURS), clockService.now().minus(12, ChronoUnit.HOURS), clockService.now().plus(5, ChronoUnit.DAYS)));

        NotificationEntity futureAnnouncement = buildNotification(
                daniel,
                "Menu Survey Opens Tomorrow",
                "Vote for the dishes you want to see in next month's rotating menu.",
                NotificationType.GENERAL_ANNOUNCEMENT,
                NotificationStatus.PENDING,
                0,
                "/user/notifications",
                clockService.now().plus(18, ChronoUnit.HOURS),
                clockService.now(),
                clockService.now().plus(14, ChronoUnit.DAYS));
        futureAnnouncement.setRetryCount(0);
        notifications.add(futureAnnouncement);

        NotificationEntity failedNotification = buildNotification(
                priya,
                "Delivery Update Retry",
                "We attempted to send a push update for your delivery, but your last registered device was unavailable.",
                NotificationType.ORDER_READY,
                NotificationStatus.FAILED,
                1,
                "/user/orders",
                clockService.now().minus(3, ChronoUnit.HOURS),
                clockService.now().minus(3, ChronoUnit.HOURS),
                clockService.now().plus(3, ChronoUnit.DAYS));
        failedNotification.setRetryCount(3);
        failedNotification.setErrorMessage("Push endpoint returned 410 Gone");
        notifications.add(failedNotification);

        notificationRepository.saveAll(notifications);
    }

    private void normalizeNotifications() {
        Instant now = clockService.now();
        List<NotificationEntity> updates = new ArrayList<>();

        for (NotificationEntity notification : notificationRepository.findAll()) {
            boolean changed = false;

            String translatedTitle = translateNotificationText(notification.getTitle());
            String translatedMessage = translateNotificationText(notification.getMessage());
            if (!Objects.equals(translatedTitle, notification.getTitle())) {
                notification.setTitle(translatedTitle);
                changed = true;
            }
            if (!Objects.equals(translatedMessage, notification.getMessage())) {
                notification.setMessage(translatedMessage);
                changed = true;
            }

            if (notification.getDeepLinkUrl() == null) {
                notification.setDeepLinkUrl(defaultNotificationLink(notification.getType()));
                changed = true;
            }

            if (notification.getScheduledFor() == null) {
                notification.setScheduledFor(Optional.ofNullable(notification.getCreatedAt()).orElse(now));
                changed = true;
            }

            if (notification.getStatus() == NotificationStatus.PENDING && notification.getScheduledFor() != null
                    && !notification.getScheduledFor().isAfter(now)) {
                if (notification.getScheduledFor().isBefore(now.minus(5, ChronoUnit.DAYS))) {
                    notification.setStatus(NotificationStatus.READ);
                    notification.setSentAt(notification.getSentAt() != null ? notification.getSentAt() : notification.getScheduledFor());
                    notification.setDeliveredAt(notification.getDeliveredAt() != null ? notification.getDeliveredAt() : notification.getScheduledFor().plus(2, ChronoUnit.MINUTES));
                    notification.setReadAt(notification.getReadAt() != null ? notification.getReadAt() : notification.getScheduledFor().plus(1, ChronoUnit.HOURS));
                } else if (notification.getScheduledFor().isBefore(now.minus(1, ChronoUnit.DAYS))) {
                    notification.setStatus(NotificationStatus.DELIVERED);
                    notification.setSentAt(notification.getSentAt() != null ? notification.getSentAt() : notification.getScheduledFor());
                    notification.setDeliveredAt(notification.getDeliveredAt() != null ? notification.getDeliveredAt() : notification.getScheduledFor().plus(2, ChronoUnit.MINUTES));
                } else {
                    notification.setStatus(NotificationStatus.SENT);
                    notification.setSentAt(notification.getSentAt() != null ? notification.getSentAt() : notification.getScheduledFor());
                }
                changed = true;
            }

            if (changed) {
                updates.add(notification);
            }
        }

        if (!updates.isEmpty()) {
            notificationRepository.saveAll(updates);
        }
    }

    private void seedMenuTemplates() {
        Set<String> presetNames = Set.copyOf(menuTemplateRepository.findDistinctPresetNames());
        if (!presetNames.contains("Portfolio Weekday Lunch")) {
            menuTemplateRepository.saveAll(List.of(
                    template("Portfolio Weekday Lunch", "Monday lunch service", 0, 12, 0, 5 * 24 * 60, 20 * 60),
                    template("Portfolio Weekday Lunch", "Tuesday lunch service", 1, 12, 0, 5 * 24 * 60, 20 * 60),
                    template("Portfolio Weekday Lunch", "Wednesday lunch service", 2, 12, 0, 5 * 24 * 60, 20 * 60),
                    template("Portfolio Weekday Lunch", "Thursday lunch service", 3, 12, 0, 5 * 24 * 60, 20 * 60),
                    template("Portfolio Weekday Lunch", "Friday lunch service", 4, 12, 0, 5 * 24 * 60, 20 * 60)
            ));
        }

        if (!presetNames.contains("Portfolio Dinner Rotation")) {
            menuTemplateRepository.saveAll(List.of(
                    template("Portfolio Dinner Rotation", "Monday dinner service", 0, 17, 30, 4 * 24 * 60, 16 * 60),
                    template("Portfolio Dinner Rotation", "Tuesday dinner service", 1, 17, 30, 4 * 24 * 60, 16 * 60),
                    template("Portfolio Dinner Rotation", "Wednesday dinner service", 2, 17, 30, 4 * 24 * 60, 16 * 60),
                    template("Portfolio Dinner Rotation", "Thursday dinner service", 3, 17, 30, 4 * 24 * 60, 16 * 60)
            ));
        }

        if (!presetNames.contains("Portfolio Weekend Brunch")) {
            menuTemplateRepository.saveAll(List.of(
                    template("Portfolio Weekend Brunch", "Saturday brunch service", 5, 10, 30, 3 * 24 * 60, 18 * 60),
                    template("Portfolio Weekend Brunch", "Sunday brunch service", 6, 10, 30, 3 * 24 * 60, 18 * 60)
            ));
        }
    }

    private void seedReports(Map<String, UserEntity> users) {
        boolean alreadySeeded = reportRepository.findAll().stream()
                .anyMatch(report -> report.getName() != null && report.getName().startsWith("Demo "));
        if (alreadySeeded) {
            return;
        }

        UserEntity auditAdmin = users.get(AUDIT_ADMIN_EMAIL);
        UserEntity financeAdmin = users.get(FINANCE_ADMIN_EMAIL);
        UserEntity menuAdmin = users.get(MENU_ADMIN_EMAIL);
        UserEntity superAdmin = users.get(SUPER_ADMIN_EMAIL);
        Instant now = clockService.now();

        List<ReportEntity> reports = List.of(
                report("Demo Monthly Sales Snapshot", "High-level revenue and spend summary for the current month.", ReportType.MONTHLY_SALES, ReportStatus.COMPLETED, financeAdmin, now.minus(30, ChronoUnit.DAYS), now.minus(1, ChronoUnit.DAYS), false, "/reports/demo-monthly-sales.pdf", "demo-monthly-sales.pdf", 248_001L, "application/pdf", Map.of("campus", "all", "format", "pdf"), now.minus(6, ChronoUnit.DAYS), now.minus(5, ChronoUnit.DAYS), now.minus(3, ChronoUnit.DAYS), null),
                report("Demo Delivery Schedule - Current Week", "Operational schedule for active campus deliveries.", ReportType.DELIVERY_SCHEDULE, ReportStatus.COMPLETED, auditAdmin, now.minus(2, ChronoUnit.DAYS), now.plus(5, ChronoUnit.DAYS), true, "/reports/demo-delivery-schedule.xlsx", "demo-delivery-schedule.xlsx", 131_220L, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", Map.of("window", "current-week", "includeResidences", "true"), now.minus(20, ChronoUnit.HOURS), now.minus(18, ChronoUnit.HOURS), now.minus(12, ChronoUnit.HOURS), null),
                report("Demo Allergy Summary", "Summary of meals tagged with allergen markers across the active menu cycle.", ReportType.ALLERGEN_REPORT, ReportStatus.COMPLETED, menuAdmin, now.minus(21, ChronoUnit.DAYS), now.plus(21, ChronoUnit.DAYS), false, "/reports/demo-allergy-summary.csv", "demo-allergy-summary.csv", 44_880L, "text/csv", Map.of("allergens", "all", "groupBy", "menu-item"), now.minus(4, ChronoUnit.DAYS), now.minus(3, ChronoUnit.DAYS), now.minus(2, ChronoUnit.DAYS), null),
                report("Demo User Activity Audit", "Recent sign-ins, profile changes, and account updates for admin review.", ReportType.USER_ACTIVITY, ReportStatus.REQUESTED, auditAdmin, now.minus(14, ChronoUnit.DAYS), now, false, null, null, null, null, Map.of("scope", "active-users"), null, null, null, null),
                report("Demo Notification History", "Push and in-app delivery log for recent communication campaigns.", ReportType.NOTIFICATION_HISTORY, ReportStatus.PROCESSING, superAdmin, now.minus(60, ChronoUnit.DAYS), now, true, null, null, null, null, Map.of("includeFailed", "true", "channel", "in-app"), now.minus(2, ChronoUnit.HOURS), null, null, null),
                report("Demo Cancelled Orders Review", "Operational review of cancelled and refunded meal items.", ReportType.CANCELLED_ORDERS, ReportStatus.FAILED, auditAdmin, now.minus(45, ChronoUnit.DAYS), now.minus(5, ChronoUnit.DAYS), false, null, null, null, null, Map.of("statuses", "cancelled,refunded"), now.minus(9, ChronoUnit.DAYS), null, null, "CSV export failed because one refund row referenced a missing legacy attachment."),
                report("Demo Menu Performance Q2", "Quarterly menu popularity and revenue trends by meal.", ReportType.MENU_PERFORMANCE, ReportStatus.COMPLETED, menuAdmin, now.minus(90, ChronoUnit.DAYS), now.minus(1, ChronoUnit.DAYS), false, "/reports/demo-menu-performance-q2.pdf", "demo-menu-performance-q2.pdf", 389_144L, "application/pdf", Map.of("period", "Q2", "topItems", "10"), now.minus(11, ChronoUnit.DAYS), now.minus(10, ChronoUnit.DAYS), now.minus(7, ChronoUnit.DAYS), null),
                report("Demo Finance Reconciliation", "Archived reconciliation pack retained for compliance retention checks.", ReportType.DETAILED_FINANCIAL, ReportStatus.EXPIRED, financeAdmin, now.minus(180, ChronoUnit.DAYS), now.minus(150, ChronoUnit.DAYS), false, "/reports/demo-finance-reconciliation.zip", "demo-finance-reconciliation.zip", 912_004L, "application/zip", Map.of("period", "historic-audit"), now.minus(120, ChronoUnit.DAYS), now.minus(119, ChronoUnit.DAYS), null, null)
        );

        reportRepository.saveAll(reports);
    }

    private void seedDevicesAndTokens(List<UserEntity> operationalUsers) {
        boolean hasDemoDevices = userDeviceRepository.findAll().stream()
                .anyMatch(device -> device.getEndpoint() != null && device.getEndpoint().contains("portfolio-demo-device"));
        if (!hasDemoDevices) {
            List<UserDeviceEntity> devices = new ArrayList<>();
            for (int i = 0; i < Math.min(6, operationalUsers.size()); i++) {
                UserEntity user = operationalUsers.get(i);
                devices.add(device(user,
                        "https://push.demo.local/portfolio-demo-device/" + slug(user.getEmail()) + "/phone",
                        "Mozilla/5.0 (iPhone; CPU iPhone OS 17_0 like Mac OS X)",
                        true,
                        clockService.now().minus(i + 1L, ChronoUnit.DAYS)));
                if (i % 2 == 0) {
                    devices.add(device(user,
                            "https://push.demo.local/portfolio-demo-device/" + slug(user.getEmail()) + "/laptop",
                            "Mozilla/5.0 (Windows NT 10.0; Win64; x64)",
                            i % 4 != 0,
                            clockService.now().minus(i + 2L, ChronoUnit.DAYS)));
                }
            }
            userDeviceRepository.saveAll(devices);
        }

        boolean hasDemoTokens = refreshTokenRepository.findAll().stream()
                .anyMatch(token -> token.getToken() != null && token.getToken().startsWith("demo-refresh-"));
        if (!hasDemoTokens) {
            List<RefreshTokenEntity> tokens = new ArrayList<>();
            for (int i = 0; i < Math.min(8, operationalUsers.size()); i++) {
                UserEntity user = operationalUsers.get(i);
                tokens.add(token(user, "demo-refresh-" + slug(user.getEmail()) + "-active", clockService.now().plus(10 + i, ChronoUnit.DAYS), false, null, clockService.now().minus(i + 1L, ChronoUnit.HOURS)));
                tokens.add(token(user, "demo-refresh-" + slug(user.getEmail()) + "-revoked", clockService.now().plus(4, ChronoUnit.DAYS), true, clockService.now().minus(3, ChronoUnit.DAYS), clockService.now().minus(5, ChronoUnit.DAYS)));
            }
            refreshTokenRepository.saveAll(tokens);
        }
    }

    private void seedUserEvents(Map<String, UserEntity> users) {
        if (userEventRepository.count() >= 60) {
            return;
        }

        Instant base = clockService.now().minus(45, ChronoUnit.DAYS);
        List<UserEventEntity> events = new ArrayList<>();

        addLifecycleEvents(events, users.get("alex.carter@portfolio.demo"), base);
        addLifecycleEvents(events, users.get("maya.singh@portfolio.demo"), base.plus(1, ChronoUnit.DAYS));
        addLifecycleEvents(events, users.get("daniel.brooks@portfolio.demo"), base.plus(2, ChronoUnit.DAYS));
        addLifecycleEvents(events, users.get("priya.naidoo@portfolio.demo"), base.plus(3, ChronoUnit.DAYS));

        events.add(event(users.get(MENU_ADMIN_EMAIL), UserEventType.ADMIN_MENU_CREATED, base.plus(5, ChronoUnit.DAYS)));
        events.add(event(users.get(MENU_ADMIN_EMAIL), UserEventType.ADMIN_MENU_TEMPLATE_CREATED, base.plus(6, ChronoUnit.DAYS)));
        events.add(event(users.get(FINANCE_ADMIN_EMAIL), UserEventType.ADMIN_NOTIFICATION_SENT_TO_ALL, base.plus(8, ChronoUnit.DAYS)));
        events.add(event(users.get(AUDIT_ADMIN_EMAIL), UserEventType.ADMIN_USER_UPDATED, base.plus(9, ChronoUnit.DAYS)));
        events.add(event(users.get("dylan.delivery@portfolio.demo"), UserEventType.ADMIN_BULK_ORDER_STATUS_UPDATED, base.plus(12, ChronoUnit.DAYS)));
        events.add(event(users.get(SUPER_ADMIN_EMAIL), UserEventType.ADMIN_NOTIFICATION_SENT_TO_USER, base.plus(16, ChronoUnit.DAYS)));

        userEventRepository.saveAll(events);
    }

    private void seedAuditSamples(Map<String, UserEntity> users, List<UserEntity> operationalUsers) {
        if (!transactionAuditRepository.findByLoadedContentContaining("Demo seed:").isEmpty()) {
            return;
        }

        UserEntity financeAdmin = users.get(FINANCE_ADMIN_EMAIL);
        UserEntity auditAdmin = users.get(AUDIT_ADMIN_EMAIL);

        clockService.setFixedTime(clockService.now().minus(8, ChronoUnit.DAYS));
        transactionAuditService.createAuditRecord(
                financeAdmin,
                TransactionAuditType.BULK_CREDIT_LOAD,
                "Demo seed: reviewed monthly allowance batches for portfolio reporting.");

        clockService.setFixedTime(clockService.now().minus(6, ChronoUnit.DAYS));
        transactionAuditService.createAuditRecord(
                auditAdmin,
                TransactionAuditType.REFUND,
                "Demo seed: sampled refund activity after kitchen and delivery exceptions.");

        if (operationalUsers.size() >= 2) {
            clockService.setFixedTime(clockService.now().minus(4, ChronoUnit.DAYS));
            TransactionEntity adjustmentA = financeService.creditUserAccount(
                    operationalUsers.get(0).getId(),
                    BigDecimal.valueOf(35),
                    "Loyalty bonus after customer survey");
            TransactionEntity adjustmentB = financeService.creditUserAccount(
                    operationalUsers.get(1).getId(),
                    BigDecimal.valueOf(25),
                    "Courtesy credit for delayed delivery");

            transactionAuditService.createAuditRecord(
                    financeAdmin,
                    TransactionAuditType.ADMIN_ADJUSTMENT,
                    "Demo seed: manual customer-service credits applied to showcase accounts.",
                    List.of(adjustmentA, adjustmentB));
        }
    }

    private List<UserEntity> getOperationalUsers(Map<String, UserEntity> users) {
        return users.values().stream()
                .filter(user -> user.getRoles().contains(Role.USER))
                .filter(user -> !user.getRoles().contains(Role.ADMIN))
                .filter(UserEntity::isEnabled)
                .filter(UserEntity::isAccountNonExpired)
                .filter(UserEntity::isAccountNonLocked)
                .filter(UserEntity::isCredentialsNonExpired)
                .sorted(Comparator.comparing(UserEntity::getEmail))
                .toList();
    }

    private String nextAvailableCredentialNumber(int base) {
        int current = base;
        while (userProfileRepository.existsByCredentialNumber(String.valueOf(current))) {
            current++;
        }
        return String.valueOf(current);
    }

    private void queueMenuSlotIfMissing(MenuItemEntity sourceItem, Instant deliverySlot, int releaseDaysBefore, int orderHoursBefore) {
        boolean alreadyQueued = menuItemRepository.findByDeliveryDateBetween(deliverySlot.minus(1, ChronoUnit.MINUTES), deliverySlot.plus(1, ChronoUnit.MINUTES))
                .stream()
                .anyMatch(item -> Objects.equals(item.getName(), sourceItem.getName()));

        if (alreadyQueued) {
            return;
        }

        menuService.queueMenuItem(new MenuItemQueueRequest(
                sourceItem.getId(),
                deliverySlot,
                deliverySlot.minus(releaseDaysBefore, ChronoUnit.DAYS),
                deliverySlot.minus(orderHoursBefore, ChronoUnit.HOURS),
                deliverySlot.minus(orderHoursBefore, ChronoUnit.HOURS)
        ));
    }

    private void advanceStatuses(Instant deliverySlot, OrderItemStatus from, OrderItemStatus to, Instant updateTime) {
        List<OrderItemEntity> items = orderItemRepository.findByDeliveryDateAndStatus(deliverySlot, from);
        if (items.isEmpty()) {
            return;
        }

        clockService.setFixedTime(updateTime);
        items.stream()
                .map(OrderItemEntity::getName)
                .distinct()
                .forEach(name -> orderService.bulkUpdateOrderStatuses(
                        new com.jel.spys.model.BulkOrderStatusUpdateRequest(deliverySlot, to, from, name)));
    }

    private boolean isFrequentBuyer(UserEntity user) {
        return Set.of(
                "alex.carter@portfolio.demo",
                "maya.singh@portfolio.demo",
                "priya.naidoo@portfolio.demo",
                "emma.jacobs@portfolio.demo"
        ).contains(user.getEmail());
    }

    private String resolveFeedbackCategory(String comment, Integer rating) {
        String lowered = comment == null ? "" : comment.toLowerCase();
        if (lowered.contains("late") || lowered.contains("delivery") || lowered.contains("courier")) {
            return "Delivery";
        }
        if (lowered.contains("cold") || lowered.contains("warm") || lowered.contains("temperature")) {
            return "Temperature";
        }
        if (lowered.contains("container") || lowered.contains("packaging") || lowered.contains("spill")) {
            return "Packaging";
        }
        if (lowered.contains("portion") || lowered.contains("size")) {
            return "Portion Size";
        }
        if (lowered.contains("season") || lowered.contains("flavour") || lowered.contains("taste")) {
            return "Taste";
        }
        return rating != null && rating >= 4 ? "Overall Satisfaction" : "Food Quality";
    }

    private NotificationEntity buildNotification(UserEntity user,
                                                 String title,
                                                 String message,
                                                 NotificationType type,
                                                 NotificationStatus status,
                                                 int priority,
                                                 String deepLink,
                                                 Instant scheduledFor,
                                                 Instant createdAt,
                                                 Instant expiresAt) {
        Instant effectiveCreatedAt = createdAt != null
                ? createdAt
                : scheduledFor != null ? scheduledFor : clockService.now();
        Instant effectiveScheduledFor = scheduledFor != null ? scheduledFor : effectiveCreatedAt;

        NotificationEntity notification = NotificationEntity.builder()
                .user(user)
                .title(title)
                .message(message)
                .type(type)
                .status(status)
                .priority(priority)
                .deepLinkUrl(deepLink)
                .scheduledFor(effectiveScheduledFor)
                .expiresAt(expiresAt)
                .build();

        notification.setCreatedAt(effectiveCreatedAt);
        notification.setUpdatedAt(effectiveCreatedAt);

        if (status == NotificationStatus.SENT || status == NotificationStatus.DELIVERED || status == NotificationStatus.READ) {
            notification.setSentAt(effectiveScheduledFor);
        }
        if (status == NotificationStatus.DELIVERED || status == NotificationStatus.READ) {
            notification.setDeliveredAt(effectiveScheduledFor.plus(2, ChronoUnit.MINUTES));
        }
        if (status == NotificationStatus.READ) {
            notification.setReadAt(effectiveScheduledFor.plus(1, ChronoUnit.HOURS));
        }

        return notification;
    }

    private String translateNotificationText(String text) {
        if (text == null) {
            return null;
        }

        return text
                .replace("Bestelling Geplaas", "Order Placed")
                .replace("Bestelling Status Opdatering", "Order Status Update")
                .replace("Bestelling Voltooi", "Order Completed")
                .replace("Jou Bestelling word Voorberei", "Your Order Is Being Prepared")
                .replace("Jou Bestelling is Op Pad", "Your Order Is On The Way")
                .replace("Bestelling Afgelewer", "Order Delivered")
                .replace("Bestelling Gekanselleer", "Order Cancelled")
                .replace("Terugbetaling Verwerk", "Refund Processed")
                .replace("Terugbetalings Verwerk", "Refunds Processed")
                .replace("Bestelling #", "Order #")
                .replace("geplaas!", "placed successfully!")
                .replace("word nou voorberei vir aflewering op ", "is now being prepared for delivery on ")
                .replace("word nou voorberei.", "are now being prepared.")
                .replace("is nou op pad vir aflewering!", "is now out for delivery!")
                .replace("is suksesvol afgelewer. Geniet jou ete!", "was delivered successfully. Enjoy your meal!")
                .replace("is gekanselleer", "was cancelled")
                .replace("terwyl dit voorberei is", "while it was being prepared")
                .replace("Jy sal 'n terugbetaling ontvang.", "A refund will be issued to your account.")
                .replace("Terugbetaling vir ", "A refund for ")
                .replace("Terugbetalings vir ", "Refunds for ")
                .replace("is verwerk na jou rekening.", "has been processed to your account.");
    }

    private String defaultNotificationLink(NotificationType type) {
        return switch (type) {
            case MENU_UPDATE, PROMOTIONAL -> "/user/menu";
            case ACCOUNT_BALANCE_LOW, ACCOUNT_CREDITED -> "/user/account";
            case ORDER_CONFIRMATION, ORDER_READY, ORDER_CANCELLED -> "/user/order";
            case SYSTEM_MAINTENANCE, GENERAL_ANNOUNCEMENT -> "/user/notifications";
        };
    }

    private MenuTemplateEntity template(String presetName,
                                        String description,
                                        int dayOffset,
                                        int hour,
                                        int minute,
                                        int releaseOffsetMinutes,
                                        int orderOffsetMinutes) {
        MenuTemplateEntity entity = new MenuTemplateEntity();
        entity.setPresetName(presetName);
        entity.setDescription(description);
        entity.setDeliveryOffsetMinutes(dayOffset * 24 * 60 + hour * 60 + minute);
        entity.setReleaseOffsetMinutes(releaseOffsetMinutes);
        entity.setOrderByOffsetMinutes(orderOffsetMinutes);
        return entity;
    }

    private ReportEntity report(String name,
                                String description,
                                ReportType type,
                                ReportStatus status,
                                UserEntity requestedBy,
                                Instant dateFrom,
                                Instant dateTo,
                                boolean scheduled,
                                String fileUrl,
                                String fileName,
                                Long fileSize,
                                String mimeType,
                                Map<String, String> parameters,
                                Instant processingStartedAt,
                                Instant processingCompletedAt,
                                Instant lastDownloadedAt,
                                String errorMessage) {
        ReportEntity entity = ReportEntity.builder()
                .name(name)
                .description(description)
                .type(type)
                .status(status)
                .requestedBy(requestedBy)
                .fileUrl(fileUrl)
                .fileName(fileName)
                .fileSize(fileSize)
                .mimeType(mimeType)
                .parameters(parameters)
                .dateFrom(dateFrom)
                .dateTo(dateTo)
                .scheduled(scheduled)
                .processingStartedAt(processingStartedAt)
                .processingCompletedAt(processingCompletedAt)
                .errorMessage(errorMessage)
                .lastDownloadedAt(lastDownloadedAt)
                .build();
        entity.setCreatedAt(processingStartedAt != null ? processingStartedAt.minus(3, ChronoUnit.HOURS) : clockService.now().minus(2, ChronoUnit.DAYS));
        entity.setUpdatedAt(processingCompletedAt != null ? processingCompletedAt : entity.getCreatedAt());
        return entity;
    }

    private UserDeviceEntity device(UserEntity user,
                                    String endpoint,
                                    String userAgent,
                                    boolean active,
                                    Instant lastUsed) {
        UserDeviceEntity entity = UserDeviceEntity.builder()
                .user(user)
                .endpoint(endpoint)
                .p256dh("demo-p256dh-" + slug(endpoint))
                .auth("demo-auth-" + slug(endpoint))
                .userAgent(userAgent)
                .isActive(active)
                .build();
        entity.setLastUsed(lastUsed);
        entity.setCreatedAt(lastUsed.minus(5, ChronoUnit.DAYS));
        entity.setUpdatedAt(lastUsed);
        return entity;
    }

    private RefreshTokenEntity token(UserEntity user,
                                     String tokenValue,
                                     Instant expiresAt,
                                     boolean revoked,
                                     Instant revokedAt,
                                     Instant lastUsed) {
        RefreshTokenEntity entity = RefreshTokenEntity.builder()
                .user(user)
                .token(tokenValue)
                .expiresAt(expiresAt)
                .revoked(revoked)
                .revokedAt(revokedAt)
                .build();
        entity.setCreatedAt(lastUsed.minus(2, ChronoUnit.DAYS));
        entity.setLastUsed(lastUsed);
        return entity;
    }

    private void addLifecycleEvents(List<UserEventEntity> events, UserEntity user, Instant base) {
        events.add(event(user, UserEventType.REGISTER, base));
        events.add(event(user, UserEventType.PROFILE_CREATED, base.plus(10, ChronoUnit.MINUTES)));
        events.add(event(user, UserEventType.LOGIN, base.plus(1, ChronoUnit.DAYS)));
        events.add(event(user, UserEventType.ORDER_CREATED, base.plus(3, ChronoUnit.DAYS)));
        events.add(event(user, UserEventType.FEEDBACK_SUBMITTED, base.plus(10, ChronoUnit.DAYS)));
        events.add(event(user, UserEventType.SETTINGS_UPDATED, base.plus(15, ChronoUnit.DAYS)));
    }

    private UserEventEntity event(UserEntity user, UserEventType type, Instant time) {
        UserEventEntity entity = new UserEventEntity();
        entity.setUser(user);
        entity.setEventType(type);
        entity.setTime(time);
        return entity;
    }

    private List<Instant> nextBusinessLunchSlots(int count) {
        List<Instant> slots = new ArrayList<>();
        LocalDate cursor = clockService.today();

        while (slots.size() < count) {
            if (cursor.getDayOfWeek().getValue() <= 5) {
                slots.add(cursor.atTime(LocalTime.NOON).atZone(APP_ZONE).toInstant());
            }
            cursor = cursor.plusDays(1);
        }

        return slots;
    }

    private UserEntity findUserByEmail(List<UserEntity> users, String email) {
        return users.stream().filter(user -> email.equals(user.getEmail())).findFirst().orElseThrow();
    }

    private String slug(String value) {
        return value.toLowerCase()
                .replace("@", "-")
                .replace(".", "-")
                .replace("/", "-")
                .replace(":", "-");
    }

    private record DemoUserSeed(String email,
                                String firstName,
                                String lastName,
                                Set<Role> roles,
                                String password,
                                boolean enabled,
                                boolean accountNonExpired,
                                boolean accountNonLocked,
                                boolean credentialsNonExpired,
                                String campus,
                                String residence,
                                List<String> allergies) {
        private boolean shouldHaveProfile() {
            return roles.contains(Role.USER);
        }
    }

    private record MenuSeed(String name,
                            String description,
                            BigDecimal price,
                            long kcal,
                            List<String> allergies) {
        private MenuSeed(String name, String description, double price, long kcal, List<String> allergies) {
            this(name, description, BigDecimal.valueOf(price), kcal, allergies);
        }
    }
}
