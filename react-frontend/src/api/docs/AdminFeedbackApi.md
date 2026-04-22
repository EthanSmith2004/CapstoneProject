# AdminFeedbackApi

All URIs are relative to *http://localhost:8080*

|Method | HTTP request | Description|
|------------- | ------------- | -------------|
|[**getAllFeedback**](#getallfeedback) | **GET** /api/admin/feedback | Get and overview|
|[**getFeedbackFiltered**](#getfeedbackfiltered) | **GET** /api/admin/feedback/filtered | Get filtered feedback|
|[**getFeedbackStatistics**](#getfeedbackstatistics) | **GET** /api/admin/feedback/statistics | Get feedback statistics for a period|
|[**getFeedbackStatisticsByItem**](#getfeedbackstatisticsbyitem) | **GET** /api/admin/feedback/statistics/by-item | Get feedback statistics by menu item|
|[**setFeedbackSentiment**](#setfeedbacksentiment) | **POST** /api/admin/feedback/sentiment | Analyze feedback sentiment|

# **getAllFeedback**
> Array<AdminFeedbackDTO> getAllFeedback()

Retrieve feedback data

### Example

```typescript
import {
    AdminFeedbackApi,
    Configuration
} from 'api-client';

const configuration = new Configuration();
const apiInstance = new AdminFeedbackApi(configuration);

const { status, data } = await apiInstance.getAllFeedback();
```

### Parameters
This endpoint does not have any parameters.


### Return type

**Array<AdminFeedbackDTO>**

### Authorization

[Bearer Authentication](../README.md#Bearer Authentication)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: */*


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | Feedback retrieved successfully |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **getFeedbackFiltered**
> Array<AdminFeedbackDTO> getFeedbackFiltered()

Retrieve feedback filtered by period, rating range, and menu item name

### Example

```typescript
import {
    AdminFeedbackApi,
    Configuration
} from 'api-client';

const configuration = new Configuration();
const apiInstance = new AdminFeedbackApi(configuration);

let start: string; // (default to undefined)
let end: string; // (default to undefined)
let minRating: number; // (optional) (default to undefined)
let maxRating: number; // (optional) (default to undefined)
let menuItemName: string; // (optional) (default to undefined)

const { status, data } = await apiInstance.getFeedbackFiltered(
    start,
    end,
    minRating,
    maxRating,
    menuItemName
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **start** | [**string**] |  | defaults to undefined|
| **end** | [**string**] |  | defaults to undefined|
| **minRating** | [**number**] |  | (optional) defaults to undefined|
| **maxRating** | [**number**] |  | (optional) defaults to undefined|
| **menuItemName** | [**string**] |  | (optional) defaults to undefined|


### Return type

**Array<AdminFeedbackDTO>**

### Authorization

[Bearer Authentication](../README.md#Bearer Authentication)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: */*


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | Feedback retrieved successfully |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **getFeedbackStatistics**
> FeedbackPeriodStatistics getFeedbackStatistics()

Retrieve aggregated feedback statistics for a specified time period

### Example

```typescript
import {
    AdminFeedbackApi,
    Configuration
} from 'api-client';

const configuration = new Configuration();
const apiInstance = new AdminFeedbackApi(configuration);

let start: string; // (default to undefined)
let end: string; // (default to undefined)
let menuItemId: number; // (optional) (default to undefined)

const { status, data } = await apiInstance.getFeedbackStatistics(
    start,
    end,
    menuItemId
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **start** | [**string**] |  | defaults to undefined|
| **end** | [**string**] |  | defaults to undefined|
| **menuItemId** | [**number**] |  | (optional) defaults to undefined|


### Return type

**FeedbackPeriodStatistics**

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

# **getFeedbackStatisticsByItem**
> Array<FeedbackItemStatistics> getFeedbackStatisticsByItem()

Retrieve feedback statistics grouped by menu item

### Example

```typescript
import {
    AdminFeedbackApi,
    Configuration
} from 'api-client';

const configuration = new Configuration();
const apiInstance = new AdminFeedbackApi(configuration);

let start: string; // (default to undefined)
let end: string; // (default to undefined)

const { status, data } = await apiInstance.getFeedbackStatisticsByItem(
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

**Array<FeedbackItemStatistics>**

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

# **setFeedbackSentiment**
> setFeedbackSentiment(feedbackSentimentRequest)

Perform sentiment analysis on feedback data

### Example

```typescript
import {
    AdminFeedbackApi,
    Configuration
} from 'api-client';

const configuration = new Configuration();
const apiInstance = new AdminFeedbackApi(configuration);

let feedbackSentimentRequest: Array<FeedbackSentimentRequest>; //

const { status, data } = await apiInstance.setFeedbackSentiment(
    feedbackSentimentRequest
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **feedbackSentimentRequest** | **Array<FeedbackSentimentRequest>**|  | |


### Return type

void (empty response body)

### Authorization

[Bearer Authentication](../README.md#Bearer Authentication)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: Not defined


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | Sentiment analysis completed successfully |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

