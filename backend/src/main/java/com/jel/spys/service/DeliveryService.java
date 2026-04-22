package com.jel.spys.service;

import com.jel.spys.entity.*;
import com.jel.spys.model.*;
import com.jel.spys.repository.*;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
public class DeliveryService {

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private NotificationService notificationService;
    @Autowired
    private ClockService clockService;

    /**
     * Get pending delivery items for a user by scanning their barcode
     */
    @Transactional(readOnly = true)
    public UserDeliveryItemsResponse getUserPendingDeliveryItems(String userBarcode) {
        // Find user by credential number (barcode)
        UserProfileEntity userProfile = userProfileRepository.findByCredentialNumber(userBarcode)
                .orElseThrow(() -> new IllegalArgumentException("Gebruiker nie gevind met barcode: " + userBarcode));

        UserEntity user = userProfile.getUser();

        List<OrderItemEntity> pendingItems = orderItemRepository.findPendingDeliveryItemsForUser(
                user, OrderItemStatus.IN_DELIVERY);

        List<OrderItemDTO> itemDTOs = pendingItems.stream()
                .map(this::convertOrderItemToDTO)
                .collect(Collectors.toList());

        log.info("Retrieved {} pending delivery items for user barcode: {}", pendingItems.size(), userBarcode);

        return UserDeliveryItemsResponse.builder()
                .credentialNumber(userProfile.getCredentialNumber())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .residence(userProfile.getResidence() != null ? userProfile.getResidence().getResidence() : null)
                .campus(userProfile.getCampus() != null ? userProfile.getCampus().getCampus() : null)
                .pendingItems(itemDTOs)
                .totalItems(itemDTOs.size())
                .build();
    }

    /**
     * Mark an item as delivered by scanning user barcode and item ID
     */
    @Transactional
    public DeliveryConfirmationResponse markItemAsDelivered(String userBarcode, Long orderItemId) {
        // Verify user exists
        UserProfileEntity userProfile = userProfileRepository.findByCredentialNumber(userBarcode)
                .orElseThrow(() -> new IllegalArgumentException("Gebruiker nie gevind met barcode: " + userBarcode));

        UserEntity user = userProfile.getUser();

        // Get the order item
        OrderItemEntity orderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new IllegalArgumentException("Bestelling item nie gevind: " + orderItemId));

        // Verify the item belongs to this user
        if (!orderItem.getOrder().getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Hierdie item behoort nie aan die gespesifiseerde gebruiker nie");
        }

        // Verify the item is in IN_DELIVERY status
        if (orderItem.getStatus() != OrderItemStatus.IN_DELIVERY) {
            throw new IllegalStateException("Item moet in IN_DELIVERY status wees om afgelewer te word. Huidige status: " + orderItem.getStatus());
        }

        // Mark as delivered
        orderItem.setStatus(OrderItemStatus.DELIVERED);
        orderItem.setDateTimeDelivered(clockService.now());
        orderItem = orderItemRepository.save(orderItem);

        // Send notification to user
        NotificationRequest notification = new NotificationRequest();
        notification.setTitle("Bestelling Afgelewer");
        notification.setBody(orderItem.getName() + " is suksesvol afgelewer. Geniet jou ete!");
        notification.setType(NotificationType.ORDER_READY);
        notificationService.sendNotificationToUser(user.getId(), notification);

        // Get remaining items count
        List<OrderItemEntity> remainingItems = orderItemRepository.findPendingDeliveryItemsForUser(
                user, OrderItemStatus.IN_DELIVERY);

        log.info("Marked order item {} as delivered for user barcode: {}. Remaining items: {}", 
                orderItemId, userBarcode, remainingItems.size());

        return DeliveryConfirmationResponse.builder()
                .deliveredItem(convertOrderItemToDTO(orderItem))
                .message("Item suksesvol afgelewer")
                .remainingItems(remainingItems.size())
                .build();
    }

    /**
     * Get delivery statistics for the dashboard
     */
    @Transactional(readOnly = true)
    public DeliveryStatisticsResponse getDeliveryStatistics() {
        // Count items by status for today
        Instant startOfDay = clockService.now().truncatedTo(java.time.temporal.ChronoUnit.DAYS);
        Instant endOfDay = startOfDay.plus(1, java.time.temporal.ChronoUnit.DAYS);

        Long inDeliveryCount = orderItemRepository.countByStatus(OrderItemStatus.IN_DELIVERY);
        Long deliveredCount = orderItemRepository.countByStatusAndDeliveredDateBetween(OrderItemStatus.DELIVERED, startOfDay, endOfDay);
        long totalCount = inDeliveryCount + deliveredCount;

        return DeliveryStatisticsResponse.builder()
                .totalItemsToday((int) totalCount)
                .itemsInDelivery(inDeliveryCount.intValue())
                .itemsDelivered(deliveredCount.intValue())
                .completionPercentage(totalCount > 0 ? (deliveredCount * 100.0 / totalCount) : 0.0)
                .build();
    }

    /**
     * Get all users with pending deliveries for today
     */
    @Transactional(readOnly = true)
    public List<UserDeliveryItemsResponse> getAllPendingDeliveries() {
        List<OrderItemEntity> allPendingItems = orderItemRepository.findByStatus(OrderItemStatus.IN_DELIVERY);

        // Group by user
        return allPendingItems.stream()
                .collect(Collectors.groupingBy(item -> item.getOrder().getUser()))
                .entrySet().stream()
                .map(entry -> {
                    UserEntity user = entry.getKey();
                    List<OrderItemEntity> items = entry.getValue();
                    UserProfileEntity profile = userProfileRepository.findByUser(user).orElse(null);

                    return UserDeliveryItemsResponse.builder()
                            .credentialNumber(profile != null ? profile.getCredentialNumber() : null)
                            .firstName(user.getFirstName())
                            .lastName(user.getLastName())
                            .email(user.getEmail())
                            .residence(profile != null && profile.getResidence() != null ? profile.getResidence().getResidence() : null)
                            .campus(profile != null && profile.getCampus() != null ? profile.getCampus().getCampus() : null)
                            .pendingItems(items.stream().map(this::convertOrderItemToDTO).collect(Collectors.toList()))
                            .totalItems(items.size())
                            .build();
                })
                .collect(Collectors.toList());
    }

    /**
     * Convert OrderItemEntity to OrderItemDTO
     */
    private OrderItemDTO convertOrderItemToDTO(OrderItemEntity orderItem) {
        return OrderItemDTO.builder()
                .id(orderItem.getId())
                .name(orderItem.getName())
                .description(orderItem.getDescription())
                .price(orderItem.getPrice())
                .quantity(orderItem.getQuantity())
                .deliveryDate(orderItem.getDeliveryDate())
                .totalPrice(orderItem.getPrice().multiply(BigDecimal.valueOf(orderItem.getQuantity())))
                .status(orderItem.getStatus())
                .editBy(orderItem.getEditBy())
                .dateTimeDelivered(orderItem.getDateTimeDelivered())
                .build();
    }
}
