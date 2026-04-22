import { useEffect } from 'react'
import { useNavigate } from '@tanstack/react-router'
import type { FailureOverlayProps } from '../interfaces/FailureOverlayProps'

export function FailureOverlay({
  message,
  autoCloseAfter = 2500,
  redirectTo,
  onClose,
}: FailureOverlayProps) {
  const navigate = useNavigate()

  useEffect(() => {
    const timer = setTimeout(() => {
      if (redirectTo) {
        navigate({ to: redirectTo })
      }
      if (onClose) {
        onClose()
      }
    }, autoCloseAfter)

    return () => clearTimeout(timer)
  }, [autoCloseAfter, redirectTo, onClose, navigate])

  return (
    <div className="
      fixed 
      inset-0 
      bg-white/90 
      backdrop-blur-sm 
      flex 
      items-center 
      justify-center 
      z-50 
      px-6"
    >
      <div className="
        bg-white 
        border 
        border-red-700 
        shadow-xl 
        rounded-xl 
        p-8 
        text-center 
        relative 
        w-full 
        max-w-sm"
      >
        <div className="
          w-24 
          h-24 
          mx-auto 
          border-2 
          border-red-700 
          rounded-full 
          flex 
          items-center 
          justify-center 
          mb-4"
        >
          <span className="
            text-4xl 
            text-red-700"
          >
            ✕
          </span>
        </div>
        <p className="
          text-xl 
          font-bold 
          text-red-700"
        >
          {message}
        </p>
      </div>
    </div>
  )
}
