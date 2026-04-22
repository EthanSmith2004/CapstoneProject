// Push Notification Service
import { NotificationsApi } from '@/api';
import type { PushSubscriptionRequest } from '@/api/models';

class PushNotificationService {
    private static instance: PushNotificationService;
    private notificationsApi: NotificationsApi | null = null;
    private vapidPublicKey: string = '';
    private getApiClient: (<T>(ApiClass: new (config?: any) => T) => T) | null = null;

    private constructor() {
        // API client will be set when initialized with auth context
    }

    public static getInstance(): PushNotificationService {
        if (!PushNotificationService.instance) {
            PushNotificationService.instance = new PushNotificationService();
        }
        return PushNotificationService.instance;
    }

    // Initialize with auth context
    public initialize(getApiClient: <T>(ApiClass: new (config?: any) => T) => T): void {
        this.getApiClient = getApiClient;
        this.notificationsApi = getApiClient(NotificationsApi);
    }

    // Get the authenticated API client (refreshes if needed)
    private getAuthenticatedApi(): NotificationsApi {
        if (!this.getApiClient) {
            throw new Error('PushNotificationService not initialized with auth context');
        }
        // This will automatically use the current access token and handle refresh
        this.notificationsApi = this.getApiClient(NotificationsApi);
        return this.notificationsApi;
    }

    // Fetch VAPID public key from backend
    public async fetchVapidPublicKey(): Promise<string> {
        try {
            const api = this.getAuthenticatedApi();
            const response = await api.publicKey();
            this.vapidPublicKey = response.data;
            return this.vapidPublicKey;
        } catch (error) {
            console.error('Failed to fetch VAPID public key:', error);
            throw error;
        }
    }

    // Set VAPID public key manually (optional, can use fetchVapidPublicKey instead)
    public setVapidPublicKey(key: string): void {
        this.vapidPublicKey = key;
    }

    // Check if push notifications are supported
    public isSupported(): boolean {
        return 'serviceWorker' in navigator && 'PushManager' in window && 'Notification' in window;
    }

    // Check current permission status
    public getPermissionStatus(): NotificationPermission {
        if (!this.isSupported()) {
            return 'denied';
        }
        return Notification.permission;
    }

    // Request notification permission
    public async requestPermission(): Promise<NotificationPermission> {
        if (!this.isSupported()) {
            return 'denied';
        }

        if (Notification.permission === 'default') {
            const permission = await Notification.requestPermission();
            return permission;
        }

        return Notification.permission;
    }

    private urlBase64ToUint8Array(base64String: string): Uint8Array {
        if (!base64String) {
            throw new Error('VAPID key is empty');
        }

        // Validate VAPID key format - should be base64url
        if (!/^[A-Za-z0-9_-]+$/.test(base64String)) {
            throw new Error('VAPID key contains invalid characters');
        }

        // VAPID keys should be 65 bytes (87-88 characters in base64url)
        if (base64String.length < 80 || base64String.length > 90) {
            console.warn('VAPID key length unusual:', base64String.length, 'expected ~87-88 chars');
        }

        try {
            const padding = '='.repeat((4 - base64String.length % 4) % 4);
            const base64 = (base64String + padding)
                .replace(/\-/g, '+')
                .replace(/_/g, '/');

            const rawData = window.atob(base64);
            const outputArray = new Uint8Array(rawData.length);

            for (let i = 0; i < rawData.length; ++i) {
                outputArray[i] = rawData.charCodeAt(i);
            }

            // VAPID keys should be exactly 65 bytes
            if (outputArray.length !== 65) {
                throw new Error(`VAPID key decoded to ${outputArray.length} bytes, expected 65`);
            }

            return outputArray;
        } catch (error) {
            if (error instanceof Error && error.message.includes('bytes')) {
                throw error; // Re-throw our custom error
            }
            throw new Error(`Failed to decode VAPID key: ${error}`);
        }
    }

    // Validate service worker registration
    private async validateServiceWorker(): Promise<boolean> {
        try {
            if (!('serviceWorker' in navigator)) {
                console.error('Service workers not supported');
                return false;
            }

            const registration = await navigator.serviceWorker.ready;
            
            if (!registration.active) {
                console.error('No active service worker found');
                return false;
            }

            if (registration.active.state !== 'activated') {
                console.warn('Service worker state:', registration.active.state);
            }

            // Check if push manager is available
            if (!('pushManager' in registration)) {
                console.error('Push manager not available in service worker');
                return false;
            }

            console.log('Service worker validation passed');
            return true;
        } catch (error) {
            console.error('Service worker validation failed:', error);
            return false;
        }
    }

    // Subscribe to push notifications
    public async subscribe(): Promise<boolean> {
        try {
            if (!this.isSupported()) {
                console.error('Push notifications are not supported');
                return false;
            }

            if (!this.vapidPublicKey) {
                console.error('VAPID public key is not set');
                return false;
            }

            const permission = await this.requestPermission();
            if (permission !== 'granted') {
                console.error('Notification permission denied');
                return false;
            }

            console.log('Requesting push notification subscription...');
            console.log('VAPID key length:', this.vapidPublicKey.length);
            console.log('VAPID key preview:', this.vapidPublicKey.substring(0, 20) + '...');

            // Validate service worker first
            const swValid = await this.validateServiceWorker();
            if (!swValid) {
                throw new Error('Service worker validation failed');
            }

            // Wait for service worker to be ready
            console.log('Waiting for service worker to be ready...');
            const registration = await navigator.serviceWorker.ready;
            console.log('Service worker ready, state:', registration.active?.state);

            // Check if already subscribed
            const existingSubscription = await registration.pushManager.getSubscription();
            if (existingSubscription) {
                console.log('Already subscribed to push notifications');
                // Verify the subscription is still valid
                try {
                    // Test if we can send subscription to backend
                    const subscriptionData: PushSubscriptionRequest = {
                        endpoint: existingSubscription.endpoint,
                        p256dh: btoa(String.fromCharCode(...new Uint8Array(existingSubscription.getKey('p256dh')!))),
                        auth: btoa(String.fromCharCode(...new Uint8Array(existingSubscription.getKey('auth')!))),
                        userAgent: navigator.userAgent
                    };
                    
                    const api = this.getAuthenticatedApi();
                    await api.subscribe(subscriptionData);
                    return true;
                } catch (backendError) {
                    console.warn('Existing subscription not valid with backend, creating new one:', backendError);
                    // Unsubscribe the existing one and create a new subscription
                    await existingSubscription.unsubscribe();
                }
            }

            // Convert VAPID key
            let applicationServerKey: Uint8Array;
            try {
                applicationServerKey = this.urlBase64ToUint8Array(this.vapidPublicKey);
                console.log('VAPID key converted successfully, length:', applicationServerKey.length);
            } catch (keyError) {
                console.error('Failed to convert VAPID key:', keyError);
                throw new Error(`Invalid VAPID key format: ${keyError}`);
            }

            // Create subscription with timeout to avoid hanging
            console.log('Creating push subscription...');
            const subscriptionPromise = registration.pushManager.subscribe({
                userVisibleOnly: true,
                applicationServerKey: applicationServerKey as BufferSource, 
            });

            // Add timeout to avoid hanging indefinitely
            const timeoutPromise = new Promise<never>((_, reject) => {
                setTimeout(() => reject(new Error('Subscription timeout after 30 seconds')), 30000);
            });

            const subscription = await Promise.race([subscriptionPromise, timeoutPromise]);
            console.log('Push subscription obtained:', {
                endpoint: subscription.endpoint,
                keys: {
                    p256dh: subscription.getKey('p256dh') ? 'present' : 'missing',
                    auth: subscription.getKey('auth') ? 'present' : 'missing'
                }
            });

            // Extract subscription details
            const p256dhKey = subscription.getKey('p256dh');
            const authKey = subscription.getKey('auth');
            
            if (!p256dhKey || !authKey) {
                throw new Error('Subscription keys are missing');
            }

            const subscriptionData: PushSubscriptionRequest = {
                endpoint: subscription.endpoint,
                p256dh: btoa(String.fromCharCode(...new Uint8Array(p256dhKey))),
                auth: btoa(String.fromCharCode(...new Uint8Array(authKey))),
                userAgent: navigator.userAgent
            };

            // Send subscription to backend
            console.log('Sending subscription to backend...');
            const api = this.getAuthenticatedApi();
            await api.subscribe(subscriptionData);

            console.log('Successfully subscribed to push notifications');
            return true;
        } catch (error) {
            console.error('Failed to subscribe to push notifications:', error);

            // Provide more specific error information
            if (error instanceof Error) {
                console.error('Error name:', error.name);
                console.error('Error message:', error.message);
                console.error('Error stack:', error.stack);

                if (error.name === 'AbortError') {
                    console.error('AbortError details:');
                    console.error('- This usually indicates a problem with the push service or VAPID key');
                    console.error('- VAPID key being used:', this.vapidPublicKey);
                    console.error('- Try refreshing VAPID keys or checking service worker registration');
                }

                if (error.name === 'NotSupportedError') {
                    console.error('NotSupportedError: Push messaging is not supported');
                }

                if (error.name === 'NotAllowedError') {
                    console.error('NotAllowedError: Permission denied or user canceled');
                }
            }

            return false;
        }
    }

    // Unsubscribe from push notifications
    public async unsubscribe(): Promise<boolean> {
        try {
            if (!this.isSupported()) {
                console.error('Push notifications are not supported');
                return false;
            }

            const registration = await navigator.serviceWorker.ready;
            const subscription = await registration.pushManager.getSubscription();

            if (!subscription) {
                console.log('No subscription found');
                return true;
            }

            // Extract subscription details for backend
            const subscriptionData: PushSubscriptionRequest = {
                endpoint: subscription.endpoint,
                p256dh: btoa(String.fromCharCode(...new Uint8Array(subscription.getKey('p256dh')!))),
                auth: btoa(String.fromCharCode(...new Uint8Array(subscription.getKey('auth')!))),
                userAgent: navigator.userAgent
            };

            // Unsubscribe from push manager
            await subscription.unsubscribe();

            // Remove subscription from backend
            const api = this.getAuthenticatedApi();
            await api.unsubscribe(subscriptionData);

            console.log('Successfully unsubscribed from push notifications');
            return true;
        } catch (error) {
            console.error('Failed to unsubscribe from push notifications:', error);
            return false;
        }
    }

    // Check if currently subscribed
    public async isSubscribed(): Promise<boolean> {
        try {
            if (!this.isSupported()) {
                return false;
            }

            const registration = await navigator.serviceWorker.ready;
            const subscription = await registration.pushManager.getSubscription();
            return subscription !== null;
        } catch (error) {
            console.error('Failed to check subscription status:', error);
            return false;
        }
    }

    // Show a local notification (for testing)
    public showNotification(title: string, options?: NotificationOptions): void {
        if (this.getPermissionStatus() === 'granted') {
            new Notification(title, options);
        }
    }

    // Diagnostic method to help troubleshoot push notification issues
    public async diagnose(): Promise<{ [key: string]: any }> {
        const diagnostics: { [key: string]: any } = {
            timestamp: new Date().toISOString(),
            userAgent: navigator.userAgent,
            isSupported: this.isSupported(),
            permission: this.getPermissionStatus(),
            vapidKeySet: !!this.vapidPublicKey,
            vapidKeyLength: this.vapidPublicKey?.length || 0,
        };

        try {
            // Service Worker diagnostics
            if ('serviceWorker' in navigator) {
                const registration = await navigator.serviceWorker.ready;
                diagnostics.serviceWorker = {
                    active: !!registration.active,
                    state: registration.active?.state,
                    scope: registration.scope,
                    scriptURL: registration.active?.scriptURL,
                    hasPushManager: 'pushManager' in registration,
                };

                // Check existing subscription
                try {
                    const existingSubscription = await registration.pushManager.getSubscription();
                    diagnostics.subscription = {
                        exists: !!existingSubscription,
                        endpoint: existingSubscription?.endpoint?.substring(0, 50) + '...',
                        hasKeys: !!(existingSubscription?.getKey('p256dh') && existingSubscription?.getKey('auth')),
                    };
                } catch (subError) {
                    diagnostics.subscription = {
                        error: subError instanceof Error ? subError.message : String(subError)
                    };
                }
            } else {
                diagnostics.serviceWorker = { supported: false };
            }

            // VAPID key diagnostics
            if (this.vapidPublicKey) {
                try {
                    const decoded = this.urlBase64ToUint8Array(this.vapidPublicKey);
                    diagnostics.vapidKey = {
                        valid: true,
                        decodedLength: decoded.length,
                        preview: this.vapidPublicKey.substring(0, 20) + '...',
                    };
                } catch (keyError) {
                    diagnostics.vapidKey = {
                        valid: false,
                        error: keyError instanceof Error ? keyError.message : String(keyError),
                    };
                }
            }

            // Browser capabilities
            diagnostics.browserCapabilities = {
                serviceWorkerSupported: 'serviceWorker' in navigator,
                pushManagerSupported: 'PushManager' in window,
                notificationSupported: 'Notification' in window,
                isSecureContext: window.isSecureContext,
                protocol: window.location.protocol,
            };

        } catch (error) {
            diagnostics.diagnosticError = error instanceof Error ? error.message : String(error);
        }

        return diagnostics;
    }
}

export default PushNotificationService;