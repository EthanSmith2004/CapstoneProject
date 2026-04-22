# AdminCampusManagementApi

All URIs are relative to *http://localhost:8080*

|Method | HTTP request | Description|
|------------- | ------------- | -------------|
|[**create1**](#create1) | **POST** /api/admin/campuses | Create a new resource|
|[**delete1**](#delete1) | **DELETE** /api/admin/campuses/{id} | Delete a resource|
|[**findAll1**](#findall1) | **GET** /api/admin/campuses | Find all resources|
|[**findById1**](#findbyid1) | **GET** /api/admin/campuses/{id} | Find a resource by ID|
|[**update1**](#update1) | **PUT** /api/admin/campuses/{id} | Update a resource|

# **create1**
> CampusEntity create1(campusEntity)


### Example

```typescript
import {
    AdminCampusManagementApi,
    Configuration,
    CampusEntity
} from 'api-client';

const configuration = new Configuration();
const apiInstance = new AdminCampusManagementApi(configuration);

let campusEntity: CampusEntity; //

const { status, data } = await apiInstance.create1(
    campusEntity
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **campusEntity** | **CampusEntity**|  | |


### Return type

**CampusEntity**

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

# **delete1**
> delete1()


### Example

```typescript
import {
    AdminCampusManagementApi,
    Configuration
} from 'api-client';

const configuration = new Configuration();
const apiInstance = new AdminCampusManagementApi(configuration);

let id: number; // (default to undefined)

const { status, data } = await apiInstance.delete1(
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

# **findAll1**
> Array<CampusEntity> findAll1()


### Example

```typescript
import {
    AdminCampusManagementApi,
    Configuration
} from 'api-client';

const configuration = new Configuration();
const apiInstance = new AdminCampusManagementApi(configuration);

const { status, data } = await apiInstance.findAll1();
```

### Parameters
This endpoint does not have any parameters.


### Return type

**Array<CampusEntity>**

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

# **findById1**
> CampusEntity findById1()


### Example

```typescript
import {
    AdminCampusManagementApi,
    Configuration
} from 'api-client';

const configuration = new Configuration();
const apiInstance = new AdminCampusManagementApi(configuration);

let id: number; // (default to undefined)

const { status, data } = await apiInstance.findById1(
    id
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **id** | [**number**] |  | defaults to undefined|


### Return type

**CampusEntity**

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

# **update1**
> CampusEntity update1(campusEntity)


### Example

```typescript
import {
    AdminCampusManagementApi,
    Configuration,
    CampusEntity
} from 'api-client';

const configuration = new Configuration();
const apiInstance = new AdminCampusManagementApi(configuration);

let id: number; // (default to undefined)
let campusEntity: CampusEntity; //

const { status, data } = await apiInstance.update1(
    id,
    campusEntity
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **campusEntity** | **CampusEntity**|  | |
| **id** | [**number**] |  | defaults to undefined|


### Return type

**CampusEntity**

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

