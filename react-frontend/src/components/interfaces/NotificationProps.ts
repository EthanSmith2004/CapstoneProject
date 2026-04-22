export interface NotificationProps {
  id: number;
  title: string;
  message: string;
  createdAt: Date;
  type: 'SUCCESS' | 'CAUTION' | 'FAILURE' | 'INFO';
}