import { useState } from 'react'
import { useLocation } from '@tanstack/react-router'
import { Button } from '@/components/ui/button'
import { MenuItemCard } from '@/components/menu/MenuItemCard'
import { SuccessOverlay } from '@/components/ui/successOverlay' 
import { useMutation } from '@tanstack/react-query'
import type { MenuItemWithQuantity } from '../interfaces/MenuItemWithQuantity'
import type { BestellingScreenProps } from '../interfaces/BestellingScreenProps'
import { FailureOverlay } from '../ui/failureOverlay'
import { useMobileUser } from '@/contexts/MobileUserContext'
import { ShoppingCart, Receipt, AlertCircle } from 'lucide-react'

export function BestellingScreen({ ordersAPI }: BestellingScreenProps) {
  const location = useLocation()
  const { userProfile } = useMobileUser()

  const initialBestelling =
    location.state &&
    typeof location.state === 'object' &&
    Array.isArray((location.state as any).bestelling)
      ? ((location.state as any).bestelling as MenuItemWithQuantity[])
      : []

  const [bestelling, setBestelling] = useState<MenuItemWithQuantity[]>(initialBestelling)
  const [showMessage, setShowMessage] = useState(false)
  const [errorMessage, setErrorMessage] = useState('')
  
  const updateQuantity = (id: number, delta: number) => {
    setBestelling((prev) => {
      const updated = prev.map(item => {
        if (item.id === id) {
          const newQuantity = Math.max(0, (item.quantity || 1) + delta);
          return { ...item, quantity: newQuantity };
        }
        return item;
      }).filter(item => (item.quantity || 0) > 0); // Remove items with 0 quantity
      
      return updated;
    });
  }
  
  const placeOrderMutation = useMutation({
    mutationFn: async () => {
      if (bestelling.length === 0) return null
      
      const orderRequest = {
        items: bestelling.map(meal => ({
          menuItemId: meal.id || 0,
          quantity: meal.quantity || 1
        }))
      }
      
      return await ordersAPI.createOrder(orderRequest)
    },
    onSuccess: () => {
      setShowMessage(true)
    },
    onError: (error) => {
      console.error('Error placing order:', error)
      setErrorMessage('Fout: Nie genoeg balans om bestelling te plaas nie.')  
    }
  })

  const totalItems = bestelling.reduce((sum, item) => sum + (item.quantity || 1), 0)
  const totalCost = bestelling.reduce((sum, item) => sum + (item.quantity || 1) * (item.price || 0), 0)
  const userAllergies = userProfile?.allergies?.map(allergy => allergy.name).filter(Boolean) as string[] || []

  const handlePlaceOrder = () => {
    if (bestelling.length > 0) {
      placeOrderMutation.mutate()
    }
  }

  return (
    <div className="flex flex-col h-full bg-gradient-to-br from-orange-50 to-orange-100">
      {/* Header Section */}
      <div className="bg-white shadow-sm border-b border-orange-200 p-4">
        <div className="flex items-center gap-2 mb-2">
          <ShoppingCart className="h-6 w-6 text-orange-600" />
          <h1 className="text-xl font-bold text-gray-800">Jou Bestelling</h1>
        </div>
        <div className="text-sm text-orange-600 font-medium">
          {totalItems} item{totalItems !== 1 ? 's' : ''} • R{totalCost.toFixed(2)}
        </div>
      </div>

      {/* Order Items */}
      <div className="flex-1 overflow-y-auto px-4 py-4 space-y-4">
        {bestelling.map((meal) => (
          <MenuItemCard
            key={meal.id}
            meal={meal}
            quantity={meal.quantity || 1}
            userAllergies={userAllergies}
            onQuantityChange={updateQuantity}
            showCostBreakdown={true}
            className="transition-all duration-200 hover:scale-[1.02]"
          />
        ))}
        
        {bestelling.length === 0 && (
          <div className="text-center py-12">
            <AlertCircle className="h-16 w-16 text-gray-400 mx-auto mb-4" />
            <p className="text-gray-500 text-lg">Geen items in jou bestelling nie</p>
          </div>
        )}
      </div>

      {/* Footer with Summary and Confirm Button */}
      <div className="bg-white border-t border-orange-200 shadow-lg">
        {/* Order Summary */}
        <div className="p-4 border-b border-gray-100">
          <div className="flex items-center gap-2 mb-3">
            <Receipt className="h-5 w-5 text-orange-600" />
            <h2 className="text-lg font-semibold text-gray-800">Bestelling Opsomming</h2>
          </div>
          <div className="space-y-2">
            <div className="flex justify-between items-center">
              <span className="text-gray-600">Totale Items:</span>
              <span className="font-semibold">{totalItems}</span>
            </div>
            <div className="flex justify-between items-center text-lg">
              <span className="font-semibold text-gray-800">Totale Koste:</span>
              <span className="font-bold text-orange-600">R{totalCost.toFixed(2)}</span>
            </div>
          </div>
        </div>

        {/* Confirm Button */}
        <div className="p-4">
          <Button
            className={`
              w-full py-4 text-lg font-bold rounded-xl transition-all duration-300
              ${bestelling.length > 0 && !placeOrderMutation.isPending
                ? 'bg-gradient-to-r from-green-600 to-green-700 hover:from-green-700 hover:to-green-800 text-white shadow-lg hover:shadow-xl transform hover:scale-[1.02]' 
                : 'bg-gray-300 text-gray-500 cursor-not-allowed'
              }
            `}
            onClick={handlePlaceOrder}
            disabled={placeOrderMutation.isPending || bestelling.length === 0}
          >
            <div className="flex items-center justify-center gap-2">
              <Receipt className="h-5 w-5" />
              <span>
                {placeOrderMutation.isPending 
                  ? 'Besig om te plaas...' 
                  : bestelling.length > 0
                    ? `Bevestig Bestelling • R${totalCost.toFixed(2)}`
                    : 'Geen items om te bevestig nie'
                }
              </span>
            </div>
          </Button>
        </div>
      </div>

      {showMessage && (
        <SuccessOverlay
          message="Bestelling Suksesvol Geplaas"
          redirectTo="/user/order"
        />
      )}

      {errorMessage && (
        <FailureOverlay
          message="Fout: Nie genoeg balans om bestelling te plaas nie."
          onClose={() => setErrorMessage('')}
        />
      )}
    </div>
  )
}
