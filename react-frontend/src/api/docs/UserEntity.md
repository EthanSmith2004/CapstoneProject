# UserEntity


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**id** | **number** |  | [optional] [default to undefined]
**firstName** | **string** |  | [optional] [default to undefined]
**lastName** | **string** |  | [optional] [default to undefined]
**email** | **string** |  | [optional] [default to undefined]
**roles** | **Set&lt;string&gt;** |  | [optional] [default to undefined]
**accountNonExpired** | **boolean** |  | [optional] [default to undefined]
**accountNonLocked** | **boolean** |  | [optional] [default to undefined]
**credentialsNonExpired** | **boolean** |  | [optional] [default to undefined]
**enabled** | **boolean** |  | [optional] [default to undefined]
**createdAt** | **string** |  | [optional] [default to undefined]
**updatedAt** | **string** |  | [optional] [default to undefined]
**username** | **string** |  | [optional] [default to undefined]
**authorities** | [**Array&lt;GrantedAuthority&gt;**](GrantedAuthority.md) |  | [optional] [default to undefined]

## Example

```typescript
import { UserEntity } from 'api-client';

const instance: UserEntity = {
    id,
    firstName,
    lastName,
    email,
    roles,
    accountNonExpired,
    accountNonLocked,
    credentialsNonExpired,
    enabled,
    createdAt,
    updatedAt,
    username,
    authorities,
};
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
