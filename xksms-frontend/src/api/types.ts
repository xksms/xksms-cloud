export interface ApiResponse<T> {
  code: number;
  message: string;
  data: T;
}

export interface User {
  id: number;
  username: string;
}

export interface Notification {
  eventId: string;
  recipientId: string;
  type: string;
  content: string;
  timestamp: string;
}
