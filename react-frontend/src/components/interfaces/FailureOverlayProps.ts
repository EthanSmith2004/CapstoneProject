export interface FailureOverlayProps {
  message: string
  autoCloseAfter?: number
  redirectTo?: string
  onClose?: () => void
}