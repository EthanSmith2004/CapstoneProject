# UserProfileDTO


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**id** | **number** |  | [optional] [default to undefined]
**credentialNumber** | **string** |  | [optional] [default to undefined]
**user** | [**CompactUserDTO**](CompactUserDTO.md) |  | [optional] [default to undefined]
**campus** | [**SelectDTO**](SelectDTO.md) |  | [optional] [default to undefined]
**residence** | [**SelectDTO**](SelectDTO.md) |  | [optional] [default to undefined]
**allergies** | [**Array&lt;SelectDTO&gt;**](SelectDTO.md) |  | [optional] [default to undefined]
**createdAt** | **string** |  | [optional] [default to undefined]
**updatedAt** | **string** |  | [optional] [default to undefined]
**balance** | **number** |  | [optional] [default to undefined]

## Example

```typescript
import { UserProfileDTO } from 'api-client';

const instance: UserProfileDTO = {
    id,
    credentialNumber,
    user,
    campus,
    residence,
    allergies,
    createdAt,
    updatedAt,
    balance,
};
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
