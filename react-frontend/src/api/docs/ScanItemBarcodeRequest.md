# ScanItemBarcodeRequest

Request to scan an item barcode for delivery confirmation

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**userBarcode** | **string** | The user\&#39;s barcode/credential number | [default to undefined]
**orderItemId** | **number** | The order item ID to mark as delivered | [default to undefined]

## Example

```typescript
import { ScanItemBarcodeRequest } from 'api-client';

const instance: ScanItemBarcodeRequest = {
    userBarcode,
    orderItemId,
};
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
