import type { UserProfileDTO, AccountDTO, TransactionDTO } from '@/api/models'

export interface ProfileScreenProps {
  profile?: UserProfileDTO
  account?: AccountDTO
  transactions?: TransactionDTO[]
  isLoading?: boolean
}