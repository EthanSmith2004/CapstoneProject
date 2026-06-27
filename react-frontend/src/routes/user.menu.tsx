
import { createFileRoute, Outlet } from '@tanstack/react-router'
import { TabBar, type TabItem } from '@/components/ui/tabBar'

export const Route = createFileRoute('/user/menu')({
  component: RouteComponent,
})

function RouteComponent() {
  const navigation: TabItem[] = [
  { name: 'All Meals', href: '/user/menu/all' },
  { name: 'Favourites', href: '/user/menu/favourites' },
  ]

  return (
      <div className="
        flex 
        flex-col 
        h-full 
        bg-gradient-to-br 
        from-orange-50 
        to-orange-100"
      >
        <TabBar 
          className='
            flex 
            flex-row 
            justify-center 
            items-center 
            w-screen 
            pt-4
            border-b
            border-orange-300
            bg-orange-200/50
            backdrop-blur-md
            sticky
            top-0'
          tabs={navigation} 
        />
        <Outlet />
      </div>
    );
}

