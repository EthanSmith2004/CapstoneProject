# ReportStatusDTO


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**id** | **number** |  | [optional] [default to undefined]
**reportType** | **string** |  | [optional] [default to undefined]
**status** | **string** |  | [optional] [default to undefined]
**requestedBy** | [**CompactUserDTO**](CompactUserDTO.md) |  | [optional] [default to undefined]
**dateFrom** | **string** |  | [optional] [default to undefined]
**dateTo** | **string** |  | [optional] [default to undefined]
**parameters** | **{ [key: string]: any; }** |  | [optional] [default to undefined]
**fileUrl** | **string** |  | [optional] [default to undefined]
**fileName** | **string** |  | [optional] [default to undefined]
**fileSize** | **number** |  | [optional] [default to undefined]
**mimeType** | **string** |  | [optional] [default to undefined]
**errorMessage** | **string** |  | [optional] [default to undefined]
**requestedAt** | **string** |  | [optional] [default to undefined]
**completedAt** | **string** |  | [optional] [default to undefined]

## Example

```typescript
import { ReportStatusDTO } from 'api-client';

const instance: ReportStatusDTO = {
    id,
    reportType,
    status,
    requestedBy,
    dateFrom,
    dateTo,
    parameters,
    fileUrl,
    fileName,
    fileSize,
    mimeType,
    errorMessage,
    requestedAt,
    completedAt,
};
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
