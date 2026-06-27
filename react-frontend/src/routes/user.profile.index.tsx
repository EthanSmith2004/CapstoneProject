import { createFileRoute } from '@tanstack/react-router'
import { UserProfileApi } from '@/api'
import { useAuth } from '@/contexts/AuthContext'
import { useQuery } from '@tanstack/react-query'
import { useMobileNavigation } from '@/contexts/MobileNavigationContext'
import { useEffect } from 'react'
import { MobileSettings } from '@/components/mobile/MobileSettings'
import { LoadScreen } from '@/components/general/LoadScreen'
import { userProfileSchema } from '@/data/user-profile-schema'

export const Route = createFileRoute('/user/profile/')({
  component: RouteComponent,
})

function RouteComponent() {
  const auth = useAuth()
  const profileAPI = auth.getApiClient(UserProfileApi)
  const mobileNavigation = useMobileNavigation()
  
  useEffect(() => {
    if (mobileNavigation.title !== 'Profile') {
      mobileNavigation.setTitle('Profile')
    }
  }, [mobileNavigation])
    const { data: profile, isLoading: profileLoading } = useQuery({
    queryKey: ['userProfile'],
    queryFn: async () => {
      const response = await profileAPI.getUserProfile()
      return response.data
    },
  })

  if (profileLoading) {
    return <LoadScreen message='Loading User Profile'/>;
  }

  const settingsData = {
    name: profile?.user?.firstName + ' ' + profile?.user?.lastName,
    email: profile?.user?.email,
    campus: profile?.campus,
    residence: profile?.residence,
    allergies: profile?.allergies,
    accountBalance: profile?.balance ? `R${profile.balance.toFixed(2)}` : 'R0.00',
    changePassword: 'Change password...'
  }

  return <MobileSettings data={settingsData} schema={userProfileSchema}/>;
}

