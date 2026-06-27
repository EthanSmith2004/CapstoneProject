import { TabBar, type TabItem } from '@/components/ui/tabBar';
import { createFileRoute, Outlet } from '@tanstack/react-router'


export const Route = createFileRoute('/admin/finance')({
  component: RouteComponent,
})

function RouteComponent() {
  const navigation: TabItem[] = [
    { name: 'Statistics', href: '/admin/finance/stats' },
    { name: 'Users', href: '/admin/finance/users' },
    { name: 'Transactions', href: '/admin/finance/transactions' },
    { name: 'Load Individual', href: '/admin/finance/load-individual' },
    { name: 'Load Bulk', href: '/admin/finance/load-bulk' },
  ]

  return (
    <div className="space-y-4">
      <TabBar tabs={navigation} />
      <Outlet />
    </div>
  );
}
