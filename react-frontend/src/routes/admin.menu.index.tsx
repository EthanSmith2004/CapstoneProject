import { createFileRoute, redirect } from '@tanstack/react-router'

export const Route = createFileRoute('/admin/menu/')({
  component: RouteComponent,
  beforeLoad: async () => {
    throw redirect({ to: '/admin/menu/items' });
  }
})

function RouteComponent() {
  return <></>;
}
