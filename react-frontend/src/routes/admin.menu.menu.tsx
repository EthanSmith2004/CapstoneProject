/**
 * Menu Template Management Page
 * Trello-style interface for managing menu templates and scheduling menu items
 */

import { useState } from 'react';
import { createFileRoute } from '@tanstack/react-router';
import {
  DndContext,
  DragOverlay,
  closestCenter,
  KeyboardSensor,
  PointerSensor,
  useSensor,
  useSensors,
  type DragEndEvent,
  type DragStartEvent
} from '@dnd-kit/core';
import type { MenuItemDTO } from '@/api';
import { PresetSelector } from '@/components/admin/menu-templates/PresetSelector';
import { WeekNavigator } from '@/components/admin/menu-templates/WeekNavigator';
import { MenuItemSidebar } from '@/components/admin/menu-templates/MenuItemSidebar';
import { TimeslotBoard } from '@/components/admin/menu-templates/TimeslotBoard';
import { AddTimeslotDialog } from '@/components/admin/menu-templates/AddTimeslotDialog';
import { MenuItemCard } from '@/components/admin/menu-templates/MenuItemCard';
import {
  usePresets,
  useTemplatesByPreset,
  useCurrentMenus,
  useHistoricMenus,
  useDraftMenuItems,
  useQueueMenuItem,
  useCreateTemplate,
  useDeleteTemplate,
  useDeleteMenuItem
} from '@/hooks/api/useMenuTemplates';
import {
  getWeekStart,
  calculateDeliveryDate,
  calculateOffsetDate
} from '@/utils/menuTemplateCalculations';
import { toast } from 'sonner';
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog';
import { Button } from '@/components/ui/button';

export const Route = createFileRoute('/admin/menu/menu')({
  component: RouteComponent,
});

function RouteComponent() {
  // State
  const [selectedPreset, setSelectedPreset] = useState<string | null>(null);
  const [currentWeekStart, setCurrentWeekStart] = useState(() => getWeekStart(new Date()));
  const [isAddDialogOpen, setIsAddDialogOpen] = useState(false);
  const [activeItem, setActiveItem] = useState<MenuItemDTO | null>(null);
  const [templateToDelete, setTemplateToDelete] = useState<number | null>(null);
  const [menuItemToDelete, setMenuItemToDelete] = useState<number | null>(null);

  // Queries
  const { data: presets = [] } = usePresets();
  const { data: templates = [] } = useTemplatesByPreset(selectedPreset);
  const { data: currentMenus = [] } = useCurrentMenus();
  const { data: historicMenus = [] } = useHistoricMenus();
  const { data: draftItems = [], isLoading: draftItemsLoading } = useDraftMenuItems();

  // Mutations
  const queueMutation = useQueueMenuItem();
  const createTemplateMutation = useCreateTemplate();
  const deleteTemplateMutation = useDeleteTemplate();
  const deleteMenuItemMutation = useDeleteMenuItem();

  // Drag and drop sensors
  const sensors = useSensors(
    useSensor(PointerSensor, {
      activationConstraint: {
        distance: 8,
      },
    }),
    useSensor(KeyboardSensor)
  );

  // Auto-select first preset if none selected
  if (!selectedPreset && presets.length > 0 && presets[0].presetName) {
    setSelectedPreset(presets[0].presetName);
  }

  // Handlers
  const handleWeekChange = (direction: 'prev' | 'next') => {
    const newWeekStart = new Date(currentWeekStart);
    newWeekStart.setDate(newWeekStart.getDate() + (direction === 'next' ? 7 : -7));
    setCurrentWeekStart(newWeekStart);
  };

  const handleDragStart = (event: DragStartEvent) => {
    const { active } = event;
    if (active.data.current?.type === 'menu-item') {
      setActiveItem(active.data.current.item);
    }
  };

  const handleDragEnd = (event: DragEndEvent) => {
    const { active, over } = event;
    setActiveItem(null);

    if (!over) return;

    const menuItem = active.data.current?.item as MenuItemDTO;
    const template = over.data.current?.template;

    if (!menuItem || !template) return;

    // Calculate dates from template
    const deliveryDate = calculateDeliveryDate(
      currentWeekStart,
      template.deliveryOffsetMinutes || 0
    );
    const releaseDate = calculateOffsetDate(
      deliveryDate,
      template.releaseOffsetMinutes || 0
    );
    const orderByDate = calculateOffsetDate(
      deliveryDate,
      template.orderByOffsetMinutes || 0
    );
    
    // editBy date should be the same as orderBy for now
    const editByDate = orderByDate;

    // Queue the menu item
    queueMutation.mutate(
      {
        sourceMenuItemId: menuItem.id || 0,
        deliveryDate: deliveryDate.toISOString(),
        releaseDate: releaseDate.toISOString(),
        orderBy: orderByDate.toISOString(),
        editBy: editByDate.toISOString()
      },
      {
        onSuccess: () => {
          toast.success('Menu item added successfully', {
            description: `${menuItem.name} has been scheduled for delivery`
          });
        },
        onError: (error) => {
          toast.error('Error scheduling', {
            description: error.message || 'Could not add the item'
          });
        }
      }
    );
  };

  const handleDragCancel = () => {
    setActiveItem(null);
  };

  const handleAddTimeslot = (data: any) => {
    createTemplateMutation.mutate(data, {
      onSuccess: () => {
        toast.success('Timeslot added successfully');
      },
      onError: (error) => {
        toast.error('Error adding timeslot', {
          description: error.message || 'Could not add the timeslot'
        });
      }
    });
  };

  const handleDeleteTemplate = (id: number) => {
    setTemplateToDelete(id);
  };

  const confirmDeleteTemplate = () => {
    if (templateToDelete) {
      deleteTemplateMutation.mutate(templateToDelete, {
        onSuccess: () => {
          toast.success('Timeslot deleted successfully');
          setTemplateToDelete(null);
        },
        onError: (error) => {
          toast.error('Error deleting timeslot', {
            description: error.message || 'Could not delete the timeslot'
          });
          setTemplateToDelete(null);
        }
      });
    }
  };

  const handleDeleteMenuItem = (id: number) => {
    setMenuItemToDelete(id);
  };

  const confirmDeleteMenuItem = () => {
    if (menuItemToDelete) {
      deleteMenuItemMutation.mutate(menuItemToDelete, {
        onSuccess: () => {
          toast.success('Item deleted successfully');
          setMenuItemToDelete(null);
        },
        onError: (error: any) => {
          toast.error('Error deleting item', {
            description: error.response?.data?.message || error.message || 'Could not delete the item'
          });
          setMenuItemToDelete(null);
        }
      });
    }
  };

  return (
    <div className="flex flex-col h-screen">
      {/* Header */}
      <div className="border-b bg-background p-4 flex items-center justify-between">
        <div className="flex items-center gap-4">
          <h1 className="text-2xl font-bold">Menu Planning</h1>
          <PresetSelector
            presets={presets}
            selectedPreset={selectedPreset}
            onPresetChange={setSelectedPreset}
          />
        </div>
        <WeekNavigator
          currentWeekStart={currentWeekStart}
          onWeekChange={handleWeekChange}
        />
      </div>

      {/* Main Content */}
      <div className="flex-1 flex overflow-hidden">
        <DndContext
          sensors={sensors}
          collisionDetection={closestCenter}
          onDragStart={handleDragStart}
          onDragEnd={handleDragEnd}
          onDragCancel={handleDragCancel}
        >
          {/* Sidebar */}
          <MenuItemSidebar items={draftItems} isLoading={draftItemsLoading} />

          {/* Board */}
          <TimeslotBoard
            templates={templates}
            currentMenus={currentMenus}
            historicMenus={historicMenus}
            weekStart={currentWeekStart}
            weekEnd={new Date(
              currentWeekStart.getTime() + 6 * 24 * 60 * 60 * 1000
            )}
            onAddTimeslot={() => setIsAddDialogOpen(true)}
            onDeleteTemplate={handleDeleteTemplate}
            onDeleteMenuItem={handleDeleteMenuItem}
          />

          {/* Drag Overlay */}
          <DragOverlay>
            {activeItem ? <MenuItemCard item={activeItem} isDragging /> : null}
          </DragOverlay>
        </DndContext>
      </div>

      {/* Add Timeslot Dialog */}
      <AddTimeslotDialog
        open={isAddDialogOpen}
        onOpenChange={setIsAddDialogOpen}
        onSubmit={handleAddTimeslot}
        currentPreset={selectedPreset}
      />

      {/* Delete Confirmation Dialog */}
      <Dialog open={!!templateToDelete} onOpenChange={(open) => !open && setTemplateToDelete(null)}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Delete timeslot?</DialogTitle>
            <DialogDescription>
              Are you sure you want to delete this timeslot?
            </DialogDescription>
          </DialogHeader>
          <DialogFooter>
            <Button variant="outline" onClick={() => setTemplateToDelete(null)}>
              Cancel
            </Button>
            <Button variant="destructive" onClick={confirmDeleteTemplate}>
              Delete
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      {/* Delete Menu Item Confirmation Dialog */}
      <Dialog open={menuItemToDelete !== null} onOpenChange={(open) => !open && setMenuItemToDelete(null)}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Delete menu item?</DialogTitle>
            <DialogDescription>
              Are you sure you want to delete this menu item?
            </DialogDescription>
          </DialogHeader>
          <DialogFooter>
            <Button variant="outline" onClick={() => setMenuItemToDelete(null)}>
              Cancel
            </Button>
            <Button variant="destructive" onClick={confirmDeleteMenuItem}>
              Delete
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  );
}
