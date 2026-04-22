// src/routes/meal.tsx
import { createFileRoute, useSearch } from '@tanstack/react-router'
import { MealScreen } from '@/components/menu/MealScreen'
import { UserMenuApi } from '@/api'
import { useAuth } from '@/contexts/AuthContext'
import { useQuery } from '@tanstack/react-query'
import { useEffect } from 'react';
import { useMobileNavigation } from '@/contexts/MobileNavigationContext'

export const Route = createFileRoute('/user/menu/meal')({
  component: RouteComponent,
  validateSearch: (search) => {
    return {
      id: String(search.id || ''),
    }
  },
})

function RouteComponent() {
  const { id } = useSearch({ from: '/user/menu/meal' }) as { id: string }
  const auth = useAuth()
  const mobileNavigation = useMobileNavigation()
  const menuAPI = auth.getApiClient(UserMenuApi)

  useEffect(() => {
    if (mobileNavigation.title !== 'Spyskaart') {
      mobileNavigation.setTitle('Spyskaart')
    }
  }, [mobileNavigation])
  const { data: meal, isLoading } = useQuery({
    queryKey: ['menuItem', id],
    queryFn: async () => {
      if (!id) return null
      const response = await menuAPI.getMenuItemDetail(Number(id))
      return response.data
    },
    enabled: !!id
  })

  return <MealScreen meal={meal} isLoading={isLoading} />
}
