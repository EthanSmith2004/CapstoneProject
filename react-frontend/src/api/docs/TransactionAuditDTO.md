# TransactionAuditDTO


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**id** | **number** |  | [optional] [default to undefined]
**transactions** | [**Array&lt;TransactionDTO&gt;**](TransactionDTO.md) |  | [optional] [default to undefined]
**transactionCount** | **number** |  | [optional] [default to undefined]
**compactUser** | [**CompactUserDTO**](CompactUserDTO.md) |  | [optional] [default to undefined]
**loadedContent** | **string** |  | [optional] [default to undefined]
**transactionAuditType** | **string** |  | [optional] [default to undefined]

## Example

```typescript
import { TransactionAuditDTO } from 'api-client';

const instance: TransactionAuditDTO = {
    id,
    transactions,
    transactionCount,
    compactUser,
    loadedContent,
    transactionAuditType,
};
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
