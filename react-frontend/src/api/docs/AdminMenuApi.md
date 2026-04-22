# AdminMenuApi

All URIs are relative to *http://localhost:8080*

|Method | HTTP request | Description|
|------------- | ------------- | -------------|
|[**createMenuItem**](#createmenuitem) | **POST** /api/admin/menu | Create menu item|
|[**deleteMenuItem**](#deletemenuitem) | **DELETE** /api/admin/menu/{id} | Delete menu item|
|[**getCurrentMenuItems**](#getcurrentmenuitems) | **GET** /api/admin/menu/current | Get current menu items|
|[**getDraftMenuItems**](#getdraftmenuitems) | **GET** /api/admin/menu/items | Get draft menu items|
|[**getHistoricMenuItems**](#gethistoricmenuitems) | **GET** /api/admin/menu/historic | Get historic menu items|
|[**getMenuItemStatistics**](#getmenuitemstatistics) | **GET** /api/admin/menu/stats | Get menu item statistics|
|[**getMenuPaginated**](#getmenupaginated) | **GET** /api/admin/menu | Get paginated menu items (DEPRECATED)|
|[**queueMenuItem**](#queuemenuitem) | **POST** /api/admin/menu/queue | Queue a menu item|
|[**searchMenuPaginated**](#searchmenupaginated) | **GET** /api/admin/menu/search | Search menu items|
|[**updateMenuItem**](#updatemenuitem) | **PUT** /api/admin/menu/{id} | Update menu item|

# **createMenuItem**
> MenuItemDTO createMenuItem(adminMenuItemCreateRequest)

Create a new menu item

### Example

```typescript
import {
    AdminMenuApi,
    Configuration,
    AdminMenuItemCreateRequest
} from 'api-client';

const configuration = new Configuration();
const apiInstance = new AdminMenuApi(configuration);

let adminMenuItemCreateRequest: AdminMenuItemCreateRequest; //

const { status, data } = await apiInstance.createMenuItem(
    adminMenuItemCreateRequest
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **adminMenuItemCreateRequest** | **AdminMenuItemCreateRequest**|  | |


### Return type

**MenuItemDTO**

### Authorization

[Bearer Authentication](../README.md#Bearer Authentication)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: */*


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | Menu item created successfully |  -  |
|**400** | Invalid menu item data |  -  |
|**403** | Access denied - Menu admin role required |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **deleteMenuItem**
> deleteMenuItem()

Delete a menu item by ID. Only draft and current (non-historic) items can be deleted.

### Example

```typescript
import {
    AdminMenuApi,
    Configuration
} from 'api-client';

const configuration = new Configuration();
const apiInstance = new AdminMenuApi(configuration);

let id: number; // (default to undefined)

const { status, data } = await apiInstance.deleteMenuItem(
    id
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **id** | [**number**] |  | defaults to undefined|


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
|**204** | Menu item deleted successfully |  -  |
|**400** | Cannot delete historic menu item |  -  |
|**403** | Access denied - Menu admin role required |  -  |
|**404** | Menu item not found |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **getCurrentMenuItems**
> Array<MenuItemDTO> getCurrentMenuItems()

Retrieve all released items with delivery, release, edit, and order dates where orderBy is not past

### Example

```typescript
import {
    AdminMenuApi,
    Configuration
} from 'api-client';

const configuration = new Configuration();
const apiInstance = new AdminMenuApi(configuration);

const { status, data } = await apiInstance.getCurrentMenuItems();
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
|**200** | Current menu items retrieved successfully |  -  |
|**403** | Access denied - Menu admin role required |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **getDraftMenuItems**
> Array<MenuItemDTO> getDraftMenuItems()

Retrieve all menu items with null delivery, release, edit and order dates

### Example

```typescript
import {
    AdminMenuApi,
    Configuration
} from 'api-client';

const configuration = new Configuration();
const apiInstance = new AdminMenuApi(configuration);

const { status, data } = await apiInstance.getDraftMenuItems();
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
|**200** | Draft menu items retrieved successfully |  -  |
|**403** | Access denied - Menu admin role required |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **getHistoricMenuItems**
> Array<MenuItemDTO> getHistoricMenuItems()

Retrieve all items where orderBy date is in the past

### Example

```typescript
import {
    AdminMenuApi,
    Configuration
} from 'api-client';

const configuration = new Configuration();
const apiInstance = new AdminMenuApi(configuration);

const { status, data } = await apiInstance.getHistoricMenuItems();
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
|**200** | Historic menu items retrieved successfully |  -  |
|**403** | Access denied - Menu admin role required |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **getMenuItemStatistics**
> Array<MenuItemStatisticsDTO> getMenuItemStatistics()

Get statistics about popular items from order history

### Example

```typescript
import {
    AdminMenuApi,
    Configuration
} from 'api-client';

const configuration = new Configuration();
const apiInstance = new AdminMenuApi(configuration);

let startDate: string; // (optional) (default to undefined)
let endDate: string; // (optional) (default to undefined)

const { status, data } = await apiInstance.getMenuItemStatistics(
    startDate,
    endDate
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **startDate** | [**string**] |  | (optional) defaults to undefined|
| **endDate** | [**string**] |  | (optional) defaults to undefined|


### Return type

**Array<MenuItemStatisticsDTO>**

### Authorization

[Bearer Authentication](../README.md#Bearer Authentication)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: */*


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | Statistics retrieved successfully |  -  |
|**403** | Access denied - Menu admin role required |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **getMenuPaginated**
> Array<MenuItemDTO> getMenuPaginated()

Retrieve menu items with pagination. Use /menu/items, /menu/current, or /menu/historic instead.

### Example

```typescript
import {
    AdminMenuApi,
    Configuration
} from 'api-client';

const configuration = new Configuration();
const apiInstance = new AdminMenuApi(configuration);

let page: number; // (optional) (default to 0)
let size: number; // (optional) (default to 10)

const { status, data } = await apiInstance.getMenuPaginated(
    page,
    size
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **page** | [**number**] |  | (optional) defaults to 0|
| **size** | [**number**] |  | (optional) defaults to 10|


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
|**200** | Menu items retrieved successfully |  -  |
|**403** | Access denied - Menu admin role required |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **queueMenuItem**
> MenuItemDTO queueMenuItem(menuItemQueueRequest)

Create a new menu item by copying an existing one with new dates

### Example

```typescript
import {
    AdminMenuApi,
    Configuration,
    MenuItemQueueRequest
} from 'api-client';

const configuration = new Configuration();
const apiInstance = new AdminMenuApi(configuration);

let menuItemQueueRequest: MenuItemQueueRequest; //

const { status, data } = await apiInstance.queueMenuItem(
    menuItemQueueRequest
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **menuItemQueueRequest** | **MenuItemQueueRequest**|  | |


### Return type

**MenuItemDTO**

### Authorization

[Bearer Authentication](../README.md#Bearer Authentication)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: */*


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | Menu item queued successfully |  -  |
|**400** | Invalid request data |  -  |
|**403** | Access denied - Menu admin role required |  -  |
|**404** | Source menu item not found |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **searchMenuPaginated**
> Array<MenuItemDTO> searchMenuPaginated()

Search menu items with pagination

### Example

```typescript
import {
    AdminMenuApi,
    Configuration
} from 'api-client';

const configuration = new Configuration();
const apiInstance = new AdminMenuApi(configuration);

let search: string; // (default to undefined)
let page: number; // (optional) (default to 0)
let size: number; // (optional) (default to 10)

const { status, data } = await apiInstance.searchMenuPaginated(
    search,
    page,
    size
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **search** | [**string**] |  | defaults to undefined|
| **page** | [**number**] |  | (optional) defaults to 0|
| **size** | [**number**] |  | (optional) defaults to 10|


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
|**200** | Menu items retrieved successfully |  -  |
|**403** | Access denied - Menu admin role required |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **updateMenuItem**
> MenuItemDTO updateMenuItem(adminMenuItemUpdateRequest)

Update an existing menu item

### Example

```typescript
import {
    AdminMenuApi,
    Configuration,
    AdminMenuItemUpdateRequest
} from 'api-client';

const configuration = new Configuration();
const apiInstance = new AdminMenuApi(configuration);

let id: number; // (default to undefined)
let adminMenuItemUpdateRequest: AdminMenuItemUpdateRequest; //

const { status, data } = await apiInstance.updateMenuItem(
    id,
    adminMenuItemUpdateRequest
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **adminMenuItemUpdateRequest** | **AdminMenuItemUpdateRequest**|  | |
| **id** | [**number**] |  | defaults to undefined|


### Return type

**MenuItemDTO**

### Authorization

[Bearer Authentication](../README.md#Bearer Authentication)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: */*


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | Menu item updated successfully |  -  |
|**400** | Invalid menu item data |  -  |
|**403** | Access denied - Menu admin role required |  -  |
|**404** | Menu item not found |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

