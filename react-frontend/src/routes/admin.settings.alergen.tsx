import { createFileRoute } from '@tanstack/react-router'
import { type ColumnDef } from '@tanstack/react-table'
import { Edit, Trash } from 'lucide-react'
import { useState } from 'react'

import { AdminAllergyManagementApi, type AllergyEntity } from '@/api'
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
import { toast } from 'sonner';

export const Route = createFileRoute('/admin/settings/alergen')({
  component: RouteComponent,
})

const allergenFormSchema = z.object({
  allergy: z.string().min(1, 'Allergy name is required'),
})

type AllergenFormValues = z.infer<typeof allergenFormSchema>

function AllergenForm({
  onSubmit,
  defaultValues,
}: {
  onSubmit: (data: AllergenFormValues) => void
  defaultValues?: AllergyEntity
}) {
  const form = useForm<AllergenFormValues>({
    resolver: zodResolver(allergenFormSchema),
    defaultValues: {
      allergy: defaultValues?.allergy || '',
    },
  })

  return (
    <Form {...form}>
      <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
        <FormField
          control={form.control}
          name="allergy"
          render={({ field }) => (
            <FormItem>
              <FormLabel>Allergy Name</FormLabel>
              <FormControl>
                <Input {...field} />
              </FormControl>
              <FormMessage />
            </FormItem>
          )}
        />
        <Button type="submit" className="w-full">
          {defaultValues ? 'Update' : 'Create'}
        </Button>
      </form>
    </Form>
  )
}

function RouteComponent() {
  const { getApiClient } = useAuth()
  const apiClient = getApiClient(AdminAllergyManagementApi)
  const queryClient = useQueryClient()

  const [isDialogOpen, setIsDialogOpen] = useState(false)
  const [selectedAllergen, setSelectedAllergen] = useState<AllergyEntity | undefined>(undefined)

  const { data: allergensResponse, isLoading: isAllergensLoading } = useQuery({
    queryKey: ['admin-allergens'],
    queryFn: () => apiClient.findAll2(),
    staleTime: 1000 * 60 * 5, // 5 minutes
    refetchOnWindowFocus: false,
  })

  const createAllergenMutation = useMutation({
    mutationFn: (data: AllergenFormValues) => apiClient.create2({ allergy: data.allergy }),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['admin-allergens'] })
      setIsDialogOpen(false)
      toast.success('The allergy was created successfully.');
    },
  })

  const updateAllergenMutation = useMutation({
    mutationFn: (data: AllergenFormValues) =>
      apiClient.update2(selectedAllergen!.id!, {
        ...selectedAllergen!,
        allergy: data.allergy
      }),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['admin-allergens'] })
      setIsDialogOpen(false)
      toast.success('The allergy was updated successfully.');
    },
  })

  const deleteAllergenMutation = useMutation({
    mutationFn: (id: number) => apiClient.delete2(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['admin-allergens'] })
      toast.success('The allergy was deleted successfully.');
    },
  })

  const allergens: AllergyEntity[] = allergensResponse?.data || []

  const columns: ColumnDef<AllergyEntity>[] = [
    {
      accessorKey: 'allergy',
      header: 'Allergy',
      meta: {
        filterVariant: 'string'
      },
      cell: ({ row }) => <div>{row.getValue('allergy')}</div>,
    },
    {
      accessorKey: 'createdAt',
      header: 'Created',
      filterFn: 'dateRange' as any,
      meta: {
        filterVariant: 'date-range'
      },
      cell: ({ row }) => formatDate(row.getValue('createdAt')),
    },
    {
      accessorKey: 'updatedAt',
      header: 'Updated',
      filterFn: 'dateRange' as any,
      meta: {
        filterVariant: 'date-range'
      },
      cell: ({ row }) => formatDate(row.getValue('updatedAt')),
    },
    {
      id: 'actions',
      header: 'Actions',
      cell: ({ row }) => {
        const allergen = row.original
        return (
          <div className="flex items-center gap-2">
            <Button
              variant="ghost"
              size="sm"
              onClick={() => handleEditAllergen(allergen)}
              className="h-8 w-8 p-0"
            >
              <Edit className="h-4 w-4" />
            </Button>
            <Button
              variant="ghost"
              size="sm"
              onClick={() => handleDeleteAllergen(allergen)}
              className="h-8 w-8 p-0"
            >
              <Trash className="h-4 w-4" />
            </Button>
          </div>
        )
      },
    },
  ]

  const handleEditAllergen = (allergen: AllergyEntity) => {
    setSelectedAllergen(allergen)
    setIsDialogOpen(true)
  }

  const handleDeleteAllergen = (allergen: AllergyEntity) => {
    if (confirm(`Are you sure you want to delete the allergy "${allergen.allergy}"?`)) {
      deleteAllergenMutation.mutate(allergen.id!)
    }
  }

  const handleFormSubmit = (data: AllergenFormValues) => {
    if (selectedAllergen) {
      updateAllergenMutation.mutate(data)
    } else {
      createAllergenMutation.mutate(data)
    }
  }

  return (
    <div className="container mx-auto py-6">
      <div className="mb-6 flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold">Allergy Management</h1>
          <p className="text-muted-foreground">Manage allergies for menu items</p>
        </div>
        <Dialog open={isDialogOpen} onOpenChange={setIsDialogOpen}>
          <DialogTrigger asChild>
            <Button onClick={() => setSelectedAllergen(undefined)}>New Allergy</Button>
          </DialogTrigger>
          <DialogContent>
            <DialogHeader>
              <DialogTitle>{selectedAllergen ? 'Edit Allergy' : 'New Allergy'}</DialogTitle>
              <DialogDescription>
                {selectedAllergen ? 'Edit the allergy details.' : 'Add a new allergy.'}
              </DialogDescription>
            </DialogHeader>
            <AllergenForm onSubmit={handleFormSubmit} defaultValues={selectedAllergen} />
          </DialogContent>
        </Dialog>
      </div>

      <DataTable
        columns={columns}
        data={allergens}
        enableSorting
        enableFiltering
        enableSearching
        searchPlaceholder="Search allergies..."
        pageSize={10}
        loading={isAllergensLoading}
        emptyMessage="No allergies found."
        className="space-y-4"
      />
    </div>
  )
}
