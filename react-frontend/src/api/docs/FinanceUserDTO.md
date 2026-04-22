# FinanceUserDTO


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**id** | **number** |  | [optional] [default to undefined]
**email** | **string** |  | [optional] [default to undefined]
**firstName** | **string** |  | [optional] [default to undefined]
**lastName** | **string** |  | [optional] [default to undefined]
**credentialNumber** | **string** |  | [optional] [default to undefined]
**balance** | **number** |  | [optional] [default to undefined]
**movementDay** | **number** |  | [optional] [default to undefined]
**movementWeek** | **number** |  | [optional] [default to undefined]
**movementMonth** | **number** |  | [optional] [default to undefined]
**movementYTD** | **number** |  | [optional] [default to undefined]
**transactions** | [**Array&lt;TransactionDTO&gt;**](TransactionDTO.md) |  | [optional] [default to undefined]

## Example

```typescript
import { FinanceUserDTO } from 'api-client';

const instance: FinanceUserDTO = {
    id,
    email,
    firstName,
    lastName,
    credentialNumber,
    balance,
    movementDay,
    movementWeek,
    movementMonth,
    movementYTD,
    transactions,
};
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
