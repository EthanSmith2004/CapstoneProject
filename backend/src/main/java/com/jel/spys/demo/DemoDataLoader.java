package com.jel.spys.demo;

import com.jel.spys.entity.AllergyEntity;
import com.jel.spys.entity.CampusEntity;
import com.jel.spys.entity.MenuItemEntity;
import com.jel.spys.entity.OrderItemStatus;
import com.jel.spys.entity.ResidenceEntity;
import com.jel.spys.entity.Role;
import com.jel.spys.entity.UserEntity;
import com.jel.spys.entity.UserProfileEntity;
import com.jel.spys.model.AdminCreateUserRequest;
import com.jel.spys.model.AdminLoadCreditRequest;
import com.jel.spys.model.BulkOrderStatusUpdateRequest;
import com.jel.spys.model.CreateUserProfileRequest;
import com.jel.spys.model.FeedbackRequest;
import com.jel.spys.model.MenuItemDTO;
import com.jel.spys.model.MenuItemQueueRequest;
import com.jel.spys.model.OrderItemRequest;
import com.jel.spys.model.PlaceOrderRequest;
import com.jel.spys.model.RegisterRequest;
import com.jel.spys.repository.*;
import com.jel.spys.service.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.random.RandomGenerator;

@Component
public class DemoDataLoader implements CommandLineRunner {

    @Autowired
    private AuthService authService;
    @Autowired
    private UserProfileRepository userProfileRepository;
    @Autowired
    private AllergyRepository allergyRepository;
    @Autowired
    private UserProfileService userProfileService;
    @Autowired
    private CampusRepository campusRepository;
    @Autowired
    private ResidenceRepository residenceRepository;
    @Autowired
    private MenuItemRepository menuItemRepository;
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
    private UserService userService;

    @Value("${spys.demoData.enabled}")
    private Boolean demoData;

    @Value("${spys.demoData.seedWeeks}")
    private Integer seedWeeks;

    @Autowired
    private UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
        if (demoData) {
            seedDatabase();
        }
    }

    private void seedDatabase() {
        if(userRepository.count() <= 100) {
            seedUsers();
        }

        if(allergyRepository.count() < 4) {
            seedAllergies();
        }

        if (campusRepository.count() < 3) {
            seedCampus();
        }

        if (residenceRepository.count() < 3) {
            seedResidences();
        }

        if (userProfileRepository.count() < userRepository.count()) {
            seedProfile();
        }

        if (seedWeeks > 0) {
            simulateWeeksOfOrders();
        }
    }

    private void simulateWeeksOfOrders() {
        // Calculate the first Monday of the simulation period
        Instant firstMonday = LocalDateTime.now().minusDays(7L * seedWeeks).truncatedTo(ChronoUnit.DAYS).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).toInstant(ZoneOffset.UTC);
        
        // Calculate yesterday end of day (23:59:59) as the cutoff point
        Instant yesterdayEndOfDay = LocalDateTime.now().minusDays(1).truncatedTo(ChronoUnit.DAYS).plus(1, ChronoUnit.DAYS).minus(1, ChronoUnit.SECONDS).toInstant(ZoneOffset.UTC);

        // Get allergies for menu items
        List<AllergyEntity> allergies = allergyRepository.findAll();
        AllergyEntity gluten = allergies.stream().filter(a -> a.getAllergy().equals("Gluten")).findFirst().orElse(null);
        AllergyEntity dairy = allergies.stream().filter(a -> a.getAllergy().equals("Suiwel")).findFirst().orElse(null);
        AllergyEntity nuts = allergies.stream().filter(a -> a.getAllergy().equals("Neute")).findFirst().orElse(null);
        AllergyEntity fish = allergies.stream().filter(a -> a.getAllergy().equals("Vis")).findFirst().orElse(null);

        // Create menu items with realistic allergies
        MenuItemEntity spaghetti = new MenuItemEntity("Spaghetti Bolognese", "Klassieke Italiaanse pasta met 'n Bolognese sous", 45.00, 1200L, "/images/spaghetti.jpg");
        if (gluten != null) spaghetti.getAllergies().add(gluten); // Pasta contains gluten
        if (dairy != null) spaghetti.getAllergies().add(dairy); // Cheese topping

        MenuItemEntity burger = new MenuItemEntity("Burger en Skyfies", "Beesvleis burger met goudbruin skyfies en slaai", 55.00, 950L, "/images/burger.jpg");
        if (gluten != null) burger.getAllergies().add(gluten); // Burger bun contains gluten

        MenuItemEntity salad = new MenuItemEntity("Slaai", "Vars groente slaai met avokado en neute", 40.00, 600L, "/images/salad.jpg");
        if (nuts != null) salad.getAllergies().add(nuts); // Contains nuts

        MenuItemEntity chickenCurry = new MenuItemEntity("Hoender Kerrie", "Hoender kerrie met rys en groente", 50.00, 1100L, "/images/chicken_curry.jpg");
        if (dairy != null) chickenCurry.getAllergies().add(dairy); // Often contains cream/yogurt

        MenuItemEntity fishAndChips = new MenuItemEntity("Vis en Skyfies", "Geurige gebakte vis met skyfies en sous", 60.00, 800L, "/images/fish_and_chips.jpg");
        if (gluten != null) fishAndChips.getAllergies().add(gluten); // Battered fish
        if (fish != null) fishAndChips.getAllergies().add(fish); // Contains fish

        MenuItemEntity lasagne = new MenuItemEntity("Lasagne", "Lae van pasta, groente en kaas in 'n ryk sous", 48.00, 900L, "/images/lasagne.jpg");
        if (gluten != null) lasagne.getAllergies().add(gluten); // Pasta sheets
        if (dairy != null) lasagne.getAllergies().add(dairy); // Cheese layers

        MenuItemEntity pizza = new MenuItemEntity("Pizza Margherita", "Tradisionele pizza met tamatie sous, mozzarella en vars kruie", 50.00, 850L, "/images/pizza.jpg");
        if (gluten != null) pizza.getAllergies().add(gluten); // Pizza dough
        if (dairy != null) pizza.getAllergies().add(dairy); // Mozzarella cheese

        MenuItemEntity beefStroganoff = new MenuItemEntity("Beesvleis Stroganoff", "Sagte beesvleis in 'n romerige sampioen sous met rys", 65.00, 1300L, "/images/beef_stroganoff.jpg");
        if (dairy != null) beefStroganoff.getAllergies().add(dairy); // Cream sauce

        MenuItemEntity veggieStirfry = new MenuItemEntity("Groente Roerbraai", "Kleurvolle groente roerbraai met sojasous en noedels", 42.00, 700L, "/images/veggie_stirfry.jpg");
        if (gluten != null) veggieStirfry.getAllergies().add(gluten); // Soy sauce and noodles contain gluten
        if (nuts != null) veggieStirfry.getAllergies().add(nuts); // Often contains cashews

        MenuItemEntity chops = new MenuItemEntity("Braaivleis en Aartappels", "Gebraaide vark tjops met gebakte aartappels en groente", 70.00, 1150L, "/images/chops_and_potatoes.jpg");
        // No common allergens

        List<MenuItemEntity> menuItems = menuItemRepository.saveAll(List.of(
            spaghetti, burger, salad, chickenCurry, fishAndChips, 
            lasagne, pizza, beefStroganoff, veggieStirfry, chops
        ));

        // Get all users for simulation
        List<UserEntity> users = userRepository.findAll().stream().filter(userEntity -> userEntity.getRoles().contains(Role.USER)).toList();

        for (int i = 0; i < seedWeeks; i++) {
            Instant week = firstMonday.plus(i* 7L, ChronoUnit.DAYS);
            
            // Check if this week extends beyond yesterday
            boolean isCurrentWeek = week.plus(6, ChronoUnit.DAYS).isAfter(yesterdayEndOfDay);
            
            // If this is the first week of the month add some financial credit
            clockService.setFixedTime(week);
            if (LocalDate.ofInstant(week, ZoneId.systemDefault()).getDayOfMonth() <= 7) {
                for (UserEntity user : users) {
                    UserProfileEntity profile = userProfileRepository.findByUser(user).orElseThrow();
                    financeService.loadCredit(AdminLoadCreditRequest.builder()
                            .credentialNumber(profile.getCredentialNumber())
                            .amount(BigDecimal.valueOf(200.00))
                            .build());
                }
            }
            // First step is generate menu for the week
            // For every day from monday to friday, assign 1 menu item for delivery 12:00 PM
            clockService.setFixedTime(week.minus(3, ChronoUnit.DAYS));
            
            // Determine how many days to process for this week
            int daysToProcess = isCurrentWeek ? 
                Math.min(5, (int) ChronoUnit.DAYS.between(week, yesterdayEndOfDay.plus(1, ChronoUnit.DAYS))) : 5;
            
            for (int d = 0; d < daysToProcess; d++) {
                Instant deliveryDate = week.plus(d, ChronoUnit.DAYS).plus(12, ChronoUnit.HOURS);
                
                // Only create menu items for days that are not in the future (beyond yesterday end of day)
                if (deliveryDate.isAfter(yesterdayEndOfDay)) {
                    break;
                }
                
                MenuItemQueueRequest request = new MenuItemQueueRequest(
                        menuItems.get(RandomGenerator.getDefault().nextInt(0, menuItems.size())).getId(),
                        deliveryDate,
                        deliveryDate.minus(3, ChronoUnit.DAYS), // release date
                        deliveryDate.minus(18, ChronoUnit.HOURS), // edit by date
                        deliveryDate.minus(18, ChronoUnit.HOURS) // order by date
                );
                menuService.queueMenuItem(request);
            }

            for (UserEntity user : users) {
                Instant orderTime = week
                    .minus(1, ChronoUnit.DAYS)
                    .plus(RandomGenerator.getDefault().nextInt(0, Math.min(5, daysToProcess + 1)), ChronoUnit.DAYS)
                    .plus(RandomGenerator.getDefault().nextInt(6, 18), ChronoUnit.HOURS)
                    .plus(RandomGenerator.getDefault().nextInt(60), ChronoUnit.MINUTES);
                
                // Only place orders if the order time is not beyond yesterday end of day
                if (orderTime.isAfter(yesterdayEndOfDay)) {
                    continue;
                }
                
                clockService.setFixedTime(orderTime);
                
                // get the menu at this time
                List<MenuItemDTO> availableMenu = menuService.getMenu();
                // 50% chance to order each available item
                List<MenuItemDTO> itemsToOrder = availableMenu.stream().filter(item -> {
                    // If the item is Pizza Margherita, decrease chance to 20%
                    if (item.getName().equalsIgnoreCase("Pizza Margherita")) {
                        return RandomGenerator.getDefault().nextDouble() < 0.2;
                    }
                    return RandomGenerator.getDefault().nextDouble() < 0.5;
                }).toList();

                if (!itemsToOrder.isEmpty()) {
                    PlaceOrderRequest request = PlaceOrderRequest.builder()
                            .items(itemsToOrder.stream().map(item -> new OrderItemRequest(
                                item.getId(),
                                RandomGenerator.getDefault().nextInt(1, 3)
                            )).toList())
                            .build();
                    try {
                        orderService.placeOrder(user, request);
                    }
                    catch (Exception e) {}
                }
            }

            for (int d = 0; d < daysToProcess; d++) {
                Instant deliveryDay = week.plus(d, ChronoUnit.DAYS);
                
                // Skip if delivery day is beyond yesterday end of day
                if (deliveryDay.isAfter(yesterdayEndOfDay)) {
                    continue;
                }
                
                // Now every day, start getting the amount of orders for the day, and advance status through to delivered
                // Advance clock to delivery day random time between 8AM and 10AM
                Instant time = deliveryDay.plus(RandomGenerator.getDefault().nextInt(8, 10), ChronoUnit.HOURS).plus(RandomGenerator.getDefault().nextInt(0, 60), ChronoUnit.MINUTES);
                clockService.setFixedTime(time);
                var orders = orderService.getOrderStatsPeriod(deliveryDay, deliveryDay.plus(1, ChronoUnit.DAYS), null, null);
                orders.getStatistics().forEach(stat -> {
                   BulkOrderStatusUpdateRequest update = new BulkOrderStatusUpdateRequest(
                        stat.getDeliveryDate(),
                        OrderItemStatus.IN_PROGRESS,
                        OrderItemStatus.PAID,
                        stat.getItemName()
                   ); 
                    orderService.bulkUpdateOrderStatuses(update);
                });
                time = time.plus(RandomGenerator.getDefault().nextInt(20, 120), ChronoUnit.MINUTES);
                clockService.setFixedTime(time);
                orders.getStatistics().forEach(stat -> {
                    BulkOrderStatusUpdateRequest update = new BulkOrderStatusUpdateRequest(
                        stat.getDeliveryDate(),
                        OrderItemStatus.IN_DELIVERY,
                        OrderItemStatus.IN_PROGRESS,
                        stat.getItemName()
                   ); 
                    orderService.bulkUpdateOrderStatuses(update);
                });
                time = time.plus(RandomGenerator.getDefault().nextInt(20, 120), ChronoUnit.MINUTES);
                clockService.setFixedTime(time);
                orders.getStatistics().forEach(stat -> {
                    BulkOrderStatusUpdateRequest update = new BulkOrderStatusUpdateRequest(
                        stat.getDeliveryDate(),
                        OrderItemStatus.DELIVERED,
                        OrderItemStatus.IN_DELIVERY,
                        stat.getItemName()
                   ); 
                    orderService.bulkUpdateOrderStatuses(update);
                });
            }

            // Now for each user again, randomly leave feedback on some of the delivered items
            // Only generate feedback if we have completed days in this week (i.e., days that are <= yesterday end of day)
            if (daysToProcess > 0) {
                Instant feedbackTime = week.plus(Math.min(6, daysToProcess - 1), ChronoUnit.DAYS).plus(12, ChronoUnit.HOURS);
                // Only generate feedback if the feedback time is not beyond yesterday end of day
                if (!feedbackTime.isAfter(yesterdayEndOfDay)) {
                    clockService.setFixedTime(feedbackTime);
                    for (UserEntity user : users) {
                        var deliveredItems = orderService.getUserOrderItemsByStatus(user, List.of(OrderItemStatus.DELIVERED));
                        for (var item : deliveredItems) {
                            if (item.getFeedback() == null && RandomGenerator.getDefault().nextDouble() < 0.3) {
                                var feedbackEntry = CanidateFeedback.getRandomFeedback(item.getName());
                                var feedback = new FeedbackRequest(item.getId(), feedbackEntry.getRating(), feedbackEntry.getFeedback());
                                feedbackService.placeUserFeedback(user, feedback);
                            }
                        }
                    }
                }
            }
        }

    }


    private void seedResidences() {
        residenceRepository.saveAll(List.of(
                new ResidenceEntity("Hennops"),
                new ResidenceEntity("Dahlia"),
                new ResidenceEntity("Strelitzia"),
                new ResidenceEntity("Magalies")
        ));
    }

    private void seedCampus() {
        campusRepository.saveAll(List.of(
                new CampusEntity("Gerhard Straat"),
                new CampusEntity("Leriba Kampus"),
                new CampusEntity("Von Willich")
        ));
    }

    private void seedAllergies() {
        allergyRepository.saveAll(List.of(
                new AllergyEntity("Gluten"),
                new AllergyEntity("Suiwel"),
                new AllergyEntity("Neute"),
                new AllergyEntity("Vis")
        ));
    }

    private void seedProfile() {
        List<AllergyEntity> allergies = allergyRepository.findAll();
        List<CampusEntity> campuses = campusRepository.findAll();
        List<ResidenceEntity> residences = residenceRepository.findAll();
        List<UserEntity> users = userRepository.findAll();
        
        Random random = new Random();
        
        // Create profiles for all users
        int credentialCounter = 10000000;
        for (UserEntity user : users) {
            // Skip if profile already exists
            if (userProfileRepository.findByUser(user).isPresent() || user.getRoles().contains(Role.ADMIN)) {
                continue;
            }
            
            // Generate random allergies (0-3 allergies per user)
            List<Long> userAllergies = allergies.stream()
                    .filter(allergy -> random.nextDouble() < 0.3) // 30% chance for each allergy
                    .limit(3) // Max 3 allergies
                    .map(AllergyEntity::getId)
                    .toList();
            
            // Random campus and residence
            CampusEntity randomCampus = campuses.get(random.nextInt(campuses.size()));
            ResidenceEntity randomResidence = random.nextDouble() < 0.7 ? // 70% chance to have residence
                    residences.get(random.nextInt(residences.size())) : null;
            
            CreateUserProfileRequest.CreateUserProfileRequestBuilder profileBuilder = CreateUserProfileRequest.builder()
                    .credentialNumber(String.valueOf(credentialCounter++))
                    .allergyIds(userAllergies)
                    .campusId(randomCampus.getId());
            
            if (randomResidence != null) {
                profileBuilder.residenceId(randomResidence.getId());
            }
            
            userProfileService.createUserProfile(user, profileBuilder.build());
            
        }
    }

    private void seedUsers() {
        // Common Afrikaans first names
        String[] firstNames = {
            "Riaan", "Charl", "Willem", "Danie", "Gerrit", "Anton", "Stephan", "Christo", "Marius", "Deon",
            "Marelize", "Annelie", "Elmarie", "Sonja", "Zelda", "Rina", "Ilse", "Chantelle", "Marlene", "Lynette",
            "Anke", "Liezl", "Corlia", "Rochelle", "Jeanette", "Melanie", "Cecile", "Bianca", "Lizelle", "Tertia",
        };
        
        // Common Afrikaans surnames
        String[] lastNames = {
            "Van der Merwe", "Botha", "Pretorius", "Joubert", "Du Plessis", "Steyn", "Nel", "Fourie",
            "Coetzee", "Van Wyk", "Venter", "Olivier", "Kruger", "Smit", "Erasmus", "Barnard", "Viljoen", "Jacobs",
            "Potgieter", "De Villiers", "Burger", "Myburgh", "Rossouw", "Schoeman", "Human", "Engelbrecht", "Swanepoel", "Naude",
        };

        Random random = new Random();
        
        // Create the original demo users first
        authService.registerUser(RegisterRequest.builder()
                        .email("sarel@demo.com")
                        .firstName("Sarel")
                        .lastName("Seemonster")
                        .password("sarel123")
                .build());

        authService.registerUser(RegisterRequest.builder()
                .email("piet@demo.com")
                .firstName("Piet")
                .lastName("Pompies")
                .password("piet123")
                .build());

        // Create Admins for demo.
        AdminCreateUserRequest auditAdmin = new AdminCreateUserRequest();
        auditAdmin.setEmail("audit@demo.com");
        auditAdmin.setFirstName("Audit");
        auditAdmin.setLastName("Admin");
        auditAdmin.setPassword("audit123");
        auditAdmin.setRoles(Set.of("ADMIN", "FINANCIAL_ADMIN", "USER_ADMIN", "AUDIT_ADMIN", "SYSTEM_ADMIN"));
        auditAdmin.setEnabled(true);
        auditAdmin.setAccountNonExpired(true);
        auditAdmin.setAccountNonLocked(true);
        auditAdmin.setCredentialsNonExpired(true);
        userService.createUser(auditAdmin);

        AdminCreateUserRequest financeAdmin = new AdminCreateUserRequest();
        financeAdmin.setEmail("finance@demo.com");
        financeAdmin.setFirstName("Finance");
        financeAdmin.setLastName("Admin");
        financeAdmin.setPassword("finance123");
        financeAdmin.setRoles(Set.of("ADMIN", "FINANCIAL_ADMIN", "USER_ADMIN"));
        financeAdmin.setEnabled(true);
        financeAdmin.setAccountNonExpired(true);
        financeAdmin.setAccountNonLocked(true);
        financeAdmin.setCredentialsNonExpired(true);
        userService.createUser(financeAdmin);

        AdminCreateUserRequest menuAdmin = new AdminCreateUserRequest();
        menuAdmin.setEmail("spyskaart@demo.com");
        menuAdmin.setFirstName("Spyskaart");
        menuAdmin.setLastName("Admin");
        menuAdmin.setPassword("spyskaart123");
        menuAdmin.setRoles(Set.of("ADMIN", "MENU_ADMIN", "FEEDBACK_ADMIN", "ORDER_ADMIN"));
        menuAdmin.setEnabled(true);
        menuAdmin.setAccountNonExpired(true);
        menuAdmin.setAccountNonLocked(true);
        menuAdmin.setCredentialsNonExpired(true);
        userService.createUser(menuAdmin);
        
        // Generate 98 more users for a total of 100 users
        for (int i = 1; i <= 98; i++) {
            String firstName = firstNames[random.nextInt(firstNames.length)];
            String lastName = lastNames[random.nextInt(lastNames.length)];
            String email = firstName.toLowerCase() + "." + lastName.toLowerCase().replace(" ", "") + i + "@demo.com";
            String password = firstName.toLowerCase() + "123";
            
            authService.registerUser(RegisterRequest.builder()
                    .email(email)
                    .firstName(firstName)
                    .lastName(lastName)
                    .password(password)
                    .build());
        }
    }

}
