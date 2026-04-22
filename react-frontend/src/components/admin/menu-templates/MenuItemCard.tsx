/**
 * MenuItemCard - Displays a menu item in the sidebar
 */

import type { MenuItemDTO } from '@/api';
import { Card, CardContent } from '@/components/ui/card';
import { cn } from '@/lib/utils';

interface MenuItemCardProps {
  item: MenuItemDTO;
  isDragging?: boolean;
  className?: string;
}

export function MenuItemCard({ item, isDragging, className }: MenuItemCardProps) {
  return (
    <Card
      className={cn(
        'cursor-grab active:cursor-grabbing transition-opacity',
        isDragging && 'opacity-50',
        className
      )}
    >
      <CardContent className="p-3">
        <div className="flex items-center gap-3">
          {item.imageHero && (
            <img
              src={item.imageHero}
              alt={item.name || ''}
              className="w-12 h-12 rounded object-cover"
            />
          )}
          <div className="flex-1 min-w-0">
            <h4 className="font-medium text-sm truncate">{item.name}</h4>
            {item.description && (
              <p className="text-xs text-muted-foreground truncate">
                {item.description}
              </p>
            )}
            <div className="flex items-center gap-2 mt-1">
              {item.price && (
                <span className="text-xs font-medium">R{item.price}</span>
              )}
              {item.kcal && (
                <span className="text-xs text-muted-foreground">{item.kcal} kcal</span>
              )}
            </div>
          </div>
        </div>
      </CardContent>
    </Card>
  );
}