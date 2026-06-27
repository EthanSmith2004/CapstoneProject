import { createFileRoute } from '@tanstack/react-router'
import { UserOrdersApi, UserProfileApi } from '@/api'
import { useAuth } from '@/contexts/AuthContext'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { useMobileNavigation } from '@/contexts/MobileNavigationContext'
import { useEffect } from 'react'
import { OrderView } from '@/components/mobile/OrderView'
import { UserBarcodeDisplay } from '@/components/mobile/UserBarcodeDisplay'

export const Route = createFileRoute('/user/order/')({
  component: RouteComponent,
})

function RouteComponent() {
  const auth = useAuth()
  const ordersAPI = auth.getApiClient(UserOrdersApi)
  const profileAPI = auth.getApiClient(UserProfileApi)
  const mobileNavigation = useMobileNavigation()
  const queryClient = useQueryClient();
  
  useEffect(() => {
    if (mobileNavigation.title !== 'Orders') {
      mobileNavigation.setTitle('Orders')
    }
  }, [mobileNavigation])

  const {mutate: cancelOrder}  =  useMutation({
    mutationKey: ['cancelOrder'],
    mutationFn: async (orderId: number) => {
      await ordersAPI.cancelOrderItem(orderId)
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['userPendingOrders'] })
      queryClient.invalidateQueries({ queryKey: ['userCompletedOrders'] })
      queryClient.invalidateQueries({ queryKey: ['userProfile'] })
    }
  })
  
  const { data: userPendingOrders, isLoading: isPendingOrdersLoading } = useQuery({
    queryKey: ['userPendingOrders'],
    queryFn: async () => {
      const response = await ordersAPI.getUserOrders()
      return response.data
    },
  })

  const { data: userCompletedOrders, isLoading: isCompletedOrdersLoading } = useQuery({
    queryKey: ['userCompletedOrders'],
    queryFn: async () => {
      const response = await ordersAPI.getUserOrderHistory()
      return response.data
    }
  })

  const { data: userProfile } = useQuery({
    queryKey: ['userProfile'],
    queryFn: async () => {
      const response = await profileAPI.getUserProfile()
      return response.data
    }
  })

  // Check if user has items in delivery status
  const hasItemsInDelivery = userPendingOrders?.some(
    (order) => order.status === 'IN_DELIVERY'
  )

  return (
    <div>
      {/* Show barcode if user has items in delivery and has a credential number */}
      {hasItemsInDelivery && userProfile?.credentialNumber && (
        <div className="p-4">
          <UserBarcodeDisplay
            credentialNumber={userProfile.credentialNumber}
            firstName={userProfile.user?.firstName}
            lastName={userProfile.user?.lastName}
          />
        </div>
      )}
      
      <OrderView loading={isPendingOrdersLoading} orders={userPendingOrders} title='Current Orders' cancelOrder={cancelOrder}/>
      <OrderView loading={isCompletedOrdersLoading} orders={userCompletedOrders} title="Past Orders" completed defaultOpen={false}/>
    </div>
  );
}

