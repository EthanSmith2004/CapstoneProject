import type { UserProfileDTO } from '@/api/models'
import { UserProfileApi } from '@/api'

export interface EditProfileScreenProps {
  profile?: UserProfileDTO
  profileAPI: UserProfileApi
  isLoading?: boolean
}