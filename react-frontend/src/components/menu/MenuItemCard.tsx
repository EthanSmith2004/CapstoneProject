import { useState, useEffect } from 'react'
import { Button } from '@/components/ui/button'
import { 
    Card, 
    CardTitle, 
    CardContent, 
    CardHeader 
} from '@/components/ui/card'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Badge } from '@/components/ui/badge'
import { formatDateLong } from '@/lib/utils'
import { 
    Clock,
    ThumbsUp, 
    ThumbsDown, 
    Star,
    Plus,
    Minu
} from 'lucide-react'
import type { MenuItemDTO } from '@/api/models'
import {
    getLikedDishes,
    getDislikedDishes,
    getFavouriteDishes,
    likeDish,
    dislikeDish,
    favouriteDish,
    unfavouriteDish
} from '@/lib/dishPreferences'

interface MenuItemCardProps {
    meal: MenuItemDTO
    quantity: number
    userAllergies: string[]
    onQuantityChange: (id: number, delta: number) => void
    showCostBreakdown?: boolean
    className?: string
}

export function MenuItemCard({
    meal,
    quantity,
    userAllergies,
    onQuantityChange,
    showCostBreakdown = false,
    className = ""
}: MenuItemCardProps) {
    const [isExpanded, setIsExpanded] = useState(false)
    const [liked, setLiked] = useState(false)
    const [disliked, setDisliked] = useState(false)
    const [favourited, setFavourited] = useState(false)

     useEffect(() => {
        setLiked(getLikedDishes().includes(Number(meal.id)))
        setDisliked(getDislikedDishes().includes(Number(meal.id)))
        setFavourited(getFavouriteDishes().includes(Number(meal.id)))
    }, [meal.id])

    const handleLike = () => {
        likeDish(Number(meal.id))
        setLiked(true)
        setDisliked(false)
    }
    const handleDislike = () => {
        dislikeDish(Number(meal.id))
        setDisliked(true)
        setLiked(false)
    }
    const handleFavourite = () => {
        if (favourited) {
            unfavouriteDish(Number(meal.id))
            setFavourited(false)
        } else {
            favouriteDish(Number(meal.id))
            setFavourited(true)
        }
    }

    const toggleExpanded = () => {
        setIsExpanded(!isExpanded)
    }

    const itemCost = (meal.price ?? 0) * quantity

    return (
        <Card className={`
            bg-white 
            shadow-md 
            hover:shadow-lg 
            transition-shadow 
            duration-200 
            overflow-hidden 
            p-0 
            gap-0 
            ${className}`
        }>
            {meal.imageHero && (
                <CardHeader
                    className="
                        cursor-pointer 
                        aspect-[16/7] 
                        -mt-px 
                        -mx-px 
                        p-0"
                    onClick={toggleExpanded}
                >
                    <img
                        src={meal.imageHero}
                        alt={meal.name}
                        className="
                            w-full 
                            h-full 
                            object-cover"
                    />
                </CardHeader>
            )}
            <CardContent
                className={`
                    px-4 
                    pb-1 
                    mt-0 
                    pt-0 
                    cursor-pointer`
                }
                onClick={toggleExpanded}
            >
                <div className="
                    flex 
                    flex-row 
                    justify-between 
                    items-start"
                >
                    <div className="
                        flex-1 
                        min-w-0"
                    >
                        <div className="
                            flex 
                            items-center 
                            justify-between"
                        >
                            <CardTitle className="
                                text-lg 
                                font-bold 
                                text-gray-800 
                                hover:text-orange-600 
                                transition-colors 
                                truncate"
                            >
                                {meal.name}
                            </CardTitle>
                            <div className="
                                flex 
                                items-center 
                                gap-4 
                                text-lg 
                                mb-2"
                            >
                                <div className="
                                    flex 
                                    items-center 
                                    gap-1 
                                    text-green-600 
                                    font-semibold"
                                >
                                    <span>
                                        R{meal.price?.toFixed(2) ?? '0.00'}
                                    </span>
                                </div>

                            </div>

                        </div>
                        <div className="
                            flex 
                            flex-wrap 
                            gap-2 
                            pb-2"
                        >
                            {meal.allergies?.map((allergy, index) => (
                                <Badge
                                    key={index}
                                    variant={userAllergies.includes(allergy) ? "destructive" : "secondary"}
                                    className={`
                                        text-xs 
                                        ${userAllergies.includes(allergy)
                                        ? 'bg-red-100 text-red-800 border-red-300 shadow-sm'
                                        : 'bg-gray-100 text-gray-700 border-gray-300'
                                        }`}
                                >
                                    {allergy}
                                </Badge>
                            )) ?? (
                                    <Badge 
                                        variant="outline" 
                                        className="
                                            text-xs 
                                            text-gray-500"
                                    >
                                        Geen allergieë
                                    </Badge>
                                )}
                        </div>
                        <div className="
                            flex 
                            items-center 
                            gap-1 
                            text-gray-600 
                            text-sm 
                            mb-2"
                        >
                            <Clock className="
                                h-4 
                                w-4" 
                            />
                            <span className="truncate">
                                Aflewering {formatDateLong(meal.deliveryDate)}
                            </span>
                        </div>
                    </div>

                </div>

                <div className="flex items-center gap-2">
                        {/* Like Button */}
                        <Button
                            size="sm"
                            variant={liked ? "default" : "outline"}
                            className={`rounded-full ${liked ? "bg-green-600 text-white" : "border-green-600 text-green-600"}`}
                            onClick={e => { e.stopPropagation(); handleLike(); }}
                            aria-label="Like"
                        >
                            <ThumbsUp />
                        </Button>
                        {/* Dislike Button */}
                        <Button
                            size="sm"
                            variant={disliked ? "default" : "outline"}
                            className={`rounded-full ${disliked ? "bg-red-600 text-white" : "border-red-600 text-red-600"}`}
                            onClick={e => { e.stopPropagation(); handleDislike(); }}
                            aria-label="Dislike"
                        >
                            <ThumbsDown />
                        </Button>
                        {/* Favourite Button */}
                        <Button
                            size="sm"
                            variant={favourited ? "default" : "outline"}
                            className={`rounded-full ${favourited ? "bg-yellow-400 text-white" : "border-yellow-400 text-yellow-600"}`}
                            onClick={e => { e.stopPropagation(); handleFavourite(); }}
                            aria-label="Favourite"
                        >
                            <Star />
                        </Button>
                    </div>

                {isExpanded && (
                    <div>
                        {meal.description && (
                            <div>
                                <p className="
                                    text-sm 
                                    text-gray-600 
                                    leading-relaxed 
                                    bg-gray-50 
                                    p-3 
                                    rounded-lg"
                                >
                                    {meal.description}
                                </p>
                            </div>
                        )}

                    </div>
                )}
            </CardContent>

            <CardContent className="
                px-4 
                pb-4"
            >
                <div
                    className="
                        flex 
                        items-center 
                        justify-between 
                        border-t 
                        border-gray-100"
                    onClick={(e) => e.stopPropagation()}
                >
                    <Label className="
                        text-sm 
                        font-medium 
                        text-gray-700"
                    >
                        Aantal:
                    </Label>
                    <div className="
                        flex 
                        items-center 
                        gap-2"
                    >
                        <Button
                            size="sm"
                            variant="outline"
                            className="
                                w-8 
                                h-8 
                                rounded-full 
                                border-orange-300 
                                text-orange-600 
                                hover:bg-orange-50 
                                hover:border-orange-400"
                            onClick={(e) => {
                                e.stopPropagation();
                                onQuantityChange(meal?.id ?? -1, -1);
                            }}
                        >
                            <Minus />
                        </Button>
                        <Input
                            type="number"
                            value={quantity}
                            readOnly
                            className="
                                w-16 
                                text-center 
                                font-medium 
                                border-gray-300 
                                focus:border-orange-400"
                        />
                        <Button
                            size="sm"
                            variant="outline"
                            className="
                                w-8 
                                h-8 
                                rounded-full 
                                border-orange-300 
                                text-orange-600 
                                hover:bg-orange-50 
                                hover:border-orange-400"
                            onClick={(e) => {
                                e.stopPropagation();
                                onQuantityChange(meal?.id ?? -1, 1);
                            }}
                        >
                            <Plus />
                        </Button>
                    </div>
                </div>
                {showCostBreakdown && quantity > 0 && (
                    <div className="
                        flex 
                        items-center 
                        gap-1 text-orange-600 
                        font-semibold"
                    >
                        <span>Totaal: R{itemCost.toFixed(2)}</span>
                    </div>
                )}
            </CardContent>
        </Card>
    )
}