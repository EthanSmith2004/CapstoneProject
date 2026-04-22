import React from 'react'
import {
  type ColumnDef,
  flexRender,
  getCoreRowModel,
  getFilteredRowModel,
  getPaginationRowModel,
  getSortedRowModel,
  useReactTable,
  type Table as TSTable,
  type SortingState,
  type ColumnFiltersState,
  type VisibilityState,
  type RowSelectionState,
  type ExpandedState,
  getExpandedRowModel,
  type Row,
} from '@tanstack/react-table'
import { ChevronDown, ChevronUp, ChevronsUpDown, FileDown, FileText, FilterIcon, Search, X, ChevronRight } from 'lucide-react'

import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Checkbox } from '@/components/ui/checkbox'
import { DropdownMenu, DropdownMenuContent, DropdownMenuTrigger } from '../ui/dropdown-menu'
import { DateRangeFilter } from './DateRangeFilter'
import { NumericRangeFilter } from './NumericRangeFilter'
import { DateFilter } from './DateFilter'
import { MultiSelectFilter } from './MultiSelectFilter'
import { SingleSelectFilter } from './SingleSelectFilter'
import { StringFilter } from './StringFilter'
import { generateCSVReport, generatePDFReport, prepareDataForExport } from '@/lib/reporting'
import { toast } from 'sonner'

// Extend column meta to include filter type
declare module '@tanstack/react-table' {
  interface ColumnMeta<TData, TValue> {
    filterVariant?: 'string' | 'numeric-range' | 'date' | 'date-range' | 'single-select' | 'multi-select'
    filterOptions?: Array<{ label: string; value: string }>
    processDate?: boolean
  }
}

// Custom filter function for numeric range
const numericRangeFilterFn = (row: any, columnId: string, filterValue: [number | undefined, number | undefined]) => {
  const value = row.getValue(columnId) as number
  const [min, max] = filterValue

  if (min !== undefined && max !== undefined) {
    return value >= min && value <= max
  } else if (min !== undefined) {
    return value >= min
  } else if (max !== undefined) {
    return value <= max
  }
  
  return true
}

// Custom filter function for date range
const dateRangeFilterFn = (row: any, columnId: string, filterValue: [string | undefined, string | undefined]) => {
  const value = row.getValue(columnId) as string
  const [startDate, endDate] = filterValue

  if (!value) return false

  // Convert to comparable date strings (YYYY-MM-DD format)
  const rowDate = new Date(value).toISOString().split('T')[0]

  if (startDate && endDate) {
    return rowDate >= startDate && rowDate <= endDate
  } else if (startDate) {
    return rowDate >= startDate
  } else if (endDate) {
    return rowDate <= endDate
  }
  
  return true
}

// Custom filter function for date
const dateFilterFn = (row: any, columnId: string, filterValue: string) => {
  const value = row.getValue(columnId) as string
  if (!value) return false
  
  const rowDate = new Date(value).toISOString().split('T')[0]
  return rowDate === filterValue
}

// Custom filter function for multi-select
const multiSelectFilterFn = (row: any, columnId: string, filterValue: string[]) => {
  const value = row.getValue(columnId)
  return filterValue.includes(String(value))
}

const arrMultiSelectFilterFn = (row: any, columnId: string, filterValue: string[]) => {
  const value = row.getValue(columnId)
  for(let i of value)
  {
    if (filterValue.includes(String(i)))
    {
      return true
    }
  }

  return false
}

function exportTableData<T>(table: TSTable<T>) {
  const columns = table.getAllLeafColumns().map(col => col.columnDef.header as string);

  const rowModel = table.getSortedRowModel();

  const rows = rowModel.rows.map(row =>
    table.getAllLeafColumns().map(col => {
      const value = row.getValue(col.id);
      if (col.columnDef.meta?.processDate)
        return new Date(String(value));
      return value;
    })
  );

  return { columns, rows };
}

interface DataTableProps<TData, TValue> {
  columns: ColumnDef<TData, TValue>[]
  data: TData[]
  searchPlaceholder?: string
  enableRowSelection?: boolean
  enableSearching?: boolean
  enableSorting?: boolean
  enableFiltering?: boolean
  enablePagination?: boolean
  pageSize?: number
  className?: string
  emptyMessage?: string
  enableReporting?: boolean
  reportTitle?: string
  reportFilename?: string
  reportRowMap?: (row: any) => any;
  loading?: boolean
  onRowSelect?: (selectedRows: TData[]) => void
  onFilteredOutputChange?: (filtered: TData[]) => void
  columnFilters?: ColumnFiltersState
  setColumnFilters?: (filters: ColumnFiltersState) => void
  getRowCanExpand?: (row: Row<TData>) => boolean
  renderSubComponent?: (props: { row: Row<TData> }) => React.ReactElement
}

export function DataTable<TData, TValue>({
  columns,
  data,
  searchPlaceholder = "Search...",
  enableRowSelection = false,
  enableSearching = false,
  enableSorting = true,
  enableFiltering = false,
  enablePagination = true,
  enableReporting = false,
  onFilteredOutputChange,
  reportTitle,
  reportFilename = 'table-report',
  pageSize = 10,
  className,
  emptyMessage = "No results found.",
  loading = false,
  columnFilters: _externalColumnFilters,
  setColumnFilters: _setExternalColumnFilters,
  onRowSelect,
  getRowCanExpand,
  renderSubComponent,
}: DataTableProps<TData, TValue>) {
  const [sorting, setSorting] = React.useState<SortingState>([])
  const [internalColumnFilters, setInternalColumnFilters] = React.useState<ColumnFiltersState>([])
  const columnFilters = _externalColumnFilters ?? internalColumnFilters
  const setColumnFilters = _setExternalColumnFilters ?? setInternalColumnFilters
  const [columnVisibility, setColumnVisibility] = React.useState<VisibilityState>({})
  const [rowSelection, setRowSelection] = React.useState<RowSelectionState>({})
  const [globalFilter, setGlobalFilter] = React.useState('')
  const [expanded, setExpanded] = React.useState<ExpandedState>({})

  // Add row selection column and expand column if enabled
  const finalColumns = React.useMemo(() => {
    let cols = [...columns]

    // Add expand column if enabled
    if (getRowCanExpand) {
      const expandColumn: ColumnDef<TData, TValue> = {
        id: 'expand',
        header: () => null,
        cell: ({ row }) => {
          return row.getCanExpand() ? (
            <button
              onClick={row.getToggleExpandedHandler()}
              className="cursor-pointer p-1 hover:bg-accent rounded"
            >
              {row.getIsExpanded() ? (
                <ChevronDown className="h-4 w-4" />
              ) : (
                <ChevronRight className="h-4 w-4" />
              )}
            </button>
          ) : null
        },
        enableSorting: false,
        enableHiding: false,
      }
      cols = [expandColumn, ...cols]
    }

    // Add selection column if enabled
    if (enableRowSelection) {
      const selectColumn: ColumnDef<TData, TValue> = {
        id: 'select',
        header: ({ table }) => (
          <Checkbox
            checked={
              table.getIsAllPageRowsSelected() ||
              (table.getIsSomePageRowsSelected() && 'indeterminate')
            }
            onCheckedChange={(value) => table.toggleAllPageRowsSelected(!!value)}
            aria-label="Select all"
          />
        ),
        cell: ({ row }) => (
          <Checkbox
            checked={row.getIsSelected()}
            onCheckedChange={(value) => row.toggleSelected(!!value)}
            aria-label="Select row"
          />
        ),
        enableSorting: false,
        enableHiding: false,
      }
      cols = [selectColumn, ...cols]
    }

    return cols
  }, [columns, enableRowSelection, getRowCanExpand])

  const table = useReactTable({
    data,
    columns: finalColumns,
    getCoreRowModel: getCoreRowModel(),
    getPaginationRowModel: enablePagination ? getPaginationRowModel() : undefined,
    getSortedRowModel: enableSorting ? getSortedRowModel() : undefined,
    getFilteredRowModel: enableFiltering || enableSearching ? getFilteredRowModel() : undefined,
    getExpandedRowModel: getRowCanExpand ? getExpandedRowModel() : undefined,
    getRowCanExpand: getRowCanExpand,
    onSortingChange: enableSorting ? setSorting : undefined,
    onColumnFiltersChange: enableFiltering ? (updaterOrValue) => {
      const newValue = typeof updaterOrValue === 'function' 
        ? updaterOrValue(columnFilters) 
        : updaterOrValue
      setColumnFilters(newValue)
    } : undefined,
    onColumnVisibilityChange: setColumnVisibility,
    onRowSelectionChange: setRowSelection,
    onGlobalFilterChange: setGlobalFilter,
    onExpandedChange: setExpanded,
    globalFilterFn: 'includesString',
    filterFns: {
      numericRange: numericRangeFilterFn,
      dateRange: dateRangeFilterFn,
      date: dateFilterFn,
      multiSelect: multiSelectFilterFn,
      arrMultiSelect: arrMultiSelectFilterFn
    },
    state: {
      sorting: enableSorting ? sorting : undefined,
      columnFilters: enableFiltering ? columnFilters : undefined,
      columnVisibility,
      rowSelection: enableRowSelection ? rowSelection : undefined,
      globalFilter: enableFiltering ? globalFilter : undefined,
      expanded,
    },
    initialState: {
      pagination: {
        pageSize,
      },
    },
  })

  // Handle row selection callback
  React.useEffect(() => {
    if (enableRowSelection && onRowSelect) {
      const selectedRows = table.getFilteredSelectedRowModel().rows.map(row => row.original)
      onRowSelect(selectedRows)
    }
  }, [rowSelection, enableRowSelection, onRowSelect, table])

  // Store the callback in a ref to avoid re-running effect when it changes
  const onFilteredOutputChangeRef = React.useRef(onFilteredOutputChange)
  const tableRef = React.useRef(table)
  
  React.useEffect(() => {
    onFilteredOutputChangeRef.current = onFilteredOutputChange
    tableRef.current = table
  })

  React.useEffect(() => {
    if (onFilteredOutputChangeRef.current && tableRef.current) {
      const filteredRows = tableRef.current.getFilteredRowModel().rows.map(row => row.original)
      onFilteredOutputChangeRef.current(filteredRows)
    }
  }, [data, columnFilters, globalFilter]);

  // Export handlers
  const handleExportCSV = () => {
    try {
      const { columns: exportColumns, rows: exportRows } = exportTableData(table)
      const cleanedRows = prepareDataForExport(exportRows)
      generateCSVReport(exportColumns, cleanedRows, reportFilename)
      toast.success('CSV verslag suksesvol afgelaai')
    } catch (error) {
      console.error('CSV export error:', error)
      toast.error('Kon nie CSV verslag genereer nie')
    }
  }

  const handleExportPDF = () => {
    try {
      const { columns: exportColumns, rows: exportRows } = exportTableData(table)
      const cleanedRows = prepareDataForExport(exportRows)
      generatePDFReport(exportColumns, cleanedRows, reportFilename, reportTitle)
      toast.success('PDF verslag suksesvol afgelaai')
    } catch (error) {
      console.error('PDF export error:', error)
      toast.error('Kon nie PDF verslag genereer nie')
    }
  }

  return (
    <div className={className}>
      {/* Search and Filters */}
      {enableSearching && (
        <div className="flex items-center justify-between pb-4">
          <div className="relative max-w-sm">
            <Search className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
            <Input
              placeholder={searchPlaceholder}
              value={globalFilter}
              onChange={(event) => setGlobalFilter(event.target.value)}
              className="pl-9 pr-9"
            />
            {globalFilter && (
              <Button
                variant="ghost"
                size="sm"
                className="absolute right-1 top-1/2 h-6 w-6 -translate-y-1/2 p-0"
                onClick={() => setGlobalFilter('')}
              >
                <X className="h-3 w-3" />
              </Button>
            )}
          </div>

          {enableRowSelection && (
            <div className="text-sm text-muted-foreground">
              {table.getFilteredSelectedRowModel().rows.length} of{' '}
              {table.getFilteredRowModel().rows.length} row(s) selected
            </div>
          )}
        </div>
      )}
      {enableReporting && (
        <div className="mb-4 flex gap-2">
          <Button 
            variant='outline' 
            onClick={handleExportPDF}
            className="gap-2"
          >
            <FileText className="h-4 w-4" />
            PDF Verslag
          </Button>
          <Button 
            variant='outline' 
            onClick={handleExportCSV}
            className="gap-2"
          >
            <FileDown className="h-4 w-4" />
            CSV Verslag
          </Button>
        </div>
      )}
      {/* Table */}
      <div className="rounded-md border">
        <Table>
          <TableHeader>
            {table.getHeaderGroups().map((headerGroup) => (
              <TableRow key={headerGroup.id}>
                {headerGroup.headers.map((header) => (
                  <TableHead key={header.id} className="relative">
                    {header.isPlaceholder ? null : (
                      <div
                        className={
                          enableSorting && header.column.getCanSort()
                            ? 'flex items-center space-x-2 cursor-pointer select-none hover:text-foreground'
                            : 'flex items-center space-x-2'
                        }
                        onClick={
                          enableSorting && header.column.getCanSort()
                            ? header.column.getToggleSortingHandler()
                            : undefined
                        }
                      >
                        <span className='ml-2'>
                          {flexRender(header.column.columnDef.header, header.getContext())}
                        </span>
                        {enableSorting && header.column.getCanSort() && (
                          <span className="ml-1">
                            {header.column.getIsSorted() === 'desc' ? (
                              <ChevronDown className="h-4 w-4" />
                            ) : header.column.getIsSorted() === 'asc' ? (
                              <ChevronUp className="h-4 w-4" />
                            ) : (
                              <ChevronsUpDown className="h-4 w-4 opacity-50" />
                            )}
                          </span>
                        )}
                        {enableFiltering && header.column.getCanFilter() && (
                            <DropdownMenu>
                              <DropdownMenuTrigger asChild>
                                <Button
                                  variant="ghost"
                                  size="sm"
                                  className="h-6 w-6 p-0 hover:bg-accent"
                                  onClick={(e) => e.stopPropagation()}
                                >
                                  <FilterIcon
                                    fill={header.column.getIsFiltered() ? 'currentColor' : 'none'}
                                    className={'h-4 w-4 ' + (header.column.getIsFiltered() ? '' : 'opacity-50')}
                                  />
                                </Button>
                              </DropdownMenuTrigger>
                              <DropdownMenuContent align="start" onClick={(e) => e.stopPropagation()}>
                                {(() => {
                                  const filterVariant = header.column.columnDef.meta?.filterVariant
                                  
                                  switch (filterVariant) {
                                    case 'numeric-range':
                                      return <NumericRangeFilter column={header.column} />
                                    case 'date':
                                      return <DateFilter column={header.column} />
                                    case 'date-range':
                                      return <DateRangeFilter column={header.column} />
                                    case 'single-select':
                                      return <SingleSelectFilter column={header.column} />
                                    case 'multi-select':
                                      return <MultiSelectFilter column={header.column} />
                                    default:
                                      return <StringFilter column={header.column} />
                                  }
                                })()}
                              </DropdownMenuContent>
                            </DropdownMenu>
                        )}
                      </div>
                    )}
                  </TableHead>
                ))}
              </TableRow>
            ))}
          </TableHeader>
          <TableBody>
            {loading ? (
              <TableRow>
                <TableCell
                  colSpan={finalColumns.length}
                  className="h-24 text-center"
                >
                  Loading...
                </TableCell>
              </TableRow>
            ) : table.getRowModel().rows?.length ? (
              table.getRowModel().rows.map((row) => (
                <React.Fragment key={row.id}>
                  <TableRow
                    data-state={enableRowSelection && row.getIsSelected() && 'selected'}
                    className={enableRowSelection && row.getIsSelected() ? 'bg-muted/50' : ''}
                  >
                    {row.getVisibleCells().map((cell) => (
                      <TableCell key={cell.id}>
                        {flexRender(cell.column.columnDef.cell, cell.getContext())}
                      </TableCell>
                    ))}
                  </TableRow>
                  {row.getIsExpanded() && renderSubComponent && (
                    <TableRow>
                      <TableCell colSpan={finalColumns.length} className="p-0">
                        {renderSubComponent({ row })}
                      </TableCell>
                    </TableRow>
                  )}
                </React.Fragment>
              ))
            ) : (
              <TableRow>
                <TableCell
                  colSpan={finalColumns.length}
                  className="h-24 text-center text-muted-foreground"
                >
                  {emptyMessage}
                </TableCell>
              </TableRow>
            )}
          </TableBody>
        </Table>
      </div>

      {/* Pagination */}
      {enablePagination && (
        <div className="flex items-center justify-between space-x-2 py-4">
          <div className="text-sm text-muted-foreground">
            Vertoon {table.getState().pagination.pageIndex * table.getState().pagination.pageSize + 1} na{' '}
            {Math.min(
              (table.getState().pagination.pageIndex + 1) * table.getState().pagination.pageSize,
              table.getFilteredRowModel().rows.length
            )}{' '}
            van {table.getFilteredRowModel().rows.length} rekords
          </div>
          <div className="flex items-center space-x-2">
            <Button
              variant="outline"
              size="sm"
              onClick={() => table.previousPage()}
              disabled={!table.getCanPreviousPage()}
            >
              Vorige
            </Button>
            <div className="flex items-center space-x-1">
              <span className="text-sm">Bladsy</span>
              <span className="text-sm font-medium">
                {table.getState().pagination.pageIndex + 1} van {table.getPageCount()}
              </span>
            </div>
            <Button
              variant="outline"
              size="sm"
              onClick={() => table.nextPage()}
              disabled={!table.getCanNextPage()}
            >
              Volgende
            </Button>
          </div>
        </div>
      )}


    </div>
  )
}

