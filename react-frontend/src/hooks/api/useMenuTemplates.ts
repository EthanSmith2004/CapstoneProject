/**
 * TanStack Query hooks for menu template management
 */

import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { 
  AdminMenuTemplatesApi, 
  AdminMenuApi
} from '@/api';
import type { 
  MenuTemplateCreateRequest,
  MenuTemplateUpdateRequest,
  MenuItemQueueRequest
} from '@/api';
import { useAuth } from '@/contexts/AuthContext';

// ============= Query Hooks =============

/**
 * Get all distinct preset names with metadata
 */
export function usePresets() {
  const { getApiClient } = useAuth();
  
  return useQuery({
    queryKey: ['menu-templates', 'presets'],
    queryFn: async () => {
      const api = getApiClient(AdminMenuTemplatesApi);
      const response = await api.getDistinctPresetNames();
      return response.data;
    }
  });
}

/**
 * Get all templates for a specific preset
 */
export function useTemplatesByPreset(presetName: string | null) {
  const { getApiClient } = useAuth();
  
  return useQuery({
    queryKey: ['menu-templates', 'preset', presetName],
    queryFn: async () => {
      if (!presetName) return [];
      const api = getApiClient(AdminMenuTemplatesApi);
      const response = await api.getTemplatesByPresetName(presetName);
      return response.data;
    },
    enabled: !!presetName
  });
}

/**
 * Get current menu items (not yet delivered)
 */
export function useCurrentMenus() {
  const { getApiClient } = useAuth();
  
  return useQuery({
    queryKey: ['admin-menus', 'current'],
    queryFn: async () => {
      const api = getApiClient(AdminMenuApi);
      const response = await api.getCurrentMenuItems();
      return response.data;
    }
  });
}

/**
 * Get historic menu items (already delivered)
 */
export function useHistoricMenus() {
  const { getApiClient } = useAuth();
  
  return useQuery({
    queryKey: ['admin-menus', 'historic'],
    queryFn: async () => {
      const api = getApiClient(AdminMenuApi);
      const response = await api.getHistoricMenuItems();
      return response.data;
    }
  });
}

/**
 * Get draft menu items (items not yet scheduled)
 */
export function useDraftMenuItems() {
  const { getApiClient } = useAuth();
  
  return useQuery({
    queryKey: ['menu-items', 'draft'],
    queryFn: async () => {
      const api = getApiClient(AdminMenuApi);
      const response = await api.getDraftMenuItems();
      return response.data;
    }
  });
}

// ============= Mutation Hooks =============

/**
 * Create a new menu template
 */
export function useCreateTemplate() {
  const { getApiClient } = useAuth();
  const queryClient = useQueryClient();
  
  return useMutation({
    mutationFn: async (data: MenuTemplateCreateRequest) => {
      const api = getApiClient(AdminMenuTemplatesApi);
      const response = await api.createTemplate(data);
      return response.data;
    },
    onSuccess: (_, variables) => {
      queryClient.refetchQueries({ queryKey: ['menu-templates', 'preset', variables.presetName] });
      queryClient.refetchQueries({ queryKey: ['menu-templates', 'presets'] });
    }
  });
}

/**
 * Update an existing menu template
 */
export function useUpdateTemplate() {
  const { getApiClient } = useAuth();
  const queryClient = useQueryClient();
  
  return useMutation({
    mutationFn: async ({ id, data }: { id: number; data: MenuTemplateUpdateRequest }) => {
      const api = getApiClient(AdminMenuTemplatesApi);
      const response = await api.updateTemplate(id, data);
      return response.data;
    },
    onSuccess: () => {
      queryClient.refetchQueries({ queryKey: ['menu-templates'] });
    }
  });
}

/**
 * Delete a menu template
 */
export function useDeleteTemplate() {
  const { getApiClient } = useAuth();
  const queryClient = useQueryClient();
  
  return useMutation({
    mutationFn: async (id: number) => {
      const api = getApiClient(AdminMenuTemplatesApi);
      await api.deleteTemplate(id);
    },
    onSuccess: () => {
      queryClient.refetchQueries({ queryKey: ['menu-templates'] });
    }
  });
}

/**
 * Delete all templates for a preset
 */
export function useDeleteTemplatesByPreset() {
  const { getApiClient } = useAuth();
  const queryClient = useQueryClient();
  
  return useMutation({
    mutationFn: async (presetName: string) => {
      const api = getApiClient(AdminMenuTemplatesApi);
      await api.deleteTemplatesByPresetName(presetName);
    },
    onSuccess: () => {
      queryClient.refetchQueries({ queryKey: ['menu-templates'] });
    }
  });
}

/**
 * Queue a menu item with calculated dates
 */
export function useQueueMenuItem() {
  const { getApiClient } = useAuth();
  const queryClient = useQueryClient();
  
  return useMutation({
    mutationFn: async (request: MenuItemQueueRequest) => {
      const api = getApiClient(AdminMenuApi);
      const response = await api.queueMenuItem(request);
      return response.data;
    },
    onSuccess: () => {
      queryClient.refetchQueries({ queryKey: ['admin-menus'] });
      queryClient.refetchQueries({ queryKey: ['menu-items'] });
    }
  });
}

/**
 * Delete a menu item (draft or current only, not historic)
 */
export function useDeleteMenuItem() {
  const { getApiClient } = useAuth();
  const queryClient = useQueryClient();
  
  return useMutation({
    mutationFn: async (id: number) => {
      const api = getApiClient(AdminMenuApi);
      await api.deleteMenuItem(id);
    },
    onSuccess: () => {
      queryClient.refetchQueries({ queryKey: ['admin-menus'] });
      queryClient.refetchQueries({ queryKey: ['menu-items'] });
      queryClient.refetchQueries({ queryKey: ['admin-menu-items'] });
    }
  });
}