/**
 * TimeslotCard - Displays a template-based timeslot with drop zone
 */

import type { MenuTemplateDTO, MenuItemDTO } from '@/api';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { cn } from '@/lib/utils';
import { 
  calculateDeliveryDate, 
  calculateOffsetDate, 
  formatAfrikaansDateTime 
} from '@/utils/menuTemplateCalculations';
import { Clock, Calendar, Trash2 } from 'lucide-react';

interface TimeslotCardProps {
  template: MenuTemplateDTO;
  assignedItems: MenuItemDTO[];
  weekStart: Date;
  isDropTarget?: boolean;
  isHeuristic?: boolean;
  isHistoric?: boolean;
  onDelete?: (id: number) => void;
  onDeleteMenuItem?: (id: number) => void;
  className?: string;
}

export function TimeslotCard({
  template,
  assignedItems,
  weekStart,
  isDropTarget,
  isHeuristic,
  isHistoric,
  onDelete,
  onDeleteMenuItem,
  className
}: TimeslotCardProps) {
  const deliveryDate = calculateDeliveryDate(
    weekStart,
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

  return (
    <Card
      className={cn(
        'w-72 flex-shrink-0 transition-colors',
        isDropTarget && 'border-blue-500 bg-blue-50 border-2',
        isHeuristic && 'bg-amber-50 border-amber-300',
        isHistoric && 'bg-gray-100 border-gray-400',
        !isDropTarget && !isHeuristic && 'border-gray-300',
        className
      )}
    >
      <CardHeader className="pb-3">
        <div className="flex items-start justify-between">
          <div className="flex-1">
            <CardTitle className="text-sm font-semibold">
              {formatAfrikaansDateTime(deliveryDate)}
            </CardTitle>
            <p className="text-xs text-muted-foreground mt-1">
              {template.description}
            </p>
          </div>
          <div className="flex items-center gap-2">
            {isHeuristic && (
              <span className="text-xs bg-amber-200 text-amber-800 px-2 py-0.5 rounded">
                Auto
              </span>
            )}
            {isHistoric && (
              <span className="text-xs bg-gray-200 text-gray-800 px-2 py-0.5 rounded">
                Histories
              </span>
            )}
            {onDelete && !isHeuristic && (
              <Button
                variant="ghost"
                size="sm"
                className="h-6 w-6 p-0 text-muted-foreground hover:text-destructive"
                onClick={(e) => {
                  e.stopPropagation();
                  if (template.id) {
                    onDelete(template.id);
                  }
                }}
                aria-label="Verwyder tydslot"
              >
                <Trash2 className="h-3 w-3" />
              </Button>
            )}
          </div>
        </div>
      </CardHeader>
      
      <CardContent className="space-y-2">
        {/* Drop zone area */}
        <div
          className={cn(
            'min-h-32 border-2 border-dashed rounded-lg p-2 space-y-2',
            isDropTarget ? 'border-blue-400 bg-blue-50/50' : 'border-gray-200',
            assignedItems.length === 0 && 'flex items-center justify-center'
          )}
        >
          {assignedItems.length === 0 ? (
            <p className="text-xs text-muted-foreground text-center">
              {isHistoric ? 'Histories vir verwysing' : 'Sleep items hierheen'}
            </p>
          ) : (
            assignedItems.map((item) => (
              <div
                key={item.id}
                className="bg-white border rounded p-2 text-xs flex items-start justify-between gap-2"
              >
                <div className="flex-1 min-w-0">
                  <div className="font-medium truncate">{item.name}</div>
                  {item.price && (
                    <div className="text-muted-foreground">R{item.price}</div>
                  )}
                </div>
                {onDeleteMenuItem && !isHistoric && (
                  <Button
                    variant="ghost"
                    size="sm"
                    className="h-5 w-5 p-0 text-muted-foreground hover:text-destructive flex-shrink-0"
                    onClick={(e) => {
                      e.stopPropagation();
                      if (item.id) {
                        onDeleteMenuItem(item.id);
                      }
                    }}
                    aria-label="Verwyder item"
                  >
                    <Trash2 className="h-3 w-3" />
                  </Button>
                )}
              </div>
            ))
          )}
        </div>

        {/* Metadata */}
        <div className="space-y-1 text-xs text-muted-foreground">
          <div className="flex items-center gap-1">
            <Calendar className="w-3 h-3" />
            <span>Bestel teen: {formatAfrikaansDateTime(orderByDate)}</span>
          </div>
          <div className="flex items-center gap-1">
            <Clock className="w-3 h-3" />
            <span>Vrygestel: {formatAfrikaansDateTime(releaseDate)}</span>
          </div>
        </div>
      </CardContent>
    </Card>
  );
}