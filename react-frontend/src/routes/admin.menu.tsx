import { createFileRoute, Outlet } from '@tanstack/react-router'
import { TabBar, type TabItem } from '@/components/ui/tabBar'

export const Route = createFileRoute('/admin/menu')({
  component: RouteComponent,
})

function RouteComponent() {
  const navigation: TabItem[] = [
    { name: 'Items', href: '/admin/menu/items' },
    { name: 'Spyskaart', href: '/admin/menu/menu' },
    { name: 'Statistiek', href: '/admin/menu/stats' },
  ]
  
  return (
    <div className="space-y-4">
      <TabBar tabs={navigation} />
      <Outlet />
    </div>
  )
}
