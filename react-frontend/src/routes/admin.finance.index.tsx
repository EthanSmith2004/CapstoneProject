import { createFileRoute, redirect } from '@tanstack/react-router'

export const Route = createFileRoute('/admin/finance/')({
  component: RouteComponent,
  beforeLoad: async () => {
    throw redirect({ to: '/admin/finance/stats' });
  }
})

function RouteComponent() {
  return <></>;
}
