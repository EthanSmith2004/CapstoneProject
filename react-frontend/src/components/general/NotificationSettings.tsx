import React from 'react';
import { Bell, BellOff, AlertCircle, CheckCircle } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Alert } from '@/components/ui/alert';
import { Switch } from '@/components/ui/switch';
import { Label } from '@/components/ui/label';
import { usePushNotifications } from '@/hooks/usePushNotifications';

interface NotificationSettingsProps {
  onSubscriptionChange?: (isSubscribed: boolean) => void;
}

export const NotificationSettings: React.FC<NotificationSettingsProps> = ({
  onSubscriptionChange
}) => {
  const {
    isSupported,
    permission,
    isSubscribed,
    isLoading,
    error,
    vapidKey,
    subscribe,
    unsubscribe,
    requestPermission,
    fetchVapidKey,
    diagnose
  } = usePushNotifications();

  // Notify parent component of subscription changes
  React.useEffect(() => {
    onSubscriptionChange?.(isSubscribed);
  }, [isSubscribed, onSubscriptionChange]);

  const handleToggleNotifications = async () => {
    if (!isSupported) return;

    if (permission === 'default') {
      await requestPermission();
      return;
    }

    if (isSubscribed) {
      await unsubscribe();
    } else {
      await subscribe();
    }
  };

  const getPermissionIcon = () => {
    switch (permission) {
      case 'granted':
        return <CheckCircle className="h-4 w-4 text-green-500" />;
      case 'denied':
        return <BellOff className="h-4 w-4 text-red-500" />;
      default:
        return <AlertCircle className="h-4 w-4 text-yellow-500" />;
    }
  };

  const getPermissionText = () => {
    switch (permission) {
      case 'granted':
        return 'Notifications allowed';
      case 'denied':
        return 'Notifications blocked';
      default:
        return 'Permission not requested';
    }
  };

  if (!isSupported) {
    return (
      <Card>
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <BellOff className="h-5 w-5" />
            Push Notifications
          </CardTitle>
        </CardHeader>
        <CardContent>
          <Alert className="border-orange-200 bg-orange-50">
            <AlertCircle className="h-4 w-4 text-orange-600" />
            <p className="text-orange-800">
              Push notifications are not supported in this browser or device.
            </p>
          </Alert>
        </CardContent>
      </Card>
    );
  }

  return (
    <Card>
      <CardHeader>
        <CardTitle className="flex items-center gap-2">
          <Bell className="h-5 w-5" />
          Push Notifications
        </CardTitle>
        <CardDescription>
          Receive notifications about new orders, updates, and important announcements.
        </CardDescription>
      </CardHeader>
      <CardContent className="space-y-4">
        {error && (
          <Alert className="border-red-200 bg-red-50">
            <AlertCircle className="h-4 w-4 text-red-600" />
            <p className="text-red-800">{error}</p>
          </Alert>
        )}

        <div className="flex items-center justify-between">
          <div className="space-y-0.5">
            <Label htmlFor="notifications-toggle" className="text-base">
              Enable Notifications
            </Label>
            <div className="flex items-center gap-2 text-sm text-muted-foreground">
              {getPermissionIcon()}
              {getPermissionText()}
            </div>
          </div>
          <Switch
            id="notifications-toggle"
            checked={isSubscribed && permission === 'granted'}
            onCheckedChange={handleToggleNotifications}
            disabled={isLoading || permission === 'denied'}
          />
        </div>

        {permission === 'denied' && (
          <Alert className="border-red-200 bg-red-50">
            <AlertCircle className="h-4 w-4 text-red-600" />
            <p className="text-red-800">
              Notifications are blocked. To enable them, please allow notifications in your browser settings and refresh the page.
            </p>
          </Alert>
        )}

        {permission === 'default' && (
          <Alert className="border-blue-200 bg-blue-50">
            <AlertCircle className="h-4 w-4 text-blue-600" />
            <p className="text-blue-800">
              Click the toggle above to request notification permission.
            </p>
          </Alert>
        )}

        {isSubscribed && permission === 'granted' && (
          <Alert className="border-green-200 bg-green-50">
            <CheckCircle className="h-4 w-4 text-green-600" />
            <p className="text-green-800">
              You're subscribed to push notifications. You'll receive updates about your orders and important announcements.
            </p>
          </Alert>
        )}

        <div className="pt-2 space-y-2">
          <div className="flex gap-2">
            <Button
              variant="outline"
              size="sm"
              onClick={() => {
                if ('serviceWorker' in navigator && 'PushManager' in window) {
                  // Test notification
                  new Notification('Test Notification', {
                    body: 'This is a test notification from Spys!',
                    icon: '/logo192.png',
                    badge: '/favicon.ico',
                  });
                }
              }}
              disabled={permission !== 'granted' || isLoading}
            >
              Test Notification
            </Button>
            
            <Button
              variant="outline"
              size="sm"
              onClick={fetchVapidKey}
              disabled={isLoading}
            >
              Refresh Keys
            </Button>
            
            <Button
              variant="outline"
              size="sm"
              onClick={async () => {
                const diagnostics = await diagnose();
                console.log('Push Notification Diagnostics:', diagnostics);
                alert('Diagnostics logged to console. Check browser developer tools.');
              }}
              disabled={isLoading}
            >
              Run Diagnostics
            </Button>
          </div>
          
          {vapidKey && (
            <div className="text-xs text-muted-foreground">
              <div className="flex items-center gap-1">
                <CheckCircle className="h-3 w-3 text-green-500" />
                <span>Server keys loaded</span>
              </div>
            </div>
          )}
        </div>
      </CardContent>
    </Card>
  );
};