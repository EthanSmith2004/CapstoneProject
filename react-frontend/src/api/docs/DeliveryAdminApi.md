# DeliveryAdminApi

All URIs are relative to *http://localhost:8080*

|Method | HTTP request | Description|
|------------- | ------------- | -------------|
|[**getAllPendingDeliveries**](#getallpendingdeliveries) | **GET** /api/delivery/pending-deliveries | Get all pending deliveries|
|[**getDeliveryStatistics**](#getdeliverystatistics) | **GET** /api/delivery/statistics | Get delivery statistics|
|[**scanItemForDelivery**](#scanitemfordelivery) | **POST** /api/delivery/scan-item | Scan item for delivery confirmation|
|[**scanUserBarcode**](#scanuserbarcode) | **POST** /api/delivery/scan-user | Scan user barcode|

# **getAllPendingDeliveries**
> Array<UserDeliveryItemsResponse> getAllPendingDeliveries()

Get all users with pending deliveries for today, grouped by user

### Example

```typescript
import {
    DeliveryAdminApi,
    Configuration
} from 'api-client';

const configuration = new Configuration();
const apiInstance = new DeliveryAdminApi(configuration);

const { status, data } = await apiInstance.getAllPendingDeliveries();
```

### Parameters
This endpoint does not have any parameters.


### Return type

**Array<UserDeliveryItemsResponse>**

### Authorization

[Bearer Authentication](../README.md#Bearer Authentication)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: */*


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | Successfully retrieved pending deliveries |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **getDeliveryStatistics**
> DeliveryStatisticsResponse getDeliveryStatistics()

Get delivery statistics for today\'s deliveries

### Example

```typescript
import {
    DeliveryAdminApi,
    Configuration
} from 'api-client';

const configuration = new Configuration();
const apiInstance = new DeliveryAdminApi(configuration);

const { status, data } = await apiInstance.getDeliveryStatistics();
```

### Parameters
This endpoint does not have any parameters.


### Return type

**DeliveryStatisticsResponse**

### Authorization

[Bearer Authentication](../README.md#Bearer Authentication)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: */*


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | Successfully retrieved delivery statistics |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **scanItemForDelivery**
> DeliveryConfirmationResponse scanItemForDelivery(scanItemBarcodeRequest)

Mark an order item as delivered by scanning user barcode and confirming item delivery

### Example

```typescript
import {
    DeliveryAdminApi,
    Configuration,
    ScanItemBarcodeRequest
} from 'api-client';

const configuration = new Configuration();
const apiInstance = new DeliveryAdminApi(configuration);

let scanItemBarcodeRequest: ScanItemBarcodeRequest; //

const { status, data } = await apiInstance.scanItemForDelivery(
    scanItemBarcodeRequest
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **scanItemBarcodeRequest** | **ScanItemBarcodeRequest**|  | |


### Return type

**DeliveryConfirmationResponse**

### Authorization

[Bearer Authentication](../README.md#Bearer Authentication)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: */*


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | Item successfully marked as delivered |  -  |
|**400** | Item does not belong to user or not in correct status |  -  |
|**404** | User or item not found |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **scanUserBarcode**
> UserDeliveryItemsResponse scanUserBarcode(scanUserBarcodeRequest)

Scan a user\'s barcode to retrieve their pending delivery items for today

### Example

```typescript
import {
    DeliveryAdminApi,
    Configuration,
    ScanUserBarcodeRequest
} from 'api-client';

const configuration = new Configuration();
const apiInstance = new DeliveryAdminApi(configuration);

let scanUserBarcodeRequest: ScanUserBarcodeRequest; //

const { status, data } = await apiInstance.scanUserBarcode(
    scanUserBarcodeRequest
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **scanUserBarcodeRequest** | **ScanUserBarcodeRequest**|  | |


### Return type

**UserDeliveryItemsResponse**

### Authorization

[Bearer Authentication](../README.md#Bearer Authentication)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: */*


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | Successfully retrieved user\&#39;s pending items |  -  |
|**404** | User not found with provided barcode |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

