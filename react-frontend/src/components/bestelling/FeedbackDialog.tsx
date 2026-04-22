import { useState } from 'react'
import { useMutation, useQueryClient } from '@tanstack/react-query'
import { Star } from 'lucide-react'

import { UserFeedbackApi } from '@/api'
import { useAuth } from '@/contexts/AuthContext'
import { Button } from '@/components/ui/button'
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { toast } from 'sonner'

interface FeedbackDialogProps {
  open: boolean
  onOpenChange: (open: boolean) => void
  menuItemId: number
  itemName?: string
}

export function FeedbackDialog({ open, onOpenChange, menuItemId, itemName }: FeedbackDialogProps) {
  const [rating, setRating] = useState(0)
  const [kommentaar, setKommentaar] = useState('')
  const [error, setError] = useState(false)
  
  const { getApiClient } = useAuth()
  const feedbackAPI = getApiClient(UserFeedbackApi)
  const queryClient = useQueryClient()

  const feedbackMutation = useMutation({
    mutationFn: async () => {
      await feedbackAPI.placeFeedback({
        menuItemId,
        rating,
        comment: kommentaar,
      })
    },
    onSuccess: () => {
      toast.success('Terugvoer suksesvol geplaas!')
      queryClient.invalidateQueries({ queryKey: ['userPendingOrders'] })
      queryClient.invalidateQueries({ queryKey: ['userCompletedOrders'] })
      onOpenChange(false)
      // Reset form
      setRating(0)
      setKommentaar('')
      setError(false)
    },
    onError: () => {
      toast.error('Daar was \'n probleem met die plasing van terugvoer.')
    }
  })

  const handleStarClick = (value: number) => {
    setRating(value)
    setError(false)
  }

  const handleSubmit = () => {
    if (rating === 0) {
      setError(true)
      return
    }
    feedbackMutation.mutate()
  }

  const handleClose = (open: boolean) => {
    if (!open) {
      // Reset form when closing
      setRating(0)
      setKommentaar('')
      setError(false)
    }
    onOpenChange(open)
  }

  return (
    <Dialog open={open} onOpenChange={handleClose}>
      <DialogContent className="sm:max-w-[425px]">
        <DialogHeader>
          <DialogTitle>Plaas Terugvoer</DialogTitle>
          <DialogDescription>
            {itemName ? `Deel jou ervaring met ${itemName}` : 'Deel jou ervaring met hierdie item'}
          </DialogDescription>
        </DialogHeader>

        <div className="space-y-4 py-4">
          {/* Star rating */}
          <div className="space-y-2">
            <Label>Gradering *</Label>
            <div className="flex justify-center space-x-2">
              {[1, 2, 3, 4, 5].map((num) => (
                <Star
                  key={num}
                  className={`h-8 w-8 cursor-pointer transition-colors ${
                    rating >= num 
                      ? 'fill-yellow-500 text-yellow-500' 
                      : 'text-gray-400 hover:text-gray-500'
                  }`}
                  onClick={() => handleStarClick(num)}
                />
              ))}
            </div>
            {error && (
              <p className="text-sm text-red-600 text-center">
                Ster-gradering is verpligtend
              </p>
            )}
          </div>

          {/* Kommentaar */}
          <div className="space-y-2">
            <Label htmlFor="feedback-comment">Kommentaar (Opsioneel)</Label>
            <Input
              id="feedback-comment"
              placeholder="Voer jou kommentaar in"
              value={kommentaar}
              onChange={(e) => setKommentaar(e.target.value)}
            />
            <p className="text-sm text-muted-foreground">
              Deel jou gedagtes oor hierdie item
            </p>
          </div>

          <Button
            className="w-full"
            onClick={handleSubmit}
            disabled={feedbackMutation.isPending}
          >
            {feedbackMutation.isPending ? 'Besig...' : 'Bevestig Terugvoer'}
          </Button>
        </div>
      </DialogContent>
    </Dialog>
  )
}
