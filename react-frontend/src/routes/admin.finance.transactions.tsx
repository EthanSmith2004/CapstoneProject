import { AdminFinanceApi, type AdminTransactionDTO} from '@/api';
import { useAuth } from '@/contexts/AuthContext';
import { useQuery } from '@tanstack/react-query';
import { createFileRoute } from '@tanstack/react-router'
import { type ColumnDef } from '@tanstack/react-table'
import { DataTable } from '@/components/table/Table'
import { formatDate } from '@/lib/utils'
import { useState, useEffect } from 'react';
import { TimeSeriesGraph } from '@/components/graph/TimeSeriesGraph';
import type { ColumnFiltersState } from '@tanstack/react-table';

interface SearchParams {
  userEmail?: string;
}

export const Route = createFileRoute('/admin/finance/transactions')({
  component: RouteComponent,
  validateSearch: (search: Record<string, unknown>): SearchParams => ({
    userEmail: search.userEmail as string | undefined,
  }),
})

function RouteComponent() {
  const searchParams = Route.useSearch()
  const { getApiClient } = useAuth();
  const financeAPI = getApiClient(AdminFinanceApi);
  const [filteredOutput, setFilteredOutput] = useState<any[]>([]);
  const [filterState, setFilterState] = useState<ColumnFiltersState>([]);

  // Set initial filter when userEmail search param is present
  useEffect(() => {
    if (searchParams.userEmail) {
      setFilterState([{ id: 'user', value: searchParams.userEmail }])
    }
  }, [searchParams.userEmail])

  const { data: transactions, isLoading: isTransactionsLoading } = useQuery({
    queryKey: ['adminTransactions'],
    queryFn: async () => (await financeAPI.findTransactions()).data,
    staleTime: 1000 * 60 * 5, // 5 minutes
    refetchOnWindowFocus: false,
  });

  // Format amount to display with 2 decimal places and proper currency symbol
  const formatAmount = (amount?: number) => {
    if (amount === undefined) return 'R0.00';
    return new Intl.NumberFormat('en-ZA', { 
      style: 'currency', 
      currency: 'ZAR',
      minimumFractionDigits: 2 
    }).format(amount);
  };

  const columns: ColumnDef<AdminTransactionDTO>[] = [
    {
      accessorKey: 'id',
      header: 'ID',
      filterFn: 'includesString',
      cell: ({ row }) => <div>{row.getValue('id')}</div>,
    },
    {
      id: 'user',
      accessorFn: (r) => `${r.user?.firstName} ${r.user?.lastName} ${r.user?.email} ${r.user?.credentialNumber}`,
      header: 'User',
      cell: ({ row }) => {
        const user = row.original.user
        return (
          <div>
            <div className="font-medium">
              {user?.firstName} {user?.lastName}
            </div>
            <div className="text-sm text-muted-foreground">{user?.email || 'N/A'}</div>
            <div className="text-sm text-muted-foreground">{user?.credentialNumber|| 'N/A'}</div>
          </div>
        );
      },
    },
    {
      accessorKey: 'debit',
      header: 'Debit',
      filterFn: 'inNumberRange',
      meta: {
        filterVariant: 'numeric-range'
      },
      cell: ({ row }) => {
        const debit = row.getValue('debit') as number | undefined;
        return debit ? <div className="text-destructive font-medium">{formatAmount(debit)}</div> : <div>-</div>;
      },
    },
    {
      accessorKey: 'credit',
      header: 'Credit',
      filterFn: 'inNumberRange',
      meta: {
        filterVariant: 'numeric-range'
      },
      cell: ({ row }) => {
        const credit = row.getValue('credit') as number | undefined;
        return credit ? <div className="text-green-600 font-medium">{formatAmount(credit)}</div> : <div>-</div>;
      },
    },
    {
      accessorKey: 'runningBalance',
      header: 'Running Balance',
      filterFn: 'inNumberRange',
      meta: {
        filterVariant: 'numeric-range'
      },
      cell: ({ row }) => {
        const balance = row.getValue('runningBalance') as number | undefined;
        const isNegative = (balance || 0) < 0;
        return (
          <div className={isNegative ? "text-destructive font-medium" : "text-green-600 font-medium"}>
            {formatAmount(balance)}
          </div>
        );
      },
    },
    {
      accessorKey: 'description',
      header: 'Description',
      cell: ({ row }) => <div className='whitespace-pre-wrap'>{row.getValue('description') || 'N/A'}</div>,
    },
    {
      accessorKey: 'transactionDate',
      header: 'Date',
      filterFn: 'dateRange' as any,
      meta: {
        filterVariant: 'date-range',
        processDate: true
      },
      cell: ({ row }) => formatDate(row.getValue('transactionDate')),
    },
  ];

  return (
    <div className="container mx-auto py-6">
      <div className="mb-6">
        <h1 className="text-3xl font-bold">Transactions</h1>
        <p className="text-muted-foreground">
          View all financial transactions
        </p>
      </div>

      <TimeSeriesGraph 
        data={filteredOutput} 
        dateKey={'transactionDate'} 
        enabledAggregations={['daily', 'hourly']}
        valueKeys={[{ key: 'debit', label: 'Debit (Purchase) Transactions' }, { key: 'credit', label: 'Credit Transactions' }]}
        chartType='histogram'
        filterState={filterState}
        setColumnFilters={setFilterState}
      />

      <DataTable
        columns={columns}
        data={transactions || []}
        enableSorting
        enableFiltering
        enableReporting
        reportTitle='Transaction Report'
        reportFilename='transactions'
        pageSize={10}
        loading={isTransactionsLoading}
        onFilteredOutputChange={setFilteredOutput}
        emptyMessage="No transactions found."
        className="space-y-4"
        searchPlaceholder="Search transactions..."
        columnFilters={filterState}
        setColumnFilters={setFilterState}
      />
    </div>
  )
}
