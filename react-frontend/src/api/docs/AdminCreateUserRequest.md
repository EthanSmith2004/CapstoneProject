# AdminCreateUserRequest


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**firstName** | **string** |  | [default to undefined]
**lastName** | **string** |  | [default to undefined]
**email** | **string** |  | [default to undefined]
**password** | **string** |  | [default to undefined]
**roles** | **Set&lt;string&gt;** |  | [optional] [default to undefined]
**enabled** | **boolean** |  | [optional] [default to undefined]
**accountNonExpired** | **boolean** |  | [optional] [default to undefined]
**accountNonLocked** | **boolean** |  | [optional] [default to undefined]
**credentialsNonExpired** | **boolean** |  | [optional] [default to undefined]

## Example

```typescript
import { AdminCreateUserRequest } from 'api-client';

const instance: AdminCreateUserRequest = {
    firstName,
    lastName,
    email,
    password,
    roles,
    enabled,
    accountNonExpired,
    accountNonLocked,
    credentialsNonExpired,
};
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
