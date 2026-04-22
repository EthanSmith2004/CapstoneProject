import { useState, useEffect, useCallback } from 'react';
import PushNotificationService from '@/lib/push-notifications';
import { useAuth } from '@/contexts/AuthContext';

export interface UsePushNotificationsReturn {
  isSupported: boolean;
  permission: NotificationPermission;
  isSubscribed: boolean;
  isLoading: boolean;
  error: string | null;
  vapidKey: string | null;
  subscribe: () => Promise<boolean>;
  unsubscribe: () => Promise<boolean>;
  requestPermission: () => Promise<NotificationPermission>;
  setVapidKey: (key: string) => void;
  fetchVapidKey: () => Promise<string | null>;
  diagnose: () => Promise<{ [key: string]: any }>;
}

export const usePushNotifications = (): UsePushNotificationsReturn => {
  const [isSupported] = useState(() => PushNotificationService.getInstance().isSupported());
  const [permission, setPermission] = useState<NotificationPermission>(() => 
    PushNotificationService.getInstance().getPermissionStatus()
  );
  const [isSubscribed, setIsSubscribed] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [vapidKey, setVapidKeyState] = useState<string | null>(null);

  const auth = useAuth();
  const pushService = PushNotificationService.getInstance();

  // Initialize the push service with auth context
  useEffect(() => {
    if (auth.isAuthenticated && auth.getApiClient) {
      pushService.initialize(auth.getApiClient);
    }
  }, [auth.isAuthenticated, auth.getApiClient, pushService]);

  // Check subscription status on mount
  useEffect(() => {
    const checkSubscription = async () => {
      if (isSupported) {
        try {
          const subscribed = await pushService.isSubscribed();
          setIsSubscribed(subscribed);
        } catch (err) {
          console.error('Failed to check subscription status:', err);
        }
      }
    };

    checkSubscription();
  }, [isSupported, pushService]);

  // Update permission status when it changes
  useEffect(() => {
    if (!isSupported) return;

    const handleVisibilityChange = () => {
      const currentPermission = pushService.getPermissionStatus();
      setPermission(currentPermission);
    };

    document.addEventListener('visibilitychange', handleVisibilityChange);
    return () => document.removeEventListener('visibilitychange', handleVisibilityChange);
  }, [isSupported, pushService]);

  const requestPermission = useCallback(async (): Promise<NotificationPermission> => {
    if (!isSupported) {
      setError('Push notifications are not supported');
      return 'denied';
    }

    setIsLoading(true);
    setError(null);

    try {
      const newPermission = await pushService.requestPermission();
      setPermission(newPermission);
      
      if (newPermission !== 'granted') {
        setError('Notification permission was denied');
      }
      
      return newPermission;
    } catch (err) {
      const errorMessage = 'Failed to request notification permission';
      setError(errorMessage);
      console.error(errorMessage, err);
      return 'denied';
    } finally {
      setIsLoading(false);
    }
  }, [isSupported, pushService]);

  const subscribe = useCallback(async (): Promise<boolean> => {
    if (!isSupported) {
      setError('Push notifications are not supported');
      return false;
    }

    setIsLoading(true);
    setError(null);

    try {
      const success = await pushService.subscribe();
      
      if (success) {
        setIsSubscribed(true);
        setPermission('granted');
      } else {
        setError('Failed to subscribe to push notifications');
      }
      
      return success;
    } catch (err) {
      const errorMessage = 'Failed to subscribe to push notifications';
      setError(errorMessage);
      console.error(errorMessage, err);
      return false;
    } finally {
      setIsLoading(false);
    }
  }, [isSupported, pushService]);

  const unsubscribe = useCallback(async (): Promise<boolean> => {
    if (!isSupported) {
      setError('Push notifications are not supported');
      return false;
    }

    setIsLoading(true);
    setError(null);

    try {
      const success = await pushService.unsubscribe();
      
      if (success) {
        setIsSubscribed(false);
      } else {
        setError('Failed to unsubscribe from push notifications');
      }
      
      return success;
    } catch (err) {
      const errorMessage = 'Failed to unsubscribe from push notifications';
      setError(errorMessage);
      console.error(errorMessage, err);
      return false;
    } finally {
      setIsLoading(false);
    }
  }, [isSupported, pushService]);

  const setVapidKey = useCallback((key: string) => {
    pushService.setVapidPublicKey(key);
    setVapidKeyState(key);
  }, [pushService]);

  const fetchVapidKey = useCallback(async (): Promise<string | null> => {
    if (!auth.isAuthenticated) {
      setError('Not authenticated');
      return null;
    }

    setIsLoading(true);
    setError(null);

    try {
      const key = await pushService.fetchVapidPublicKey();
      setVapidKeyState(key);
      return key;
    } catch (err) {
      const errorMessage = 'Failed to fetch VAPID public key';
      setError(errorMessage);
      console.error(errorMessage, err);
      return null;
    } finally {
      setIsLoading(false);
    }
  }, [auth.isAuthenticated, pushService]);

  const diagnose = useCallback(async () => {
    return await pushService.diagnose();
  }, [pushService]);

  // Auto-fetch VAPID key when authenticated
  useEffect(() => {
    if (auth.isAuthenticated && !vapidKey && isSupported) {
      fetchVapidKey();
    }
  }, [auth.isAuthenticated, vapidKey, isSupported, fetchVapidKey]);

  return {
    isSupported,
    permission,
    isSubscribed,
    isLoading,
    error,
    vapidKey,
    subscribe,
    unsubscribe,
    requestPermission,
    setVapidKey,
    fetchVapidKey,
    diagnose,
  };
};