import NotificationStream from '../components/NotificationStream';

const NotificationsPage = () => (
  <div className="page">
    <div className="card">
      <h2>实时通知</h2>
      <p>选择用户后，将通过 Server-Sent Events 接收后台推送的通知。</p>
      <NotificationStream />
    </div>
  </div>
);

export default NotificationsPage;
