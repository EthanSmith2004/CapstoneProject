import { createFileRoute } from '@tanstack/react-router'
import { PrivacyPolicyScreen } from '@/components/consent/PrivacyPolicyScreen'

export const Route = createFileRoute('/policy')({
  component: PrivacyPolicyScreen,
})

