# UserOrdersApi

All URIs are relative to *http://localhost:8080*

|Method | HTTP request | Description|
|------------- | ------------- | -------------|
|[**cancelOrderItem**](#cancelorderitem) | **DELETE** /api/user/orders/{orderItemId}/cancel | Cancel an order item|
|[**createOrder**](#createorder) | **POST** /api/user/orders | Create a new order|
|[**getUserOrderHistory**](#getuserorderhistory) | **GET** /api/user/orders/historic | Get user order history|
|[**getUserOrders**](#getuserorders) | **GET** /api/user/orders/schedule | Get user order item schedule|

# **cancelOrderItem**
> cancelOrderItem()

Cancel a specific order item for the current user

### Example

```typescript
import {
    UserOrdersApi,
    Configuration
} from 'api-client';

const configuration = new Configuration();
const apiInstance = new UserOrdersApi(configuration);

let orderItemId: number; // (default to undefined)

const { status, data } = await apiInstance.cancelOrderItem(
    orderItemId
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **orderItemId** | [**number**] |  | defaults to undefined|


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
|**200** | Order item cancelled successfully |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **createOrder**
> OrderDTO createOrder(placeOrderRequest)

Create a new order for the current user

### Example

```typescript
import {
    UserOrdersApi,
    Configuration,
    PlaceOrderRequest
} from 'api-client';

const configuration = new Configuration();
const apiInstance = new UserOrdersApi(configuration);

let placeOrderRequest: PlaceOrderRequest; //

const { status, data } = await apiInstance.createOrder(
    placeOrderRequest
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **placeOrderRequest** | **PlaceOrderRequest**|  | |


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
|**201** | Order created successfully |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **getUserOrderHistory**
> Array<OrderItemDTO> getUserOrderHistory()

Retrieve historic user orders

### Example

```typescript
import {
    UserOrdersApi,
    Configuration
} from 'api-client';

const configuration = new Configuration();
const apiInstance = new UserOrdersApi(configuration);

const { status, data } = await apiInstance.getUserOrderHistory();
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
|**200** | Orders retrieved successfully |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **getUserOrders**
> Array<OrderItemDTO> getUserOrders()

Retrieve all pending order items for current user

### Example

```typescript
import {
    UserOrdersApi,
    Configuration
} from 'api-client';

const configuration = new Configuration();
const apiInstance = new UserOrdersApi(configuration);

const { status, data } = await apiInstance.getUserOrders();
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
|**200** | Orders retrieved successfully |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

