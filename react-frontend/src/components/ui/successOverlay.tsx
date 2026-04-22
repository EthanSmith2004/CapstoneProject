// src/components/ui/SuccessOverlay.tsx
import { useEffect } from 'react'
import { useNavigate } from '@tanstack/react-router'
import type { SuccessOverlayProps } from '../interfaces/SuccessOverlayProps'

export function SuccessOverlay({ message, redirectTo, delay = 3000 }: SuccessOverlayProps) {
  const navigate = useNavigate()

  useEffect(() => {
    const timer = setTimeout(() => {
      navigate({ to: redirectTo })
    }, delay)

    return () => clearTimeout(timer)
  }, [navigate, redirectTo, delay])

  return (
    <div className="
      absolute 
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
        border-green-700 
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
          border-green-700 
          rounded-full 
          flex 
          items-center 
          justify-center 
          mb-4"
        >
          <span className="
            text-4xl 
            text-green-700"
          >
            ✓
          </span>
        </div>
        <p className="
          text-xl 
          font-bold 
          text-green-700"
        >
          {message}
        </p>
      </div>
    </div>
  )
}
