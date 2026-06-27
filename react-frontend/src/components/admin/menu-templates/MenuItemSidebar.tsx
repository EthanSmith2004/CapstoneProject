/**
 * MenuItemSidebar - Draggable menu items library
 */

import { useDraggable } from '@dnd-kit/core';
import type { MenuItemDTO } from '@/api';
import { MenuItemCard } from './MenuItemCard';
import { Input } from '@/components/ui/input';
import { useState } from 'react';
import { Search } from 'lucide-react';

interface MenuItemSidebarProps {
  items: MenuItemDTO[];
  isLoading?: boolean;
}

function DraggableMenuItem({ item }: { item: MenuItemDTO }) {
  const { attributes, listeners, setNodeRef, isDragging } = useDraggable({
    id: `menu-item-${item.id}`,
    data: { type: 'menu-item', item }
  });

  return (
    <div ref={setNodeRef} {...listeners} {...attributes}>
      <MenuItemCard item={item} isDragging={isDragging} />
    </div>
  );
}

export function MenuItemSidebar({ items, isLoading }: MenuItemSidebarProps) {
  const [searchTerm, setSearchTerm] = useState('');

  const filteredItems = items.filter((item) => {
    const searchLower = searchTerm.toLowerCase();
    return (
      item.name?.toLowerCase().includes(searchLower) ||
      item.description?.toLowerCase().includes(searchLower)
    );
  });

  return (
    <div className="w-80 border-r bg-muted/30 flex flex-col h-full">
      <div className="p-4 border-b">
        <h2 className="font-semibold text-lg mb-3">Items</h2>
        <div className="relative">
          <Search className="absolute left-2 top-2.5 h-4 w-4 text-muted-foreground" />
          <Input
            placeholder="Search items..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            className="pl-8"
          />
        </div>
      </div>

      <div className="flex-1 overflow-y-auto p-4 space-y-2">
        {isLoading ? (
          <div className="text-sm text-muted-foreground text-center py-8">
            Loading...
          </div>
        ) : filteredItems.length === 0 ? (
          <div className="text-sm text-muted-foreground text-center py-8">
            {searchTerm ? 'No items found' : 'No items available'}
          </div>
        ) : (
          filteredItems.map((item) => (
            <DraggableMenuItem key={item.id} item={item} />
          ))
        )}
      </div>
    </div>
  );
}