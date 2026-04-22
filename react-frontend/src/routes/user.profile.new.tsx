import { ProfileCreateScreen } from '@/components/profile/ProfileCreateScreen'
import { createFileRoute } from '@tanstack/react-router'

export const Route = createFileRoute('/user/profile/new')({
  component: ProfileCreateScreen,
})


