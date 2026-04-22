import { createFileRoute, redirect } from '@tanstack/react-router'

export const Route = createFileRoute('/admin/feedback/')({
  beforeLoad: () => {
    throw redirect({ to: '/admin/feedback/overview' });
  }
})
