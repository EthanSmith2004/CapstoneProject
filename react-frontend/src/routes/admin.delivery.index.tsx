import { createFileRoute } from '@tanstack/react-router'
import { DeliveryDashboard } from '@/components/admin/DeliveryDashboard'
import { useAuth } from '@/contexts/AuthContext'
import { DeliveryAdminApi } from '@/api/api'
// import { DeliveryAdminApi } from '@/api' // Uncomment after regenerating API client

export const Route = createFileRoute('/admin/delivery/')({
  component: RouteComponent,
})

function RouteComponent() {
  const { getApiClient } = useAuth()
  
  const deliveryApi = getApiClient(DeliveryAdminApi)
  return <DeliveryDashboard deliveryApi={deliveryApi} />
}

