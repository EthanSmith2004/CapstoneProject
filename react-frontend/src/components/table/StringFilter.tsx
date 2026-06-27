import React from "react"
import { Input } from "../ui/input"
import { Button } from "../ui/button"
import type { Column } from "@tanstack/react-table"

// String filter component
export function StringFilter<TData, TValue>({ column }: { column: Column<TData, TValue> }) {
  const [value, setValue] = React.useState((column.getFilterValue() as string) ?? '')

  return (
    <div className="p-2 space-y-2">
      <Input
        placeholder="Search..."
        value={value}
        onChange={(e) => {
          setValue(e.target.value)
          column.setFilterValue(e.target.value || undefined)
        }}
        className="h-8"
      />
      {value && (
        <Button
          variant="outline"
          size="sm"
          onClick={() => {
            setValue('')
            column.setFilterValue(undefined)
          }}
          className="w-full"
        >
          Clear
        </Button>
      )}
    </div>
  )
}


