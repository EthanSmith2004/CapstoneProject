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
  dayOfWeek: string, // 'Maandag', 'Dinsdag', etc.
  time: string // 'HH:MM'
): number {
  const dayMap: Record<string, number> = {
    'Maandag': 0,
    'Dinsdag': 1,
    'Woensdag': 2,
    'Donderdag': 3,
    'Vrydag': 4,
    'Saterdag': 5,
    'Sondag': 6
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
  const days = ['Maandag', 'Dinsdag', 'Woensdag', 'Donderdag', 'Vrydag', 'Saterdag', 'Sondag'];
  const dayIndex = Math.floor(mins / (24 * 60));
  const remainingMinutes = mins % (24 * 60);
  const hours = Math.floor(remainingMinutes / 60);
  const minutes = remainingMinutes % 60;

  
  return {
    day: days[dayIndex] || 'Maandag',
    time: `${String(hours).padStart(2, '0')}:${String(minutes).padStart(2, '0')}`
  };
}

/**
 * Format a date to Afrikaans day and time
 */
export function formatAfrikaansDateTime(date: Date): string {
  const days = ['Sondag', 'Maandag', 'Dinsdag', 'Woensdag', 'Donderdag', 'Vrydag', 'Saterdag'];
  const day = days[date.getDay()];
  const hours = String(date.getHours()).padStart(2, '0');
  const minutes = String(date.getMinutes()).padStart(2, '0');
  
  return `${day} ${hours}:${minutes}`;
}

/**
 * Format a date to short Afrikaans format
 */
export function formatAfrikaansDate(date: Date): string {
  const months = ['Jan', 'Feb', 'Mrt', 'Apr', 'Mei', 'Jun', 'Jul', 'Aug', 'Sep', 'Okt', 'Nov', 'Des'];
  const day = date.getDate();
  const month = months[date.getMonth()];
  
  return `${day} ${month}`;
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
 * Get the days of the week in Afrikaans
 */
export const DAYS_OF_WEEK = [
  'Maandag',
  'Dinsdag',
  'Woensdag',
  'Donderdag',
  'Vrydag',
  'Saterdag',
  'Sondag'
] as const;

export type DayOfWeek = typeof DAYS_OF_WEEK[number];