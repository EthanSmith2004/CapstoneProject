import type { FinancialPeriodStatistic } from "@/api"
import { ArrowUp, ArrowDown } from 'lucide-react'

interface AdminFinanceStatsProps {
    stat: FinancialPeriodStatistic
    previousStat?: FinancialPeriodStatistic | null
    contrastName?: string
}

interface ComparisonProps {
    current: number
    previous?: number
    contrastName?: string
}

function ComparisonIndicator({ current, previous, contrastName }: ComparisonProps) {
    if (!previous || !contrastName) return null;

    const difference = current - previous;
    const percentageChange = previous !== 0 ? (difference / previous) * 100 : 0;
    const isPositive = difference >= 0;

    return (
        <div className={`flex items-center gap-1 text-sm mt-1 ${isPositive ? 'text-green-600' : 'text-red-600'}`}>
            {isPositive ? <ArrowUp className="h-4 w-4" /> : <ArrowDown className="h-4 w-4" />}
            <span className="font-medium">
                {isPositive ? 'Op' : 'Af'} {Math.abs(percentageChange).toFixed(1)}% 
                (R{Math.abs(difference).toFixed(2)}) 
                vanaf {contrastName}
            </span>
        </div>
    );
}

export function AdminFinanceStats({stat, previousStat, contrastName}: AdminFinanceStatsProps) {
    return (
        <div className="grid grid-cols-1 md:grid-cols-1 gap-4">
            <div className="p-4 bg-white rounded-lg shadow">
                <h2 className="text-lg font-medium mb-2">Bruto Verkope</h2>
                <p className="text-2xl font-bold text-green-600">R {stat.totalRevenue?.toFixed(2) ?? 0}</p>
                <ComparisonIndicator 
                    current={stat.totalRevenue ?? 0} 
                    previous={previousStat?.totalRevenue} 
                    contrastName={contrastName}
                />
            </div>            
            <div className="p-4 bg-white rounded-lg shadow">
                <h2 className="text-lg font-medium mb-2">Kanseleerbare Verkope</h2>
                <p className="text-2xl font-bold text-green-600">R {stat.totalRevenuePending?.toFixed(2) ?? 0}</p>
                <ComparisonIndicator 
                    current={stat.totalRevenuePending ?? 0} 
                    contrastName={contrastName}
                />
            </div>      
            <div className="p-4 bg-white rounded-lg shadow">
                <h2 className="text-lg font-medium mb-2">Gebruiker Krediet Laai Transaksies</h2>
                <p className="text-2xl font-bold text-green-600">R {stat.totalCreditTransactions?.toFixed(2) ?? 0}</p>
                <ComparisonIndicator 
                    current={stat.totalCreditTransactions ?? 0} 
                    previous={previousStat?.totalCreditTransactions} 
                    contrastName={contrastName}
                />
            </div>            
            <div className="p-4 bg-white rounded-lg shadow">
                <h2 className="text-lg font-medium mb-2">Terugbetalings</h2>
                <p className="text-2xl font-bold text-green-600">R {stat.totalRefunds?.toFixed(2) ?? 0}</p>
                <ComparisonIndicator 
                    current={stat.totalRefunds ?? 0} 
                    previous={previousStat?.totalRefunds} 
                    contrastName={contrastName}
                />
            </div>

        </div>
    );
}