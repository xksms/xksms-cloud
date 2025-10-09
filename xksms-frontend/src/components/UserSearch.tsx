import { FormEvent, useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { client } from '../api/client';
import type { ApiResponse, User } from '../api/types';

const fetchUser = async (userId: number): Promise<ApiResponse<User>> => {
  const { data } = await client.get<ApiResponse<User>>(`/users/${userId}`);
  return data;
};

const UserSearch = () => {
  const [userId, setUserId] = useState<number | ''>('');
  const { data, isFetching, isError, error, refetch } = useQuery({
    queryKey: ['user', userId],
    queryFn: () => fetchUser(Number(userId)),
    enabled: false,
    retry: false,
  });

  const handleSubmit = (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    if (!userId) return;
    refetch();
  };

  return (
    <form onSubmit={handleSubmit}>
      <div className="input-group">
        <label htmlFor="userId">用户 ID</label>
        <input
          id="userId"
          type="number"
          min={1}
          placeholder="例如 1"
          value={userId}
          onChange={(event) => setUserId(event.target.value ? Number(event.target.value) : '')}
        />
      </div>
      <button type="submit" disabled={!userId || isFetching}>
        {isFetching ? '查询中…' : '获取用户信息'}
      </button>

      {data && (
        <div className="notification-item" style={{ marginTop: '1.5rem' }}>
          <div className="notification-item__meta">
            <span className="badge">ID #{data.data.id}</span>
          </div>
          <strong>{data.data.username}</strong>
          <p>接口调用成功，全局响应包装正常工作。</p>
        </div>
      )}

      {isError && (
        <div className="error-banner">
          {(error as Error).message || '查询失败，请稍后重试。'}
        </div>
      )}
    </form>
  );
};

export default UserSearch;
