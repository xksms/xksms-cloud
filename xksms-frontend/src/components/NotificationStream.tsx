import { FormEvent, useState } from 'react';
import dayjs from 'dayjs';
import relativeTime from 'dayjs/plugin/relativeTime';
import 'dayjs/locale/zh-cn';
import { useNotificationStream } from '../hooks/useNotificationStream';

dayjs.extend(relativeTime);
dayjs.locale('zh-cn');

const NotificationStream = () => {
  const [userId, setUserId] = useState('');
  const [activeUserId, setActiveUserId] = useState<string | undefined>();
  const { notifications, isConnected, error } = useNotificationStream({
    userId: activeUserId,
    enabled: Boolean(activeUserId),
  });

  const handleSubmit = (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setActiveUserId(userId.trim() || undefined);
  };

  return (
    <div>
      <form onSubmit={handleSubmit}>
        <div className="input-group">
          <label htmlFor="notification-user">接收用户 ID</label>
          <input
            id="notification-user"
            type="text"
            placeholder="输入 userId，例如 user-001"
            value={userId}
            onChange={(event) => setUserId(event.target.value)}
          />
        </div>
        <button type="submit">连接通知流</button>
      </form>

      {activeUserId ? (
        <p className="sse-status">
          {isConnected
            ? `已连接到 ${activeUserId} 的通知流。`
            : '正在尝试建立通知连接…'}
        </p>
      ) : (
        <div className="empty-state">请输入用户 ID 后开始监听通知。</div>
      )}

      {error && <div className="error-banner">{error}</div>}

      {activeUserId && notifications.length === 0 && !error && (
        <div className="empty-state">暂未接收到通知，请触发后台推送。</div>
      )}

      {notifications.length > 0 && (
        <div className="notification-list" style={{ marginTop: '1.5rem' }}>
          {notifications.map((notification) => (
            <div key={notification.eventId} className="notification-item">
              <div className="notification-item__meta">
                <span className="badge">{notification.type}</span>
                <span style={{ marginLeft: '0.75rem' }}>
                  {dayjs(notification.timestamp).fromNow()}
                </span>
              </div>
              <strong>{notification.content}</strong>
              <p>接收者：{notification.recipientId}</p>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default NotificationStream;
