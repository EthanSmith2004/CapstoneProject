# UserSettingsApi

All URIs are relative to *http://localhost:8080*

|Method | HTTP request | Description|
|------------- | ------------- | -------------|
|[**getUserSettings**](#getusersettings) | **GET** /api/user/settings | Get user settings|
|[**updateUserSettings**](#updateusersettings) | **PUT** /api/user/settings | Update user settings|

# **getUserSettings**
> UserSettingsDTO getUserSettings()

Retrieve current user\'s settings and preferences

### Example

```typescript
import {
    UserSettingsApi,
    Configuration
} from 'api-client';

const configuration = new Configuration();
const apiInstance = new UserSettingsApi(configuration);

const { status, data } = await apiInstance.getUserSettings();
```

### Parameters
This endpoint does not have any parameters.


### Return type

**UserSettingsDTO**

### Authorization

[Bearer Authentication](../README.md#Bearer Authentication)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: */*


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | Settings retrieved successfully |  -  |
|**404** | User or settings not found |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **updateUserSettings**
> UserSettingsDTO updateUserSettings(updateUserSettingsRequest)

Update current user\'s settings and preferences

### Example

```typescript
import {
    UserSettingsApi,
    Configuration,
    UpdateUserSettingsRequest
} from 'api-client';

const configuration = new Configuration();
const apiInstance = new UserSettingsApi(configuration);

let updateUserSettingsRequest: UpdateUserSettingsRequest; //

const { status, data } = await apiInstance.updateUserSettings(
    updateUserSettingsRequest
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **updateUserSettingsRequest** | **UpdateUserSettingsRequest**|  | |


### Return type

**UserSettingsDTO**

### Authorization

[Bearer Authentication](../README.md#Bearer Authentication)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: */*


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | Settings updated successfully |  -  |
|**400** | Invalid settings data |  -  |
|**404** | User or settings not found |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

