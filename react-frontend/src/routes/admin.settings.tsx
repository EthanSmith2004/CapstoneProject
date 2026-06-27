import { createFileRoute, Outlet } from '@tanstack/react-router'
import { TabBar } from '../components/ui/tabBar'
import type { TabItem } from '../components/ui/tabBar'

export const Route = createFileRoute('/admin/settings')({
  component: RouteComponent,
})

function RouteComponent() {
  return Settings()
}

function Settings() {
  const navigation: TabItem[] = [
    { name: 'Campuses', href: '/admin/settings/campus' },
    { name: 'Residences', href: '/admin/settings/residence' },
    { name: 'Allergies', href: '/admin/settings/alergen' },
  ]

  return (
    <div className="space-y-4">
      <TabBar tabs={navigation} />
      <Outlet />
    </div>
  );
}
