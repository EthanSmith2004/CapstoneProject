import { Outlet, createRootRouteWithContext } from '@tanstack/react-router'
import { TanStackRouterDevtools } from '@tanstack/react-router-devtools'
import { Toaster } from 'sonner'

import TanStackQueryLayout from '../integrations/tanstack-query/layout.tsx'

import type { QueryClient } from '@tanstack/react-query'

interface AuthContextType {
  user: { email: string; roles: string[] } | null
  isAuthenticated: boolean
  isAdmin: boolean
  isUser: boolean
  isLoading: boolean
}

interface MyRouterContext {
  queryClient: QueryClient
  auth: AuthContextType
}

export const Route = createRootRouteWithContext<MyRouterContext>()({
  component: () => (
    <>
      <Outlet />
      <Toaster position="top-right" />
      <TanStackRouterDevtools />
      <TanStackQueryLayout />
    </>
  ),
})
