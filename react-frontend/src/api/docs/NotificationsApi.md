# NotificationsApi

All URIs are relative to *http://localhost:8080*

|Method | HTTP request | Description|
|------------- | ------------- | -------------|
|[**getNotifications**](#getnotifications) | **GET** /api/user/notifications | Get all notifications|
|[**getUnreadNotificationCount**](#getunreadnotificationcount) | **GET** /api/user/notifications/unread/count | Get unread notification count|
|[**getUnreadNotifications**](#getunreadnotifications) | **GET** /api/user/notifications/unread | Get unread notifications|
|[**markAllNotificationsAsRead**](#markallnotificationsasread) | **PATCH** /api/user/notifications/read-all | Mark all notifications as read|
|[**markNotificationAsRead**](#marknotificationasread) | **PATCH** /api/user/notifications/{notificationId}/read | Mark notification as read|
|[**publicKey**](#publickey) | **GET** /api/user/notifications/publicKey | Get public key|
|[**subscribe**](#subscribe) | **POST** /api/user/notifications/subscribe | Subscribe to push notifications|
|[**unsubscribe**](#unsubscribe) | **POST** /api/user/notifications/unsubscribe | Unsubscribe from push notifications|

# **getNotifications**
> Array<NotificationDTO> getNotifications()

Get all notifications for the current user

### Example

```typescript
import {
    NotificationsApi,
    Configuration
} from 'api-client';

const configuration = new Configuration();
const apiInstance = new NotificationsApi(configuration);

const { status, data } = await apiInstance.getNotifications();
```

### Parameters
This endpoint does not have any parameters.


### Return type

**Array<NotificationDTO>**

### Authorization

[Bearer Authentication](../README.md#Bearer Authentication)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: */*


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | Notifications retrieved successfully |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **getUnreadNotificationCount**
> number getUnreadNotificationCount()

Get count of unread notifications for the current user

### Example

```typescript
import {
    NotificationsApi,
    Configuration
} from 'api-client';

const configuration = new Configuration();
const apiInstance = new NotificationsApi(configuration);

const { status, data } = await apiInstance.getUnreadNotificationCount();
```

### Parameters
This endpoint does not have any parameters.


### Return type

**number**

### Authorization

[Bearer Authentication](../README.md#Bearer Authentication)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: */*


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | Unread notification count retrieved successfully |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **getUnreadNotifications**
> Array<NotificationDTO> getUnreadNotifications()

Get all unread notifications for the current user

### Example

```typescript
import {
    NotificationsApi,
    Configuration
} from 'api-client';

const configuration = new Configuration();
const apiInstance = new NotificationsApi(configuration);

const { status, data } = await apiInstance.getUnreadNotifications();
```

### Parameters
This endpoint does not have any parameters.


### Return type

**Array<NotificationDTO>**

### Authorization

[Bearer Authentication](../README.md#Bearer Authentication)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: */*


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | Unread notifications retrieved successfully |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **markAllNotificationsAsRead**
> markAllNotificationsAsRead()

Mark all notifications as read for the current user

### Example

```typescript
import {
    NotificationsApi,
    Configuration
} from 'api-client';

const configuration = new Configuration();
const apiInstance = new NotificationsApi(configuration);

const { status, data } = await apiInstance.markAllNotificationsAsRead();
```

### Parameters
This endpoint does not have any parameters.


### Return type

void (empty response body)

### Authorization

[Bearer Authentication](../README.md#Bearer Authentication)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: Not defined


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | All notifications marked as read successfully |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **markNotificationAsRead**
> markNotificationAsRead()

Mark a specific notification as read

### Example

```typescript
import {
    NotificationsApi,
    Configuration
} from 'api-client';

const configuration = new Configuration();
const apiInstance = new NotificationsApi(configuration);

let notificationId: number; // (default to undefined)

const { status, data } = await apiInstance.markNotificationAsRead(
    notificationId
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **notificationId** | [**number**] |  | defaults to undefined|


### Return type

void (empty response body)

### Authorization

[Bearer Authentication](../README.md#Bearer Authentication)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: Not defined


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | Notification marked as read successfully |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **publicKey**
> string publicKey()

Get the backend public key

### Example

```typescript
import {
    NotificationsApi,
    Configuration
} from 'api-client';

const configuration = new Configuration();
const apiInstance = new NotificationsApi(configuration);

const { status, data } = await apiInstance.publicKey();
```

### Parameters
This endpoint does not have any parameters.


### Return type

**string**

### Authorization

[Bearer Authentication](../README.md#Bearer Authentication)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: */*


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | Got public key successfully |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **subscribe**
> subscribe(pushSubscriptionRequest)

Register a device for push notifications.

### Example

```typescript
import {
    NotificationsApi,
    Configuration,
    PushSubscriptionRequest
} from 'api-client';

const configuration = new Configuration();
const apiInstance = new NotificationsApi(configuration);

let pushSubscriptionRequest: PushSubscriptionRequest; //

const { status, data } = await apiInstance.subscribe(
    pushSubscriptionRequest
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **pushSubscriptionRequest** | **PushSubscriptionRequest**|  | |


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
|**201** | Subscribed successfully |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **unsubscribe**
> unsubscribe(pushSubscriptionRequest)

Unregister a device for push notifications.

### Example

```typescript
import {
    NotificationsApi,
    Configuration,
    PushSubscriptionRequest
} from 'api-client';

const configuration = new Configuration();
const apiInstance = new NotificationsApi(configuration);

let pushSubscriptionRequest: PushSubscriptionRequest; //

const { status, data } = await apiInstance.unsubscribe(
    pushSubscriptionRequest
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **pushSubscriptionRequest** | **PushSubscriptionRequest**|  | |


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
|**200** | Unsubscribed successfully |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

