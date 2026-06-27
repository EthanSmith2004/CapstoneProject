/**
 * Utility functions for menu template date calculations
 */

/**
 * Calculate absolute delivery date from template offset
 * @param weekStart - Monday 00:00 UTC of the current week
 * @param offsetMinutes - Minutes from Monday 00:00 UTC
 */
export function calculateDeliveryDate(
  weekStart: Date,
  offsetMinutes: number
): Date {
  const deliveryDate = new Date(weekStart);
  deliveryDate.setMinutes(deliveryDate.getMinutes() + offsetMinutes);
  return deliveryDate;
}

/**
 * Calculate release/orderBy date by adding offset from delivery
 * @param deliveryDate - Calculated delivery date
 * @param offsetMinutes - Minutes offset from delivery
 */
export function calculateOffsetDate(
  deliveryDate: Date,
  offsetMinutes: number
): Date {
  const offsetDate = new Date(deliveryDate);
  offsetDate.setMinutes(offsetDate.getMinutes() + offsetMinutes);
  return offsetDate;
}

/**
 * Get Monday 00:00 UTC for a given week
 */
export function getWeekStart(date: Date): Date {
  const weekStart = new Date(date);
  const day = weekStart.getUTCDay();
  const diff = weekStart.getUTCDate() - day + (day === 0 ? -6 : 1);
  weekStart.setUTCDate(diff);
  weekStart.setUTCHours(0, 0, 0, 0);
  return weekStart;
}

/**
 * Get the end of the week (Sunday 23:59:59)
 */
export function getWeekEnd(weekStart: Date): Date {
  const weekEnd = new Date(weekStart);
  weekEnd.setUTCDate(weekEnd.getUTCDate() + 6);
  weekEnd.setUTCHours(23, 59, 59, 999);
  return weekEnd;
}

/**
 * Convert day name and time to minutes offset from Monday 00:00
 */
export function dayTimeToOffset(
  dayOfWeek: string, // 'Monday', 'Tuesday', etc.
  time: string // 'HH:MM'
): number {
  const dayMap: Record<string, number> = {
    'Monday': 0,
    'Tuesday': 1,
    'Wednesday': 2,
    'Thursday': 3,
    'Friday': 4,
    'Saturday': 5,
    'Sunday': 6
  };
  
  const [hours, minutes] = time.split(':').map(Number);
  const dayMinutes = dayMap[dayOfWeek] * 24 * 60;
  const timeMinutes = hours * 60 + minutes;
  const timeZoneOffset = new Date().getTimezoneOffset(); // in minutes
  
  return dayMinutes + timeMinutes + timeZoneOffset;
}

/**
 * Convert offset minutes to day name and time
 */
export function offsetToDayTime(offsetMinutes: number): {
  day: string;
  time: string;
} {
  const timeZoneOffset = new Date().getTimezoneOffset(); // in minutes
  let mins = offsetMinutes + timeZoneOffset;
  while (mins < 0) {
    mins += 7 * 24 * 60; // wrap around week
  }
  while (mins >= 7 * 24 * 60) {
    mins -= 7 * 24 * 60; // wrap around week
  }
  const days = ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday'];
  const dayIndex = Math.floor(mins / (24 * 60));
  const remainingMinutes = mins % (24 * 60);
  const hours = Math.floor(remainingMinutes / 60);
  const minutes = remainingMinutes % 60;


  return {
    day: days[dayIndex] || 'Monday',
    time: `${String(hours).padStart(2, '0')}:${String(minutes).padStart(2, '0')}`
  };
}

/**
 * Format a date to day and time
 */
export function formatAfrikaansDateTime(date: Date): string {
  return date.toLocaleString('en-ZA', {
    weekday: 'long',
    hour: '2-digit',
    minute: '2-digit',
    hour12: false,
  });
}

/**
 * Format a date to short format
 */
export function formatAfrikaansDate(date: Date): string {
  return date.toLocaleDateString('en-ZA', {
    day: 'numeric',
    month: 'short',
  });
}

/**
 * Check if a menu item matches a template's delivery time
 */
export function menuMatchesTemplate(
  menuDeliveryDate: string,
  templateDeliveryDate: Date
): boolean {
  const menuDate = new Date(menuDeliveryDate);
  // Compare dates within a 30-minute window
  const diff = Math.abs(menuDate.getTime() - templateDeliveryDate.getTime());
  return diff < 30 * 60 * 1000; // 30 minutes in milliseconds
}

/**
 * Check if two dates are the same day
 */
export function isSameDay(date1: Date, date2: Date): boolean {
  return (
    date1.getFullYear() === date2.getFullYear() &&
    date1.getMonth() === date2.getMonth() &&
    date1.getDate() === date2.getDate()
  );
}

/**
 * Get the days of the week
 */
export const DAYS_OF_WEEK = [
  'Monday',
  'Tuesday',
  'Wednesday',
  'Thursday',
  'Friday',
  'Saturday',
  'Sunday'
] as const;

export type DayOfWeek = typeof DAYS_OF_WEEK[number];
