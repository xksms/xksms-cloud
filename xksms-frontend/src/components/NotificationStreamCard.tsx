import NotificationStream from './NotificationStream';

const NotificationStreamCard = () => (
  <div className="card">
    <h2>通知推送监控</h2>
    <p>输入用户 ID 后，实时跟踪后端推送的通知事件。</p>
    <NotificationStream />
  </div>
);

export default NotificationStreamCard;
