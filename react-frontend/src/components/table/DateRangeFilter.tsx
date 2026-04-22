import type { Column } from "@tanstack/react-table"
import React, { useEffect } from "react"
import { Input } from "../ui/input"
import { Button } from "@/components/ui/button"

// Date range filter component
export function DateRangeFilter<TData, TValue>({ column }: { column: Column<TData, TValue> }) {
    const [start, end] = column.getFilterValue() as (string | undefined)[] ?? ['', '']
    const [startDate, setStartDate] = React.useState<string>(start ?? '')
    const [endDate, setEndDate] = React.useState<string>(end ?? '')

    useEffect(() => {
        if (startDate || endDate) {
            column.setFilterValue([startDate || undefined, endDate || undefined])
        } else {
            column.setFilterValue(undefined)
        }
    },[startDate, endDate])

    const clearFilter = () => {
        setStartDate('')
        setEndDate('')
        column.setFilterValue(undefined)
    }

    return (
        <div className="p-2 space-y-2 w-56">
            <div className="space-y-2">
                <div>
                    <label className="text-xs text-muted-foreground">Van</label>
                    <Input
                        type="date"
                        value={startDate}
                        onChange={(e) => setStartDate(e.target.value)}
                        className="h-8"
                    />
                </div>
                <div>
                    <label className="text-xs text-muted-foreground">Tot</label>
                    <Input
                        type="date"
                        value={endDate}
                        onChange={(e) => setEndDate(e.target.value)}
                        className="h-8"
                    />
                </div>
            </div>
            {(startDate || endDate) && (
                <Button
                    variant="outline"
                    size="sm"
                    onClick={clearFilter}
                    className="w-full"
                >
                    Maak skoon
                </Button>
            )}
        </div>
    )
}