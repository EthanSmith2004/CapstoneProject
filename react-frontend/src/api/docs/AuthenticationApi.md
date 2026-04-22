# AuthenticationApi

All URIs are relative to *http://localhost:8080*

|Method | HTTP request | Description|
|------------- | ------------- | -------------|
|[**authenticateUser**](#authenticateuser) | **POST** /api/auth/login | Authenticate user|
|[**logout**](#logout) | **POST** /api/auth/logout | Logout user|
|[**refreshToken**](#refreshtoken) | **POST** /api/auth/refresh | Refresh access token|
|[**registerUser**](#registeruser) | **POST** /api/auth/register | Register new user|

# **authenticateUser**
> AuthResponse authenticateUser(loginRequest)

Authenticate a user with username and password

### Example

```typescript
import {
    AuthenticationApi,
    Configuration,
    LoginRequest
} from 'api-client';

const configuration = new Configuration();
const apiInstance = new AuthenticationApi(configuration);

let loginRequest: LoginRequest; //

const { status, data } = await apiInstance.authenticateUser(
    loginRequest
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **loginRequest** | **LoginRequest**|  | |


### Return type

**AuthResponse**

### Authorization

[Bearer Authentication](../README.md#Bearer Authentication)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: */*


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | User authenticated successfully |  -  |
|**401** | Invalid credentials |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **logout**
> string logout(refreshTokenRequest)

Logout a user by invalidating their refresh token

### Example

```typescript
import {
    AuthenticationApi,
    Configuration,
    RefreshTokenRequest
} from 'api-client';

const configuration = new Configuration();
const apiInstance = new AuthenticationApi(configuration);

let refreshTokenRequest: RefreshTokenRequest; //

const { status, data } = await apiInstance.logout(
    refreshTokenRequest
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **refreshTokenRequest** | **RefreshTokenRequest**|  | |


### Return type

**string**

### Authorization

[Bearer Authentication](../README.md#Bearer Authentication)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: */*


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | User logged out successfully |  -  |
|**401** | Invalid refresh token |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **refreshToken**
> AuthResponse refreshToken(refreshTokenRequest)

Refresh an expired access token using a refresh token

### Example

```typescript
import {
    AuthenticationApi,
    Configuration,
    RefreshTokenRequest
} from 'api-client';

const configuration = new Configuration();
const apiInstance = new AuthenticationApi(configuration);

let refreshTokenRequest: RefreshTokenRequest; //

const { status, data } = await apiInstance.refreshToken(
    refreshTokenRequest
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **refreshTokenRequest** | **RefreshTokenRequest**|  | |


### Return type

**AuthResponse**

### Authorization

[Bearer Authentication](../README.md#Bearer Authentication)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: */*


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | Token refreshed successfully |  -  |
|**401** | Invalid or expired refresh token |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **registerUser**
> AuthResponse registerUser(registerRequest)

Register a new user account

### Example

```typescript
import {
    AuthenticationApi,
    Configuration,
    RegisterRequest
} from 'api-client';

const configuration = new Configuration();
const apiInstance = new AuthenticationApi(configuration);

let registerRequest: RegisterRequest; //

const { status, data } = await apiInstance.registerUser(
    registerRequest
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **registerRequest** | **RegisterRequest**|  | |


### Return type

**AuthResponse**

### Authorization

[Bearer Authentication](../README.md#Bearer Authentication)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: */*


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | User registered successfully |  -  |
|**400** | User already exists or invalid data |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

