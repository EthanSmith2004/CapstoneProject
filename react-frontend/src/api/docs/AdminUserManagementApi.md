# AdminUserManagementApi

All URIs are relative to *http://localhost:8080*

|Method | HTTP request | Description|
|------------- | ------------- | -------------|
|[**createUser**](#createuser) | **POST** /api/admin/users | Create user|
|[**deleteUser**](#deleteuser) | **DELETE** /api/admin/users/{id} | Delete user|
|[**getAllUsers**](#getallusers) | **GET** /api/admin/users | Get all users|
|[**getUserById**](#getuserbyid) | **GET** /api/admin/users/{id} | Get user by ID|
|[**updateUser**](#updateuser) | **PUT** /api/admin/users/{id} | Update user|

# **createUser**
> UserEntity createUser(adminCreateUserRequest)

Create a new user with admin privileges

### Example

```typescript
import {
    AdminUserManagementApi,
    Configuration,
    AdminCreateUserRequest
} from 'api-client';

const configuration = new Configuration();
const apiInstance = new AdminUserManagementApi(configuration);

let adminCreateUserRequest: AdminCreateUserRequest; //

const { status, data } = await apiInstance.createUser(
    adminCreateUserRequest
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **adminCreateUserRequest** | **AdminCreateUserRequest**|  | |


### Return type

**UserEntity**

### Authorization

[Bearer Authentication](../README.md#Bearer Authentication)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: */*


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**201** | User created successfully |  -  |
|**400** | Invalid input data |  -  |
|**403** | Access denied - User admin role required |  -  |
|**409** | Email already exists |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **deleteUser**
> deleteUser()

Delete a user by their ID

### Example

```typescript
import {
    AdminUserManagementApi,
    Configuration
} from 'api-client';

const configuration = new Configuration();
const apiInstance = new AdminUserManagementApi(configuration);

let id: number; //User ID (default to undefined)

const { status, data } = await apiInstance.deleteUser(
    id
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **id** | [**number**] | User ID | defaults to undefined|


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
|**200** | User deleted successfully |  -  |
|**403** | Access denied - User admin role required |  -  |
|**404** | User not found |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **getAllUsers**
> Array<UserEntity> getAllUsers()

Retrieve a list of all users in the system

### Example

```typescript
import {
    AdminUserManagementApi,
    Configuration
} from 'api-client';

const configuration = new Configuration();
const apiInstance = new AdminUserManagementApi(configuration);

const { status, data } = await apiInstance.getAllUsers();
```

### Parameters
This endpoint does not have any parameters.


### Return type

**Array<UserEntity>**

### Authorization

[Bearer Authentication](../README.md#Bearer Authentication)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: */*


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | Users retrieved successfully |  -  |
|**403** | Access denied - User admin role required |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **getUserById**
> UserEntity getUserById()

Retrieve a specific user by their ID

### Example

```typescript
import {
    AdminUserManagementApi,
    Configuration
} from 'api-client';

const configuration = new Configuration();
const apiInstance = new AdminUserManagementApi(configuration);

let id: number; //User ID (default to undefined)

const { status, data } = await apiInstance.getUserById(
    id
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **id** | [**number**] | User ID | defaults to undefined|


### Return type

**UserEntity**

### Authorization

[Bearer Authentication](../README.md#Bearer Authentication)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: */*


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | User found successfully |  -  |
|**403** | Access denied - User admin role required |  -  |
|**404** | User not found |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **updateUser**
> UserEntity updateUser(adminUpdateUserRequest)

Update a user by their ID with admin privileges

### Example

```typescript
import {
    AdminUserManagementApi,
    Configuration,
    AdminUpdateUserRequest
} from 'api-client';

const configuration = new Configuration();
const apiInstance = new AdminUserManagementApi(configuration);

let id: number; //User ID (default to undefined)
let adminUpdateUserRequest: AdminUpdateUserRequest; //

const { status, data } = await apiInstance.updateUser(
    id,
    adminUpdateUserRequest
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **adminUpdateUserRequest** | **AdminUpdateUserRequest**|  | |
| **id** | [**number**] | User ID | defaults to undefined|


### Return type

**UserEntity**

### Authorization

[Bearer Authentication](../README.md#Bearer Authentication)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: */*


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | User updated successfully |  -  |
|**400** | Invalid input data |  -  |
|**403** | Access denied - User admin role required |  -  |
|**404** | User not found |  -  |
|**409** | Username or email already exists |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

