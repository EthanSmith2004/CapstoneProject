import { createFileRoute, redirect } from '@tanstack/react-router'
import { RegisterForm } from '../components/auth/RegisterForm'

export const Route = createFileRoute('/register')({
  beforeLoad: async ({ context }) => {
    if (context.auth.isAuthenticated) {
      throw redirect({
        to: '/',
      })
    }
  },
  component: RegisterForm
})
