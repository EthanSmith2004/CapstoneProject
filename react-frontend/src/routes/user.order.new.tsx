import { createFileRoute } from '@tanstack/react-router'
import { BestellingScreen } from '@/components/bestelling/BestellingScreen'
import { UserOrdersApi } from '@/api'
import { useAuth } from '@/contexts/AuthContext'
import { useMobileNavigation } from '@/contexts/MobileNavigationContext'
import { useEffect } from 'react'

export const Route = createFileRoute('/user/order/new')({
  component: RouteComponent,
})

function RouteComponent() {
  const auth = useAuth()
  const ordersAPI = auth.getApiClient(UserOrdersApi)
  const mobileNavigation = useMobileNavigation()
  
  useEffect(() => {
    if (mobileNavigation.title !== 'Place Order') {
      mobileNavigation.setTitle('Place Order')
    }
  }, [mobileNavigation])
  
  return <BestellingScreen ordersAPI={ordersAPI} />
}

