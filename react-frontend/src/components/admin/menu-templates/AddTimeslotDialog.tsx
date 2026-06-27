/**
 * AddTimeslotDialog - Form modal for creating new timeslots
 */

import { useState } from 'react';
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';
import { DAYS_OF_WEEK, dayTimeToOffset } from '@/utils/menuTemplateCalculations';
import type { MenuTemplateCreateRequest } from '@/api';

interface AddTimeslotDialogProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  onSubmit: (data: MenuTemplateCreateRequest) => void;
  currentPreset: string | null;
}

export function AddTimeslotDialog({
  open,
  onOpenChange,
  onSubmit,
  currentPreset
}: AddTimeslotDialogProps) {
  const [presetName, setPresetName] = useState(currentPreset || '');
  const [description, setDescription] = useState('');
  
  // Delivery settings
  const [deliveryDay, setDeliveryDay] = useState<string>('Monday');
  const [deliveryTime, setDeliveryTime] = useState('12:00');

  // Order by settings
  const [orderByDay, setOrderByDay] = useState<string>('Friday');
  const [orderByTime, setOrderByTime] = useState('10:00');

  // Release settings
  const [releaseDay, setReleaseDay] = useState<string>('Thursday');
  const [releaseTime, setReleaseTime] = useState('08:00');
  
  const deliveryOffset = dayTimeToOffset(deliveryDay, deliveryTime);
  const orderByOffset = dayTimeToOffset(orderByDay, orderByTime);
  const releaseOffset = dayTimeToOffset(releaseDay, releaseTime);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();

    let orderByRelativeOffset = orderByOffset - deliveryOffset;
    // If orderBy time is after delivery time, subtract a week (10080 minutes)
    if (orderByRelativeOffset > 0) {
      orderByRelativeOffset = orderByRelativeOffset - 10080;
    }

    let releaseRelativeOffset = releaseOffset - deliveryOffset;
    // If release time is after delivery time, subtract a week (10080 minutes)
    if (releaseRelativeOffset > 0) {
        releaseRelativeOffset = releaseRelativeOffset - 10080;
    }


    const data: MenuTemplateCreateRequest = {
      presetName: presetName,
      description: description,
      deliveryOffsetMinutes: deliveryOffset,
      orderByOffsetMinutes: orderByRelativeOffset,
      releaseOffsetMinutes: releaseRelativeOffset
    };
    console.log(data)

    onSubmit(data);
    onOpenChange(false);
    
    // Reset form
    setDescription('');
    setDeliveryDay('Monday');
    setDeliveryTime('12:00');
    setOrderByDay('Friday');
    setOrderByTime('10:00');
    setReleaseDay('Thursday');
    setReleaseTime('08:00');
  };

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="sm:max-w-[600px]">
        <form onSubmit={handleSubmit}>
          <DialogHeader>
            <DialogTitle>Add a new timeslot</DialogTitle>
            <DialogDescription>
              Set the times for delivery, ordering and release.
            </DialogDescription>
          </DialogHeader>

          <div className="space-y-6 py-4">
            {/* Preset Name */}
            <div className="space-y-2">
              <Label htmlFor="preset-name">Preset Name</Label>
              <Input
                id="preset-name"
                value={presetName}
                onChange={(e) => setPresetName(e.target.value)}
                placeholder="e.g. weekly-schedule"
                required
              />
            </div>

            {/* Description */}
            <div className="space-y-2">
              <Label htmlFor="description">Description</Label>
              <Input
                id="description"
                value={description}
                onChange={(e) => setDescription(e.target.value)}
                placeholder="e.g. Monday Lunch"
                required
              />
            </div>

            {/* Delivery Time */}
            <div className="space-y-2">
              <Label>Delivery</Label>
              <p className="text-sm text-muted-foreground">
                Delivery on{' '}
                <Select value={deliveryDay} onValueChange={setDeliveryDay}>
                  <SelectTrigger className="inline-flex w-32">
                    <SelectValue />
                  </SelectTrigger>
                  <SelectContent>
                    {DAYS_OF_WEEK.map((day) => (
                      <SelectItem key={day} value={day}>
                        {day}
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
                , at{' '}
                <Input
                  type="time"
                  value={deliveryTime}
                  onChange={(e) => setDeliveryTime(e.target.value)}
                  className="inline-flex w-24"
                  required
                />
              </p>
            </div>

            {/* Order By Time */}
            <div className="space-y-2">
              <Label>Order by</Label>
              <p className="text-sm text-muted-foreground">
                Orders must be placed by{' '}
                <Input
                  type="time"
                  value={orderByTime}
                  onChange={(e) => setOrderByTime(e.target.value)}
                  className="inline-flex w-24"
                  required
                />{' '}
                (the previous){' '}
                <Select value={orderByDay} onValueChange={setOrderByDay}>
                  <SelectTrigger className="inline-flex w-32">
                    <SelectValue />
                  </SelectTrigger>
                  <SelectContent>
                    {DAYS_OF_WEEK.map((day) => (
                      <SelectItem key={day} value={day}>
                        {day}
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>.
              </p>
            </div>

            {/* Release Time */}
            <div className="space-y-2">
              <Label>Release</Label>
              <p className="text-sm text-muted-foreground">
                The item is released at{' '}
                <Input
                  type="time"
                  value={releaseTime}
                  onChange={(e) => setReleaseTime(e.target.value)}
                  className="inline-flex w-24"
                  required
                />{' (the previous) '}
                <Select value={releaseDay} onValueChange={setReleaseDay}>
                  <SelectTrigger className="inline-flex w-32">
                    <SelectValue />
                  </SelectTrigger>
                  <SelectContent>
                    {DAYS_OF_WEEK.map((day) => (
                      <SelectItem key={day} value={day}>
                        {day}
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>.
              </p>
            </div>
          </div>

          <DialogFooter>
            <Button
              type="button"
              variant="outline"
              onClick={() => onOpenChange(false)}
            >
              Cancel
            </Button>
            <Button type="submit">Add</Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  );
}