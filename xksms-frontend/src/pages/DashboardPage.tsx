import NotificationStreamCard from '../components/NotificationStreamCard';
import UserLookupCard from '../components/UserLookupCard';

const DashboardPage = () => (
  <div className="page">
    <div className="card-grid">
      <UserLookupCard />
      <NotificationStreamCard />
    </div>
  </div>
);

export default DashboardPage;
