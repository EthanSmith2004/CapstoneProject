import { TabBar, type TabItem } from '@/components/ui/tabBar';
import { createFileRoute, Outlet } from '@tanstack/react-router'

export const Route = createFileRoute('/admin/audit')({
  component: RouteComponent,
})

function RouteComponent() {  
  const navigation: TabItem[] = [
    { name: 'Aanteken Log', href: '/admin/audit/login' },
    { name: 'Gebruiker Log', href: '/admin/audit/user' },
    { name: 'Krediet Laai Transaksie Log', href: '/admin/audit/transaction' },
  ]

  return (
    <div className="space-y-4">
      <TabBar tabs={navigation} />
      <Outlet />
    </div>
  );
}
