import { AdminAuditApi, type TransactionAuditDTO } from '@/api';
import { useAuth } from '@/contexts/AuthContext';
import { useQuery } from '@tanstack/react-query';
import { createFileRoute, Link } from '@tanstack/react-router'
import { type ColumnDef } from '@tanstack/react-table'
import { DataTable } from '@/components/table/Table'
import { Badge } from '@/components/ui/badge'
import { formatDate } from '@/lib/utils'
import { FileText, User } from 'lucide-react'

export const Route = createFileRoute('/admin/audit/transaction')({
  component: RouteComponent,
})

// Sub-component for transaction details
function TransactionDetailsSubTable({ auditId }: { auditId: number | undefined }) {
  const { getApiClient, user } = useAuth();
  const auditApi = getApiClient(AdminAuditApi);

  const { data: transactionDetails, isLoading } = useQuery({
    queryKey: ['transactionDetails', auditId],
    queryFn: async () => {
      if (!auditId) return [];
      return (await auditApi.getTransactionDetailsForAudit(auditId)).data;
    },
    enabled: !!auditId,
    staleTime: 1000 * 60 * 5,
  });

  const formatAmount = (amount?: number) => {
    if (amount === undefined || amount === null) return 'R0.00';
    return new Intl.NumberFormat('en-ZA', { 
      style: 'currency', 
      currency: 'ZAR',
      minimumFractionDigits: 2 
    }).format(amount);
  };

  // Check if user has the required roles
  const hasFinanceRole = user?.roles.includes('ROLE_FINANCIAL_ADMIN') || user?.roles.includes('ROLE_ADMIN');
  const hasUserAdminRole = user?.roles.includes('ROLE_USER_ADMIN') || user?.roles.includes('ROLE_ADMIN');

  const columns: ColumnDef<any>[] = [
    {
      accessorKey: 'transactionDate',
      header: 'Date',
      cell: ({ row }) => (
        <div className="text-sm">
          {row.getValue('transactionDate') ? formatDate(row.getValue('transactionDate')) : 'N/A'}
        </div>
      ),
    },
    {
      accessorKey: 'description',
      header: 'Description',
      cell: ({ row }) => <div className="text-sm">{row.getValue('description') || 'N/A'}</div>,
    },
    {
      accessorKey: 'debit',
      header: 'Debit',
      cell: ({ row }) => {
        const debit = row.getValue('debit') as number | undefined;
        return (
          <div className="text-right text-sm text-destructive font-medium">
            {debit ? formatAmount(debit) : '-'}
          </div>
        );
      },
    },
    {
      accessorKey: 'credit',
      header: 'Credit',
      cell: ({ row }) => {
        const credit = row.getValue('credit') as number | undefined;
        return (
          <div className="text-right text-sm text-green-600 font-medium">
            {credit ? formatAmount(credit) : '-'}
          </div>
        );
      },
    },
    {
      accessorKey: 'runningBalance',
      header: 'Balance',
      cell: ({ row }) => {
        const balance = row.getValue('runningBalance') as number | undefined;
        const isNegative = (balance || 0) < 0;
        return (
          <div className={`text-right text-sm font-medium ${isNegative ? 'text-destructive' : 'text-green-600'}`}>
            {balance !== undefined && balance !== null ? formatAmount(balance) : 'N/A'}
          </div>
        );
      },
    },
    {
      id: 'accountOwner',
      accessorFn: (row) => `${row.accountOwnerName} ${row.accountOwnerEmail}`,
      header: 'Account Owner',
      cell: ({ row }) => (
        <div className="text-sm">
          <div className="font-medium">{row.original.accountOwnerName || 'N/A'}</div>
          <div className="text-muted-foreground">{row.original.accountOwnerEmail || 'N/A'}</div>
        </div>
      ),
    },
    {
      id: 'actions',
      header: 'Actions',
      cell: ({ row }) => {
        const userEmail = row.original.accountOwnerEmail;
        
        return (
          <div className="flex gap-2">
            {hasUserAdminRole && userEmail && (
              <Link
                to="/admin/user"
                search={{ userEmail }}
                className="inline-flex items-center justify-center rounded-md text-sm font-medium transition-colors hover:bg-accent hover:text-accent-foreground h-9 px-3"
                title="View user"
              >
                <User className="h-4 w-4" />
              </Link>
            )}
            {hasFinanceRole && userEmail && (
              <Link
                to="/admin/finance/transactions"
                search={{ userEmail }}
                className="inline-flex items-center justify-center rounded-md text-sm font-medium transition-colors hover:bg-accent hover:text-accent-foreground h-9 px-3"
                title="View transactions"
              >
                <FileText className="h-4 w-4" />
              </Link>
            )}
          </div>
        );
      },
    },
  ];

  if (isLoading) {
    return (
      <div className="p-4 text-center text-muted-foreground">
        Loading transactions...
      </div>
    );
  }

  if (!transactionDetails || transactionDetails.length === 0) {
    return (
      <div className="p-4 text-center text-muted-foreground">
        No transactions found.
      </div>
    );
  }

  return (
    <div className="p-4 bg-muted/30">
      <h4 className="font-semibold mb-3 text-sm">Transaction Details</h4>
      <DataTable
        columns={columns}
        data={transactionDetails}
        enableSorting
        enableFiltering
        pageSize={5}
        emptyMessage="No transactions found."
      />
    </div>
  );
}

function RouteComponent() {
  const {getApiClient} = useAuth();
  const auditApi = getApiClient(AdminAuditApi);

  const {data: transactionLog, isLoading: transactionLogLoading} = useQuery({
    queryKey: ['transactionAuditLog'],
    queryFn: async () => (await auditApi.getTransactionAuditPaginated(0, 100)).data.content,
    staleTime: 1000 * 60 * 5,
    refetchOnWindowFocus: false,
  });

  const transactions: TransactionAuditDTO[] = transactionLog || []

  const columns: ColumnDef<TransactionAuditDTO>[] = [
    {
      id: 'compactUser',
      accessorFn: (row) => `${row?.compactUser?.firstName} ${row?.compactUser?.lastName} (${row?.compactUser?.email})`,
      header: 'User',
      cell: ({ row }) => {
        const user = row.original.compactUser
        if (!user) return <div>N/A</div>
        return (
          <Link 
            to="/admin/user" 
            search={{ userId: user.id }}
            className="hover:underline text-blue-600 hover:text-blue-800"
          >
            {user.firstName} {user.lastName} ({user.email})
          </Link>
        )
      },
    },
    {
      accessorKey: 'transactionAuditType',
      header: 'Type',
      cell: ({ row }) => {
        const type = row.getValue('transactionAuditType') as string
        return <Badge variant="outline">{type || 'N/A'}</Badge>
      },
    },
    {
      accessorKey: 'transactionCount',
      header: 'Transactions',
      cell: ({ row }) => {
        const count = row.getValue('transactionCount') as number | undefined
        return <div>{count !== undefined ? `${count} transaction(s)` : 'N/A'}</div>
      },
    },
    {
      accessorKey: 'loadedContent',
      header: 'Details',
      cell: ({ row }) => <div className="max-w-md truncate text-sm">{row.getValue('loadedContent') || 'N/A'}</div>,
    },
  ]

  return (
    <div className="container mx-auto py-6">
      <div className="mb-6">
        <h1 className="text-3xl font-bold">Transaction Audit Log</h1>
        <p className="text-muted-foreground">
          View all transaction events and activity
        </p>
      </div>

      <DataTable
        columns={columns}
        data={transactions}
        enableSorting
        enableFiltering
        pageSize={10}
        loading={transactionLogLoading}
        enableReporting
        reportTitle='Transaction Audit Report'
        reportFilename='transaction-audit'
        emptyMessage="No transaction audit logs found."
        className="space-y-4"
        getRowCanExpand={(row) => {
          const count = row.original.transactionCount
          return count !== undefined && count > 0
        }}
        renderSubComponent={({ row }) => {
          return <TransactionDetailsSubTable auditId={row.original.id} />
        }}
      />
    </div>
  )
}
