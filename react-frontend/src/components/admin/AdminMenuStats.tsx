import type { MenuItemStatisticsDTO } from "@/api"
import { ArrowUp, ArrowDown, ChevronDown, ChevronUp, EllipsisVertical } from 'lucide-react'
import { useState } from 'react'
import { DropdownMenu, DropdownMenuContent, DropdownMenuTrigger} from "../ui/dropdown-menu"
import { DropdownMenuItem } from "@radix-ui/react-dropdown-menu"
import { useNavigate } from "@tanstack/react-router"

interface AdminMenuStatsProps {
    stats: MenuItemStatisticsDTO[]
    previousStats?: MenuItemStatisticsDTO[] | null
    contrastName?: string
}

interface ComparisonProps {
    current: number
    previous?: number
    contrastName?: string
    isRevenue?: boolean
}

function ComparisonIndicator({ current, previous, contrastName, isRevenue = false }: ComparisonProps) {
    if (!previous || !contrastName) return null;

    const difference = current - previous;
    const percentageChange = previous !== 0 ? (difference / previous) * 100 : 0;
    const isPositive = difference >= 0;

    return (
        <div className={`flex items-center gap-1 text-sm mt-1 ${isPositive ? 'text-green-600' : 'text-red-600'}`}>
            {isPositive ? <ArrowUp className="h-4 w-4" /> : <ArrowDown className="h-4 w-4" />}
            <span className="font-medium">
                {isPositive ? 'Op' : 'Af'} {Math.abs(percentageChange).toFixed(1)}% 
                ({isRevenue ? 'R' : ''}{Math.abs(difference).toFixed(isRevenue ? 2 : 0)}) 
                vanaf {contrastName}
            </span>
        </div>
    );
}

interface TopSellersListProps {
    title: string
    items: MenuItemStatisticsDTO[]
    valueKey: 'totalRevenue' | 'totalQuantity'
    isRevenue?: boolean
}

function TopSellersList({ title, items, valueKey, isRevenue = false }: TopSellersListProps) {
    const navigate = useNavigate();

    const [isExpanded, setIsExpanded] = useState(false);
    
    const sortedItems = items.sort((a, b) => (b[valueKey] ?? 0) - (a[valueKey] ?? 0));
    const topThreeItems = sortedItems.slice(0, 3);
    const itemsToShow = isExpanded ? sortedItems : topThreeItems;

    if (sortedItems.length === 0) {
        return (
            <div className="p-4 bg-white rounded-lg shadow">
                <h3 className="text-lg font-medium mb-2">{title}</h3>
                <p className="text-gray-500">Geen data beskikbaar</p>
            </div>
        );
    }

    return (
        <div className="p-4 bg-white rounded-lg shadow">
            <button
                onClick={() => setIsExpanded(!isExpanded)}
                className="flex items-center justify-between w-full text-left mb-3"
            >
                <h3 className="text-lg font-medium">{title}</h3>
                {sortedItems.length > 3 && (
                    <div className="flex items-center gap-2">
                        {isExpanded ? <ChevronUp className="h-5 w-5" /> : <ChevronDown className="h-5 w-5" />}
                    </div>
                )}
            </button>
            
            <div className="space-y-2">
                {itemsToShow.map((item, index) => (
                    <div key={item.menuItemId || index} className="flex justify-between items-center p-2 bg-gray-50 rounded">
                        <div>
                            <span className="font-medium text-sm">#{index + 1}</span>
                            <span className="ml-2 text-sm">{item.menuItemName}</span>
                        </div>
                        <div className="flex items-center">
                            <span className="font-semibold text-sm">
                                {isRevenue ? 'R' : ''}{(item[valueKey] ?? 0).toFixed(isRevenue ? 2 : 0)}
                            </span>
                            <DropdownMenu>
                                <DropdownMenuTrigger className="ml-2 p-1 rounded hover:bg-gray-200">
                                    <EllipsisVertical size={16} />
                                </DropdownMenuTrigger>
                                <DropdownMenuContent align="end" className="bg-white shadow-lg rounded-md p-2">
                                    <DropdownMenuItem onClick={() => {
                                        navigate({to: '/admin/feedback/list', search: { menuItem: item.menuItemName || '' } });
                                    }}>
                                        Bekyk terugvoer
                                    </DropdownMenuItem>
                                </DropdownMenuContent>
                            </DropdownMenu>
                        </div>
                    </div>
                ))}
            </div>
            
            {sortedItems.length > 3 && !isExpanded && (
                <div className="mt-2 text-center">
                    <span className="text-sm text-gray-500 cursor-pointer" onClick={() => setIsExpanded(true)}>
                        en {sortedItems.length - 3} meer items...
                    </span>
                </div>
            )}
        </div>
    );
}

export function AdminMenuStats({ stats, previousStats, contrastName }: AdminMenuStatsProps) {
    // Calculate totals
    const totalRevenue = stats.reduce((sum, item) => sum + (item.totalRevenue ?? 0), 0);
    const totalQuantity = stats.reduce((sum, item) => sum + (item.totalQuantity ?? 0), 0);

    // Calculate previous totals for comparison
    const previousTotalRevenue = previousStats?.reduce((sum, item) => sum + (item.totalRevenue ?? 0), 0);
    const previousTotalQuantity = previousStats?.reduce((sum, item) => sum + (item.totalQuantity ?? 0), 0);

    return (
        <div className="space-y-6">
            {/* Overall Stats */}
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div className="p-4 bg-white rounded-lg shadow">
                    <h2 className="text-lg font-medium mb-2">Bruto Verkope</h2>
                    <p className="text-2xl font-bold text-green-600">R {totalRevenue.toFixed(2)}</p>
                    <ComparisonIndicator 
                        current={totalRevenue} 
                        previous={previousTotalRevenue} 
                        contrastName={contrastName}
                        isRevenue={true}
                    />
                </div>
                
                <div className="p-4 bg-white rounded-lg shadow">
                    <h2 className="text-lg font-medium mb-2">Totale Items</h2>
                    <p className="text-2xl font-bold text-blue-600">{totalQuantity}</p>
                    <ComparisonIndicator 
                        current={totalQuantity} 
                        previous={previousTotalQuantity} 
                        contrastName={contrastName}
                    />
                </div>
            </div>

            {/* Top Sellers */}
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <TopSellersList 
                    title="Top verkopers: Bruto Verkope"
                    items={stats}
                    valueKey="totalRevenue"
                    isRevenue={true}
                />
                
                <TopSellersList 
                    title="Top verkopers: Aantal"
                    items={stats}
                    valueKey="totalQuantity"
                />
            </div>
        </div>
    );
}