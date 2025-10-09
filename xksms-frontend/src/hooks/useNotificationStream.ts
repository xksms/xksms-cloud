import { useEffect, useRef, useState } from 'react';
import type { Notification } from '../api/types';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080';

interface UseNotificationStreamOptions {
  userId?: string;
  enabled?: boolean;
}

export const useNotificationStream = ({ userId, enabled = false }: UseNotificationStreamOptions) => {
  const eventSourceRef = useRef<EventSource | null>(null);
  const [notifications, setNotifications] = useState<Notification[]>([]);
  const [isConnected, setIsConnected] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!enabled || !userId) {
      setNotifications([]);
      setError(null);
      setIsConnected(false);
      if (eventSourceRef.current) {
        eventSourceRef.current.close();
        eventSourceRef.current = null;
      }
      return;
    }

    const url = `${API_BASE_URL}/notifications/stream/${userId}`;
    const source = new EventSource(url);
    eventSourceRef.current = source;
    setNotifications([]);
    setError(null);
    setIsConnected(false);

    source.onopen = () => {
      setIsConnected(true);
      setError(null);
    };

    source.onmessage = (event) => {
      try {
        const data = JSON.parse(event.data) as Notification;
        setNotifications((prev) => [data, ...prev].slice(0, 50));
      } catch (err) {
        console.error('无法解析通知消息', err);
      }
    };

    source.onerror = () => {
      setIsConnected(false);
      setError('通知连接中断，将尝试自动重连。');
    };

    return () => {
      source.close();
      eventSourceRef.current = null;
      setIsConnected(false);
    };
  }, [enabled, userId]);

  return { notifications, isConnected, error };
};
