import { createFileRoute } from '@tanstack/react-router'
import { DataConsentScreen } from '@/components/consent/DataConsentScreen'

export const Route = createFileRoute('/consent')({
  component: DataConsentScreen,
})