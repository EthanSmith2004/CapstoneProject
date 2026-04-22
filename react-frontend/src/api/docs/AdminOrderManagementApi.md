# AdminOrderManagementApi

All URIs are relative to *http://localhost:8080*

|Method | HTTP request | Description|
|------------- | ------------- | -------------|
|[**bulkUpdateOrderStatuses**](#bulkupdateorderstatuses) | **PUT** /api/admin/orders/bulk-status | Bulk update order statuses|
|[**getAllOrders**](#getallorders) | **GET** /api/admin/orders | Get all orders|
|[**getAllOrdersItems**](#getallordersitems) | **GET** /api/admin/orders/items | Get all order items|
|[**getDeliveryReportData**](#getdeliveryreportdata) | **GET** /api/admin/orders/report/delivery | Get delivery report data|
|[**getDeliveryReportPeriod**](#getdeliveryreportperiod) | **GET** /api/admin/orders/report/delivery/period | Get delivery report data for period|
|[**getKitchenReportData**](#getkitchenreportdata) | **GET** /api/admin/orders/report/kitchen | Get kitchen report data|
|[**getKitchenReportPeriod**](#getkitchenreportperiod) | **GET** /api/admin/orders/report/kitchen/period | Get kitchen report data for period|
|[**getOrderStatistics**](#getorderstatistics) | **GET** /api/admin/orders/statistics | Get Order statistics|
|[**getOrderStatisticsPeriod**](#getorderstatisticsperiod) | **GET** /api/admin/orders/statistics/detail | Get Order statistics|
|[**updateOrderItemStatus**](#updateorderitemstatus) | **PUT** /api/admin/order-items/{id}/status | Update order item status|
|[**updateOrderStatus**](#updateorderstatus) | **PUT** /api/admin/orders/{id}/status | Update order status|

# **bulkUpdateOrderStatuses**
> bulkUpdateOrderStatuses(bulkOrderStatusUpdateRequest)

Update the statuses of multiple orders at once.

### Example

```typescript
import {
    AdminOrderManagementApi,
    Configuration,
    BulkOrderStatusUpdateRequest
} from 'api-client';

const configuration = new Configuration();
const apiInstance = new AdminOrderManagementApi(configuration);

let bulkOrderStatusUpdateRequest: BulkOrderStatusUpdateRequest; //

const { status, data } = await apiInstance.bulkUpdateOrderStatuses(
    bulkOrderStatusUpdateRequest
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **bulkOrderStatusUpdateRequest** | **BulkOrderStatusUpdateRequest**|  | |


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
|**200** | Successfully updated order statuses |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **getAllOrders**
> Array<OrderDTO> getAllOrders()

Retrieve a list of all user orders.

### Example

```typescript
import {
    AdminOrderManagementApi,
    Configuration
} from 'api-client';

const configuration = new Configuration();
const apiInstance = new AdminOrderManagementApi(configuration);

const { status, data } = await apiInstance.getAllOrders();
```

### Parameters
This endpoint does not have any parameters.


### Return type

**Array<OrderDTO>**

### Authorization

[Bearer Authentication](../README.md#Bearer Authentication)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: */*


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | Successfully retrieved list of orders |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **getAllOrdersItems**
> Array<OrderItemDTO> getAllOrdersItems()

Retrieve a list of all order items

### Example

```typescript
import {
    AdminOrderManagementApi,
    Configuration
} from 'api-client';

const configuration = new Configuration();
const apiInstance = new AdminOrderManagementApi(configuration);

const { status, data } = await apiInstance.getAllOrdersItems();
```

### Parameters
This endpoint does not have any parameters.


### Return type

**Array<OrderItemDTO>**

### Authorization

[Bearer Authentication](../README.md#Bearer Authentication)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: */*


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | Successfully retrieved list of order items |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **getDeliveryReportData**
> DeliveryReportData getDeliveryReportData()

Get delivery report data for orders within a specified date

### Example

```typescript
import {
    AdminOrderManagementApi,
    Configuration
} from 'api-client';

const configuration = new Configuration();
const apiInstance = new AdminOrderManagementApi(configuration);

let date: string; // (default to undefined)

const { status, data } = await apiInstance.getDeliveryReportData(
    date
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **date** | [**string**] |  | defaults to undefined|


### Return type

**DeliveryReportData**

### Authorization

[Bearer Authentication](../README.md#Bearer Authentication)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: */*


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | Successfully retrieved delivery report data |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **getDeliveryReportPeriod**
> DeliveryReportData getDeliveryReportPeriod()

Get delivery report data for orders within a specified period with optional campus and residence filters

### Example

```typescript
import {
    AdminOrderManagementApi,
    Configuration
} from 'api-client';

const configuration = new Configuration();
const apiInstance = new AdminOrderManagementApi(configuration);

let start: string; // (default to undefined)
let end: string; // (default to undefined)
let campusId: number; // (optional) (default to undefined)
let residenceId: number; // (optional) (default to undefined)

const { status, data } = await apiInstance.getDeliveryReportPeriod(
    start,
    end,
    campusId,
    residenceId
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **start** | [**string**] |  | defaults to undefined|
| **end** | [**string**] |  | defaults to undefined|
| **campusId** | [**number**] |  | (optional) defaults to undefined|
| **residenceId** | [**number**] |  | (optional) defaults to undefined|


### Return type

**DeliveryReportData**

### Authorization

[Bearer Authentication](../README.md#Bearer Authentication)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: */*


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | Successfully retrieved delivery report data for period |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **getKitchenReportData**
> KitchenReportData getKitchenReportData()

Get kitchen report data for orders within a specified date

### Example

```typescript
import {
    AdminOrderManagementApi,
    Configuration
} from 'api-client';

const configuration = new Configuration();
const apiInstance = new AdminOrderManagementApi(configuration);

let date: string; // (default to undefined)

const { status, data } = await apiInstance.getKitchenReportData(
    date
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **date** | [**string**] |  | defaults to undefined|


### Return type

**KitchenReportData**

### Authorization

[Bearer Authentication](../README.md#Bearer Authentication)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: */*


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | Successfully retrieved kitchen report data |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **getKitchenReportPeriod**
> KitchenReportData getKitchenReportPeriod()

Get kitchen report data for orders within a specified period with optional campus and residence filters

### Example

```typescript
import {
    AdminOrderManagementApi,
    Configuration
} from 'api-client';

const configuration = new Configuration();
const apiInstance = new AdminOrderManagementApi(configuration);

let start: string; // (default to undefined)
let end: string; // (default to undefined)
let campusId: number; // (optional) (default to undefined)
let residenceId: number; // (optional) (default to undefined)

const { status, data } = await apiInstance.getKitchenReportPeriod(
    start,
    end,
    campusId,
    residenceId
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **start** | [**string**] |  | defaults to undefined|
| **end** | [**string**] |  | defaults to undefined|
| **campusId** | [**number**] |  | (optional) defaults to undefined|
| **residenceId** | [**number**] |  | (optional) defaults to undefined|


### Return type

**KitchenReportData**

### Authorization

[Bearer Authentication](../README.md#Bearer Authentication)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: */*


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | Successfully retrieved kitchen report data for period |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **getOrderStatistics**
> AdminOrderStatistics getOrderStatistics()

Get order statistics for the default period

### Example

```typescript
import {
    AdminOrderManagementApi,
    Configuration
} from 'api-client';

const configuration = new Configuration();
const apiInstance = new AdminOrderManagementApi(configuration);

const { status, data } = await apiInstance.getOrderStatistics();
```

### Parameters
This endpoint does not have any parameters.


### Return type

**AdminOrderStatistics**

### Authorization

[Bearer Authentication](../README.md#Bearer Authentication)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: */*


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | Successfully retrieved order statistics |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **getOrderStatisticsPeriod**
> AdminOrderPeriodStatistics getOrderStatisticsPeriod()

Get order statistics for the specified period with optional campus and residence filters

### Example

```typescript
import {
    AdminOrderManagementApi,
    Configuration
} from 'api-client';

const configuration = new Configuration();
const apiInstance = new AdminOrderManagementApi(configuration);

let start: string; // (default to undefined)
let end: string; // (default to undefined)
let campusId: number; // (optional) (default to undefined)
let residenceId: number; // (optional) (default to undefined)

const { status, data } = await apiInstance.getOrderStatisticsPeriod(
    start,
    end,
    campusId,
    residenceId
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **start** | [**string**] |  | defaults to undefined|
| **end** | [**string**] |  | defaults to undefined|
| **campusId** | [**number**] |  | (optional) defaults to undefined|
| **residenceId** | [**number**] |  | (optional) defaults to undefined|


### Return type

**AdminOrderPeriodStatistics**

### Authorization

[Bearer Authentication](../README.md#Bearer Authentication)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: */*


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | Successfully retrieved order statistics |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **updateOrderItemStatus**
> OrderItemDTO updateOrderItemStatus(body)

Update the status of a specific order item with user notification.

### Example

```typescript
import {
    AdminOrderManagementApi,
    Configuration
} from 'api-client';

const configuration = new Configuration();
const apiInstance = new AdminOrderManagementApi(configuration);

let id: number; // (default to undefined)
let body: string; //

const { status, data } = await apiInstance.updateOrderItemStatus(
    id,
    body
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **body** | **string**|  | |
| **id** | [**number**] |  | defaults to undefined|


### Return type

**OrderItemDTO**

### Authorization

[Bearer Authentication](../README.md#Bearer Authentication)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: */*


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | Successfully updated order item status |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **updateOrderStatus**
> OrderDTO updateOrderStatus(body)

Update the status of a specific order.

### Example

```typescript
import {
    AdminOrderManagementApi,
    Configuration
} from 'api-client';

const configuration = new Configuration();
const apiInstance = new AdminOrderManagementApi(configuration);

let id: number; // (default to undefined)
let body: string; //

const { status, data } = await apiInstance.updateOrderStatus(
    id,
    body
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **body** | **string**|  | |
| **id** | [**number**] |  | defaults to undefined|


### Return type

**OrderDTO**

### Authorization

[Bearer Authentication](../README.md#Bearer Authentication)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: */*


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | Successfully updated order status |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

