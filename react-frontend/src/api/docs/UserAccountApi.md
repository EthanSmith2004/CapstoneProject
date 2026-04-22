# UserAccountApi

All URIs are relative to *http://localhost:8080*

|Method | HTTP request | Description|
|------------- | ------------- | -------------|
|[**getAccount**](#getaccount) | **GET** /api/user/account | Get account details|
|[**getTransactionsPaginated**](#gettransactionspaginated) | **GET** /api/user/transactions | Get paginated transactions|

# **getAccount**
> AccountDTO getAccount()

Retrieve current user\'s account information

### Example

```typescript
import {
    UserAccountApi,
    Configuration
} from 'api-client';

const configuration = new Configuration();
const apiInstance = new UserAccountApi(configuration);

const { status, data } = await apiInstance.getAccount();
```

### Parameters
This endpoint does not have any parameters.


### Return type

**AccountDTO**

### Authorization

[Bearer Authentication](../README.md#Bearer Authentication)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: */*


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | Account retrieved successfully |  -  |
|**404** | User or account not found |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **getTransactionsPaginated**
> Array<TransactionDTO> getTransactionsPaginated()

Retrieve transactions with pagination

### Example

```typescript
import {
    UserAccountApi,
    Configuration
} from 'api-client';

const configuration = new Configuration();
const apiInstance = new UserAccountApi(configuration);

let page: number; // (optional) (default to 0)
let size: number; // (optional) (default to 10)

const { status, data } = await apiInstance.getTransactionsPaginated(
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

**Array<TransactionDTO>**

### Authorization

[Bearer Authentication](../README.md#Bearer Authentication)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: */*


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | Transactions retrieved successfully |  -  |
|**404** | User or account not found |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

