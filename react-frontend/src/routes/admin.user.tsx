import { createFileRoute } from '@tanstack/react-router'
import { type ColumnDef, type ColumnFiltersState } from '@tanstack/react-table'
import { Edit, Shield, ShieldOff } from 'lucide-react'
import { useState, useEffect } from 'react'

import { AdminUserManagementApi, type UserEntity, type AdminUpdateUserRequest, type AdminCreateUserRequest } from '@/api'
import { useAuth } from '@/contexts/AuthContext'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { DataTable } from '@/components/table/Table'
import { Button } from '@/components/ui/button'
import { Badge } from '@/components/ui/badge'
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from '@/components/ui/dialog'
import { UserForm } from '@/components/forms/UserForm'
import { formatDate } from '@/lib/utils'
import { RolesEnum } from '@/api'
import { toast } from 'sonner'

interface SearchParams {
  userId?: number;
  userEmail?: string;
}

export const Route = createFileRoute('/admin/user')({
  component: RouteComponent,
  validateSearch: (search: Record<string, unknown>): SearchParams => ({
    userId: search.userId as number | undefined,
    userEmail: search.userEmail as string | undefined,
  }),
})

function RouteComponent() {
  const searchParams = Route.useSearch()
  const { getApiClient, user } = useAuth()
  const apiClient = getApiClient(AdminUserManagementApi)
  const queryClient = useQueryClient()

  const [isDialogOpen, setIsDialogOpen] = useState(false)
  const [selectedUser, setSelectedUser] = useState<UserEntity | undefined>(undefined)
  const [columnFilters, setColumnFilters] = useState<ColumnFiltersState>([])

  // Set initial filter when userId search param is present
  useEffect(() => {
    if (searchParams.userId) {
      setColumnFilters([{ id: 'id', value: searchParams.userId.toString() }])
    }
    if (searchParams.userEmail) {
      setColumnFilters([{ id: 'user', value: searchParams.userEmail }])
    }
  }, [searchParams.userId, searchParams.userEmail])

  const { data: usersResponse, isLoading: isUsersLoading } = useQuery({
    queryKey: ['admin-users'],
    queryFn: () => apiClient.getAllUsers(),
    staleTime: 1000 * 60 * 5, // 5 minutes
    refetchOnWindowFocus: false,
  })

  const createUserMutation = useMutation({
    mutationFn: (data: AdminCreateUserRequest) => apiClient.createUser(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['admin-users'] })
      setIsDialogOpen(false)
      toast.success('Gebruiker suksesvol geskep')
    },
    onError: (error: any) => {
      toast.error(error.response?.data?.message || 'Kon nie gebruiker skep nie')
    },
  })

  const updateUserMutation = useMutation({
    mutationFn: (data: {id: number, data: AdminUpdateUserRequest}) => apiClient.updateUser(data.id, data.data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['admin-users'] })
      setIsDialogOpen(false)
      toast.success('Gebruiker suksesvol opgedateer')
    },
    onError: (error: any) => {
      toast.error(error.response?.data?.message || 'Kon nie gebruiker opdateer nie')
    },
  })

  // Get all possible roles:
  const roles = Object.values(RolesEnum).map(v => ({label: v, value: v}));

  const users: UserEntity[] = usersResponse?.data || []

  const columns: ColumnDef<UserEntity>[] = [
    {
      accessorKey: 'id',
      header: 'ID',
      enableColumnFilter: true,
      enableGlobalFilter: false,
      filterFn: 'equalsString',
      meta: {
        filterVariant: 'string',
      },
    },
    {
      id: 'user',
      header: 'Gebruiker',
      accessorFn: (row) => `${row.firstName} ${row.lastName} ${row.email}`,
      cell: ({ row }) => {
        const user = row.original;
        return (
          <div>
            <div className="font-medium">
              {user?.firstName} {user?.lastName}
            </div>
            <div className="text-sm text-muted-foreground">{user?.email || 'N/A'}</div>
          </div>
        );
      },
    },
    {
      accessorKey: 'roles',
      header: 'Rolle',
      enableSorting: false,
      filterFn: 'arrMultiSelect' as any,
      meta: {
        filterVariant: 'multi-select',
        filterOptions: roles
      },
      cell: ({ row }) => {
        const roles = row.getValue('roles') as Set<string> | undefined
        const roleArray = roles ? Array.from(roles) : []
        return (
          <div className="flex flex-wrap gap-1">
            {roleArray.map((role) => (
              <Badge
                key={role}
                variant={role.includes('ADMIN') ? 'default' : 'secondary'}
                className="text-xs"
              >
                {role.replace('ROLE_', '')}
              </Badge>
            ))}
          </div>
        )
      },
    },
    {
      id: 'enabled',
      accessorFn: (row) => row.enabled ? 'Aktief' : 'Onaktief',
      header: 'Status',
      meta: {
        filterVariant: 'multi-select',
        filterOptions: [
          {
            label: "Aktief",
            value: "Aktief"
          },
          {
            label: "Onaktief",
            value: "Onaktief"
          }
        ]
      },
      cell: ({ row }) => {
        const enabled = row.getValue('enabled') as boolean | undefined
        return (
          <Badge variant={enabled ? 'default' : 'destructive'}>
            {enabled ? 'Aktief' : 'Onaktief'}
          </Badge>
        )
      },
    },
    {
      accessorKey: 'createdAt',
      header: 'Geskep',
      filterFn: 'dateRange' as any,
      meta: {
        filterVariant: "date-range"
      },
      cell: ({ row }) => formatDate(row.getValue('createdAt')),
    },
    {
      accessorKey: 'updatedAt',
      header: 'Opgedateer',
      filterFn: 'dateRange' as any,
      meta: {
        filterVariant: "date-range"
      },
      cell: ({ row }) => formatDate(row.getValue('updatedAt')),
    },
    {
      id: 'actions',
      header: 'Aksies',
      cell: ({ row }) => {
        const rowUser = row.original
        const isCurrentUser = rowUser.email === user?.email
        return (
          <div className="flex items-center gap-2">
            <Button
              variant="ghost"
              size="sm"
              onClick={() => handleEditUser(rowUser)}
              className="h-8 w-8 p-0"
              disabled={isCurrentUser}
            >
              <Edit className="h-4 w-4" />
            </Button>
            <Button
              variant="ghost"
              size="sm"
              onClick={() => handleToggleUserStatus(rowUser)}
              className="h-8 w-8 p-0"
              disabled={isCurrentUser}
            >
              {rowUser.enabled ? (
                <ShieldOff className="h-4 w-4" />
              ) : (
                <Shield className="h-4 w-4" />
              )}
            </Button>
          </div>
        )
      },
    },
  ]

  const handleEditUser = (user: UserEntity) => {
    setSelectedUser(user)
    setIsDialogOpen(true)
  }

  const handleToggleUserStatus = (user: UserEntity) => {
    console.log(user);
    if (!user?.id)
      return;
    updateUserMutation.mutate({id: user.id, data: {enabled: !user.enabled }})
  }

  const handleFormSubmit = (data: any) => {
    if (selectedUser?.id) {
      // Update existing user
      updateUserMutation.mutate({id: selectedUser.id ,data})
    } else {
      // Create new user
      createUserMutation.mutate(data as AdminCreateUserRequest)
    }
  }

  return (
    <div className="container mx-auto py-6">
      <div className="mb-6 flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold">Gebruiker Bestuur</h1>
          <p className="text-muted-foreground">
            Bestuur gebruiker rekeninge, rolle en toestemmings
          </p>
        </div>
        <Dialog open={isDialogOpen} onOpenChange={setIsDialogOpen}>
          <DialogTrigger asChild>
            <Button onClick={() => setSelectedUser(undefined)}>Nuwe Gebruiker</Button>
          </DialogTrigger>
          <DialogContent>
            <DialogHeader>
              <DialogTitle>{selectedUser ? 'Wysig Gebruiker' : 'Nuwe Gebruiker'}</DialogTitle>
              <DialogDescription>
                {selectedUser ? 'Wysig die besonderhede van die gebruiker.' : 'Voeg \'n nuwe gebruiker by.'}
              </DialogDescription>
            </DialogHeader>
            <UserForm onSubmit={handleFormSubmit} defaultValues={selectedUser} />
          </DialogContent>
        </Dialog>
      </div>

      <DataTable
        columns={columns}
        data={users}
        enableSorting
        enableFiltering
        pageSize={10}
        loading={isUsersLoading}
        emptyMessage="No users found."
        className="space-y-4"
        columnFilters={columnFilters}
        setColumnFilters={setColumnFilters}
      />
    </div>
  )
}
