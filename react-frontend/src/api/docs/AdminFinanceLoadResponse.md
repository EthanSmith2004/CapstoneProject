# AdminFinanceLoadResponse


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**auditId** | **number** |  | [optional] [default to undefined]
**totalRequests** | **number** |  | [optional] [default to undefined]
**successfulLoads** | **number** |  | [optional] [default to undefined]
**failedLoads** | **number** |  | [optional] [default to undefined]
**totalAmountLoaded** | **number** |  | [optional] [default to undefined]
**results** | [**Array&lt;AdminLoadCreditResult&gt;**](AdminLoadCreditResult.md) |  | [optional] [default to undefined]
**processedAt** | **string** |  | [optional] [default to undefined]
**processedBy** | [**CompactUserDTO**](CompactUserDTO.md) |  | [optional] [default to undefined]

## Example

```typescript
import { AdminFinanceLoadResponse } from 'api-client';

const instance: AdminFinanceLoadResponse = {
    auditId,
    totalRequests,
    successfulLoads,
    failedLoads,
    totalAmountLoaded,
    results,
    processedAt,
    processedBy,
};
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
