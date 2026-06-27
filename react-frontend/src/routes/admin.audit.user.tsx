import { AdminAuditApi, type UserEventAuditDTO } from '@/api';
import { useAuth } from '@/contexts/AuthContext';
import { useQuery } from '@tanstack/react-query';
import { createFileRoute, Link } from '@tanstack/react-router'
import { type ColumnDef } from '@tanstack/react-table'
import { DataTable } from '@/components/table/Table'
import { Badge } from '@/components/ui/badge'
import { formatDate } from '@/lib/utils'

export const Route = createFileRoute('/admin/audit/user')({
  component: RouteComponent,
})

function RouteComponent() {
  const {getApiClient} = useAuth();
  const auditApi = getApiClient(AdminAuditApi);

  const {data: userLog, isLoading: userLogLoading} = useQuery({
    queryKey: ['userAuditLog'],
    queryFn: async () => (await auditApi.getUserEventLogPaginated(0, 100)).data.content,
    staleTime: 1000 * 60 * 5, // 5 minutes
    refetchOnWindowFocus: false,
  });

  const events: UserEventAuditDTO[] = userLog || []

  const columns: ColumnDef<UserEventAuditDTO>[] = [
    {
      accessorKey: 'timestamp',
      header: 'Time',
      filterFn: 'dateRange' as any,
      meta: {
        filterVariant: "date-range"
      },
      cell: ({ row }) => formatDate(row.getValue('timestamp')),
    },
    {
      id: 'user',
      accessorFn: (row) => `${row?.user?.firstName} ${row?.user?.lastName} (${row?.user?.email})`,
      header: 'User',
      cell: ({ row }) => {
        const user = row.original.user
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
      accessorKey: 'type',
      header: 'Event Type',
      cell: ({ row }) => {
        const type = row.getValue('type') as string
        return <Badge variant="outline">{type || 'N/A'}</Badge>
      },
    },
  ]

  return (
    <div className="container mx-auto py-6">
      <div className="mb-6">
        <h1 className="text-3xl font-bold">User Audit Log</h1>
        <p className="text-muted-foreground">
          View all user events and activity
        </p>
      </div>

      <DataTable
        columns={columns}
        data={events}
        enableSorting
        enableFiltering
        pageSize={10}
        loading={userLogLoading}
        enableReporting
        reportTitle='User Audit Report'
        reportFilename='user-audit'
        emptyMessage="No user audit logs found."
        className="space-y-4"
      />
    </div>
  )
}
