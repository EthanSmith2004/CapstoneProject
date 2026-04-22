# AdminFinanceApi

All URIs are relative to *http://localhost:8080*

|Method | HTTP request | Description|
|------------- | ------------- | -------------|
|[**bulkLoadCredit**](#bulkloadcredit) | **POST** /api/admin/finance/bulk-load | Bulk load credit|
|[**findTransactions**](#findtransactions) | **GET** /api/admin/finance/transactions | Search transactions|
|[**findUsers**](#findusers) | **GET** /api/admin/finance/users | Search users with pagination|
|[**findUsersSearch**](#finduserssearch) | **GET** /api/admin/finance/users/search | Search users|
|[**getStatistics**](#getstatistics) | **GET** /api/admin/finance/statistics | Get and overview|
|[**getUserDetail**](#getuserdetail) | **GET** /api/admin/finance/users/{userId} | Get user financial details|
|[**loadUserCredit**](#loadusercredit) | **POST** /api/admin/finance/load | Load user credit|
|[**overview**](#overview) | **GET** /api/admin/finance/overview | Get and overview|

# **bulkLoadCredit**
> AdminFinanceLoadResponse bulkLoadCredit()

Load credit to multiple user accounts in bulk

### Example

```typescript
import {
    AdminFinanceApi,
    Configuration
} from 'api-client';

const configuration = new Configuration();
const apiInstance = new AdminFinanceApi(configuration);

let csvFile: File; // (default to undefined)

const { status, data } = await apiInstance.bulkLoadCredit(
    csvFile
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **csvFile** | [**File**] |  | defaults to undefined|


### Return type

**AdminFinanceLoadResponse**

### Authorization

[Bearer Authentication](../README.md#Bearer Authentication)

### HTTP request headers

 - **Content-Type**: multipart/form-data
 - **Accept**: */*


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | Bulk load completed |  -  |
|**400** | Invalid bulk load request |  -  |
|**403** | Access denied - Financial admin role required |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **findTransactions**
> Array<AdminTransactionDTO> findTransactions()

Search transactions

### Example

```typescript
import {
    AdminFinanceApi,
    Configuration
} from 'api-client';

const configuration = new Configuration();
const apiInstance = new AdminFinanceApi(configuration);

const { status, data } = await apiInstance.findTransactions();
```

### Parameters
This endpoint does not have any parameters.


### Return type

**Array<AdminTransactionDTO>**

### Authorization

[Bearer Authentication](../README.md#Bearer Authentication)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: */*


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | Transactions retrieved successfully |  -  |
|**403** | Access denied - Financial admin role required |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **findUsers**
> Array<FinanceUserDTO> findUsers()

Search users with financial information using pagination

### Example

```typescript
import {
    AdminFinanceApi,
    Configuration
} from 'api-client';

const configuration = new Configuration();
const apiInstance = new AdminFinanceApi(configuration);

const { status, data } = await apiInstance.findUsers();
```

### Parameters
This endpoint does not have any parameters.


### Return type

**Array<FinanceUserDTO>**

### Authorization

[Bearer Authentication](../README.md#Bearer Authentication)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: */*


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | Users retrieved successfully |  -  |
|**403** | Access denied - Financial admin role required |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **findUsersSearch**
> Array<CompactUserDTO> findUsersSearch()

Search users with financial information using pagination

### Example

```typescript
import {
    AdminFinanceApi,
    Configuration
} from 'api-client';

const configuration = new Configuration();
const apiInstance = new AdminFinanceApi(configuration);

let query: string; // (default to undefined)
let page: number; // (default to undefined)
let pageSize: number; // (default to undefined)

const { status, data } = await apiInstance.findUsersSearch(
    query,
    page,
    pageSize
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **query** | [**string**] |  | defaults to undefined|
| **page** | [**number**] |  | defaults to undefined|
| **pageSize** | [**number**] |  | defaults to undefined|


### Return type

**Array<CompactUserDTO>**

### Authorization

[Bearer Authentication](../README.md#Bearer Authentication)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: */*


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | Users retrieved successfully |  -  |
|**403** | Access denied - Financial admin role required |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **getStatistics**
> FinancialPeriodStatistic getStatistics()

Retrieve financial overview data

### Example

```typescript
import {
    AdminFinanceApi,
    Configuration
} from 'api-client';

const configuration = new Configuration();
const apiInstance = new AdminFinanceApi(configuration);

let start: string; // (default to undefined)
let end: string; // (default to undefined)

const { status, data } = await apiInstance.getStatistics(
    start,
    end
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **start** | [**string**] |  | defaults to undefined|
| **end** | [**string**] |  | defaults to undefined|


### Return type

**FinancialPeriodStatistic**

### Authorization

[Bearer Authentication](../README.md#Bearer Authentication)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: */*


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | Statistics retrieved successfully |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **getUserDetail**
> FinanceUserDTO getUserDetail()

Retrieve detailed financial information for a specific user

### Example

```typescript
import {
    AdminFinanceApi,
    Configuration
} from 'api-client';

const configuration = new Configuration();
const apiInstance = new AdminFinanceApi(configuration);

let userId: number; //User ID (default to undefined)

const { status, data } = await apiInstance.getUserDetail(
    userId
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **userId** | [**number**] | User ID | defaults to undefined|


### Return type

**FinanceUserDTO**

### Authorization

[Bearer Authentication](../README.md#Bearer Authentication)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: */*


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | User details retrieved successfully |  -  |
|**403** | Access denied - Financial admin role required |  -  |
|**404** | User not found |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **loadUserCredit**
> AdminLoadCreditResult loadUserCredit(adminLoadCreditRequest)

Load user Credit

### Example

```typescript
import {
    AdminFinanceApi,
    Configuration,
    AdminLoadCreditRequest
} from 'api-client';

const configuration = new Configuration();
const apiInstance = new AdminFinanceApi(configuration);

let adminLoadCreditRequest: AdminLoadCreditRequest; //

const { status, data } = await apiInstance.loadUserCredit(
    adminLoadCreditRequest
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **adminLoadCreditRequest** | **AdminLoadCreditRequest**|  | |


### Return type

**AdminLoadCreditResult**

### Authorization

[Bearer Authentication](../README.md#Bearer Authentication)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: */*


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | Load ok |  -  |
|**403** | Access denied - Financial admin role required |  -  |
|**404** | User not found |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **overview**
> FinancialOverview overview()

Retrieve financial overview data

### Example

```typescript
import {
    AdminFinanceApi,
    Configuration
} from 'api-client';

const configuration = new Configuration();
const apiInstance = new AdminFinanceApi(configuration);

const { status, data } = await apiInstance.overview();
```

### Parameters
This endpoint does not have any parameters.


### Return type

**FinancialOverview**

### Authorization

[Bearer Authentication](../README.md#Bearer Authentication)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: */*


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | Statistics retrieved successfully |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

