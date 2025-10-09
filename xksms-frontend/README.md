# XKSMS Cloud Web Console

基于 Vite + React + TypeScript 构建的前端控制台，用于联调 `xksms-cloud` 后端的核心示例接口：

- 用户中心：`GET /users/{id}`
- 通知中心：`GET /notifications/stream/{userId}` (Server-Sent Events)

## 快速开始

```bash
cd xksms-frontend
npm install
npm run dev
```

默认后端地址为 `http://localhost:8080`，可通过 `.env` 或运行命令时注入 `VITE_API_BASE_URL` 覆盖。

## 目录结构

```
src/
├── api          # Axios 实例与接口类型定义
├── components   # UI 组件（通知流、用户查询卡片等）
├── hooks        # 自定义 Hooks（SSE 通知流）
├── pages        # 页面级组件
└── styles.css   # 全局样式
```

## 设计亮点

- 使用 React Query 管理异步请求缓存与状态。
- 封装 SSE 连接 Hook，自动清理连接并限制通知列表长度。
- 轻量化 UI，便于快速扩展到更多业务模块。
