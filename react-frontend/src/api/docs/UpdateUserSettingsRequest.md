# UpdateUserSettingsRequest


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**pushEnabled** | **boolean** |  | [optional] [default to undefined]
**emailEnabled** | **boolean** |  | [optional] [default to undefined]
**orderUpdates** | **boolean** |  | [optional] [default to undefined]
**menuUpdates** | **boolean** |  | [optional] [default to undefined]
**accountUpdates** | **boolean** |  | [optional] [default to undefined]
**promotional** | **boolean** |  | [optional] [default to undefined]
**systemAnnouncements** | **boolean** |  | [optional] [default to undefined]
**quietHoursStart** | **string** |  | [optional] [default to undefined]
**quietHoursEnd** | **string** |  | [optional] [default to undefined]

## Example

```typescript
import { UpdateUserSettingsRequest } from 'api-client';

const instance: UpdateUserSettingsRequest = {
    pushEnabled,
    emailEnabled,
    orderUpdates,
    menuUpdates,
    accountUpdates,
    promotional,
    systemAnnouncements,
    quietHoursStart,
    quietHoursEnd,
};
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
