# UserSettingsDTO


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**id** | **number** |  | [optional] [default to undefined]
**user** | [**CompactUserDTO**](CompactUserDTO.md) |  | [optional] [default to undefined]
**pushEnabled** | **boolean** |  | [optional] [default to undefined]
**emailEnabled** | **boolean** |  | [optional] [default to undefined]
**orderUpdates** | **boolean** |  | [optional] [default to undefined]
**menuUpdates** | **boolean** |  | [optional] [default to undefined]
**accountUpdates** | **boolean** |  | [optional] [default to undefined]
**promotional** | **boolean** |  | [optional] [default to undefined]
**systemAnnouncements** | **boolean** |  | [optional] [default to undefined]
**quietHoursStart** | **string** |  | [optional] [default to undefined]
**quietHoursEnd** | **string** |  | [optional] [default to undefined]
**createdAt** | **string** |  | [optional] [default to undefined]
**updatedAt** | **string** |  | [optional] [default to undefined]

## Example

```typescript
import { UserSettingsDTO } from 'api-client';

const instance: UserSettingsDTO = {
    id,
    user,
    pushEnabled,
    emailEnabled,
    orderUpdates,
    menuUpdates,
    accountUpdates,
    promotional,
    systemAnnouncements,
    quietHoursStart,
    quietHoursEnd,
    createdAt,
    updatedAt,
};
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
