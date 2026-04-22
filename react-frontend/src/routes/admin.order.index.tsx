import { createFileRoute, redirect } from '@tanstack/react-router'

export const Route = createFileRoute('/admin/order/')({
  beforeLoad: async ({}) => {
    throw redirect({
      to: '/admin/order/overview'
    })
  }
})

