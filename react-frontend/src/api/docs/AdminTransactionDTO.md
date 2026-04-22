# AdminTransactionDTO


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**id** | **number** |  | [optional] [default to undefined]
**debit** | **number** |  | [optional] [default to undefined]
**credit** | **number** |  | [optional] [default to undefined]
**runningBalance** | **number** |  | [optional] [default to undefined]
**transactionDate** | **string** |  | [optional] [default to undefined]
**description** | **string** |  | [optional] [default to undefined]
**user** | [**UserWithProfileDTO**](UserWithProfileDTO.md) |  | [optional] [default to undefined]

## Example

```typescript
import { AdminTransactionDTO } from 'api-client';

const instance: AdminTransactionDTO = {
    id,
    debit,
    credit,
    runningBalance,
    transactionDate,
    description,
    user,
};
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
