## api-client@1.0.0

This generator creates TypeScript/JavaScript client that utilizes [axios](https://github.com/axios/axios). The generated Node module can be used in the following environments:

Environment
* Node.js
* Webpack
* Browserify

Language level
* ES5 - you must have a Promises/A+ library installed
* ES6

Module system
* CommonJS
* ES6 module system

It can be used in both TypeScript and JavaScript. In TypeScript, the definition will be automatically resolved via `package.json`. ([Reference](https://www.typescriptlang.org/docs/handbook/declaration-files/consumption.html))

### Building

To build and compile the typescript sources to javascript use:
```
npm install
npm run build
```

### Publishing

First build the package then run `npm publish`

### Consuming

navigate to the folder of your consuming project and run one of the following commands.

_published:_

```
npm install api-client@1.0.0 --save
```

_unPublished (not recommended):_

```
npm install PATH_TO_GENERATED_PACKAGE --save
```

### Documentation for API Endpoints

All URIs are relative to *http://localhost:8080*

Class | Method | HTTP request | Description
------------ | ------------- | ------------- | -------------
*AdminAllergyManagementApi* | [**create2**](docs/AdminAllergyManagementApi.md#create2) | **POST** /api/admin/allergies | Create a new resource
*AdminAllergyManagementApi* | [**delete2**](docs/AdminAllergyManagementApi.md#delete2) | **DELETE** /api/admin/allergies/{id} | Delete a resource
*AdminAllergyManagementApi* | [**findAll2**](docs/AdminAllergyManagementApi.md#findall2) | **GET** /api/admin/allergies | Find all resources
*AdminAllergyManagementApi* | [**findById2**](docs/AdminAllergyManagementApi.md#findbyid2) | **GET** /api/admin/allergies/{id} | Find a resource by ID
*AdminAllergyManagementApi* | [**update2**](docs/AdminAllergyManagementApi.md#update2) | **PUT** /api/admin/allergies/{id} | Update a resource
*AdminAuditApi* | [**getRecentLogins**](docs/AdminAuditApi.md#getrecentlogins) | **GET** /api/admin/audit/logins/recent | Get recent user logins
*AdminAuditApi* | [**getTransactionAuditPaginated**](docs/AdminAuditApi.md#gettransactionauditpaginated) | **GET** /api/admin/audit/transactions | Get paginated transaction audit
*AdminAuditApi* | [**getTransactionDetailsForAudit**](docs/AdminAuditApi.md#gettransactiondetailsforaudit) | **GET** /api/admin/audit/transactions/{auditId}/details | Get transaction details for audit
*AdminAuditApi* | [**getUserEventLogPaginated**](docs/AdminAuditApi.md#getusereventlogpaginated) | **GET** /api/admin/audit/user-events | Get paginated user events
*AdminCampusManagementApi* | [**create1**](docs/AdminCampusManagementApi.md#create1) | **POST** /api/admin/campuses | Create a new resource
*AdminCampusManagementApi* | [**delete1**](docs/AdminCampusManagementApi.md#delete1) | **DELETE** /api/admin/campuses/{id} | Delete a resource
*AdminCampusManagementApi* | [**findAll1**](docs/AdminCampusManagementApi.md#findall1) | **GET** /api/admin/campuses | Find all resources
*AdminCampusManagementApi* | [**findById1**](docs/AdminCampusManagementApi.md#findbyid1) | **GET** /api/admin/campuses/{id} | Find a resource by ID
*AdminCampusManagementApi* | [**update1**](docs/AdminCampusManagementApi.md#update1) | **PUT** /api/admin/campuses/{id} | Update a resource
*AdminFeedbackApi* | [**getAllFeedback**](docs/AdminFeedbackApi.md#getallfeedback) | **GET** /api/admin/feedback | Get and overview
*AdminFeedbackApi* | [**getFeedbackFiltered**](docs/AdminFeedbackApi.md#getfeedbackfiltered) | **GET** /api/admin/feedback/filtered | Get filtered feedback
*AdminFeedbackApi* | [**getFeedbackStatistics**](docs/AdminFeedbackApi.md#getfeedbackstatistics) | **GET** /api/admin/feedback/statistics | Get feedback statistics for a period
*AdminFeedbackApi* | [**getFeedbackStatisticsByItem**](docs/AdminFeedbackApi.md#getfeedbackstatisticsbyitem) | **GET** /api/admin/feedback/statistics/by-item | Get feedback statistics by menu item
*AdminFeedbackApi* | [**setFeedbackSentiment**](docs/AdminFeedbackApi.md#setfeedbacksentiment) | **POST** /api/admin/feedback/sentiment | Analyze feedback sentiment
*AdminFinanceApi* | [**bulkLoadCredit**](docs/AdminFinanceApi.md#bulkloadcredit) | **POST** /api/admin/finance/bulk-load | Bulk load credit
*AdminFinanceApi* | [**findTransactions**](docs/AdminFinanceApi.md#findtransactions) | **GET** /api/admin/finance/transactions | Search transactions
*AdminFinanceApi* | [**findUsers**](docs/AdminFinanceApi.md#findusers) | **GET** /api/admin/finance/users | Search users with pagination
*AdminFinanceApi* | [**findUsersSearch**](docs/AdminFinanceApi.md#finduserssearch) | **GET** /api/admin/finance/users/search | Search users
*AdminFinanceApi* | [**getStatistics**](docs/AdminFinanceApi.md#getstatistics) | **GET** /api/admin/finance/statistics | Get and overview
*AdminFinanceApi* | [**getUserDetail**](docs/AdminFinanceApi.md#getuserdetail) | **GET** /api/admin/finance/users/{userId} | Get user financial details
*AdminFinanceApi* | [**loadUserCredit**](docs/AdminFinanceApi.md#loadusercredit) | **POST** /api/admin/finance/load | Load user credit
*AdminFinanceApi* | [**overview**](docs/AdminFinanceApi.md#overview) | **GET** /api/admin/finance/overview | Get and overview
*AdminMenuApi* | [**createMenuItem**](docs/AdminMenuApi.md#createmenuitem) | **POST** /api/admin/menu | Create menu item
*AdminMenuApi* | [**deleteMenuItem**](docs/AdminMenuApi.md#deletemenuitem) | **DELETE** /api/admin/menu/{id} | Delete menu item
*AdminMenuApi* | [**getCurrentMenuItems**](docs/AdminMenuApi.md#getcurrentmenuitems) | **GET** /api/admin/menu/current | Get current menu items
*AdminMenuApi* | [**getDraftMenuItems**](docs/AdminMenuApi.md#getdraftmenuitems) | **GET** /api/admin/menu/items | Get draft menu items
*AdminMenuApi* | [**getHistoricMenuItems**](docs/AdminMenuApi.md#gethistoricmenuitems) | **GET** /api/admin/menu/historic | Get historic menu items
*AdminMenuApi* | [**getMenuItemStatistics**](docs/AdminMenuApi.md#getmenuitemstatistics) | **GET** /api/admin/menu/stats | Get menu item statistics
*AdminMenuApi* | [**getMenuPaginated**](docs/AdminMenuApi.md#getmenupaginated) | **GET** /api/admin/menu | Get paginated menu items (DEPRECATED)
*AdminMenuApi* | [**queueMenuItem**](docs/AdminMenuApi.md#queuemenuitem) | **POST** /api/admin/menu/queue | Queue a menu item
*AdminMenuApi* | [**searchMenuPaginated**](docs/AdminMenuApi.md#searchmenupaginated) | **GET** /api/admin/menu/search | Search menu items
*AdminMenuApi* | [**updateMenuItem**](docs/AdminMenuApi.md#updatemenuitem) | **PUT** /api/admin/menu/{id} | Update menu item
*AdminMenuTemplatesApi* | [**createTemplate**](docs/AdminMenuTemplatesApi.md#createtemplate) | **POST** /api/admin/menu-templates | Create new template
*AdminMenuTemplatesApi* | [**deleteTemplate**](docs/AdminMenuTemplatesApi.md#deletetemplate) | **DELETE** /api/admin/menu-templates/{id} | Delete template
*AdminMenuTemplatesApi* | [**deleteTemplatesByPresetName**](docs/AdminMenuTemplatesApi.md#deletetemplatesbypresetname) | **DELETE** /api/admin/menu-templates/preset/{presetName} | Batch delete templates by preset
*AdminMenuTemplatesApi* | [**getDistinctPresetNames**](docs/AdminMenuTemplatesApi.md#getdistinctpresetnames) | **GET** /api/admin/menu-templates/presets | Get distinct preset names
*AdminMenuTemplatesApi* | [**getTemplatesByPresetName**](docs/AdminMenuTemplatesApi.md#gettemplatesbypresetname) | **GET** /api/admin/menu-templates/preset/{presetName} | Get templates by preset name
*AdminMenuTemplatesApi* | [**updateTemplate**](docs/AdminMenuTemplatesApi.md#updatetemplate) | **PUT** /api/admin/menu-templates/{id} | Update existing template
*AdminNotificationsApi* | [**sendToAll**](docs/AdminNotificationsApi.md#sendtoall) | **POST** /api/admin/notifications/send-to-all | Send notification to all users
*AdminNotificationsApi* | [**sendToUser**](docs/AdminNotificationsApi.md#sendtouser) | **POST** /api/admin/notifications/send-to-user/{userId} | Send notification to a specific user
*AdminOrderManagementApi* | [**bulkUpdateOrderStatuses**](docs/AdminOrderManagementApi.md#bulkupdateorderstatuses) | **PUT** /api/admin/orders/bulk-status | Bulk update order statuses
*AdminOrderManagementApi* | [**getAllOrders**](docs/AdminOrderManagementApi.md#getallorders) | **GET** /api/admin/orders | Get all orders
*AdminOrderManagementApi* | [**getAllOrdersItems**](docs/AdminOrderManagementApi.md#getallordersitems) | **GET** /api/admin/orders/items | Get all order items
*AdminOrderManagementApi* | [**getDeliveryReportData**](docs/AdminOrderManagementApi.md#getdeliveryreportdata) | **GET** /api/admin/orders/report/delivery | Get delivery report data
*AdminOrderManagementApi* | [**getDeliveryReportPeriod**](docs/AdminOrderManagementApi.md#getdeliveryreportperiod) | **GET** /api/admin/orders/report/delivery/period | Get delivery report data for period
*AdminOrderManagementApi* | [**getKitchenReportData**](docs/AdminOrderManagementApi.md#getkitchenreportdata) | **GET** /api/admin/orders/report/kitchen | Get kitchen report data
*AdminOrderManagementApi* | [**getKitchenReportPeriod**](docs/AdminOrderManagementApi.md#getkitchenreportperiod) | **GET** /api/admin/orders/report/kitchen/period | Get kitchen report data for period
*AdminOrderManagementApi* | [**getOrderStatistics**](docs/AdminOrderManagementApi.md#getorderstatistics) | **GET** /api/admin/orders/statistics | Get Order statistics
*AdminOrderManagementApi* | [**getOrderStatisticsPeriod**](docs/AdminOrderManagementApi.md#getorderstatisticsperiod) | **GET** /api/admin/orders/statistics/detail | Get Order statistics
*AdminOrderManagementApi* | [**updateOrderItemStatus**](docs/AdminOrderManagementApi.md#updateorderitemstatus) | **PUT** /api/admin/order-items/{id}/status | Update order item status
*AdminOrderManagementApi* | [**updateOrderStatus**](docs/AdminOrderManagementApi.md#updateorderstatus) | **PUT** /api/admin/orders/{id}/status | Update order status
*AdminReportsApi* | [**createReport**](docs/AdminReportsApi.md#createreport) | **POST** /api/admin/reports | Create new report
*AdminReportsApi* | [**getReportStatus**](docs/AdminReportsApi.md#getreportstatus) | **GET** /api/admin/reports/{reportId}/status | Get report status
*AdminResidenceManagementApi* | [**_delete**](docs/AdminResidenceManagementApi.md#_delete) | **DELETE** /api/admin/residences/{id} | Delete a resource
*AdminResidenceManagementApi* | [**create**](docs/AdminResidenceManagementApi.md#create) | **POST** /api/admin/residences | Create a new resource
*AdminResidenceManagementApi* | [**findAll**](docs/AdminResidenceManagementApi.md#findall) | **GET** /api/admin/residences | Find all resources
*AdminResidenceManagementApi* | [**findById**](docs/AdminResidenceManagementApi.md#findbyid) | **GET** /api/admin/residences/{id} | Find a resource by ID
*AdminResidenceManagementApi* | [**update**](docs/AdminResidenceManagementApi.md#update) | **PUT** /api/admin/residences/{id} | Update a resource
*AdminUserManagementApi* | [**createUser**](docs/AdminUserManagementApi.md#createuser) | **POST** /api/admin/users | Create user
*AdminUserManagementApi* | [**deleteUser**](docs/AdminUserManagementApi.md#deleteuser) | **DELETE** /api/admin/users/{id} | Delete user
*AdminUserManagementApi* | [**getAllUsers**](docs/AdminUserManagementApi.md#getallusers) | **GET** /api/admin/users | Get all users
*AdminUserManagementApi* | [**getUserById**](docs/AdminUserManagementApi.md#getuserbyid) | **GET** /api/admin/users/{id} | Get user by ID
*AdminUserManagementApi* | [**updateUser**](docs/AdminUserManagementApi.md#updateuser) | **PUT** /api/admin/users/{id} | Update user
*AuthenticationApi* | [**authenticateUser**](docs/AuthenticationApi.md#authenticateuser) | **POST** /api/auth/login | Authenticate user
*AuthenticationApi* | [**logout**](docs/AuthenticationApi.md#logout) | **POST** /api/auth/logout | Logout user
*AuthenticationApi* | [**refreshToken**](docs/AuthenticationApi.md#refreshtoken) | **POST** /api/auth/refresh | Refresh access token
*AuthenticationApi* | [**registerUser**](docs/AuthenticationApi.md#registeruser) | **POST** /api/auth/register | Register new user
*DeliveryAdminApi* | [**getAllPendingDeliveries**](docs/DeliveryAdminApi.md#getallpendingdeliveries) | **GET** /api/delivery/pending-deliveries | Get all pending deliveries
*DeliveryAdminApi* | [**getDeliveryStatistics**](docs/DeliveryAdminApi.md#getdeliverystatistics) | **GET** /api/delivery/statistics | Get delivery statistics
*DeliveryAdminApi* | [**scanItemForDelivery**](docs/DeliveryAdminApi.md#scanitemfordelivery) | **POST** /api/delivery/scan-item | Scan item for delivery confirmation
*DeliveryAdminApi* | [**scanUserBarcode**](docs/DeliveryAdminApi.md#scanuserbarcode) | **POST** /api/delivery/scan-user | Scan user barcode
*ListApi* | [**getAllergyNames**](docs/ListApi.md#getallergynames) | **GET** /api/list/allergies | Get all allergy names
*ListApi* | [**getCampusNames**](docs/ListApi.md#getcampusnames) | **GET** /api/list/campuses | Get all campus names
*ListApi* | [**getResidenceNames**](docs/ListApi.md#getresidencenames) | **GET** /api/list/residences | Get all residence names
*NotificationsApi* | [**getNotifications**](docs/NotificationsApi.md#getnotifications) | **GET** /api/user/notifications | Get all notifications
*NotificationsApi* | [**getUnreadNotificationCount**](docs/NotificationsApi.md#getunreadnotificationcount) | **GET** /api/user/notifications/unread/count | Get unread notification count
*NotificationsApi* | [**getUnreadNotifications**](docs/NotificationsApi.md#getunreadnotifications) | **GET** /api/user/notifications/unread | Get unread notifications
*NotificationsApi* | [**markAllNotificationsAsRead**](docs/NotificationsApi.md#markallnotificationsasread) | **PATCH** /api/user/notifications/read-all | Mark all notifications as read
*NotificationsApi* | [**markNotificationAsRead**](docs/NotificationsApi.md#marknotificationasread) | **PATCH** /api/user/notifications/{notificationId}/read | Mark notification as read
*NotificationsApi* | [**publicKey**](docs/NotificationsApi.md#publickey) | **GET** /api/user/notifications/publicKey | Get public key
*NotificationsApi* | [**subscribe**](docs/NotificationsApi.md#subscribe) | **POST** /api/user/notifications/subscribe | Subscribe to push notifications
*NotificationsApi* | [**unsubscribe**](docs/NotificationsApi.md#unsubscribe) | **POST** /api/user/notifications/unsubscribe | Unsubscribe from push notifications
*UserAccountApi* | [**getAccount**](docs/UserAccountApi.md#getaccount) | **GET** /api/user/account | Get account details
*UserAccountApi* | [**getTransactionsPaginated**](docs/UserAccountApi.md#gettransactionspaginated) | **GET** /api/user/transactions | Get paginated transactions
*UserFeedbackApi* | [**placeFeedback**](docs/UserFeedbackApi.md#placefeedback) | **POST** /api/user/feedback | Submit feedback
*UserMenuApi* | [**getMenu**](docs/UserMenuApi.md#getmenu) | **GET** /api/user/menu | Get menu
*UserMenuApi* | [**getMenuItemDetail**](docs/UserMenuApi.md#getmenuitemdetail) | **GET** /api/user/menu/{itemId} | Get menu item detail
*UserOrdersApi* | [**cancelOrderItem**](docs/UserOrdersApi.md#cancelorderitem) | **DELETE** /api/user/orders/{orderItemId}/cancel | Cancel an order item
*UserOrdersApi* | [**createOrder**](docs/UserOrdersApi.md#createorder) | **POST** /api/user/orders | Create a new order
*UserOrdersApi* | [**getUserOrderHistory**](docs/UserOrdersApi.md#getuserorderhistory) | **GET** /api/user/orders/historic | Get user order history
*UserOrdersApi* | [**getUserOrders**](docs/UserOrdersApi.md#getuserorders) | **GET** /api/user/orders/schedule | Get user order item schedule
*UserProfileApi* | [**createUserProfile**](docs/UserProfileApi.md#createuserprofile) | **POST** /api/user/profile | Create user profile
*UserProfileApi* | [**getUserProfile**](docs/UserProfileApi.md#getuserprofile) | **GET** /api/user/profile | Get user profile
*UserProfileApi* | [**updateUserProfile**](docs/UserProfileApi.md#updateuserprofile) | **PUT** /api/user/profile | Update user profile
*UserSettingsApi* | [**getUserSettings**](docs/UserSettingsApi.md#getusersettings) | **GET** /api/user/settings | Get user settings
*UserSettingsApi* | [**updateUserSettings**](docs/UserSettingsApi.md#updateusersettings) | **PUT** /api/user/settings | Update user settings


### Documentation For Models

 - [AccountDTO](docs/AccountDTO.md)
 - [AdminCreateReportRequest](docs/AdminCreateReportRequest.md)
 - [AdminCreateUserRequest](docs/AdminCreateUserRequest.md)
 - [AdminFeedbackDTO](docs/AdminFeedbackDTO.md)
 - [AdminFinanceLoadResponse](docs/AdminFinanceLoadResponse.md)
 - [AdminLoadCreditRequest](docs/AdminLoadCreditRequest.md)
 - [AdminLoadCreditResult](docs/AdminLoadCreditResult.md)
 - [AdminMenuItemCreateRequest](docs/AdminMenuItemCreateRequest.md)
 - [AdminMenuItemUpdateRequest](docs/AdminMenuItemUpdateRequest.md)
 - [AdminOrderPeriodStatistics](docs/AdminOrderPeriodStatistics.md)
 - [AdminOrderStatisticLine](docs/AdminOrderStatisticLine.md)
 - [AdminOrderStatistics](docs/AdminOrderStatistics.md)
 - [AdminTransactionDTO](docs/AdminTransactionDTO.md)
 - [AdminUpdateUserRequest](docs/AdminUpdateUserRequest.md)
 - [AllergyEntity](docs/AllergyEntity.md)
 - [AuthResponse](docs/AuthResponse.md)
 - [BulkOrderStatusUpdateRequest](docs/BulkOrderStatusUpdateRequest.md)
 - [CampusEntity](docs/CampusEntity.md)
 - [CompactUserDTO](docs/CompactUserDTO.md)
 - [CreateUserProfileRequest](docs/CreateUserProfileRequest.md)
 - [DeliveryConfirmationResponse](docs/DeliveryConfirmationResponse.md)
 - [DeliveryReportData](docs/DeliveryReportData.md)
 - [DeliveryReportItem](docs/DeliveryReportItem.md)
 - [DeliveryStatisticsResponse](docs/DeliveryStatisticsResponse.md)
 - [FeedbackDTO](docs/FeedbackDTO.md)
 - [FeedbackItemStatistics](docs/FeedbackItemStatistics.md)
 - [FeedbackPeriodStatistics](docs/FeedbackPeriodStatistics.md)
 - [FeedbackRequest](docs/FeedbackRequest.md)
 - [FeedbackSentimentRequest](docs/FeedbackSentimentRequest.md)
 - [FinanceUserDTO](docs/FinanceUserDTO.md)
 - [FinancialOverview](docs/FinancialOverview.md)
 - [FinancialPeriodStatistic](docs/FinancialPeriodStatistic.md)
 - [GrantedAuthority](docs/GrantedAuthority.md)
 - [KitchenReportData](docs/KitchenReportData.md)
 - [KitchenReportItem](docs/KitchenReportItem.md)
 - [LoginRequest](docs/LoginRequest.md)
 - [MenuItemDTO](docs/MenuItemDTO.md)
 - [MenuItemQueueRequest](docs/MenuItemQueueRequest.md)
 - [MenuItemStatisticsDTO](docs/MenuItemStatisticsDTO.md)
 - [MenuTemplateCreateRequest](docs/MenuTemplateCreateRequest.md)
 - [MenuTemplateDTO](docs/MenuTemplateDTO.md)
 - [MenuTemplateUpdateRequest](docs/MenuTemplateUpdateRequest.md)
 - [NotificationDTO](docs/NotificationDTO.md)
 - [NotificationRequest](docs/NotificationRequest.md)
 - [OrderDTO](docs/OrderDTO.md)
 - [OrderItemDTO](docs/OrderItemDTO.md)
 - [OrderItemRequest](docs/OrderItemRequest.md)
 - [PageTransactionAuditDTO](docs/PageTransactionAuditDTO.md)
 - [PageUserEventAuditDTO](docs/PageUserEventAuditDTO.md)
 - [PageableObject](docs/PageableObject.md)
 - [PlaceOrderRequest](docs/PlaceOrderRequest.md)
 - [PresetNameDTO](docs/PresetNameDTO.md)
 - [PushSubscriptionRequest](docs/PushSubscriptionRequest.md)
 - [RefreshTokenRequest](docs/RefreshTokenRequest.md)
 - [RegisterRequest](docs/RegisterRequest.md)
 - [ReportStatusDTO](docs/ReportStatusDTO.md)
 - [ResidenceEntity](docs/ResidenceEntity.md)
 - [ScanItemBarcodeRequest](docs/ScanItemBarcodeRequest.md)
 - [ScanUserBarcodeRequest](docs/ScanUserBarcodeRequest.md)
 - [SelectDTO](docs/SelectDTO.md)
 - [SortObject](docs/SortObject.md)
 - [TransactionAuditDTO](docs/TransactionAuditDTO.md)
 - [TransactionDTO](docs/TransactionDTO.md)
 - [TransactionWithUserDTO](docs/TransactionWithUserDTO.md)
 - [UpdateUserProfileRequest](docs/UpdateUserProfileRequest.md)
 - [UpdateUserSettingsRequest](docs/UpdateUserSettingsRequest.md)
 - [UserDeliveryItemsResponse](docs/UserDeliveryItemsResponse.md)
 - [UserEntity](docs/UserEntity.md)
 - [UserEventAuditDTO](docs/UserEventAuditDTO.md)
 - [UserProfileDTO](docs/UserProfileDTO.md)
 - [UserSettingsDTO](docs/UserSettingsDTO.md)
 - [UserWithProfileDTO](docs/UserWithProfileDTO.md)


<a id="documentation-for-authorization"></a>
## Documentation For Authorization


Authentication schemes defined for the API:
<a id="Bearer Authentication"></a>
### Bearer Authentication

- **Type**: Bearer authentication (JWT)

