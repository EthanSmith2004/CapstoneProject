package com.jel.spys.service;

import com.jel.spys.entity.*;
import com.jel.spys.model.*;
import com.jel.spys.repository.*;

import jakarta.persistence.Tuple;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Autowired
    private FinanceService financeService;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private ClockService clockService;

    @Transactional(readOnly = true)
    public List<OrderDTO> getUserOrders(UserEntity user) {
        List<OrderEntity> orders = orderRepository.findByUserOrderByCreatedAtDesc(user);
        return orders.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<OrderItemDTO> getUserOrderItemsByStatus(UserEntity user, OrderItemStatus status) {
        List<OrderItemEntity> orderItems = orderItemRepository.getUserOrdersByStatus(user, status);
        return orderItems.stream()
                .map(this::convertOrderItemToDTO)
                .collect(Collectors.toList());
    }

    public List<OrderItemDTO> getUserOrderItemsByStatus(UserEntity user, List<OrderItemStatus> status) {
        List<OrderItemEntity> orderItems = orderItemRepository.getUserOrdersByStatusIn(user, status);
        return orderItems.stream()
                .map(this::convertOrderItemToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void cancelOrderItem(UserEntity user, Long orderItemId) {
        OrderItemEntity orderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new IllegalArgumentException("Order item not found: " + orderItemId));
        if (!orderItem.getOrder().getUser().getId().equals(user.getId())) {
            throw new SecurityException("User not authorized to cancel this order item.");
        } 
        if (orderItem.getStatus() != OrderItemStatus.PAID) {
            throw new IllegalStateException("Only order items can be cancelled.");
        }
        if (clockService.now().isAfter(orderItem.getEditBy())) {
            throw new IllegalStateException("Order item cannot be cancelled after the edit deadline.");
        }
        orderItem.setStatus(OrderItemStatus.REFUNDED);
        // Refund the user
        financeService.creditUserAccount(
            user.getId(),
            orderItem.getPrice().multiply(BigDecimal.valueOf(orderItem.getQuantity())), 
            "Order #" + orderItem.getOrder().getId() + " item " + orderItem.getName() + " refund");

        orderItemRepository.save(orderItem);
    }

    /**
     * Process refund for an order item (used by admin operations)
     * Creates credit transaction and links it to the order item
     * 
     * @param orderItem The order item to refund
     * @return The created refund transaction
     */
    private TransactionEntity processRefund(OrderItemEntity orderItem) {
        BigDecimal refundAmount = orderItem.getPrice()
                .multiply(BigDecimal.valueOf(orderItem.getQuantity()));
        
        UserEntity user = orderItem.getOrder().getUser();
        
        // Create refund transaction
        TransactionEntity refundTransaction = financeService.creditUserAccount(
                user.getId(),
                refundAmount,
                "Refund: Order #" + orderItem.getOrder().getId() + " - " + orderItem.getName()
        );
        
        // Link transaction to order item
        orderItem.setRefundTransaction(refundTransaction);
        
        log.info("Processed refund for order item {}: R{} to user {}", 
                orderItem.getId(), refundAmount, user.getId());
        
        return refundTransaction;
    }


    @Transactional
    public OrderDTO placeOrder(UserEntity user, @Valid PlaceOrderRequest request) {
        // Calculate total and validate menu items
        BigDecimal total = BigDecimal.ZERO;
        for (OrderItemRequest itemRequest : request.getItems()) {
            MenuItemEntity menuItem = menuItemRepository.findById(itemRequest.getMenuItemId())
                    .orElseThrow(
                            () -> new IllegalArgumentException("Menu item not found: " + itemRequest.getMenuItemId()));

            if(!menuItem.isValidOrderItem(clockService)) {
               throw new IllegalArgumentException("User tried to place an invalid order");
            }

            BigDecimal itemTotal = menuItem.getPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity()));
            total = total.add(itemTotal);
        }

        // Check if the user has enough balance
        int comparison = financeService.getUserAccount(user).getCurrentBalance().compareTo(total);
        if (comparison < 0) {
            throw new IllegalArgumentException("Insufficient balance to place the order.");
        }

        // Create order
        OrderEntity order = new OrderEntity();
        order.setUser(user);
        order.setTotal(total);
        order.setStatus(OrderStatus.PENDING);
        order = orderRepository.save(order);

        StringBuilder orderItemList = new StringBuilder();
        orderItemList.append("O#");
        orderItemList.append(order.getId());
        orderItemList.append(":\n");

        // Create order items
        for (OrderItemRequest itemRequest : request.getItems()) {
            MenuItemEntity menuItem = menuItemRepository.findById(itemRequest.getMenuItemId()).get();
            BigDecimal itemTotal = menuItem.getPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity()));

            OrderItemEntity orderItem = new OrderItemEntity();
            orderItem.setOrder(order);
            orderItem.setName(menuItem.getName());
            orderItem.setDescription(menuItem.getDescription());
            orderItem.setPrice(menuItem.getPrice());
            orderItem.setDeliveryDate(menuItem.getDeliveryDate());
            orderItem.setEditBy(menuItem.getEditBy());
            orderItem.setQuantity(itemRequest.getQuantity());
            orderItem.setAllergies(new ArrayList<>(menuItem.getAllergies()));
            orderItem.setStatus(OrderItemStatus.PAID);
            orderItemRepository.save(orderItem);
            orderItemList.append("\t- #");
            orderItemList.append(orderItem.getId());
            orderItemList.append(" ");
            orderItemList.append(orderItem.getQuantity());
            orderItemList.append(" ");
            orderItemList.append(orderItem.getName());
            orderItemList.append(" @ ");
            orderItemList.append("R");
            orderItemList.append(orderItem.getPrice().toString());
            orderItemList.append(" (");
            orderItemList.append(itemTotal);
            orderItemList.append(")");
            orderItemList.append("\n");
        }

        order.setTransaction(financeService.debitUserAccount(user.getId(), total, orderItemList.toString()));
        orderRepository.save(order);
        NotificationRequest notification = new NotificationRequest();
        notification.setBody("Bestelling #" + order.getId() + " geplaas!");
        notification.setTitle("Bestelling Geplaas");
        notification.setType(NotificationType.ORDER_CONFIRMATION);
        notificationService.sendNotificationToUser(user.getId(), notification);

        log.info("Order placed successfully: {} for user: {}", order.getId(), user.getEmail());
        return convertToDTO(order);
    }

    /**
     * Update order status (admin function)
     */
    public OrderDTO updateOrderStatus(Long orderId, OrderStatus newStatus) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));

        OrderStatus previousStatus = order.getStatus();
        order.setStatus(newStatus);
        order = orderRepository.save(order);

        // Send notification to user about order status change
        sendOrderStatusNotification(order, previousStatus, newStatus);

        log.info("Order status updated: {} from {} to {}", orderId, previousStatus, newStatus);
        return convertToDTO(order);
    }

    /**
     * Get order by ID
     */
    @Transactional(readOnly = true)
    public OrderDTO getOrderById(Long orderId) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));
        return convertToDTO(order);
    }

    /**
     * Get all orders (admin function)
     */
    @Transactional(readOnly = true)
    public List<OrderDTO> getAllOrders() {
        List<OrderEntity> orders = orderRepository.findAllByOrderByCreatedAtDesc();
        return orders.stream()
                .map(this::convertToDTO)
                .sorted(Comparator.comparing(OrderDTO::getCreatedAt))
                .collect(Collectors.toList());
    }

    /**
     * Convert OrderEntity to OrderDTO
     */
    private OrderDTO convertToDTO(OrderEntity order) {
        return OrderDTO.builder()
                .id(order.getId())
                .user(CompactUserDTO.builder()
                        .id(order.getUser().getId())
                        .firstName(order.getUser().getFirstName())
                        .lastName(order.getUser().getLastName())
                        .email(order.getUser().getEmail())
                        .isAdmin(order.getUser().getRoles() != null &&
                                order.getUser().getRoles().contains(Role.ADMIN))
                        .build())
                .items(order.getOrderItems() != null ? order.getOrderItems().stream()
                        .map(this::convertOrderItemToDTO)
                        .sorted(Comparator.comparing(OrderItemDTO::getDeliveryDate))
                        .collect(Collectors.toList()) : List.of())
                .totalAmount(order.getTotal())
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .canEdit(order.getStatus() == OrderStatus.PENDING)
                .canCancel(order.getStatus() == OrderStatus.PENDING)
                .build();
    }

    /**
     * Convert OrderItemEntity to OrderItemDTO
     */
    private OrderItemDTO convertOrderItemToDTO(OrderItemEntity orderItem) {
        var builder = OrderItemDTO.builder()
                        .id(orderItem.getId())
                        .name(orderItem.getName())
                        .description(orderItem.getDescription())
                        .price(orderItem.getPrice())
                        .quantity(orderItem.getQuantity())
                        .deliveryDate(orderItem.getDeliveryDate())
                        .totalPrice(orderItem.getPrice().multiply(BigDecimal.valueOf(orderItem.getQuantity())))
                        .status(orderItem.getStatus())
                        .editBy(orderItem.getEditBy());
        if (orderItem.getFeedback() != null) {
            builder.feedback(new FeedbackDTO(orderItem.getFeedback()));
        }

        return builder.build();
    }

    public AdminOrderStatistics getOrderStats() {
        // Today (00:00 to 23:59)
        Instant now = clockService.now();
        Instant startOfDay = now.atZone(java.time.ZoneId.systemDefault()).toLocalDate().atStartOfDay(java.time.ZoneId.systemDefault()).toInstant();
        Instant endOfDay = startOfDay.plusSeconds(86399); // 23:59:59
        // This week (Monday 00:00 to Sunday 23:59)
        Instant startOfWeek = now.atZone(java.time.ZoneId.systemDefault()).toLocalDate().with(java.time.DayOfWeek.MONDAY).atStartOfDay(java.time.ZoneId.systemDefault()).toInstant();
        Instant endOfWeek = startOfWeek.plusSeconds(604799); // Sunday 23:59:59
        // Next week (Monday 00:00 to Sunday 23:59)
        Instant startOfNextWeek = startOfWeek.plusSeconds(604800);
        Instant endOfNextWeek = endOfWeek.plusSeconds(604800);

        AdminOrderStatistics stats = new AdminOrderStatistics();
        stats.setStatistics(
            Map.of(
                "today",
                getOrderStatsPeriod(startOfDay, endOfDay, null, null),
                "week",
                getOrderStatsPeriod(startOfWeek, endOfWeek, null, null),
                "next_week",
                getOrderStatsPeriod(startOfNextWeek, endOfNextWeek, null, null)
            )
        );

        return stats;
    }

    public AdminOrderPeriodStatistics getOrderStatsPeriod(Instant start, Instant end, Long campusId, Long residenceId) {
        List<Tuple> rawStats = orderItemRepository.getOrderStats(start, end, campusId, residenceId);
        AdminOrderPeriodStatistics periodStats = new AdminOrderPeriodStatistics();
        periodStats.setPeriodStart(start);
        periodStats.setPeriodEnd(end);
        periodStats.setStatistics(
            rawStats.stream().map(t -> new AdminOrderStatisticLine(
                t.get(0, String.class),
                t.get(1, OrderItemStatus.class).toString(),
                t.get(2, Instant.class),
                t.get(3, Long.class).intValue(),
                t.get(4, BigDecimal.class)
            )).collect(Collectors.toList())
        );
        
        return periodStats;
    }

    public void bulkUpdateOrderStatuses(BulkOrderStatusUpdateRequest request) {
        // Get affected order items before update to send notifications
        List<OrderItemEntity> affectedItems = getOrderItemsForBulkUpdate(request);
        
        // If updating to REFUNDED status, process refunds individually
        if (request.getStatus() == OrderItemStatus.REFUNDED) {
            log.info("Processing bulk refunds for {} order items", affectedItems.size());
            
            int successCount = 0;
            int failureCount = 0;
            
            for (OrderItemEntity orderItem : affectedItems) {
                try {
                    // Prevent double refunds
                    if (orderItem.getStatus() == OrderItemStatus.REFUNDED) {
                        log.warn("Skipping already refunded order item: {}", orderItem.getId());
                        continue;
                    }
                    
                    // Process refund and update status
                    processRefund(orderItem);
                    orderItem.setStatus(OrderItemStatus.REFUNDED);
                    orderItemRepository.save(orderItem);
                    successCount++;
                } catch (Exception e) {
                    log.error("Failed to process refund for order item {}: {}", orderItem.getId(), e.getMessage());
                    failureCount++;
                }
            }
            
            log.info("Bulk refund complete: {} successful, {} failed", successCount, failureCount);
        } else {
            // For non-refund updates, use fast bulk update
            orderItemRepository.bulkUpdateOrderStatuses(
                request.getDeliveryDate(), 
                request.getItemName(), 
                request.getStatus(), 
                request.getPreviousStatus()
            );
        }
        
        // Send notifications to users about status changes
        sendBulkOrderItemStatusNotifications(affectedItems, request.getPreviousStatus(), request.getStatus());
    }

    public List<OrderItemDTO> getAllOrderItems() {
        List<OrderItemEntity> orderItems = orderItemRepository.findAll();
        return orderItems.stream().map(this::convertOrderItemToDTO).sorted(Comparator.comparing(OrderItemDTO::getDeliveryDate)).toList();
    }

    /**
     * Update individual order item status (admin function)
     */
    @Transactional
    public OrderItemDTO updateOrderItemStatus(Long orderItemId, OrderItemStatus newStatus) {
        OrderItemEntity orderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new IllegalArgumentException("Order item not found: " + orderItemId));

        OrderItemStatus previousStatus = orderItem.getStatus();
        
        // Prevent double refunds
        if (newStatus == OrderItemStatus.REFUNDED && previousStatus == OrderItemStatus.REFUNDED) {
            throw new IllegalStateException("Order item is already refunded: " + orderItemId);
        }
        
        // Process refund if changing to REFUNDED status
        if (newStatus == OrderItemStatus.REFUNDED && previousStatus != OrderItemStatus.REFUNDED) {
            processRefund(orderItem);
        }
        
        orderItem.setStatus(newStatus);
        orderItem = orderItemRepository.save(orderItem);

        // Send notification to user about order item status change
        sendOrderItemStatusNotification(orderItem, previousStatus, newStatus);

        log.info("Order item status updated: {} from {} to {}", orderItemId, previousStatus, newStatus);
        return convertOrderItemToDTO(orderItem);
    }

    /**
     * Get order items that would be affected by bulk update (for notification purposes)
     */
    private List<OrderItemEntity> getOrderItemsForBulkUpdate(BulkOrderStatusUpdateRequest request) {
        if (request.getDeliveryDate() != null && request.getItemName() != null && request.getPreviousStatus() != null) {
            return orderItemRepository.findByDeliveryDateAndNameAndStatus(
                request.getDeliveryDate(), request.getItemName(), request.getPreviousStatus());
        } else if (request.getDeliveryDate() != null && request.getPreviousStatus() != null) {
            return orderItemRepository.findByDeliveryDateAndStatus(request.getDeliveryDate(), request.getPreviousStatus());
        } else if (request.getItemName() != null && request.getPreviousStatus() != null) {
            return orderItemRepository.findByNameAndStatus(request.getItemName(), request.getPreviousStatus());
        } else if (request.getPreviousStatus() != null) {
            return orderItemRepository.findByStatus(request.getPreviousStatus());
        }
        return List.of();
    }

    /**
     * Send notification for order status change
     */
    private void sendOrderStatusNotification(OrderEntity order, OrderStatus previousStatus, OrderStatus newStatus) {
        NotificationRequest notification = new NotificationRequest();
        
        switch (newStatus) {
            case PENDING:
                notification.setTitle("Bestelling Status Opdatering");
                notification.setBody("Bestelling #" + order.getId() + " is nou hangende.");
                notification.setType(NotificationType.ORDER_CONFIRMATION);
                break;
            case HISTORIC:
                notification.setTitle("Bestelling Voltooi");
                notification.setBody("Bestelling #" + order.getId() + " is voltooi en is nou histories.");
                notification.setType(NotificationType.ORDER_READY);
                break;
            default:
                return; // No notification for unknown status
        }
        
        notificationService.sendNotificationToUser(order.getUser().getId(), notification);
        log.info("Sent order status notification to user {} for order {}: {} -> {}", 
                order.getUser().getId(), order.getId(), previousStatus, newStatus);
    }

    /**
     * Send notification for order item status change
     */
    private void sendOrderItemStatusNotification(OrderItemEntity orderItem, OrderItemStatus previousStatus, OrderItemStatus newStatus) {
        NotificationRequest notification = new NotificationRequest();
        
        switch (newStatus) {
            case IN_PROGRESS:
                notification.setTitle("Jou Bestelling word Voorberei");
                notification.setBody(orderItem.getName() + " word nou voorberei vir aflewering op " + 
                                   orderItem.getDeliveryDate().toString().substring(0, 10));
                notification.setType(NotificationType.ORDER_CONFIRMATION);
                break;
            case IN_DELIVERY:
                notification.setTitle("Jou Bestelling is Op Pad");
                notification.setBody(orderItem.getName() + " is nou op pad vir aflewering!");
                notification.setType(NotificationType.ORDER_READY);
                break;
            case DELIVERED:
                notification.setTitle("Bestelling Afgelewer");
                notification.setBody(orderItem.getName() + " is suksesvol afgelewer. Geniet jou ete!");
                notification.setType(NotificationType.ORDER_READY);
                break;
            case CANCELLED:
                notification.setTitle("Bestelling Gekanselleer");
                notification.setBody(orderItem.getName() + " is gekanselleer" + 
                                   (previousStatus == OrderItemStatus.IN_PROGRESS ? " terwyl dit voorberei is" : "") + 
                                   ". Jy sal 'n terugbetaling ontvang.");
                notification.setType(NotificationType.ORDER_CANCELLED);
                break;
            case REFUNDED:
                notification.setTitle("Terugbetaling Verwerk");
                notification.setBody("Terugbetaling vir " + orderItem.getName() + " is verwerk na jou rekening.");
                notification.setType(NotificationType.ACCOUNT_CREDITED);
                break;
            case PAID:
                // Don't send notification for PAID status as it's handled during order placement
                return;
            default:
                return; // No notification for unknown status
        }
        
        notificationService.sendNotificationToUser(orderItem.getOrder().getUser().getId(), notification);
        log.info("Sent order item status notification to user {} for order item {}: {} -> {}", 
                orderItem.getOrder().getUser().getId(), orderItem.getId(), previousStatus, newStatus);
    }

    /**
     * Send notifications for bulk order item status updates
     */
    private void sendBulkOrderItemStatusNotifications(List<OrderItemEntity> orderItems, OrderItemStatus previousStatus, OrderItemStatus newStatus) {
        // Group order items by user to send consolidated notifications
        Map<Long, List<OrderItemEntity>> itemsByUser = orderItems.stream()
                .collect(Collectors.groupingBy(item -> item.getOrder().getUser().getId()));

        for (Map.Entry<Long, List<OrderItemEntity>> entry : itemsByUser.entrySet()) {
            Long userId = entry.getKey();
            List<OrderItemEntity> userItems = entry.getValue();
            
            if (userItems.isEmpty()) continue;

            NotificationRequest notification = new NotificationRequest();
            String itemNames = userItems.size() == 1 
                ? userItems.get(0).getName()
                : userItems.size() + " items";

            switch (newStatus) {
                case IN_PROGRESS:
                    notification.setTitle("Jou Bestelling word Voorberei");
                    notification.setBody(itemNames + " word nou voorberei.");
                    notification.setType(NotificationType.ORDER_CONFIRMATION);
                    break;
                case IN_DELIVERY:
                    notification.setTitle("Jou Bestelling is Op Pad");
                    notification.setBody(itemNames + " is nou op pad vir aflewering!");
                    notification.setType(NotificationType.ORDER_READY);
                    break;
                case DELIVERED:
                    notification.setTitle("Bestelling Afgelewer");
                    notification.setBody(itemNames + " is suksesvol afgelewer. Geniet jou ete!");
                    notification.setType(NotificationType.ORDER_READY);
                    break;
                case CANCELLED:
                    notification.setTitle("Bestelling Gekanselleer");
                    notification.setBody(itemNames + " is gekanselleer" + 
                                       (previousStatus == OrderItemStatus.IN_PROGRESS ? " terwyl dit voorberei is" : "") + 
                                       ". Jy sal 'n terugbetaling ontvang.");
                    notification.setType(NotificationType.ORDER_CANCELLED);
                    break;
                case REFUNDED:
                    notification.setTitle("Terugbetalings Verwerk");
                    notification.setBody("Terugbetalings vir " + itemNames + " is verwerk na jou rekening.");
                    notification.setType(NotificationType.ACCOUNT_CREDITED);
                    break;
                default:
                    continue; // Skip unknown statuses
            }
            
            notificationService.sendNotificationToUser(userId, notification);
            log.info("Sent bulk order item status notification to user {} for {} items: {} -> {}", 
                    userId, userItems.size(), previousStatus, newStatus);
        }
    }
}
