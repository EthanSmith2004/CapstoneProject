# UserProfileApi

All URIs are relative to *http://localhost:8080*

|Method | HTTP request | Description|
|------------- | ------------- | -------------|
|[**createUserProfile**](#createuserprofile) | **POST** /api/user/profile | Create user profile|
|[**getUserProfile**](#getuserprofile) | **GET** /api/user/profile | Get user profile|
|[**updateUserProfile**](#updateuserprofile) | **PUT** /api/user/profile | Update user profile|

# **createUserProfile**
> UserProfileDTO createUserProfile(createUserProfileRequest)

Create a new user profile

### Example

```typescript
import {
    UserProfileApi,
    Configuration,
    CreateUserProfileRequest
} from 'api-client';

const configuration = new Configuration();
const apiInstance = new UserProfileApi(configuration);

let createUserProfileRequest: CreateUserProfileRequest; //

const { status, data } = await apiInstance.createUserProfile(
    createUserProfileRequest
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **createUserProfileRequest** | **CreateUserProfileRequest**|  | |


### Return type

**UserProfileDTO**

### Authorization

[Bearer Authentication](../README.md#Bearer Authentication)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: */*


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | Profile created successfully |  -  |
|**400** | Invalid profile data |  -  |
|**404** | User not found |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **getUserProfile**
> UserProfileDTO getUserProfile()

Retrieve the current user\'s profile

### Example

```typescript
import {
    UserProfileApi,
    Configuration
} from 'api-client';

const configuration = new Configuration();
const apiInstance = new UserProfileApi(configuration);

const { status, data } = await apiInstance.getUserProfile();
```

### Parameters
This endpoint does not have any parameters.


### Return type

**UserProfileDTO**

### Authorization

[Bearer Authentication](../README.md#Bearer Authentication)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: */*


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | Profile retrieved successfully |  -  |
|**404** | Profile not found |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **updateUserProfile**
> UserProfileDTO updateUserProfile(updateUserProfileRequest)

Update the current user\'s profile

### Example

```typescript
import {
    UserProfileApi,
    Configuration,
    UpdateUserProfileRequest
} from 'api-client';

const configuration = new Configuration();
const apiInstance = new UserProfileApi(configuration);

let updateUserProfileRequest: UpdateUserProfileRequest; //

const { status, data } = await apiInstance.updateUserProfile(
    updateUserProfileRequest
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **updateUserProfileRequest** | **UpdateUserProfileRequest**|  | |


### Return type

**UserProfileDTO**

### Authorization

[Bearer Authentication](../README.md#Bearer Authentication)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: */*


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | Profile updated successfully |  -  |
|**400** | Invalid profile data |  -  |
|**404** | Profile not found |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

