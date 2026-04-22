# Delivery Admin - Barcode Scanning System

## Overview
The Delivery Admin system provides a barcode-based delivery confirmation workflow. Delivery drivers use barcode scanners to:
1. Scan a user's credential barcode to view their pending deliveries
2. Scan/confirm individual items as delivered
3. Track delivery progress throughout the day

## New Role
- **DELIVERY_ADMIN**: New role for delivery drivers with access to the delivery dashboard and barcode scanning endpoints

## Architecture

### Flow Diagram
```
1. Driver scans user barcode
   ↓
2. System shows all IN_DELIVERY items for that user
   ↓
3. Driver confirms delivery of each item
   ↓
4. System marks item as DELIVERED
   ↓
5. System sends notification to user
   ↓
6. Shows remaining items for that user
```

## API Endpoints

### 1. Scan User Barcode
**Endpoint**: `POST /api/delivery/scan-user`
**Role**: DELIVERY_ADMIN, ADMIN
**Purpose**: Retrieve all pending delivery items for a user

**Request**:
```json
{
  "barcode": "123456789"
}
```

**Response**:
```json
{
  "credentialNumber": "123456789",
  "firstName": "Johan",
  "lastName": "Smit",
  "email": "johan@example.com",
  "residence": "Elandia",
  "campus": "Stellenbosch",
  "pendingItems": [
    {
      "id": 42,
      "name": "Chicken Curry",
      "description": "Mild curry with rice",
      "price": 45.00,
      "quantity": 2,
      "deliveryDate": "2024-12-15T00:00:00Z",
      "totalPrice": 90.00,
      "status": "IN_DELIVERY"
    }
  ],
  "totalItems": 1
}
```

### 2. Confirm Item Delivery
**Endpoint**: `POST /api/delivery/scan-item`
**Role**: DELIVERY_ADMIN, ADMIN
**Purpose**: Mark an order item as delivered

**Request**:
```json
{
  "userBarcode": "123456789",
  "orderItemId": 42
}
```

**Response**:
```json
{
  "deliveredItem": {
    "id": 42,
    "name": "Chicken Curry",
    "status": "DELIVERED",
    "...": "..."
  },
  "message": "Item suksesvol afgelewer",
  "remainingItems": 0
}
```

### 3. Get Delivery Statistics
**Endpoint**: `GET /api/delivery/statistics`
**Role**: DELIVERY_ADMIN, ADMIN
**Purpose**: Dashboard statistics for today's deliveries

**Response**:
```json
{
  "totalItemsToday": 150,
  "itemsInDelivery": 45,
  "itemsDelivered": 105,
  "completionPercentage": 70.0
}
```

### 4. Get All Pending Deliveries
**Endpoint**: `GET /api/delivery/pending-deliveries`
**Role**: DELIVERY_ADMIN, ADMIN
**Purpose**: List all users with pending deliveries grouped by user

**Response**: Array of `UserDeliveryItemsResponse` objects

## Frontend Integration

### Mobile App - User Barcode Generation
The mobile app should generate a barcode/QR code on the user's order screen that encodes their credential number.

**Implementation Suggestions**:
```typescript
// Generate QR code with user's credential number
import QRCode from 'react-qr-code';

<QRCode 
  value={userProfile.credentialNumber} 
  size={256}
  level="H"
/>
```

### Delivery Dashboard - Barcode Scanner
The delivery admin dashboard should have:

**Layout**:
```
+----------------------------------+
|  Delivery Dashboard              |
|  Stats: 105/150 delivered (70%)  |
+----------------------------------+
|                                  |
|  [Scan User Barcode Input]       |
|  [  🔍 Scan                ]     |
|                                  |
+----------------------------------+
|  User: Johan Smit                |
|  Residence: Elandia              |
|                                  |
|  Items to Deliver:               |
|  ☐ Chicken Curry (x2)            |
|  ☐ Salad (x1)                    |
|                                  |
+----------------------------------+
```

**Scanner Input Field Features**:
- Auto-focus after each scan
- Auto-submit on barcode scan (no need for button press)
- Clear after successful scan
- Audio/visual feedback on success/error

**Example Implementation**:
```typescript
const DeliveryDashboard = () => {
  const [userBarcode, setUserBarcode] = useState('');
  const [currentUser, setCurrentUser] = useState(null);
  
  const handleBarcodeInput = async (barcode: string) => {
    try {
      const response = await deliveryApi.scanUser({ barcode });
      setCurrentUser(response.data);
      playSuccessSound();
    } catch (error) {
      showError(error.message);
      playErrorSound();
    }
  };
  
  const handleItemDelivery = async (itemId: number) => {
    try {
      const response = await deliveryApi.scanItem({
        userBarcode: currentUser.credentialNumber,
        orderItemId: itemId
      });
      
      if (response.data.remainingItems === 0) {
        setCurrentUser(null); // Clear screen
        showSuccess("All items delivered!");
      } else {
        // Refresh user items
        const updated = await deliveryApi.scanUser({ 
          barcode: currentUser.credentialNumber 
        });
        setCurrentUser(updated.data);
      }
    } catch (error) {
      showError(error.message);
    }
  };
  
  return (
    <div>
      <input
        type="text"
        value={userBarcode}
        onChange={(e) => setUserBarcode(e.target.value)}
        onKeyPress={(e) => {
          if (e.key === 'Enter') {
            handleBarcodeInput(userBarcode);
            setUserBarcode(''); // Clear for next scan
          }
        }}
        placeholder="Scan gebruiker barcode..."
        autoFocus
      />
      
      {currentUser && (
        <div>
          <h2>{currentUser.firstName} {currentUser.lastName}</h2>
          <p>{currentUser.residence} - {currentUser.campus}</p>
          
          <ul>
            {currentUser.pendingItems.map(item => (
              <li key={item.id}>
                <button onClick={() => handleItemDelivery(item.id)}>
                  ✓ {item.name} (x{item.quantity})
                </button>
              </li>
            ))}
          </ul>
        </div>
      )}
    </div>
  );
};
```

## Workflow Examples

### Scenario 1: Successful Delivery
1. Driver arrives at residence
2. Scans user's barcode → System displays: "Johan Smit - 2 items"
3. Confirms each item delivery with button/second scan
4. System marks as DELIVERED
5. User receives notification
6. Driver moves to next user

### Scenario 2: Multiple Items
1. Scan user → Shows 5 items
2. Deliver item 1 → Remaining: 4 items
3. Deliver item 2 → Remaining: 3 items
4. Continue until all items delivered
5. Screen auto-clears when all items done

### Scenario 3: Error Handling
1. Scan invalid barcode → Error: "Gebruiker nie gevind"
2. Scan user with no pending items → Shows: "Geen hangende aflewerings"
3. Try to deliver wrong item → Error: "Item behoort nie aan gebruiker nie"

## Security Considerations

1. **Role-Based Access**: Only DELIVERY_ADMIN and ADMIN can access endpoints
2. **User Verification**: System verifies item belongs to scanned user
3. **Status Validation**: Only items in IN_DELIVERY status can be marked as delivered
4. **Audit Trail**: All deliveries are logged with timestamps

## Database Impact

### New Queries
- `findPendingDeliveryItemsForUser`: Get user's IN_DELIVERY items for today
- `findByStatusAndDeliveryDateBetween`: Get all items by status and date range
- `countByStatusAndDeliveryDateBetween`: Count items by status and date range

### Indexes Recommended
- Index on `order_item(status, delivery_date)` for fast filtering
- Index on `user_profile(credential_number)` for fast barcode lookup

## Notifications

When an item is marked as delivered, the user receives:
- **Title**: "Bestelling Afgelewer"
- **Body**: "[Item Name] is suksesvol afgelewer. Geniet jou ete!"
- **Type**: ORDER_READY

## Testing

Use the provided `deliveryadmin.http` file to test:
1. Login as delivery admin
2. View statistics and pending deliveries
3. Scan user barcodes
4. Mark items as delivered
5. Test error scenarios

## Migration Notes

### Creating a Delivery Admin User
You'll need to create a delivery admin user in the database or via admin panel:

```sql
-- Add DELIVERY_ADMIN role to existing user
INSERT INTO user_roles (user_id, roles) 
VALUES (
  (SELECT id FROM "user" WHERE email = 'delivery@demo.com'),
  'DELIVERY_ADMIN'
);
```

Or update via admin user management interface to add the DELIVERY_ADMIN role.

## Future Enhancements

1. **Offline Support**: Cache pending deliveries for offline scanning
2. **Photo Confirmation**: Optional photo proof of delivery
3. **Signature Capture**: Digital signature from recipient
4. **Route Optimization**: Suggest optimal delivery order by location
5. **Batch Operations**: Mark multiple items as delivered at once
6. **Delivery Time Tracking**: Record exact delivery timestamp
7. **GPS Location**: Log delivery location for verification
