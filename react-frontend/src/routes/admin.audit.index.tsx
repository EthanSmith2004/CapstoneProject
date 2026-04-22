import { createFileRoute, redirect } from '@tanstack/react-router'

export const Route = createFileRoute('/admin/audit/')({
  component: RouteComponent,
  beforeLoad: () => {
    throw redirect({ to: '/admin/audit/login' });
  }
})

function RouteComponent() {
  return <div>Hello "/admin/audit/"!</div>
}
