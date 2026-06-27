import { TabBar, type TabItem } from '@/components/ui/tabBar';
import { createFileRoute, Outlet } from '@tanstack/react-router'

export const Route = createFileRoute('/admin/audit')({
  component: RouteComponent,
})

function RouteComponent() {  
  const navigation: TabItem[] = [
    { name: 'Login Log', href: '/admin/audit/login' },
    { name: 'User Log', href: '/admin/audit/user' },
    { name: 'Credit Load Transaction Log', href: '/admin/audit/transaction' },
  ]

  return (
    <div className="space-y-4">
      <TabBar tabs={navigation} />
      <Outlet />
    </div>
  );
}
