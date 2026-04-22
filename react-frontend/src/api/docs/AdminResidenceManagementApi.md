# AdminResidenceManagementApi

All URIs are relative to *http://localhost:8080*

|Method | HTTP request | Description|
|------------- | ------------- | -------------|
|[**_delete**](#_delete) | **DELETE** /api/admin/residences/{id} | Delete a resource|
|[**create**](#create) | **POST** /api/admin/residences | Create a new resource|
|[**findAll**](#findall) | **GET** /api/admin/residences | Find all resources|
|[**findById**](#findbyid) | **GET** /api/admin/residences/{id} | Find a resource by ID|
|[**update**](#update) | **PUT** /api/admin/residences/{id} | Update a resource|

# **_delete**
> _delete()


### Example

```typescript
import {
    AdminResidenceManagementApi,
    Configuration
} from 'api-client';

const configuration = new Configuration();
const apiInstance = new AdminResidenceManagementApi(configuration);

let id: number; // (default to undefined)

const { status, data } = await apiInstance._delete(
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
|**204** | Resource deleted successfully |  -  |
|**404** | Resource not found |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **create**
> ResidenceEntity create(residenceEntity)


### Example

```typescript
import {
    AdminResidenceManagementApi,
    Configuration,
    ResidenceEntity
} from 'api-client';

const configuration = new Configuration();
const apiInstance = new AdminResidenceManagementApi(configuration);

let residenceEntity: ResidenceEntity; //

const { status, data } = await apiInstance.create(
    residenceEntity
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **residenceEntity** | **ResidenceEntity**|  | |


### Return type

**ResidenceEntity**

### Authorization

[Bearer Authentication](../README.md#Bearer Authentication)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: */*


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | Resource created successfully |  -  |
|**400** | Invalid input |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **findAll**
> Array<ResidenceEntity> findAll()


### Example

```typescript
import {
    AdminResidenceManagementApi,
    Configuration
} from 'api-client';

const configuration = new Configuration();
const apiInstance = new AdminResidenceManagementApi(configuration);

const { status, data } = await apiInstance.findAll();
```

### Parameters
This endpoint does not have any parameters.


### Return type

**Array<ResidenceEntity>**

### Authorization

[Bearer Authentication](../README.md#Bearer Authentication)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: */*


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | Resources found |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **findById**
> ResidenceEntity findById()


### Example

```typescript
import {
    AdminResidenceManagementApi,
    Configuration
} from 'api-client';

const configuration = new Configuration();
const apiInstance = new AdminResidenceManagementApi(configuration);

let id: number; // (default to undefined)

const { status, data } = await apiInstance.findById(
    id
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **id** | [**number**] |  | defaults to undefined|


### Return type

**ResidenceEntity**

### Authorization

[Bearer Authentication](../README.md#Bearer Authentication)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: */*


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | Resource found |  -  |
|**404** | Resource not found |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **update**
> ResidenceEntity update(residenceEntity)


### Example

```typescript
import {
    AdminResidenceManagementApi,
    Configuration,
    ResidenceEntity
} from 'api-client';

const configuration = new Configuration();
const apiInstance = new AdminResidenceManagementApi(configuration);

let id: number; // (default to undefined)
let residenceEntity: ResidenceEntity; //

const { status, data } = await apiInstance.update(
    id,
    residenceEntity
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **residenceEntity** | **ResidenceEntity**|  | |
| **id** | [**number**] |  | defaults to undefined|


### Return type

**ResidenceEntity**

### Authorization

[Bearer Authentication](../README.md#Bearer Authentication)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: */*


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | Resource updated successfully |  -  |
|**400** | Invalid input |  -  |
|**404** | Resource not found |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

