/**
 * AddTimeslotDialog - Form modal for creating new timeslots with Afrikaans text
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
  const [deliveryDay, setDeliveryDay] = useState<string>('Maandag');
  const [deliveryTime, setDeliveryTime] = useState('12:00');
  
  // Order by settings
  const [orderByDay, setOrderByDay] = useState<string>('Vrydag');
  const [orderByTime, setOrderByTime] = useState('10:00');
  
  // Release settings
  const [releaseDay, setReleaseDay] = useState<string>('Donderdag');
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
    setDeliveryDay('Maandag');
    setDeliveryTime('12:00');
    setOrderByDay('Vrydag');
    setOrderByTime('10:00');
    setReleaseDay('Donderdag');
    setReleaseTime('08:00');
  };

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="sm:max-w-[600px]">
        <form onSubmit={handleSubmit}>
          <DialogHeader>
            <DialogTitle>Voeg 'n nuwe tydslot by</DialogTitle>
            <DialogDescription>
              Stel die tye vir aflewering, bestellings en vrystelling in.
            </DialogDescription>
          </DialogHeader>

          <div className="space-y-6 py-4">
            {/* Preset Name */}
            <div className="space-y-2">
              <Label htmlFor="preset-name">Preset Naam</Label>
              <Input
                id="preset-name"
                value={presetName}
                onChange={(e) => setPresetName(e.target.value)}
                placeholder="bv. weeklikse-rooster"
                required
              />
            </div>

            {/* Description */}
            <div className="space-y-2">
              <Label htmlFor="description">Beskrywing</Label>
              <Input
                id="description"
                value={description}
                onChange={(e) => setDescription(e.target.value)}
                placeholder="bv. Maandag Middagete"
                required
              />
            </div>

            {/* Delivery Time */}
            <div className="space-y-2">
              <Label>Aflewering</Label>
              <p className="text-sm text-muted-foreground">
                Aflewering vir{' '}
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
                , om{' '}
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
              <Label>Bestel teen</Label>
              <p className="text-sm text-muted-foreground">
                Bestellings moet teen{' '}
                <Input
                  type="time"
                  value={orderByTime}
                  onChange={(e) => setOrderByTime(e.target.value)}
                  className="inline-flex w-24"
                  required
                />{' '}
                (die vorige){' '}
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
                </Select>{' '}
                geplaas word.
              </p>
            </div>

            {/* Release Time */}
            <div className="space-y-2">
              <Label>Vrystelling</Label>
              <p className="text-sm text-muted-foreground">
                Die item word{' '}
                <Input
                  type="time"
                  value={releaseTime}
                  onChange={(e) => setReleaseTime(e.target.value)}
                  className="inline-flex w-24"
                  required
                />{' (die vorige) '}
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
                </Select>{' '}
                vrygestel.
              </p>
            </div>
          </div>

          <DialogFooter>
            <Button
              type="button"
              variant="outline"
              onClick={() => onOpenChange(false)}
            >
              Kanselleer
            </Button>
            <Button type="submit">Voeg by</Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  );
}