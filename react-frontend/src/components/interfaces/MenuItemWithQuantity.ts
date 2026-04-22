import type { MenuItemDTO } from '@/api/models'

export interface MenuItemWithQuantity extends MenuItemDTO {
  quantity?: number;
}