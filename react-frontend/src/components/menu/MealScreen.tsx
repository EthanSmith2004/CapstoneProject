import { useNavigate } from '@tanstack/react-router'
import { Button } from '@/components/ui/button'
import { formatDate } from '@/lib/utils'
import type { MealScreenProps } from '@/components/interfaces/MealScreenProps'

export function MealScreen({ meal, isLoading = false }: MealScreenProps) {
  const navigate = useNavigate()

  // Fallback descriptions if the API doesn't provide them
  const fallbackDescriptions: Record<string, { beskrywing: string; voedingswaarde: string }> = {
    'Spaghetti Bolognese': {
      beskrywing: "\'n Klassieke Italiaanse gereg met maalvleis in \'n ryk tamatie-en-kruie sous, bedien oor spaghetti.",
      voedingswaarde: "Energie: 450 kcal | Proteïen: 25g | Koolhidrate: 55g | Vet: 15g",
    },
    'Hoender Wrap': {
      beskrywing: "Gegrilde hoenderfilet met slaai, tamatie en jogurtversiersel, toegedraai in \'n sagte tortillabrood.",
      voedingswaarde: "Energie: 380 kcal | Proteïen: 30g | Koolhidrate: 35g | Vet: 12g",
    },
    'Beesburger': {
      beskrywing: "\'n Sappige beesvleispattie op \'n geroosterde broodjie met kaas, blaarslaai en sous.",
      voedingswaarde: "Energie: 520 kcal | Proteïen: 28g | Koolhidrate: 40g | Vet: 25g",
    },
    'Groente Curry': {
      beskrywing: "Seisoenale groente in \'n mild kerrie-kokosmelksous, bedien met geurige rys.",
      voedingswaarde: "Energie: 400 kcal | Proteïen: 10g | Koolhidrate: 60g | Vet: 12g",
    },
  }

  if (isLoading) {
    return (
      <div className="
        min-h-screen 
        bg-[rgb(252,225,211)] 
        flex 
        items-center 
        justify-center"
      >
        <p>Laai gereg besonderhede...</p>
      </div>
    )
  }

  if (!meal) {
    return (
      <div className="
        min-h-screen 
        bg-[rgb(252,225,211)] 
        flex 
        flex-col 
        items-center 
        justify-center 
        p-4"
      >
        <p className="
          text-center 
          mb-4"
        >
          Gereg nie gevind nie.
        </p>
        <Button
          onClick={() => navigate({ to: '/user/menu' })}
          className="
            bg-orange-600 
            text-white 
            font-bold 
            py-2 
            px-4"
        >
          Terug na Spyskaart
        </Button>
      </div>
    )
  }

  // Get fallback description if needed
  const fallback = fallbackDescriptions[meal.name || ''] || {
    beskrywing: 'Geen beskrywing beskikbaar nie.',
    voedingswaarde: 'Geen voedingswaarde beskikbaar nie.',
  }

  // Use API description if available, otherwise use fallback
  const beskrywing = meal.description || fallback.beskrywing
  const voedingswaarde = meal.kcal ? `Energie: ${meal.kcal} kcal` : fallback.voedingswaarde

  return (
    <div className="
      min-h-screen 
      bg-[rgb(252,225,211)] 
      pb-20 
      pt-4"
    >
      <div className="
        px-4 
        mt-4 
        space-y-6"
      >
        <img 
          src={meal.imageDetail || meal.imageHero || '/images/default-meal.jpg'} 
          alt={meal.name} 
          className="
            w-full 
            h-52 
            object-cover 
            rounded-xl 
            shadow" 
        />

        <div className="
          bg-white 
          p-6 
          rounded-xl 
          shadow 
          space-y-4"
        >
          <h2 className="
            text-2xl 
            font-bold"
          >
            {meal.name}
          </h2>
          <p className="
            text-gray-600 
            font-semibold"
          >
            Allergieë: {meal.allergies?.join(', ') || 'Geen'}
          </p>
          <p className="
            text-orange-700 
            font-semibold"
          >
            Koste: R{meal.price?.toFixed(2) || '0.00'}
          </p>
          
          {meal.deliveryDate && (
            <p className="
              text-gray-600"
            >
              Aflewering: <strong>{formatDate(meal.deliveryDate)}</strong>
            </p>
          )}

          <div>
            <h3 className="
              font-semibold 
              mb-1"
            >
              Beskrywing
            </h3>
            <p>{beskrywing}</p>
          </div>

          <div>
            <h3 className="
              font-semibold mb-1"
            >
              Voedingswaarde
            </h3>
            <p>{voedingswaarde}</p>
          </div>
        </div>

        <Button
          onClick={() => navigate({ to: '/user/menu' })}
          className="
            w-full 
            bg-orange-600 
            text-white 
            font-bold 
            py-3"
        >
          Terug
        </Button>
      </div>
    </div>
  )
}
