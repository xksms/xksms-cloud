import UserSearch from './UserSearch';

const UserLookupCard = () => (
  <div className="card">
    <h2>用户信息查询</h2>
    <p>验证用户中心接口，确认全局响应包装是否符合预期。</p>
    <UserSearch />
  </div>
);

export default UserLookupCard;
