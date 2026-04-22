import { createFileRoute, Outlet } from '@tanstack/react-router'
import { TabBar, type TabItem } from '@/components/ui/tabBar';

export const Route = createFileRoute('/admin/order')({
  component: RouteComponent,
})

function RouteComponent() {
  const navigation: TabItem[] = [
    { name: 'Oorsig', href: '/admin/order/overview' },
    { name: 'Geskiedenis', href: '/admin/order/history' },
  ]

  return (
    <div className="space-y-4">
      <TabBar tabs={navigation} />
      <Outlet />
    </div>
  );
}
