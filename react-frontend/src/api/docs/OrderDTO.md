# OrderDTO


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**id** | **number** |  | [optional] [default to undefined]
**user** | [**CompactUserDTO**](CompactUserDTO.md) |  | [optional] [default to undefined]
**status** | **string** |  | [optional] [default to undefined]
**totalAmount** | **number** |  | [optional] [default to undefined]
**items** | [**Array&lt;OrderItemDTO&gt;**](OrderItemDTO.md) |  | [optional] [default to undefined]
**transaction** | [**TransactionDTO**](TransactionDTO.md) |  | [optional] [default to undefined]
**createdAt** | **string** |  | [optional] [default to undefined]
**updatedAt** | **string** |  | [optional] [default to undefined]
**canEdit** | **boolean** |  | [optional] [default to undefined]
**canCancel** | **boolean** |  | [optional] [default to undefined]
**canPay** | **boolean** |  | [optional] [default to undefined]

## Example

```typescript
import { OrderDTO } from 'api-client';

const instance: OrderDTO = {
    id,
    user,
    status,
    totalAmount,
    items,
    transaction,
    createdAt,
    updatedAt,
    canEdit,
    canCancel,
    canPay,
};
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
