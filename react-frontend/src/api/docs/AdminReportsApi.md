# AdminReportsApi

All URIs are relative to *http://localhost:8080*

|Method | HTTP request | Description|
|------------- | ------------- | -------------|
|[**createReport**](#createreport) | **POST** /api/admin/reports | Create new report|
|[**getReportStatus**](#getreportstatus) | **GET** /api/admin/reports/{reportId}/status | Get report status|

# **createReport**
> ReportStatusDTO createReport(adminCreateReportRequest)

Request generation of a new report

### Example

```typescript
import {
    AdminReportsApi,
    Configuration,
    AdminCreateReportRequest
} from 'api-client';

const configuration = new Configuration();
const apiInstance = new AdminReportsApi(configuration);

let adminCreateReportRequest: AdminCreateReportRequest; //

const { status, data } = await apiInstance.createReport(
    adminCreateReportRequest
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **adminCreateReportRequest** | **AdminCreateReportRequest**|  | |


### Return type

**ReportStatusDTO**

### Authorization

[Bearer Authentication](../README.md#Bearer Authentication)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: */*


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | Report creation request submitted successfully |  -  |
|**400** | Invalid report request |  -  |
|**403** | Access denied - Admin role required |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **getReportStatus**
> ReportStatusDTO getReportStatus()

Check the status of a report generation request

### Example

```typescript
import {
    AdminReportsApi,
    Configuration
} from 'api-client';

const configuration = new Configuration();
const apiInstance = new AdminReportsApi(configuration);

let reportId: number; //Report ID (default to undefined)

const { status, data } = await apiInstance.getReportStatus(
    reportId
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **reportId** | [**number**] | Report ID | defaults to undefined|


### Return type

**ReportStatusDTO**

### Authorization

[Bearer Authentication](../README.md#Bearer Authentication)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: */*


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | Report status retrieved successfully |  -  |
|**403** | Access denied - Admin role required |  -  |
|**404** | Report not found |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

