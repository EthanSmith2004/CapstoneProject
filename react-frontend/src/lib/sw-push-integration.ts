// Push notification utilities for service worker integration
export const registerPushEventHandlers = () => {
  if ('serviceWorker' in navigator) {
    navigator.serviceWorker.addEventListener('message', (event) => {
      console.log('Received message from service worker:', event.data);
      
      // Handle different message types from service worker
      if (event.data && event.data.type === 'NOTIFICATION_CLICKED') {
        // Handle notification click
        const { url } = event.data;
        console.log('Notification clicked, navigating to:', url);
        
        // You can add custom navigation logic here
        if (url) {
          window.location.href = url;
        }
      }
    });

    // Register the service worker and prepare for push events
    navigator.serviceWorker.ready.then(() => {
      console.log('Service worker is ready for push notifications');
    });
  }
};

export const sendMessageToServiceWorker = (message: any) => {
  if ('serviceWorker' in navigator && navigator.serviceWorker.controller) {
    navigator.serviceWorker.controller.postMessage(message);
  }
};

export const showLocalNotification = (title: string, options?: NotificationOptions) => {
  sendMessageToServiceWorker({
    type: 'SHOW_LOCAL_NOTIFICATION',
    title,
    options
  });
};