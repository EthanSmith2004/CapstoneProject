/**
 * PresetSelector - Dropdown for selecting menu template presets
 */

import type { PresetNameDTO } from '@/api';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';
import { Label } from '@/components/ui/label';

interface PresetSelectorProps {
  presets: PresetNameDTO[];
  selectedPreset: string | null;
  onPresetChange: (preset: string) => void;
}

export function PresetSelector({
  presets,
  selectedPreset,
  onPresetChange
}: PresetSelectorProps) {
  return (
    <div className="flex items-center gap-2">
      <Label htmlFor="preset-select">Template:</Label>
      <Select value={selectedPreset || undefined} onValueChange={onPresetChange}>
        <SelectTrigger id="preset-select" className="w-64">
          <SelectValue placeholder="Select a Template" />
        </SelectTrigger>
        <SelectContent>
          {presets.map((preset) => (
            <SelectItem key={preset.presetName} value={preset.presetName || ''}>
              {preset.presetName} ({preset.templateCount} timeslots)
            </SelectItem>
          ))}
        </SelectContent>
      </Select>
    </div>
  );
}