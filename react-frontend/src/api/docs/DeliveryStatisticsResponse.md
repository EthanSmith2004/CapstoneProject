# DeliveryStatisticsResponse

Delivery statistics for the dashboard

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**totalItemsToday** | **number** | Total items to deliver today | [optional] [default to undefined]
**itemsInDelivery** | **number** | Items currently in delivery status | [optional] [default to undefined]
**itemsDelivered** | **number** | Items successfully delivered today | [optional] [default to undefined]
**completionPercentage** | **number** | Completion percentage | [optional] [default to undefined]

## Example

```typescript
import { DeliveryStatisticsResponse } from 'api-client';

const instance: DeliveryStatisticsResponse = {
    totalItemsToday,
    itemsInDelivery,
    itemsDelivered,
    completionPercentage,
};
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
