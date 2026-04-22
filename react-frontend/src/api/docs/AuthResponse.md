# AuthResponse


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**accessToken** | **string** |  | [optional] [default to undefined]
**refreshToken** | **string** |  | [optional] [default to undefined]
**tokenType** | **string** |  | [optional] [default to undefined]
**expiresIn** | **number** |  | [optional] [default to undefined]
**firstName** | **string** |  | [optional] [default to undefined]
**lastName** | **string** |  | [optional] [default to undefined]
**email** | **string** |  | [optional] [default to undefined]
**roles** | **Set&lt;string&gt;** |  | [optional] [default to undefined]

## Example

```typescript
import { AuthResponse } from 'api-client';

const instance: AuthResponse = {
    accessToken,
    refreshToken,
    tokenType,
    expiresIn,
    firstName,
    lastName,
    email,
    roles,
};
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
