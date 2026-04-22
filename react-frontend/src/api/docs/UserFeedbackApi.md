# UserFeedbackApi

All URIs are relative to *http://localhost:8080*

|Method | HTTP request | Description|
|------------- | ------------- | -------------|
|[**placeFeedback**](#placefeedback) | **POST** /api/user/feedback | Submit feedback|

# **placeFeedback**
> FeedbackDTO placeFeedback(feedbackRequest)

Submit feedback for a menu item in an order

### Example

```typescript
import {
    UserFeedbackApi,
    Configuration,
    FeedbackRequest
} from 'api-client';

const configuration = new Configuration();
const apiInstance = new UserFeedbackApi(configuration);

let feedbackRequest: FeedbackRequest; //

const { status, data } = await apiInstance.placeFeedback(
    feedbackRequest
);
```

### Parameters

|Name | Type | Description  | Notes|
|------------- | ------------- | ------------- | -------------|
| **feedbackRequest** | **FeedbackRequest**|  | |


### Return type

**FeedbackDTO**

### Authorization

[Bearer Authentication](../README.md#Bearer Authentication)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: */*


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
|**200** | Feedback submitted successfully |  -  |
|**400** | Invalid feedback data |  -  |
|**403** | Access denied |  -  |
|**404** | User or order not found |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

