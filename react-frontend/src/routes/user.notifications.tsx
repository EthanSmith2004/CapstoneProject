import { NotificationsApi } from '@/api'
import { LoadScreen } from '@/components/general/LoadScreen'
import { useAuth } from '@/contexts/AuthContext'
import { useMobileNavigation } from '@/contexts/MobileNavigationContext'
import { formatDate } from '@/lib/utils'
import { useQuery } from '@tanstack/react-query'
import { createFileRoute } from '@tanstack/react-router'
import { useEffect, useState } from 'react'
import { 
  CheckCircle, 
  AlertTriangle,
  XCircle,
  Info 
} from 'lucide-react'

export const Route = createFileRoute('/user/notifications')({
  component: RouteComponent,
})

function RouteComponent() {
  const auth = useAuth()
  const notificationsAPI = auth.getApiClient(NotificationsApi)
  const mobileNavigation = useMobileNavigation()
  const [expandedId, setExpandedId] = useState<string | undefined>(undefined)

  const mappingTypes = (backendType: string) => {
    switch (backendType) {
      case 'ORDER_CONFIRMATION': 
        return 'SUCCESS'
      case 'ORDER_READY': 
        return 'SUCCESS'
      case 'ACCOUNT_CREDITED': 
        return 'SUCCESS'
      case 'ACCOUNT_BALANCE_LOW': 
        return 'QAUTION'
      case 'SYSTEM_MAINTAINANCE': 
        return 'QAUTION'
      case 'ORDER_CANCELLED': 
        return 'FAILURE'
      case 'MENU_UPDATE': 
        return 'INFO'
      case 'PROMOTIONAL': 
        return 'INFO'
      case 'GENERAL_ANNOUNCEMENT': 
        return 'INFO'
      default: 
        return 'INFO'
    }
  }

  const getNotificationStyles = (backendType: string) => {
    const type = mappingTypes(backendType)
    switch (type) {
      case 'SUCCESS':
        return {
          bgClass: 'bg-green-50',
          borderClass: 'border-green-500',
          icon: <CheckCircle className='w-6 h-6 text-green-500' />
        }
      case 'QAUTION':
        return {
          bgClass: 'bg-yellow-50',
          borderClass: 'border-yellow-500',
          icon: <AlertTriangle className='w-6 h-6 text-yellow-500' />
        }
      case 'FAILURE':
        return {
          bgClass: 'bg-red-50',
          borderClass: 'border-red-500',
          icon: <XCircle className='w-6 h-6 text-red-500' />
        }
      case 'INFO':
        return {
          bgClass: 'bg-blue-50',
          borderClass: 'border-blue-500',
          icon: <Info className='w-6 h-6 text-blue-500' />
        }
      default:
        return {
          bgClass: 'bg-blue-50',
          borderClass: 'border-blue-500',
          icon: <Info className='w-6 h-6 text-blue-500' />
        }
    }
  }

  const {data: notifications, isLoading: isNotificationsLoading} = useQuery({
    queryKey: ['notifications'],
    queryFn: async () => {
      const response = await notificationsAPI.getNotifications()
      return response.data
    },
    staleTime: 1000 * 60 * 5, // 5 minutes
    refetchOnWindowFocus: true,
  })

  useEffect(() => {
    if (mobileNavigation.title !== 'Kennisgewings') {
      mobileNavigation.setTitle('Kennisgewings')
    }
  }, [mobileNavigation])

  const toggleNotification = (id: any) => {
    setExpandedId(expandedId === id ? undefined : id)
  }

  if (isNotificationsLoading || !notifications) {
    return (
      <LoadScreen />
    )
  }
  return (
    <div className="
      max-w-2xl 
      mx-auto 
      p-4"
    >
      <div className="space-y-6">
        
        
        <div className="space-y-4">
          {notifications.map((notification) => {
            const { bgClass, borderClass, icon } = getNotificationStyles(notification.type!)
            return (
              <div
                key={notification.id}
                className={`
                  p-4 
                  border 
                  rounded-lg 
                  cursor-pointer 
                  transition-all 
                  duration-300 
                  ${borderClass}
                  ${expandedId === notification.id ? 
                    `${bgClass} bg-opacity-50` : bgClass
                  } 
                  hover:shadow-md`}
                onClick={() => toggleNotification(notification.id)}
              >
                <div className="
                  flex 
                  justify-between 
                  items-start
                  gap-4"
                >
                  <div className='flex-1 flex justify-between item-start gap-3'>
                    <div className='mb-auto mt-auto'>{icon}</div>
                    <div className="flex-1 min-w-0">
                      <div>
                        <h2 className="
                          text-lg 
                          font-semibold"
                        >
                          {notification.title}
                        </h2>
                        <span className="
                          text-sm 
                          text-gray-400"
                        >
                          {formatDate(notification.createdAt)}
                        </span>
                      </div>
                      <p className={`text-gray-600 ${expandedId === notification.id ? '' : 'whitespace-normal break-words'}`}>
                        {notification.message}
                      </p>
                    </div>
                  </div>
                </div>
              </div>
            )
          })}
        </div>
      </div>
    </div>
  )
}