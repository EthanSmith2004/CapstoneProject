import { createFileRoute } from '@tanstack/react-router'
import { type ColumnDef } from '@tanstack/react-table'
import { Edit, Trash2 } from 'lucide-react'
import { useState } from 'react'
import { 
  AdminMenuApi, 
  type MenuItemDTO, 
  type AdminMenuItemCreateRequest, 
  AdminAllergyManagementApi, 
  type AdminMenuItemUpdateRequest 
} from '@/api'
import { useAuth } from '@/contexts/AuthContext'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { DataTable } from '@/components/table/Table'
import { Button } from '@/components/ui/button'
import { Badge } from '@/components/ui/badge'
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from '@/components/ui/dialog'
import { MenuItemForm } from '@/components/forms/MenuItemForm'
import { useDeleteMenuItem } from '@/hooks/api/useMenuTemplates'
import { toast } from 'sonner'

export const Route = createFileRoute('/admin/menu/items')({
  component: RouteComponent,
})

function RouteComponent() {
  const { getApiClient } = useAuth()
    const apiClient = getApiClient(AdminMenuApi)
    const listApi = getApiClient(AdminAllergyManagementApi)
    const queryClient = useQueryClient()
  
    const [isDialogOpen, setIsDialogOpen] = useState(false)
    const [selectedMenuItem, setSelectedMenuItem] = useState<MenuItemDTO | undefined>(undefined)
    const [itemToDelete, setItemToDelete] = useState<MenuItemDTO | null>(null)
  
    const { data: menuItemsResponse, isLoading: isMenuItemsLoading } = useQuery({
      queryKey: ['admin-menu-items'],
      queryFn: () => apiClient.getDraftMenuItems(),
      staleTime: 1000 * 60 * 5, // 5 minutes
      refetchOnWindowFocus: false,
    })
  
    const { data: allergiesResponse } = useQuery({
      queryKey: ['allergies'],
      queryFn: () => listApi.findAll2(),
      staleTime: 1000 * 60 * 5, // 5 minutes
      refetchOnWindowFocus: false,
    })
  
    const createMenuItemMutation = useMutation({
      mutationFn: (data: MenuItemDTO) => {
        let dataRequest: AdminMenuItemCreateRequest = {
          name: data.name!,
          description: data.description,
          price: data.price!,
          kcal: data.kcal,
          imageHero: data.imageHero,
          imageDetail: data.imageDetail,
          allergyIds: data.allergies as unknown as number[] ?? [],
        };
  
        return apiClient.createMenuItem(dataRequest);
      },
      onSuccess: () => {
        queryClient.invalidateQueries({ queryKey: ['admin-menu-items'] })
        setIsDialogOpen(false)
        setSelectedMenuItem(undefined)
      },
    })
  
    const updateMenuItemMutation = useMutation({
      mutationFn: (data: MenuItemDTO) => {
        console.log(data);
        const dataRequest: AdminMenuItemUpdateRequest = {
          name: data.name,
          description: data.description,
          price: data.price,
          kcal: data.kcal,
          imageHero: data.imageHero,
          imageDetail: data.imageDetail,
          allergyIds: data.allergies as unknown as number[] ?? [],
        };
        console.log(dataRequest);
        
        return apiClient.updateMenuItem(data.id!, dataRequest);
      },
      onSuccess: () => {
        queryClient.invalidateQueries({ queryKey: ['admin-menu-items'] })
        setIsDialogOpen(false)
        setSelectedMenuItem(undefined)
      },
    })
  
    const deleteMenuItemMutation = useDeleteMenuItem()
  
    const menuItems: MenuItemDTO[] = menuItemsResponse?.data || []
  
    const columns: ColumnDef<MenuItemDTO>[] = [
      {
        accessorKey: 'name',
        header: 'Naam',
        cell: ({ row }) => <div>{row.getValue('name') || 'N/A'}</div>,
      },
      {
        accessorKey: 'description',
        header: 'Beskrywing',
        cell: ({ row }) => <div>{(row.getValue('description') || 'N/A')}</div>,
      },
      {
        accessorKey: 'imageHero',
        header: 'Beeld',
        enableSorting: false,
        cell: ({ row }) => {
          const imageUrl = row.getValue('imageHero') as string | undefined
          return imageUrl ? (
                <img src={imageUrl} alt="Hoof Beeld" className="w-32 h-32 object-cover" />
              ) : (
                'Geen Beeld'
              )
        },
      },
      {
        accessorKey: 'price',
        header: 'Prys',
        cell: ({ row }) => {
          const price = row.getValue('price') as number | undefined
          return <div>{price ? `R ${price.toFixed(2)}` : 'N/A'}</div>
        },
      },
      {
        accessorKey: "kcal",
        header: 'Voedingswaarde',
        cell: ({ row }) => {
          const kcal = row.getValue('kcal') as number | undefined
          return <div>{kcal ? `${kcal} Kcal` : 'N/A'}</div>
        },
      },
      {
        accessorKey: 'allergies',
        header: 'Allergieë',
        enableSorting: false,
        cell: ({ row }) => {
          const allergies = row.getValue('allergies') as string[] | undefined
          return (
            <div className="flex flex-wrap gap-1">
              {allergies?.length ? (
                allergies.map((allergy) => (
                  <Badge key={allergy} variant="secondary">
                    {allergy}
                  </Badge>
                ))
              ) : (
                <span>Geen Allergieë</span>
              )}
            </div>
          )
        },
      },
      {
        id: 'actions',
        header: 'Aksies',
        cell: ({ row }) => {
          const menuItem = row.original
          return (
            <div className="flex items-center gap-2">
              <Button
                variant="ghost"
                size="sm"
                onClick={() => handleEditMenuItem(menuItem)}
                className="h-8 w-8 p-0"
              >
                <Edit className="h-4 w-4" />
              </Button>
              <Button
                variant="ghost"
                size="sm"
                onClick={() => handleDeleteMenuItem(menuItem)}
                className="h-8 w-8 p-0 text-destructive hover:text-destructive"
              >
                <Trash2 className="h-4 w-4" />
              </Button>
            </div>
          )
        },
      },
    ]
  
    const handleEditMenuItem = (menuItem: MenuItemDTO) => {
      setSelectedMenuItem(menuItem)
      setIsDialogOpen(true)
    }
  
    const handleDeleteMenuItem = (menuItem: MenuItemDTO) => {
      setItemToDelete(menuItem)
    }
  
    const confirmDeleteMenuItem = () => {
      if (itemToDelete && itemToDelete.id) {
        deleteMenuItemMutation.mutate(itemToDelete.id, {
          onSuccess: () => {
            toast.success('Item suksesvol verwyder')
            setItemToDelete(null)
          },
          onError: (error: any) => {
            toast.error('Fout met verwyder van item', {
              description: error.response?.data?.message || error.message || 'Kon nie die item verwyder nie'
            })
            setItemToDelete(null)
          }
        })
      }
    }
  
    const handleFormSubmit = (data: any) => {
      if (selectedMenuItem) {
      updateMenuItemMutation.mutate({ id: selectedMenuItem.id, ...data })
    } else {
      createMenuItemMutation.mutate(data)
    }
    }

  return (
    <div className='
      container
      mx-auto
      py-6'
    >
      <div className="
        mb-6 
        flex 
        items-center 
        justify-between"
      >
        <div>
          <h1 className="
            text-3xl 
            font-bold"
          >
            Item Bestuur
          </h1>
          <p className="text-muted-foreground">
            Bestuur en skep nuwe spyskaart items.
          </p>
        </div>
        <Dialog 
          open={isDialogOpen} 
          onOpenChange={setIsDialogOpen}
        >
          <DialogTrigger asChild>
            <Button 
              onClick={() => setSelectedMenuItem(undefined)}
            >
              Nuwe Item
            </Button>
          </DialogTrigger>
          <DialogContent className='
            w-full 
            max-w-2xl
            max-h-[90vh]
            overflow-hidden
            flex
            flex-col'
          >
            <DialogHeader className="flex-shrink-0">
              <DialogTitle>{selectedMenuItem ? 'Wysig Item' : 'Nuwe Item'}</DialogTitle>
              <DialogDescription>
                {selectedMenuItem ? 'Wysig die besonderhede van die item.' : 'Voeg \'n nuwe item by die spyskaart.'}
              </DialogDescription>
            </DialogHeader>
            <div className="flex-1 overflow-y-auto pr-2">
              <MenuItemForm 
                onSubmit={handleFormSubmit} 
                defaultValues={selectedMenuItem} 
                allergiesList={allergiesResponse?.data} 
              />
            </div>
          </DialogContent>
        </Dialog>
      </div>

      <DataTable
        columns={columns}
        data={menuItems}
        enableSorting
        pageSize={10}
        loading={isMenuItemsLoading}
        emptyMessage="Geen items gevind nie"
        className="space-y-4"
      />

      {/* Delete Confirmation Dialog */}
      <Dialog open={!!itemToDelete} onOpenChange={(open) => !open && setItemToDelete(null)}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Verwyder item?</DialogTitle>
            <DialogDescription>
              Is jy seker jy wil "{itemToDelete?.name}" verwyder?
            </DialogDescription>
          </DialogHeader>
          <DialogFooter>
            <Button variant="outline" onClick={() => setItemToDelete(null)}>
              Kanselleer
            </Button>
            <Button variant="destructive" onClick={confirmDeleteMenuItem}>
              Verwyder
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  )
}
