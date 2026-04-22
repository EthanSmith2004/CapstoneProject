import { createFileRoute, redirect } from '@tanstack/react-router'
import { LoginForm } from '../components/auth/LoginForm'

export const Route = createFileRoute('/login')({
  beforeLoad: async ({ context }) => {
    if (context.auth.isAuthenticated) {
      throw redirect({
        to: '/',
      })
    }
  },
  component: LoginForm
})