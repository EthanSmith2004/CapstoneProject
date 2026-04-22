import { TabBar, type TabItem } from '@/components/ui/tabBar';
import { createFileRoute, Outlet } from '@tanstack/react-router'

export const Route = createFileRoute('/admin/feedback')({
  component: RouteComponent,
})

function RouteComponent() {
  const navigation: TabItem[] = [
    { name: 'Oorsig', href: '/admin/feedback/overview' },
    { name: 'Lys', href: '/admin/feedback/list' },
  ]

  return (
    <div className="container mx-auto py-6">
      <div className="mb-6">
        <h1 className="text-3xl font-bold">Terugvoer</h1>
        <p className="text-muted-foreground">
          Bekyk statistieke en gebruikersterugvoer op spyskaartitems
        </p>
      </div>
      
      <div className="space-y-4">
        <TabBar tabs={navigation} />
        <Outlet />
      </div>
    </div>
  );
}