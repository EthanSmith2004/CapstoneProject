import { createFileRoute } from '@tanstack/react-router'
import { type ColumnDef } from '@tanstack/react-table'
import { Edit, Trash } from 'lucide-react'
import { useState } from 'react'

import { AdminCampusManagementApi, type CampusEntity } from '@/api'
import { useAuth } from '@/contexts/AuthContext'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { DataTable } from '@/components/table/Table'
import { Button } from '@/components/ui/button'
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from '@/components/ui/dialog'
import { formatDate } from '@/lib/utils'
import { z } from 'zod'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from '@/components/ui/form'
import { Input } from '@/components/ui/input'
import { toast } from 'sonner'

export const Route = createFileRoute('/admin/settings/campus')({
  component: RouteComponent,
})

const campusFormSchema = z.object({
  campus: z.string().min(1, 'Campus name is required'),
})

type CampusFormValues = z.infer<typeof campusFormSchema>

function CampusForm({
  onSubmit,
  defaultValues,
}: {
  onSubmit: (data: CampusFormValues) => void
  defaultValues?: CampusEntity
}) {
  const form = useForm<CampusFormValues>({
    resolver: zodResolver(campusFormSchema),
    defaultValues: {
      campus: defaultValues?.campus || '',
    },
  })

  return (
    <Form {...form}>
      <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
        <FormField
          control={form.control}
          name="campus"
          render={({ field }) => (
            <FormItem>
              <FormLabel>Kampus Naam</FormLabel>
              <FormControl>
                <Input {...field} />
              </FormControl>
              <FormMessage />
            </FormItem>
          )}
        />
        <Button type="submit" className="w-full">
          {defaultValues ? 'Opdateer' : 'Skep'}
        </Button>
      </form>
    </Form>
  )
}

function RouteComponent() {
  const { getApiClient } = useAuth()
  const apiClient = getApiClient(AdminCampusManagementApi)
  const queryClient = useQueryClient()

  const [isDialogOpen, setIsDialogOpen] = useState(false)
  const [selectedCampus, setSelectedCampus] = useState<CampusEntity | undefined>(undefined)

  const { data: campusesResponse, isLoading: isCampusesLoading } = useQuery({
    queryKey: ['admin-campuses'],
    queryFn: () => apiClient.findAll1(),
    staleTime: 1000 * 60 * 5, // 5 minutes
    refetchOnWindowFocus: false,
  })

  const createCampusMutation = useMutation({
    mutationFn: (data: CampusFormValues) => apiClient.create1({ campus: data.campus }),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['admin-campuses'] })
      setIsDialogOpen(false)
      toast.success('Die kampus is suksesvol geskep.')
    },
  })

  const updateCampusMutation = useMutation({
    mutationFn: (data: CampusFormValues) => 
      apiClient.update1(selectedCampus!.id!, { 
        ...selectedCampus!, 
        campus: data.campus 
      }),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['admin-campuses'] })
      setIsDialogOpen(false)
      toast.success('Die kampus is suksesvol opgedateer.')
    },
  })

  const deleteCampusMutation = useMutation({
    mutationFn: (id: number) => apiClient.delete1(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['admin-campuses'] })
      toast.success('Die kampus is suksesvol verwyder.');
    },
  })

  const campuses: CampusEntity[] = campusesResponse?.data || []

  const columns: ColumnDef<CampusEntity>[] = [
    {
      accessorKey: 'campus',
      header: 'Kampus',
      meta: {
        filterVariant: 'string'
      },
      cell: ({ row }) => <div>{row.getValue('campus')}</div>,
    },
    {
      accessorKey: 'createdAt',
      header: 'Geskep',
      filterFn: 'dateRange' as any,
      meta: {
        filterVariant: 'date-range'
      },
      cell: ({ row }) => formatDate(row.getValue('createdAt')),
    },
    {
      accessorKey: 'updatedAt',
      header: 'Opgedateer',
      filterFn: 'dateRange' as any,
      meta: {
        filterVariant: 'date-range'
      },
      cell: ({ row }) => formatDate(row.getValue('updatedAt')),
    },
    {
      id: 'actions',
      header: 'Aksies',
      cell: ({ row }) => {
        const campus = row.original
        return (
          <div className="flex items-center gap-2">
            <Button
              variant="ghost"
              size="sm"
              onClick={() => handleEditCampus(campus)}
              className="h-8 w-8 p-0"
            >
              <Edit className="h-4 w-4" />
            </Button>
            <Button
              variant="ghost"
              size="sm"
              onClick={() => handleDeleteCampus(campus)}
              className="h-8 w-8 p-0"
            >
              <Trash className="h-4 w-4" />
            </Button>
          </div>
        )
      },
    },
  ]

  const handleEditCampus = (campus: CampusEntity) => {
    setSelectedCampus(campus)
    setIsDialogOpen(true)
  }

  const handleDeleteCampus = (campus: CampusEntity) => {
    if (confirm(`Is jy seker jy wil die kampus "${campus.campus}" verwyder?`)) {
      deleteCampusMutation.mutate(campus.id!)
    }
  }

  const handleFormSubmit = (data: CampusFormValues) => {
    if (selectedCampus) {
      updateCampusMutation.mutate(data)
    } else {
      createCampusMutation.mutate(data)
    }
  }

  return (
    <div className="container mx-auto py-6">
      <div className="mb-6 flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold">Kampus Bestuur</h1>
          <p className="text-muted-foreground">Bestuur kampusse vir gebruikersprofiele</p>
        </div>
        <Dialog open={isDialogOpen} onOpenChange={setIsDialogOpen}>
          <DialogTrigger asChild>
            <Button onClick={() => setSelectedCampus(undefined)}>Nuwe Kampus</Button>
          </DialogTrigger>
          <DialogContent>
            <DialogHeader>
              <DialogTitle>{selectedCampus ? 'Wysig Kampus' : 'Nuwe Kampus'}</DialogTitle>
              <DialogDescription>
                {selectedCampus ? 'Wysig die besonderhede van die kampus.' : 'Voeg \'n nuwe kampus by.'}
              </DialogDescription>
            </DialogHeader>
            <CampusForm onSubmit={handleFormSubmit} defaultValues={selectedCampus} />
          </DialogContent>
        </Dialog>
      </div>

      <DataTable
        columns={columns}
        data={campuses}
        enableSorting
        enableFiltering
        enableSearching
        searchPlaceholder="Soek kampusse..."
        pageSize={10}
        loading={isCampusesLoading}
        emptyMessage="Geen kampusse gevind nie."
        className="space-y-4"
      />
    </div>
  )
}
