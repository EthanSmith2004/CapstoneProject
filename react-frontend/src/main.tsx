import { StrictMode } from 'react'
import ReactDOM from 'react-dom/client'
import { RouterProvider, createRouter } from '@tanstack/react-router'

import * as TanStackQueryProvider from './integrations/tanstack-query/root-provider.tsx'
import { AuthProvider, useAuth } from './contexts/AuthContext'
import { registerSW } from 'virtual:pwa-register'
import { registerPushEventHandlers } from './lib/sw-push-integration'
import { ErrorFallback } from './components/general/ErrorFallback'

// Import the generated route tree
import { routeTree } from './routeTree.gen'

import './styles.css'
import reportWebVitals from './reportWebVitals.ts'

// Create a new router instance
const router = createRouter({
  routeTree,
  context: {
    ...TanStackQueryProvider.getContext(),
    auth: undefined!,
  },
  defaultPreload: 'intent',
  scrollRestoration: true,
  defaultStructuralSharing: true,
  defaultPreloadStaleTime: 0,
  defaultErrorComponent: ErrorFallback,
})

// Register the router instance for type safety
declare module '@tanstack/react-router' {
  interface Register {
    router: typeof router
  }
}

function InnerApp() {
  const auth = useAuth()
  if (auth.isLoading) {
    return <></>;
  }
  return <RouterProvider router={router} context={{ 
    ...TanStackQueryProvider.getContext(),
    auth 
  }} />
}

function App() {
  return (
    <TanStackQueryProvider.Provider>
      <AuthProvider>
        <InnerApp />
      </AuthProvider>
    </TanStackQueryProvider.Provider>
  )
}

registerSW({
  onNeedRefresh() {
    window.location.reload()
  }
})

// Initialize push notification handlers
registerPushEventHandlers()

// Add service worker debugging
if ('serviceWorker' in navigator) {
  navigator.serviceWorker.ready.then((registration) => {
    console.log('Service Worker ready:', {
      scope: registration.scope,
      state: registration.active?.state,
      scriptURL: registration.active?.scriptURL,
      hasPushManager: 'pushManager' in registration
    });

    // Test push manager capabilities
    if ('pushManager' in registration) {
      console.log('Push manager available, testing permissions...');
      console.log('Push manager permissions supported:', 'permissionState' in registration.pushManager);
    }
  }).catch((error) => {
    console.error('Service Worker ready failed:', error);
  });

  navigator.serviceWorker.addEventListener('error', (error) => {
    console.error('Service Worker error:', error);
  });

  navigator.serviceWorker.addEventListener('message', (event) => {
    console.log('Message from service worker:', event.data);
  });
}

// Render the app
const rootElement = document.getElementById('app')
if (rootElement && !rootElement.innerHTML) {
  const root = ReactDOM.createRoot(rootElement)
  root.render(
    <StrictMode>
      <App />
    </StrictMode>,
  )
}

// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
reportWebVitals()
