# AdminNotificationsApi

All URIs are relative to *http://localhost:8080*

|Method | HTTP request | Description|
|------------- | ------------- | -------------|
|[**sendToAll**](#sendtoall) | **POST** /api/admin/notifications/send-to-all | Send notification to all users|
|[**sendToUser**](#sendtouser) | **POST** /api/admin/notifications/send-to-user/{userId} | Send notification to a specific user|

# **sendToAll**
> sendToAll(notificationRequest)

Send a push notification to all subscribed devices.

### Example

```typescript
import {
    AdminNotificationsApi,
    Configuration,
    NotificationRequest
} from 'api-client';

const configuration = new Configuration();
const apiInstance = new AdminNotificationsApi(configuration);

let notificationRequest: NotificationRequest; //

const { status, data } = await apiInstance.sendToAll(
    notificationRequest
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **notificationRequest** | **NotificationRequest**|  | |


### Return type

void (empty response body)

### Authorization

[Bearer Authentication](../README.md#Bearer Authentication)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: Not defined


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | Notification sent |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **sendToUser**
> sendToUser(notificationRequest)

Send a push notification to a specific user\'s devices.

### Example

```typescript
import {
    AdminNotificationsApi,
    Configuration,
    NotificationRequest
} from 'api-client';

const configuration = new Configuration();
const apiInstance = new AdminNotificationsApi(configuration);

let userId: number; // (default to undefined)
let notificationRequest: NotificationRequest; //

const { status, data } = await apiInstance.sendToUser(
    userId,
    notificationRequest
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **notificationRequest** | **NotificationRequest**|  | |
| **userId** | [**number**] |  | defaults to undefined|


### Return type

void (empty response body)

### Authorization

[Bearer Authentication](../README.md#Bearer Authentication)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: Not defined


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | Notification sent |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

