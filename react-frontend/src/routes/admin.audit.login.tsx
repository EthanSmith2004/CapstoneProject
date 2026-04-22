import { AdminAuditApi, type UserEventAuditDTO } from '@/api';
import { useAuth } from '@/contexts/AuthContext';
import { useQuery } from '@tanstack/react-query';
import { createFileRoute, Link } from '@tanstack/react-router'
import { type ColumnDef } from '@tanstack/react-table'
import { DataTable } from '@/components/table/Table'
import { Badge } from '@/components/ui/badge'
import { formatDate } from '@/lib/utils'

export const Route = createFileRoute('/admin/audit/login')({
  component: RouteComponent,
})

function RouteComponent() {
  const {getApiClient} = useAuth();
  const auditApi = getApiClient(AdminAuditApi);

  const {data: loginLog, isLoading: loginLogLoading} = useQuery({
    queryKey: ['loginAuditLog'],
    queryFn: async () => (await auditApi.getRecentLogins()).data,
    staleTime: 1000 * 60 * 5, // 5 minutes
    refetchOnWindowFocus: false,
  });

  const logins: UserEventAuditDTO[] = loginLog || []

  const columns: ColumnDef<UserEventAuditDTO>[] = [
    {
      accessorKey: 'timestamp',
      header: 'Tyd',
      filterFn: 'dateRange' as any,
      meta: {
        filterVariant: "date-range"
      },
      cell: ({ row }) => formatDate(row.getValue('timestamp')),
    },
    {
      id: 'user',
      accessorFn: (row) => `${row?.user?.firstName} ${row?.user?.lastName} (${row?.user?.email})`,
      header: 'Gebruiker',
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
      header: 'Gebeurtenis Tipe',
      cell: ({ row }) => {
        const type = row.getValue('type') as string
        const isLogin = type?.includes('LOGIN')
        return (
          <Badge variant={isLogin ? 'default' : 'secondary'}>
            {type || 'N/A'}
          </Badge>
        )
      },
    },
  ]

  return (
    <div className="container mx-auto py-6">
      <div className="mb-6">
        <h1 className="text-3xl font-bold">Aanteken Oudit Logboek</h1>
        <p className="text-muted-foreground">
          Bekyk alle aanteken pogings en aktiwiteite
        </p>
      </div>

      <DataTable
        columns={columns}
        data={logins}
        enableSorting
        enableFiltering
        pageSize={10}
        loading={loginLogLoading}
        enableReporting
        reportTitle='Aanteken Oudit Verslag'
        reportFilename='aanteken-oudit'
        emptyMessage="Geen aanteken logboeke gevind nie."
        className="space-y-4"
      />
    </div>
  )
}
