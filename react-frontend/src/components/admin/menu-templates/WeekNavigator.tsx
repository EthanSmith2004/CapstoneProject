/**
 * WeekNavigator - Controls for navigating between weeks
 */

import { Button } from '@/components/ui/button';
import { ChevronLeft, ChevronRight } from 'lucide-react';
import { formatAfrikaansDate, getWeekEnd } from '@/utils/menuTemplateCalculations';

interface WeekNavigatorProps {
  currentWeekStart: Date;
  onWeekChange: (direction: 'prev' | 'next') => void;
}

export function WeekNavigator({ currentWeekStart, onWeekChange }: WeekNavigatorProps) {
  const weekEnd = getWeekEnd(currentWeekStart);

  return (
    <div className="flex items-center gap-2">
      <Button
        variant="outline"
        size="icon"
        onClick={() => onWeekChange('prev')}
      >
        <ChevronLeft className="w-4 h-4" />
      </Button>
      
      <div className="min-w-48 text-center">
        <span className="font-medium">
          {formatAfrikaansDate(currentWeekStart)} - {formatAfrikaansDate(weekEnd)}
        </span>
      </div>
      
      <Button
        variant="outline"
        size="icon"
        onClick={() => onWeekChange('next')}
      >
        <ChevronRight className="w-4 h-4" />
      </Button>
    </div>
  );
}