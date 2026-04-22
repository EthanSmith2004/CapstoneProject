import { clsx, type ClassValue } from 'clsx'
import { twMerge } from 'tailwind-merge'

export function cn(...inputs: ClassValue[]) {
  return twMerge(clsx(inputs))
}
export function formatDate(dateString: string | undefined){
  if (!dateString) 
    return 'N/A';
  const date = new Date(dateString)
  return date.toLocaleString('af-ZA', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
  })
}

export function formatDateLong(dateString: string | undefined){
  if (!dateString) 
    return 'N/A';
  const date = new Date(dateString)
  return date.toLocaleString('af-ZA', {
    weekday: 'long',
    year: 'numeric',
    month: 'long',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit',
  })
}

export function formatDateNoTime(dateString: string | undefined){
  if (!dateString) 
    return 'N/A';
  const date = new Date(dateString)
  return date.toLocaleDateString('af-ZA', {
    weekday: 'long',
    month: 'long',
    day: '2-digit',
  });
}