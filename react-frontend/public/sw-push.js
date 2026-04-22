import { precacheAndRoute, cleanupOutdatedCaches } from 'workbox-precaching';
import { NavigationRoute, registerRoute } from 'workbox-routing';
import { NetworkFirst, StaleWhileRevalidate } from 'workbox-strategies';

// Add debugging for service worker initialization
console.log('Push notification service worker initializing...');

// Check if push manager is available
if (!self.registration.pushManager) {
  console.error('Push manager unavailable in service worker');
} else {
  console.log('Push manager available in service worker');
}

// Precache and route
precacheAndRoute(self.__WB_MANIFEST);
cleanupOutdatedCaches();

// Register navigation route
const navigationRoute = new NavigationRoute(
  new NetworkFirst({
    cacheName: 'navigation-cache',
  }),
  {
    denylist: [/^\/api/],
  }
);
registerRoute(navigationRoute);

// Cache API responses
registerRoute(
  ({ url }) => url.pathname.startsWith('/api/'),
  new StaleWhileRevalidate({
    cacheName: 'api-cache',
  })
);

// Push notification handling
self.addEventListener('push', (event) => {
  console.log('Push event received:', event);

  if (!event.data) {
    console.log('Push event received but no data');
    return;
  }

  let notificationData;
  try {
    notificationData = event.data.json();
    console.log('Push notification data parsed:', notificationData);
  } catch (error) {
    console.error('Failed to parse push notification data:', error);
    // Try to show a default notification
    notificationData = {
      title: 'New Notification',
      body: 'You have a new notification from Spys'
    };
  }

  const title = notificationData.title || 'Spys Notification';
  const options = {
    body: notificationData.body || 'You have a new notification',
    icon: notificationData.icon || '/logo192.png',
    badge: notificationData.badge || '/favicon.ico',
    image: notificationData.image,
    data: notificationData.data || {},
    actions: notificationData.actions || [],
    tag: notificationData.tag || 'default',
    renotify: notificationData.renotify || false,
    requireInteraction: notificationData.requireInteraction || false,
    silent: notificationData.silent || false,
    timestamp: Date.now(),
    vibrate: notificationData.vibrate || [200, 100, 200],
  };

  event.waitUntil(
    self.registration.showNotification(title, options)
  );
});

// Handle notification clicks
self.addEventListener('notificationclick', (event) => {
  console.log('Notification clicked:', event);
  event.notification.close();

  const notificationData = event.notification.data || {};
  const urlToOpen = notificationData.url || '/';

  event.waitUntil(
    clients.matchAll({ type: 'window', includeUncontrolled: true }).then((clientList) => {
      // Check if there's already a window/tab open
      for (const client of clientList) {
        if (client.url.includes(urlToOpen) && 'focus' in client) {
          return client.focus();
        }
      }
      
      // If no window/tab is open, open a new one
      if (clients.openWindow) {
        return clients.openWindow(urlToOpen);
      }
    })
  );
});

// Handle notification close
self.addEventListener('notificationclose', (event) => {
  console.log('Notification closed:', event);
});

// Handle messages from main app
self.addEventListener('message', (event) => {
  if (event.data && event.data.type === 'SHOW_LOCAL_NOTIFICATION') {
    const { title, options } = event.data;
    self.registration.showNotification(title, options);
  }
});