# ListApi

All URIs are relative to *http://localhost:8080*

|Method | HTTP request | Description|
|------------- | ------------- | -------------|
|[**getAllergyNames**](#getallergynames) | **GET** /api/list/allergies | Get all allergy names|
|[**getCampusNames**](#getcampusnames) | **GET** /api/list/campuses | Get all campus names|
|[**getResidenceNames**](#getresidencenames) | **GET** /api/list/residences | Get all residence names|

# **getAllergyNames**
> Array<SelectDTO> getAllergyNames()


### Example

```typescript
import {
    ListApi,
    Configuration
} from 'api-client';

const configuration = new Configuration();
const apiInstance = new ListApi(configuration);

const { status, data } = await apiInstance.getAllergyNames();
```

### Parameters
This endpoint does not have any parameters.


### Return type

**Array<SelectDTO>**

### Authorization

[Bearer Authentication](../README.md#Bearer Authentication)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: */*


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | Allergy names found |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **getCampusNames**
> Array<SelectDTO> getCampusNames()


### Example

```typescript
import {
    ListApi,
    Configuration
} from 'api-client';

const configuration = new Configuration();
const apiInstance = new ListApi(configuration);

const { status, data } = await apiInstance.getCampusNames();
```

### Parameters
This endpoint does not have any parameters.


### Return type

**Array<SelectDTO>**

### Authorization

[Bearer Authentication](../README.md#Bearer Authentication)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: */*


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | Campus names found |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **getResidenceNames**
> Array<SelectDTO> getResidenceNames()


### Example

```typescript
import {
    ListApi,
    Configuration
} from 'api-client';

const configuration = new Configuration();
const apiInstance = new ListApi(configuration);

const { status, data } = await apiInstance.getResidenceNames();
```

### Parameters
This endpoint does not have any parameters.


### Return type

**Array<SelectDTO>**

### Authorization

[Bearer Authentication](../README.md#Bearer Authentication)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: */*


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | Residence names found |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

