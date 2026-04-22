import { AdminDashboard } from '@/components/dashboard/AdminDashboard'
import { createFileRoute, redirect } from '@tanstack/react-router'

const roleMapping: {[key: string]: string} = {
  'ROLE_USER_ADMIN': '/admin/user',
  'ROLE_FINANCIAL_ADMIN': '/admin/finance',
  'ROLE_MENU_ADMIN': '/admin/menu',
  'ROLE_AUDIT_ADMIN': '/admin/audit',
  'ROLE_DELIVERY_ADMIN': '/admin/delivery',
}


export const Route = createFileRoute('/admin')({
  component: RouteComponent,
  beforeLoad: async ({ context, location }) => {
    // Check authentication first
    if (!context.auth.isAuthenticated) {
      throw redirect({
        to: '/login',
        search: {
          redirect: '/admin'
        }
      })
    }

    // Check if user is admin
    if (!context.auth.isAdmin) {
      throw redirect({
        to: '/'
      })
    }

    if (location.pathname === '/admin') {
      context.auth.user?.roles?.forEach(role => {
        const path = roleMapping[role];
        if (path) { 
          throw redirect({
            to: path
          })
        }
      });
    }
  },
})

function RouteComponent() {
  return (
    <AdminDashboard />
  )
}
