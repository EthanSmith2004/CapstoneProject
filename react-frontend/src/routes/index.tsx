import { createFileRoute, redirect } from '@tanstack/react-router'

export const Route = createFileRoute('/')({
  beforeLoad: async ({ context }) => {
    if (!context.auth.isAuthenticated) {
      throw redirect({
        to: '/login',
        search: {
          redirect: '/'
        }
      })
    }

    if(context.auth.isAdmin) {
      return redirect({
        to: '/admin'
      })
    }

    return redirect({
      to: '/user'
    })
  },
  component: () => null
})
