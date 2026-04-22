# AdminAuditApi

All URIs are relative to *http://localhost:8080*

|Method | HTTP request | Description|
|------------- | ------------- | -------------|
|[**getRecentLogins**](#getrecentlogins) | **GET** /api/admin/audit/logins/recent | Get recent user logins|
|[**getTransactionAuditPaginated**](#gettransactionauditpaginated) | **GET** /api/admin/audit/transactions | Get paginated transaction audit|
|[**getTransactionDetailsForAudit**](#gettransactiondetailsforaudit) | **GET** /api/admin/audit/transactions/{auditId}/details | Get transaction details for audit|
|[**getUserEventLogPaginated**](#getusereventlogpaginated) | **GET** /api/admin/audit/user-events | Get paginated user events|

# **getRecentLogins**
> Array<UserEventAuditDTO> getRecentLogins()

Retrieve recent user login events

### Example

```typescript
import {
    AdminAuditApi,
    Configuration
} from 'api-client';

const configuration = new Configuration();
const apiInstance = new AdminAuditApi(configuration);

const { status, data } = await apiInstance.getRecentLogins();
```

### Parameters
This endpoint does not have any parameters.


### Return type

**Array<UserEventAuditDTO>**

### Authorization

[Bearer Authentication](../README.md#Bearer Authentication)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: */*


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | Recent logins retrieved successfully |  -  |
|**403** | Access denied - Audit admin role required |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **getTransactionAuditPaginated**
> PageTransactionAuditDTO getTransactionAuditPaginated()

Retrieve transaction audit records with pagination

### Example

```typescript
import {
    AdminAuditApi,
    Configuration
} from 'api-client';

const configuration = new Configuration();
const apiInstance = new AdminAuditApi(configuration);

let page: number; // (optional) (default to 0)
let size: number; // (optional) (default to 10)

const { status, data } = await apiInstance.getTransactionAuditPaginated(
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

**PageTransactionAuditDTO**

### Authorization

[Bearer Authentication](../README.md#Bearer Authentication)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: */*


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | Transaction audit retrieved successfully |  -  |
|**403** | Access denied - Audit admin role required |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **getTransactionDetailsForAudit**
> Array<TransactionWithUserDTO> getTransactionDetailsForAudit()

Retrieve detailed transaction information including account owner details for a specific audit record

### Example

```typescript
import {
    AdminAuditApi,
    Configuration
} from 'api-client';

const configuration = new Configuration();
const apiInstance = new AdminAuditApi(configuration);

let auditId: number; // (default to undefined)

const { status, data } = await apiInstance.getTransactionDetailsForAudit(
    auditId
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **auditId** | [**number**] |  | defaults to undefined|


### Return type

**Array<TransactionWithUserDTO>**

### Authorization

[Bearer Authentication](../README.md#Bearer Authentication)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: */*


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | Transaction details retrieved successfully |  -  |
|**403** | Access denied - Audit admin role required |  -  |
|**404** | Audit record not found |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **getUserEventLogPaginated**
> PageUserEventAuditDTO getUserEventLogPaginated()

Retrieve user events with pagination

### Example

```typescript
import {
    AdminAuditApi,
    Configuration
} from 'api-client';

const configuration = new Configuration();
const apiInstance = new AdminAuditApi(configuration);

let page: number; // (optional) (default to 0)
let size: number; // (optional) (default to 10)

const { status, data } = await apiInstance.getUserEventLogPaginated(
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

**PageUserEventAuditDTO**

### Authorization

[Bearer Authentication](../README.md#Bearer Authentication)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: */*


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | User events retrieved successfully |  -  |
|**403** | Access denied - Audit admin role required |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

