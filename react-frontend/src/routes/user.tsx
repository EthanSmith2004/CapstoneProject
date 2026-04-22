import { AppBar } from '@/components/ui/appbar';
import { MobileUserProvider, useMobileUser } from '@/contexts/MobileUserContext';
import { MobileNavigationProvider, useMobileNavigation } from '@/contexts/MobileNavigationContext';
import { createFileRoute, Outlet, redirect, useLocation, useNavigate } from '@tanstack/react-router'
import { Button } from '@/components/ui/button';
import { CheckCircle, Menu, UserCircle, MailCheck } from 'lucide-react';

export const Route = createFileRoute('/user')({
  component: RouteComponent,
  beforeLoad: async ({ context, location }) => {
    if (!context.auth.isAuthenticated) {
      throw redirect({
        to: '/login',
        search: {
          redirect: '/user'
        }
      })
    }

    if (location.pathname === '/user') {
      throw redirect({
        to: '/user/menu'
      })
    }
  },
})

function AppBarRenderer() {
  const mobileUser = useMobileUser();
  const { title } = useMobileNavigation();
  console.log("Rendering AppBar with title:", title, "and balance:", mobileUser?.userProfile?.balance);

  return <AppBar title={title} balance={mobileUser?.userProfile?.balance ?? 0} />
}

function MobileAppRenderer() {
  const location = useLocation();
  const navigate = useNavigate();

  if (location.pathname.startsWith('/user/profile/new')) {
    return <Outlet />;
  }

  // Determine current active path
  const isProfileActive = location.pathname.startsWith('/user/profile');
  const isMenuActive = location.pathname.startsWith('/user/menu');
  const isOrderActive = location.pathname.startsWith('/user/order');
  const isNotificationsActive = location.pathname.startsWith('/user/notifications');

  // Shared button styling
  const baseButtonClass = "flex-1 h-15 text-lg font-bold text-center py-5 rounded-none transition-colors";
  
  // Default button style (orange background, white text)
  const defaultButtonClass = `${baseButtonClass} bg-orange-600 text-white hover:bg-orange-600 hover:text-white active:bg-white active:text-orange-600`;
  
  // Inverted button style for active buttons (white background, orange text)
  const activeButtonClass = `${baseButtonClass} bg-white text-orange-600 hover:bg-white hover:text-orange-600 active:bg-orange-600 active:text-white`;

  return (
    <MobileUserProvider>
      <div className="flex flex-col h-screen">
        <AppBarRenderer />
        <div className='overflow-y-auto flex-1 h-full'>
          <Outlet />
        </div>
        <div className="
        w-full 
        z-10
        flex
        flex-row
        justify-center"
        >
          <Button
            onClick={() => { navigate({ to: '/user/profile' }) }}
            className={isProfileActive ? activeButtonClass : defaultButtonClass}
          >
            <UserCircle className="inline h-15 w-10 " />
          </Button>
          <Button
            onClick={() => { navigate({ to: '/user/menu' }) }}
            className={isMenuActive ? activeButtonClass : defaultButtonClass}
          >
            <Menu className="inline h-15 w-10 " />
          </Button>
          <Button
            onClick={() => { navigate({ to: '/user/order' }) }}
            className={isOrderActive ? activeButtonClass : defaultButtonClass}
          >
            <CheckCircle className="inline h-15 w-10" />
          </Button>
          <Button
            onClick={() => { navigate({ to: '/user/notifications' }) }}
            className={isNotificationsActive ? activeButtonClass : defaultButtonClass}
          >
            <MailCheck className="inline h-15 w-10" />
          </Button>
        </div>
      </div>
    </MobileUserProvider>
  );
}

function RouteComponent() {
  return (
    <MobileNavigationProvider>
      <MobileAppRenderer />
    </MobileNavigationProvider>
  );
}
