import { Checkbox } from "../ui/checkbox";
import { DropdownMenuSeparator } from "@radix-ui/react-dropdown-menu";
import type { Column } from "@tanstack/react-table";
import React from "react";
import { Button } from "../ui/button";

// Multi select filter component
export function MultiSelectFilter<TData, TValue>({ 
  column 
}: { 
  column: Column<TData, TValue> 
}) {
  const options = (column.columnDef.meta as any)?.filterOptions as Array<{ label: string; value: string }> ?? []
  const [selectedValues, setSelectedValues] = React.useState<Set<string>>(
    new Set((column.getFilterValue() as string[]) ?? [])
  )

  const toggleValue = (value: string) => {
    const newSet = new Set(selectedValues)
    if (newSet.has(value)) {
      newSet.delete(value)
    } else {
      newSet.add(value)
    }
    setSelectedValues(newSet)
    column.setFilterValue(newSet.size > 0 ? Array.from(newSet) : undefined)
  }

  const clearAll = () => {
    setSelectedValues(new Set())
    column.setFilterValue(undefined)
  }

  const selectAll = () => {
    const newSet = new Set(options.map(opt => opt.value))
    setSelectedValues(newSet)
    column.setFilterValue(Array.from(newSet))
  }

  return (
    <div className="p-2 space-y-2 w-56">
      <div className="flex gap-2">
        <Button
          variant="outline"
          size="sm"
          onClick={selectAll}
          className="flex-1 h-7 text-xs"
        >
          Select all
        </Button>
        <Button
          variant="outline"
          size="sm"
          onClick={clearAll}
          className="flex-1 h-7 text-xs"
        >
          Clear
        </Button>
      </div>
      <DropdownMenuSeparator />
      <div className="space-y-1 max-h-64 overflow-y-auto">
        {options.map((option) => (
          <div
            key={option.value}
            className="flex items-center space-x-2 p-2 rounded cursor-pointer hover:bg-accent"
            onClick={() => toggleValue(option.value)}
          >
            <Checkbox
              checked={selectedValues.has(option.value)}
              onCheckedChange={() => toggleValue(option.value)}
              onClick={(e) => e.stopPropagation()}
            />
            <span className="text-sm">{option.label}</span>
          </div>
        ))}
      </div>
      {selectedValues.size > 0 && (
        <div className="text-xs text-muted-foreground pt-1">
          {selectedValues.size} van {options.length} selected
        </div>
      )}
    </div>
  )
}
