import { UserMenuApi } from '@/api'
import { Button } from '@/components/ui/button'
import { MenuItemCard } from '@/components/menu/MenuItemCard'
import { useAuth } from '@/contexts/AuthContext'
import { useMobileNavigation } from '@/contexts/MobileNavigationContext'
import { useMobileUser } from '@/contexts/MobileUserContext'
import { useQuery } from '@tanstack/react-query'
import { createFileRoute, useNavigate } from '@tanstack/react-router'
import { useEffect, useState } from 'react'
import { ShoppingCart, Package } from 'lucide-react'
import { getUserPreferences } from '@/lib/userPreferences'
import { getDislikedDishes, getLikedDishes } from '@/lib/dishPreferences'

export const Route = createFileRoute('/user/menu/all')({
  component: RouteComponent,
})

function RouteComponent() {
  const navigate = useNavigate()
  const auth = useAuth()
  const mobileNavigation = useMobileNavigation()
  const { userProfile } = useMobileUser()
  const menuAPI = auth.getApiClient(UserMenuApi);

  useEffect(() => {
    if (mobileNavigation.title !== 'Menu') {
      mobileNavigation.setTitle('Menu')
    }
  }, [mobileNavigation])

  const { data: meals } = useQuery({
    queryKey: ['menu'],
    queryFn: async () => {
      const response = await menuAPI.getMenu();
      return response.data;
    },
  })

  const [menuQuantity, setMenuQuantity] = useState<Record<number, number>>({})

  const updateQuantity = (id: number, delta: number) => {
    setMenuQuantity((prev) => {
      const currentQuantity = prev[id] || 0;
      const newQuantity = Math.max(0, currentQuantity + delta);
      return { ...prev, [id]: newQuantity };
    })
  }

  const handlePlaasBestelling = () => {
    const bestelling = meals?.filter(meal => (menuQuantity[meal?.id ?? -1] ?? -1) > 0) as any[] ?? []
    bestelling.forEach(meal => {
      meal.quantity = menuQuantity[meal?.id ?? -1] ?? 0
    })
    if (bestelling.length > 0) {
      navigate({
        to: '/user/order/new',
        state: { bestelling } as any,
      })
    }
  }

  const hasItemSelected = Object.values(menuQuantity).some(quantity => quantity > 0)
  const totalCost = meals?.reduce((sum, meal) => {
    const quantity = menuQuantity[meal?.id ?? -1] ?? 0;
    return sum + (quantity * (meal.price ?? 0));
  }, 0) ?? 0

  const userPreferences = getUserPreferences()
  const dislikedIds = getDislikedDishes()
  const likedIds = getLikedDishes()
  const userAllergies = 
    userProfile?.allergies?.map(allergy => allergy.name).filter(Boolean) as string[] || []

  // Filter and sort meals according to preferences
  let filteredMeals = meals ?? []

  if (userPreferences.hideAllergyDishes && userAllergies.length > 0) {
    filteredMeals = filteredMeals.filter(meal =>
      !meal.allergies?.some((allergy: string) => userAllergies.includes(allergy))
    )
  }

  if (userPreferences.hideDislikedDishes) {
    filteredMeals = filteredMeals.filter(meal => !dislikedIds.includes(Number(meal.id)))
  }

  if (userPreferences.sortByLikedDishes) {
    filteredMeals = [...filteredMeals].sort((a, b) => {
      const aLiked = likedIds.includes(Number(a.id)) ? 1 : 0
      const bLiked = likedIds.includes(Number(b.id)) ? 1 : 0
      return bLiked - aLiked
    })
  }

  return (
    <div className='
      flex 
      flex-col 
      h-full 
      bg-gradient-to-br 
      from-orange-50 
      to-orange-100'
    >
      {/* Menu Items */}
      <div className="
        flex-1 
        overflow-y-auto 
        px-4 
        py-4 
        space-y-4"
      >
        {filteredMeals?.map((meal) => (
          <MenuItemCard
            key={meal.id}
            meal={meal}
            quantity={menuQuantity[meal?.id ?? -1] ?? 0}
            userAllergies={userAllergies}
            onQuantityChange={updateQuantity}
            className="
              transition-all 
              duration-200"
          />
        ))}
        
        {filteredMeals && filteredMeals.length === 0 && (
          <div className="
            text-center 
            py-12"
          >
            <Package className="
              h-16 
              w-16 
              text-gray-400 
              mx-auto 
              mb-4" 
            />
            <p className="
              text-gray-500 
              text-lg"
            >
              No meals available
            </p>
          </div>
        )}
      </div>
      {/* Footer with Order Button */}
      <div className="
        bg-white 
        border
        border-orange-300 
        p-4 
        shadow-lg"
      >
        <Button
          disabled={!hasItemSelected}
          onClick={handlePlaasBestelling}
          className={`
            w-full 
            py-4 
            text-lg 
            font-bold 
            rounded-xl 
            transition-all 
            duration-300
            ${hasItemSelected 
              ? 'bg-gradient-to-r from-orange-600 to-orange-700 hover:from-orange-700 hover:to-orange-800 text-white shadow-lg hover:shadow-xl' 
              : 'bg-gray-300 text-gray-500 cursor-not-allowed'
            }
          `}
        >
          <div className="
            flex 
            items-center 
            justify-center 
            gap-2"
          >
            <ShoppingCart className="h-5 w-5" />
            <span>
              {hasItemSelected 
                ? `Place Order - R${totalCost.toFixed(2)}` 
                : 'Choose Items to Order'
              }
            </span>
          </div>
        </Button>
      </div>
    </div>
  );
}
