import { TabBar, type TabItem } from '@/components/ui/tabBar';
import { createFileRoute, Outlet } from '@tanstack/react-router'


export const Route = createFileRoute('/admin/finance')({
  component: RouteComponent,
})

function RouteComponent() {
  const navigation: TabItem[] = [
    { name: 'Statistiek', href: '/admin/finance/stats' },
    { name: 'Gebruikers', href: '/admin/finance/users' },
    { name: 'Transaksies', href: '/admin/finance/transactions' },
    { name: 'Laai Individueel', href: '/admin/finance/load-individual' },
    { name: 'Laai Bulk', href: '/admin/finance/load-bulk' },
  ]

  return (
    <div className="space-y-4">
      <TabBar tabs={navigation} />
      <Outlet />
    </div>
  );
}
