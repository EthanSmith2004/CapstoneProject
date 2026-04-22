# DeliveryConfirmationResponse

Response after marking an item as delivered

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**deliveredItem** | [**OrderItemDTO**](OrderItemDTO.md) | The delivered order item | [optional] [default to undefined]
**message** | **string** | Success message | [optional] [default to undefined]
**remainingItems** | **number** | Number of remaining items for this user | [optional] [default to undefined]

## Example

```typescript
import { DeliveryConfirmationResponse } from 'api-client';

const instance: DeliveryConfirmationResponse = {
    deliveredItem,
    message,
    remainingItems,
};
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
