# UserDeliveryItemsResponse

Response containing user details and pending delivery items

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**credentialNumber** | **string** | User\&#39;s credential number/barcode | [optional] [default to undefined]
**firstName** | **string** | User\&#39;s first name | [optional] [default to undefined]
**lastName** | **string** | User\&#39;s last name | [optional] [default to undefined]
**email** | **string** | User\&#39;s email | [optional] [default to undefined]
**residence** | **string** | User\&#39;s residence | [optional] [default to undefined]
**campus** | **string** | User\&#39;s campus | [optional] [default to undefined]
**pendingItems** | [**Array&lt;OrderItemDTO&gt;**](OrderItemDTO.md) | List of order items pending delivery for this user | [optional] [default to undefined]
**totalItems** | **number** | Total number of items pending delivery | [optional] [default to undefined]

## Example

```typescript
import { UserDeliveryItemsResponse } from 'api-client';

const instance: UserDeliveryItemsResponse = {
    credentialNumber,
    firstName,
    lastName,
    email,
    residence,
    campus,
    pendingItems,
    totalItems,
};
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
