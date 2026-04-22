import type { MenuItemDTO } from '@/api/models'

export interface MealScreenProps {
  meal?: MenuItemDTO | null
  isLoading?: boolean
}