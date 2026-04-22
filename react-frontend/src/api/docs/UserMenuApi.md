# UserMenuApi

All URIs are relative to *http://localhost:8080*

|Method | HTTP request | Description|
|------------- | ------------- | -------------|
|[**getMenu**](#getmenu) | **GET** /api/user/menu | Get menu|
|[**getMenuItemDetail**](#getmenuitemdetail) | **GET** /api/user/menu/{itemId} | Get menu item detail|

# **getMenu**
> Array<MenuItemDTO> getMenu()

Retrieve available menu items

### Example

```typescript
import {
    UserMenuApi,
    Configuration
} from 'api-client';

const configuration = new Configuration();
const apiInstance = new UserMenuApi(configuration);

const { status, data } = await apiInstance.getMenu();
```

### Parameters
This endpoint does not have any parameters.


### Return type

**Array<MenuItemDTO>**

### Authorization

[Bearer Authentication](../README.md#Bearer Authentication)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: */*


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | Menu retrieved successfully |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **getMenuItemDetail**
> MenuItemDTO getMenuItemDetail()

Retrieve detailed information about a menu item

### Example

```typescript
import {
    UserMenuApi,
    Configuration
} from 'api-client';

const configuration = new Configuration();
const apiInstance = new UserMenuApi(configuration);

let itemId: number; // (default to undefined)

const { status, data } = await apiInstance.getMenuItemDetail(
    itemId
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **itemId** | [**number**] |  | defaults to undefined|


### Return type

**MenuItemDTO**

### Authorization

[Bearer Authentication](../README.md#Bearer Authentication)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: */*


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | Menu item retrieved successfully |  -  |
|**404** | Menu item not found |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

