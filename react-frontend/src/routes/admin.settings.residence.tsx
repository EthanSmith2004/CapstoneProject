import { createFileRoute } from '@tanstack/react-router'
import { type ColumnDef } from '@tanstack/react-table'
import { Edit, Trash } from 'lucide-react'
import { useState } from 'react'

import { AdminResidenceManagementApi, type ResidenceEntity } from '@/api'
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

export const Route = createFileRoute('/admin/settings/residence')({
  component: RouteComponent,
})

const residenceFormSchema = z.object({
  residence: z.string().min(1, 'Residence name is required'),
})

type ResidenceFormValues = z.infer<typeof residenceFormSchema>

function ResidenceForm({
  onSubmit,
  defaultValues,
}: {
  onSubmit: (data: ResidenceFormValues) => void
  defaultValues?: ResidenceEntity
}) {
  const form = useForm<ResidenceFormValues>({
    resolver: zodResolver(residenceFormSchema),
    defaultValues: {
      residence: defaultValues?.residence || '',
    },
  })

  return (
    <Form {...form}>
      <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
        <FormField
          control={form.control}
          name="residence"
          render={({ field }) => (
            <FormItem>
              <FormLabel>Koshuis Naam</FormLabel>
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
  const apiClient = getApiClient(AdminResidenceManagementApi)
  const queryClient = useQueryClient()

  const [isDialogOpen, setIsDialogOpen] = useState(false)
  const [selectedResidence, setSelectedResidence] = useState<ResidenceEntity | undefined>(undefined)

  const { data: residencesResponse, isLoading: isResidencesLoading } = useQuery({
    queryKey: ['admin-residences'],
    queryFn: () => apiClient.findAll(),
    staleTime: 1000 * 60 * 5, // 5 minutes
    refetchOnWindowFocus: false,
  })

  const createResidenceMutation = useMutation({
    mutationFn: (data: ResidenceFormValues) => apiClient.create({ residence: data.residence }),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['admin-residences'] })
      setIsDialogOpen(false)
      toast.success('Die koshuis is suksesvol geskep.');
    },
  })

  const updateResidenceMutation = useMutation({
    mutationFn: (data: ResidenceFormValues) => 
      apiClient.update(selectedResidence!.id!, { 
        ...selectedResidence!, 
        residence: data.residence 
      }),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['admin-residences'] })
      setIsDialogOpen(false)
      toast.success('Die koshuis is suksesvol opgedateer.');
    },
  })

  const deleteResidenceMutation = useMutation({
    mutationFn: (id: number) => apiClient._delete(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['admin-residences'] })
      toast.success('Die koshuis is suksesvol verwyder.');
    },
  })

  const residences: ResidenceEntity[] = residencesResponse?.data || []

  const columns: ColumnDef<ResidenceEntity>[] = [
    {
      accessorKey: 'residence',
      header: 'Koshuis',
      meta: {
        filterVariant: 'string'
      },
      cell: ({ row }) => <div>{row.getValue('residence')}</div>,
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
        const residence = row.original
        return (
          <div className="flex items-center gap-2">
            <Button
              variant="ghost"
              size="sm"
              onClick={() => handleEditResidence(residence)}
              className="h-8 w-8 p-0"
            >
              <Edit className="h-4 w-4" />
            </Button>
            <Button
              variant="ghost"
              size="sm"
              onClick={() => handleDeleteResidence(residence)}
              className="h-8 w-8 p-0"
            >
              <Trash className="h-4 w-4" />
            </Button>
          </div>
        )
      },
    },
  ]

  const handleEditResidence = (residence: ResidenceEntity) => {
    setSelectedResidence(residence)
    setIsDialogOpen(true)
  }

  const handleDeleteResidence = (residence: ResidenceEntity) => {
    if (confirm(`Is jy seker jy wil die koshuis "${residence.residence}" verwyder?`)) {
      deleteResidenceMutation.mutate(residence.id!)
    }
  }

  const handleFormSubmit = (data: ResidenceFormValues) => {
    if (selectedResidence) {
      updateResidenceMutation.mutate(data)
    } else {
      createResidenceMutation.mutate(data)
    }
  }

  return (
    <div className="container mx-auto py-6">
      <div className="mb-6 flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold">Koshuis Bestuur</h1>
          <p className="text-muted-foreground">Bestuur koshuise</p>
        </div>
        <Dialog open={isDialogOpen} onOpenChange={setIsDialogOpen}>
          <DialogTrigger asChild>
            <Button onClick={() => setSelectedResidence(undefined)}>Nuwe Koshuis</Button>
          </DialogTrigger>
          <DialogContent>
            <DialogHeader>
              <DialogTitle>{selectedResidence ? 'Wysig Koshuis' : 'Nuwe Koshuis'}</DialogTitle>
              <DialogDescription>
                {selectedResidence ? 'Wysig die besonderhede van die koshuis.' : 'Voeg \'n nuwe koshuis by.'}
              </DialogDescription>
            </DialogHeader>
            <ResidenceForm onSubmit={handleFormSubmit} defaultValues={selectedResidence} />
          </DialogContent>
        </Dialog>
      </div>

      <DataTable
        columns={columns}
        data={residences}
        enableSorting
        enableFiltering
        enableSearching
        searchPlaceholder="Soek koshuise..."
        pageSize={10}
        loading={isResidencesLoading}
        emptyMessage="Geen koshuise gevind nie."
        className="space-y-4"
      />
    </div>
  )
}
