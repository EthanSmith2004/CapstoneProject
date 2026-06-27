/**
 * TimeslotBoard - Main board displaying timeslots with drop zones
 */

import { useDroppable } from '@dnd-kit/core';
import type { MenuTemplateDTO, MenuItemDTO } from '@/api';
import { TimeslotCard } from './TimeslotCard';
import { Card, CardContent } from '@/components/ui/card';
import { Plus } from 'lucide-react';
import { cn } from '@/lib/utils';
import { 
  calculateDeliveryDate, 
  menuMatchesTemplate 
} from '@/utils/menuTemplateCalculations';

interface TimeslotBoardProps {
  templates: MenuTemplateDTO[];
  currentMenus: MenuItemDTO[];
  historicMenus: MenuItemDTO[];
  weekStart: Date;
  weekEnd: Date;
  onAddTimeslot: () => void;
  onDeleteTemplate?: (id: number) => void;
  onDeleteMenuItem?: (id: number) => void;
}

interface DroppableTimeslotProps {
  template: MenuTemplateDTO;
  assignedItems: MenuItemDTO[];
  weekStart: Date;
  isHeuristic?: boolean;
  isHistoric?: boolean;
  onDelete?: (id: number) => void;
  onDeleteMenuItem?: (id: number) => void;
}

function DroppableTimeslot({
  template,
  assignedItems,
  weekStart,
  isHeuristic,
  isHistoric,
  onDelete,
  onDeleteMenuItem
}: DroppableTimeslotProps) {
  const { setNodeRef, isOver } = useDroppable({
    id: `timeslot-${template.id}`,
    data: { type: 'timeslot', template }
  });

  return (
    <div ref={setNodeRef}>
      <TimeslotCard
        template={template}
        assignedItems={assignedItems}
        weekStart={weekStart}
        isDropTarget={isOver && !isHistoric}
        isHeuristic={isHeuristic}
        isHistoric={isHistoric}
        onDelete={onDelete}
        onDeleteMenuItem={onDeleteMenuItem}
      />
    </div>
  );
}

function AutoTimeslot({
  template,
  assignedItems,
  weekStart,
  isHeuristic,
  isHistoric,
  onDelete,
  onDeleteMenuItem
}: DroppableTimeslotProps) {
    if (isHistoric) {
      return <TimeslotCard
        template={template}
        assignedItems={assignedItems}
        weekStart={weekStart}
        isHeuristic={isHeuristic}
        isHistoric={isHistoric}
        onDelete={onDelete}
        onDeleteMenuItem={onDeleteMenuItem}
      />
    }
    return <DroppableTimeslot
      template={template}
      assignedItems={assignedItems}
      weekStart={weekStart}
      isHeuristic={isHeuristic}
      isHistoric={isHistoric}
      onDelete={onDelete}
      onDeleteMenuItem={onDeleteMenuItem}
    />;
}

export function TimeslotBoard({
  templates,
  currentMenus,
  historicMenus,
  weekStart,
  weekEnd,
  onAddTimeslot,
  onDeleteTemplate,
  onDeleteMenuItem
}: TimeslotBoardProps) {
  // Sort templates by delivery offset
  const sortedTemplates = [...templates].sort(
    (a, b) => (a.deliveryOffsetMinutes || 0) - (b.deliveryOffsetMinutes || 0)
  );

  // Generate heuristic slots for unmatched current menus
  const heuristicSlots: MenuTemplateDTO[] = [];

  const activeMenus = [...currentMenus, ...historicMenus].filter((menu) => {
    const deliveryDate = new Date(menu.deliveryDate || '');
    return deliveryDate >= weekStart && deliveryDate <= weekEnd;
  });

  console.log('Active Menus:', activeMenus);

  activeMenus.forEach((menu) => {
    const matchesTemplate = sortedTemplates.some((template) => {
      const deliveryDate = calculateDeliveryDate(
        weekStart,
        template.deliveryOffsetMinutes || 0
      );
      return menuMatchesTemplate(menu.deliveryDate || '', deliveryDate);
    });

    const matchesHeuristic = heuristicSlots.some((heuristic) => {
      const deliveryDate = calculateDeliveryDate(
        weekStart,
        heuristic.deliveryOffsetMinutes || 0
      );
      return menuMatchesTemplate(menu.deliveryDate || '', deliveryDate);
    });

    if (!matchesTemplate && !matchesHeuristic && menu.deliveryDate) {
      // Create a heuristic template for this menu
      const deliveryDate = new Date(menu.deliveryDate);
      const weekStartTime = weekStart.getTime();
      const deliveryTime = deliveryDate.getTime();
      const offsetMinutes = Math.floor((deliveryTime - weekStartTime) / (60 * 1000));

      heuristicSlots.push({
          id: -heuristicSlots.length - 1, // Negative ID for heuristic
          description: 'Auto-gegenereer',
          deliveryOffsetMinutes: offsetMinutes,
          releaseOffsetMinutes: 120, // Default 2 hours
          orderByOffsetMinutes: 60, // Default 1 hour
          presetName: 'heuristic'
      });
    }
  });

  // Map menus to their templates
  const getAssignedItems = (template: MenuTemplateDTO): MenuItemDTO[] => {
    const deliveryDate = calculateDeliveryDate(
      weekStart,
      template.deliveryOffsetMinutes || 0
    );
    
    return activeMenus.filter((menu) =>
      menuMatchesTemplate(menu.deliveryDate || '', deliveryDate)
    );
  };

  const isTemplateHistoric = (template: MenuTemplateDTO): boolean => {
    const deliveryDate = calculateDeliveryDate(
      weekStart,
        template.deliveryOffsetMinutes || 0
    );
    const now = new Date();
    return deliveryDate < now;
  }


  // Combine templates and heuristic slots, then sort
  const allSlots = [
    ...sortedTemplates.map((template) => ({
      template,
      items: getAssignedItems(template),
      isHeuristic: false,
      isHistoric: isTemplateHistoric(template)
    })),
    ...heuristicSlots.map((template) => ({
      template,
      items: getAssignedItems(template),
      isHeuristic: true,
      isHistoric: isTemplateHistoric(template)
    }))
  ].sort((a, b) => 
    (a.template.deliveryOffsetMinutes || 0) - (b.template.deliveryOffsetMinutes || 0)
  );

  return (
    <div className="flex-1 p-6 overflow-scroll">
      <div className="flex gap-4 flex-wrap justify-center">
        {allSlots.map((slot, index) => (
          <AutoTimeslot
            key={slot.isHeuristic ? `heuristic-${index}` : `template-${slot.template.id}`}
            template={slot.template}
            assignedItems={slot.items}
            weekStart={weekStart}
            isHeuristic={slot.isHeuristic}
            isHistoric={slot.isHistoric}
            onDelete={onDeleteTemplate}
            onDeleteMenuItem={onDeleteMenuItem}
          />
        ))}

        {/* Add Timeslot Card */}
        <Card
          className={cn(
            'w-72 flex-shrink-0 border-dashed border-2 cursor-pointer bg-none',
            'hover:border-primary hover:bg-muted/50 transition-colors'
          )}
          onClick={onAddTimeslot}
        >
          <CardContent className="h-full flex flex-col items-center justify-center p-6">
            <Plus className="w-8 h-8 text-muted-foreground mb-2" />
            <p className="text-sm font-medium text-muted-foreground">
              Add timeslot
            </p>
          </CardContent>
        </Card>
      </div>
    </div>
  );
}
