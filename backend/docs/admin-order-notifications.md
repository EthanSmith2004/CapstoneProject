# Enhanced Admin Order Notifications

## Overview
The admin order management system has been enhanced to automatically send notifications to users when order statuses change. This provides better communication and transparency in the meal ordering process.

## New Features

### 1. Individual Order Status Updates
- **Endpoint**: `PUT /api/admin/orders/{id}/status`
- **Enhancement**: Now sends appropriate notifications to users when order status changes
- **Notifications**:
  - `PENDING`: "Bestelling Status Opdatering" - Order is now pending
  - `HISTORIC`: "Bestelling Voltooi" - Order is complete and now historic

### 2. Individual Order Item Status Updates  
- **New Endpoint**: `PUT /api/admin/order-items/{id}/status`
- **Purpose**: Update individual order item status with detailed user notifications
- **Notifications**:
  - `IN_PROGRESS`: "Jou Bestelling word Voorberei" - Item is being prepared
  - `IN_DELIVERY`: "Jou Bestelling is Op Pad" - Item is out for delivery
  - `DELIVERED`: "Bestelling Afgelewer" - Item has been delivered successfully
  - `CANCELLED`: "Bestelling Gekanselleer" - Item cancelled (with context about when)
  - `REFUNDED`: "Terugbetaling Verwerk" - Refund has been processed

### 3. Enhanced Bulk Status Updates
- **Endpoint**: `PUT /api/admin/orders/bulk-status`
- **Enhancement**: Now sends consolidated notifications to affected users
- **Smart Grouping**: Groups multiple items per user into single notification
- **Context-Aware**: Different messages based on previous status (e.g., cancelling during preparation)

## Notification Types Used

| Order Item Status | Notification Type | Message Language |
|------------------|------------------|------------------|
| IN_PROGRESS | ORDER_CONFIRMATION | Afrikaans |
| IN_DELIVERY | ORDER_READY | Afrikaans |
| DELIVERED | ORDER_READY | Afrikaans |
| CANCELLED | ORDER_CANCELLED | Afrikaans |
| REFUNDED | ACCOUNT_CREDITED | Afrikaans |

## State Transition Notifications

### Critical State Transitions
1. **PAID → IN_PROGRESS**: User knows their order is being prepared
2. **IN_PROGRESS → CANCELLED**: Special message indicating cancellation during preparation
3. **IN_DELIVERY → DELIVERED**: Confirmation of successful delivery
4. **Any → CANCELLED**: Context-aware cancellation message
5. **CANCELLED → REFUNDED**: Financial confirmation of refund processing

### Bulk Operations
- Groups notifications by user to avoid spam
- Sends single notification for multiple items when possible
- Uses "X items" format when multiple items are affected

## Technical Implementation

### New Methods Added to OrderService:
1. `updateOrderItemStatus(Long, OrderItemStatus)` - Update individual item with notification
2. `sendOrderStatusNotification(OrderEntity, OrderStatus, OrderStatus)` - Order-level notifications
3. `sendOrderItemStatusNotification(OrderItemEntity, OrderItemStatus, OrderItemStatus)` - Item-level notifications
4. `sendBulkOrderItemStatusNotifications(List<OrderItemEntity>, OrderItemStatus, OrderItemStatus)` - Bulk notifications
5. `getOrderItemsForBulkUpdate(BulkOrderStatusUpdateRequest)` - Query items before bulk update

### New Repository Methods:
- `findByDeliveryDateAndNameAndStatus()` - Find items for specific date/name/status
- `findByDeliveryDateAndStatus()` - Find items for specific date/status
- `findByNameAndStatus()` - Find items for specific name/status
- `findByStatus()` - Find items for specific status

## Usage Examples

### Update Single Order Item
```http
PUT /api/admin/order-items/123/status
Content-Type: application/json
Authorization: Bearer {admin_token}

"IN_PROGRESS"
```

### Bulk Update with Notifications
```http
PUT /api/admin/orders/bulk-status
Content-Type: application/json
Authorization: Bearer {admin_token}

{
  "deliveryDate": "2024-12-15T00:00:00Z",
  "status": "IN_DELIVERY",
  "previousStatus": "IN_PROGRESS"
}
```

## Benefits

1. **Improved User Experience**: Users receive timely updates about their orders
2. **Transparency**: Clear communication about order progress and issues
3. **Automated Communication**: Reduces manual notification overhead for staff
4. **Context-Aware Messages**: Different messages based on transition context
5. **Bulk Efficiency**: Smart grouping prevents notification spam
6. **Multi-language Support**: Messages in Afrikaans for the target audience

## Testing

Use the provided `adminorders-notifications.http` file to test all notification scenarios:
- Individual status updates
- Bulk status updates  
- Various state transitions
- User notification checking

The test file includes scenarios for all major state transitions and demonstrates the notification system in action.