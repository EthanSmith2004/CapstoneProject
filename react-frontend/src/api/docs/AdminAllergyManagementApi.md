# AdminAllergyManagementApi

All URIs are relative to *http://localhost:8080*

|Method | HTTP request | Description|
|------------- | ------------- | -------------|
|[**create2**](#create2) | **POST** /api/admin/allergies | Create a new resource|
|[**delete2**](#delete2) | **DELETE** /api/admin/allergies/{id} | Delete a resource|
|[**findAll2**](#findall2) | **GET** /api/admin/allergies | Find all resources|
|[**findById2**](#findbyid2) | **GET** /api/admin/allergies/{id} | Find a resource by ID|
|[**update2**](#update2) | **PUT** /api/admin/allergies/{id} | Update a resource|

# **create2**
> AllergyEntity create2(allergyEntity)


### Example

```typescript
import {
    AdminAllergyManagementApi,
    Configuration,
    AllergyEntity
} from 'api-client';

const configuration = new Configuration();
const apiInstance = new AdminAllergyManagementApi(configuration);

let allergyEntity: AllergyEntity; //

const { status, data } = await apiInstance.create2(
    allergyEntity
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **allergyEntity** | **AllergyEntity**|  | |


### Return type

**AllergyEntity**

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

# **delete2**
> delete2()


### Example

```typescript
import {
    AdminAllergyManagementApi,
    Configuration
} from 'api-client';

const configuration = new Configuration();
const apiInstance = new AdminAllergyManagementApi(configuration);

let id: number; // (default to undefined)

const { status, data } = await apiInstance.delete2(
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

# **findAll2**
> Array<AllergyEntity> findAll2()


### Example

```typescript
import {
    AdminAllergyManagementApi,
    Configuration
} from 'api-client';

const configuration = new Configuration();
const apiInstance = new AdminAllergyManagementApi(configuration);

const { status, data } = await apiInstance.findAll2();
```

### Parameters
This endpoint does not have any parameters.


### Return type

**Array<AllergyEntity>**

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

# **findById2**
> AllergyEntity findById2()


### Example

```typescript
import {
    AdminAllergyManagementApi,
    Configuration
} from 'api-client';

const configuration = new Configuration();
const apiInstance = new AdminAllergyManagementApi(configuration);

let id: number; // (default to undefined)

const { status, data } = await apiInstance.findById2(
    id
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **id** | [**number**] |  | defaults to undefined|


### Return type

**AllergyEntity**

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

# **update2**
> AllergyEntity update2(allergyEntity)


### Example

```typescript
import {
    AdminAllergyManagementApi,
    Configuration,
    AllergyEntity
} from 'api-client';

const configuration = new Configuration();
const apiInstance = new AdminAllergyManagementApi(configuration);

let id: number; // (default to undefined)
let allergyEntity: AllergyEntity; //

const { status, data } = await apiInstance.update2(
    id,
    allergyEntity
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **allergyEntity** | **AllergyEntity**|  | |
| **id** | [**number**] |  | defaults to undefined|


### Return type

**AllergyEntity**

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

