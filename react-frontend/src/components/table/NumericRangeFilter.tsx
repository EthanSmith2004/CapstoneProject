import type { Column } from "@tanstack/react-table"
import React, { useEffect } from "react"
import { Input } from "../ui/input"
import { Button } from "../ui/button"

// Numeric range filter component
export function NumericRangeFilter<TData, TValue>({ column }: { column: Column<TData, TValue> }) {
  const [start, end] = column.getFilterValue() as (number | undefined)[] ?? [undefined, undefined]
  const [min, setMin] = React.useState<string>(start?.toString() ?? '')
  const [max, setMax] = React.useState<string>(end?.toString() ?? '')

  useEffect(() => {
    const minNum = min ? Number(min) : undefined
    const maxNum = max ? Number(max) : undefined
    console.log(minNum, maxNum)
    
    if (minNum !== undefined || maxNum !== undefined) {
      column.setFilterValue([minNum, maxNum])
    } else {
      column.setFilterValue(undefined)
    }
  }, [min, max])

  const clearFilter = () => {
    setMin('')
    setMax('')
    column.setFilterValue(undefined)
  }

  return (
    <div className="p-2 space-y-2 w-48">
      <div className="space-y-2">
        <Input
          type="number"
          placeholder="Min"
          value={min}
          onChange={(e) => setMin(e.target.value)}
          className="h-8"
        />
        <Input
          type="number"
          placeholder="Maks"
          value={max}
          onChange={(e) => setMax(e.target.value)}
          className="h-8"
        />
      </div>
      {(min || max) && (
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