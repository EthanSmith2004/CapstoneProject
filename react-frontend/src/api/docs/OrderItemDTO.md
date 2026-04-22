# OrderItemDTO


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**id** | **number** |  | [optional] [default to undefined]
**name** | **string** |  | [optional] [default to undefined]
**description** | **string** |  | [optional] [default to undefined]
**price** | **number** |  | [optional] [default to undefined]
**quantity** | **number** |  | [optional] [default to undefined]
**deliveryDate** | **string** |  | [optional] [default to undefined]
**totalPrice** | **number** |  | [optional] [default to undefined]
**allergies** | **Array&lt;string&gt;** |  | [optional] [default to undefined]
**menuItem** | [**MenuItemDTO**](MenuItemDTO.md) |  | [optional] [default to undefined]
**feedback** | [**FeedbackDTO**](FeedbackDTO.md) |  | [optional] [default to undefined]
**status** | **string** |  | [optional] [default to undefined]
**editBy** | **string** |  | [optional] [default to undefined]
**dateTimeDelivered** | **string** |  | [optional] [default to undefined]

## Example

```typescript
import { OrderItemDTO } from 'api-client';

const instance: OrderItemDTO = {
    id,
    name,
    description,
    price,
    quantity,
    deliveryDate,
    totalPrice,
    allergies,
    menuItem,
    feedback,
    status,
    editBy,
    dateTimeDelivered,
};
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
