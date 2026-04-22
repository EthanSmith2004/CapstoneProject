# DeliveryReportData


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**startDate** | **string** |  | [optional] [default to undefined]
**endDate** | **string** |  | [optional] [default to undefined]
**generatedAt** | **string** |  | [optional] [default to undefined]
**deliveries** | [**Array&lt;DeliveryReportItem&gt;**](DeliveryReportItem.md) |  | [optional] [default to undefined]
**totalDeliveries** | **number** |  | [optional] [default to undefined]
**uniqueCampuses** | **number** |  | [optional] [default to undefined]
**uniqueResidences** | **number** |  | [optional] [default to undefined]

## Example

```typescript
import { DeliveryReportData } from 'api-client';

const instance: DeliveryReportData = {
    startDate,
    endDate,
    generatedAt,
    deliveries,
    totalDeliveries,
    uniqueCampuses,
    uniqueResidences,
};
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
