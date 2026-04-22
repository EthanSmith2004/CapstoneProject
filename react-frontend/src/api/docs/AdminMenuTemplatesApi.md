# AdminMenuTemplatesApi

All URIs are relative to *http://localhost:8080*

|Method | HTTP request | Description|
|------------- | ------------- | -------------|
|[**createTemplate**](#createtemplate) | **POST** /api/admin/menu-templates | Create new template|
|[**deleteTemplate**](#deletetemplate) | **DELETE** /api/admin/menu-templates/{id} | Delete template|
|[**deleteTemplatesByPresetName**](#deletetemplatesbypresetname) | **DELETE** /api/admin/menu-templates/preset/{presetName} | Batch delete templates by preset|
|[**getDistinctPresetNames**](#getdistinctpresetnames) | **GET** /api/admin/menu-templates/presets | Get distinct preset names|
|[**getTemplatesByPresetName**](#gettemplatesbypresetname) | **GET** /api/admin/menu-templates/preset/{presetName} | Get templates by preset name|
|[**updateTemplate**](#updatetemplate) | **PUT** /api/admin/menu-templates/{id} | Update existing template|

# **createTemplate**
> MenuTemplateDTO createTemplate(menuTemplateCreateRequest)

Create a new menu template

### Example

```typescript
import {
    AdminMenuTemplatesApi,
    Configuration,
    MenuTemplateCreateRequest
} from 'api-client';

const configuration = new Configuration();
const apiInstance = new AdminMenuTemplatesApi(configuration);

let menuTemplateCreateRequest: MenuTemplateCreateRequest; //

const { status, data } = await apiInstance.createTemplate(
    menuTemplateCreateRequest
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **menuTemplateCreateRequest** | **MenuTemplateCreateRequest**|  | |


### Return type

**MenuTemplateDTO**

### Authorization

[Bearer Authentication](../README.md#Bearer Authentication)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: */*


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | Template created successfully |  -  |
|**400** | Invalid request data |  -  |
|**403** | Access denied - Menu admin role required |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **deleteTemplate**
> deleteTemplate()

Delete a menu template by ID

### Example

```typescript
import {
    AdminMenuTemplatesApi,
    Configuration
} from 'api-client';

const configuration = new Configuration();
const apiInstance = new AdminMenuTemplatesApi(configuration);

let id: number; // (default to undefined)

const { status, data } = await apiInstance.deleteTemplate(
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
|**204** | Template deleted successfully |  -  |
|**403** | Access denied - Menu admin role required |  -  |
|**404** | Template not found |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **deleteTemplatesByPresetName**
> deleteTemplatesByPresetName()

Delete all templates for a specific preset name

### Example

```typescript
import {
    AdminMenuTemplatesApi,
    Configuration
} from 'api-client';

const configuration = new Configuration();
const apiInstance = new AdminMenuTemplatesApi(configuration);

let presetName: string; // (default to undefined)

const { status, data } = await apiInstance.deleteTemplatesByPresetName(
    presetName
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **presetName** | [**string**] |  | defaults to undefined|


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
|**204** | Templates deleted successfully |  -  |
|**403** | Access denied - Menu admin role required |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **getDistinctPresetNames**
> Array<PresetNameDTO> getDistinctPresetNames()

Retrieve all distinct preset names with metadata

### Example

```typescript
import {
    AdminMenuTemplatesApi,
    Configuration
} from 'api-client';

const configuration = new Configuration();
const apiInstance = new AdminMenuTemplatesApi(configuration);

const { status, data } = await apiInstance.getDistinctPresetNames();
```

### Parameters
This endpoint does not have any parameters.


### Return type

**Array<PresetNameDTO>**

### Authorization

[Bearer Authentication](../README.md#Bearer Authentication)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: */*


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | Preset names retrieved successfully |  -  |
|**403** | Access denied - Menu admin role required |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **getTemplatesByPresetName**
> Array<MenuTemplateDTO> getTemplatesByPresetName()

Retrieve all templates for a specific preset name

### Example

```typescript
import {
    AdminMenuTemplatesApi,
    Configuration
} from 'api-client';

const configuration = new Configuration();
const apiInstance = new AdminMenuTemplatesApi(configuration);

let presetName: string; // (default to undefined)

const { status, data } = await apiInstance.getTemplatesByPresetName(
    presetName
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **presetName** | [**string**] |  | defaults to undefined|


### Return type

**Array<MenuTemplateDTO>**

### Authorization

[Bearer Authentication](../README.md#Bearer Authentication)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: */*


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | Templates retrieved successfully |  -  |
|**403** | Access denied - Menu admin role required |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **updateTemplate**
> MenuTemplateDTO updateTemplate(menuTemplateUpdateRequest)

Update an existing menu template

### Example

```typescript
import {
    AdminMenuTemplatesApi,
    Configuration,
    MenuTemplateUpdateRequest
} from 'api-client';

const configuration = new Configuration();
const apiInstance = new AdminMenuTemplatesApi(configuration);

let id: number; // (default to undefined)
let menuTemplateUpdateRequest: MenuTemplateUpdateRequest; //

const { status, data } = await apiInstance.updateTemplate(
    id,
    menuTemplateUpdateRequest
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **menuTemplateUpdateRequest** | **MenuTemplateUpdateRequest**|  | |
| **id** | [**number**] |  | defaults to undefined|


### Return type

**MenuTemplateDTO**

### Authorization

[Bearer Authentication](../README.md#Bearer Authentication)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: */*


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | Template updated successfully |  -  |
|**400** | Invalid request data |  -  |
|**403** | Access denied - Menu admin role required |  -  |
|**404** | Template not found |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

