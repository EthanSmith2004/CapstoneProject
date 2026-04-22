import { createFileRoute, redirect } from '@tanstack/react-router'
import { getUserPreferences } from '@/lib/userPreferences';

export const Route = createFileRoute('/user/menu/')({
  component: RouteComponent,
  beforeLoad: async () => {
    const userPreferences = getUserPreferences();
    if (userPreferences.favTabDefault) {
      throw redirect({ to: '/user/menu/favourites' });
    } else {
      throw redirect({ to: '/user/menu/all' });
    }
  }
})

function RouteComponent() {
  return <></>;
}
