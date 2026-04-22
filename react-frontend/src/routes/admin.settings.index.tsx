import { createFileRoute, redirect } from '@tanstack/react-router'

export const Route = createFileRoute('/admin/settings/')({
  component: RouteComponent,
  beforeLoad: async ({}) => {
    throw redirect({
      to: '/admin/settings/campus'
    })
  }
})

function RouteComponent() {
  return <></>;
}
