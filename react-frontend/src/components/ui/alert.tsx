// src/components/ui/alert.tsx
import * as React from 'react'
import { cn } from '@/lib/utils'

interface AlertProps extends React.HTMLAttributes<HTMLDivElement> {}

export function Alert({ className, ...props }: AlertProps) {
  return (
    <div
      role="alert"
      className={cn('rounded border px-4 py-3 text-sm', className)}
      {...props}
    />
  )
}

export function AlertTitle({ className, ...props }: React.HTMLAttributes<HTMLHeadingElement>) {
  return (
    <h5 className={cn('mb-1 font-medium leading-none tracking-tight', className)} {...props} />
  )
}
