import { TriangleAlert } from 'lucide-react'
import { Button } from '../ui/button'

export function ErrorFallback({ error, reset }: { error: unknown; reset: () => void }) {
  const message = error instanceof Error ? error.message : 'Something went wrong'

  return (
    <div className="flex min-h-screen flex-col items-center justify-center gap-4 bg-gray-100 px-4 text-center spys-body">
      <TriangleAlert className="h-12 w-12 text-orange-500" />
      <div>
        <p className="text-lg font-semibold text-gray-800">Something went wrong</p>
        <p className="mt-1 text-sm text-gray-500">{message}</p>
      </div>
      <Button onClick={reset}>Try again</Button>
    </div>
  )
}
