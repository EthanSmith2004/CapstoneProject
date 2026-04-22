import type { Column } from "@tanstack/react-table";
import React from "react";
import { Button } from "../ui/button";

// Single select filter component
export function SingleSelectFilter<TData, TValue>({ 
  column 
}: { 
  column: Column<TData, TValue> 
}) {
  const options = (column.columnDef.meta as any)?.filterOptions as Array<{ label: string; value: string }> ?? []
  const [value, setValue] = React.useState((column.getFilterValue() as string) ?? '')

  return (
    <div className="p-2 space-y-2 w-48">
      <div className="space-y-1 max-h-64 overflow-y-auto">
        {options.map((option) => (
          <div
            key={option.value}
            className={`flex items-center space-x-2 p-2 rounded cursor-pointer hover:bg-accent ${
              value === option.value ? 'bg-accent' : ''
            }`}
            onClick={() => {
              const newValue = value === option.value ? '' : option.value
              setValue(newValue)
              column.setFilterValue(newValue || undefined)
            }}
          >
            <div className={`h-4 w-4 rounded border ${
              value === option.value ? 'bg-primary border-primary' : 'border-input'
            }`}>
              {value === option.value && (
                <div className="h-full w-full flex items-center justify-center text-primary-foreground text-xs">✓</div>
              )}
            </div>
            <span className="text-sm">{option.label}</span>
          </div>
        ))}
      </div>
      {value && (
        <>
          <Button
            variant="outline"
            size="sm"
            onClick={() => {
              setValue('')
              column.setFilterValue(undefined)
            }}
            className="w-full"
          >
            Maak skoon
          </Button>
        </>
      )}
    </div>
  )
}