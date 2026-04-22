import { LoadScreen } from '@/components/general/LoadScreen';
import { useAuth } from '@/contexts/AuthContext';
import { createFileRoute, redirect, useNavigate } from '@tanstack/react-router'
import { useEffect, useRef } from 'react';

export const Route = createFileRoute('/logout')({
  component: RouteComponent,
  beforeLoad: async ({ context }) => {
    if (!context.auth.isAuthenticated) {
      throw redirect({
        to: '/login'
      })
    }
  }
})

function RouteComponent() {
  const auth = useAuth();
  const navigate = useNavigate();
  const hasLoggedOut = useRef(false);

  useEffect(() => {
    if (hasLoggedOut.current) {
      return;
    }

    console.log("Logging out user...", auth);
    if (auth.isAuthenticated && !auth.isLoggingOut) {
      hasLoggedOut.current = true;
      auth.logout().then(() => {
        navigate({ to: '/login', replace: true });
      });
    }
  }, [auth, navigate]);

  return (
    <LoadScreen message="Teken uit..." />
  );
}
