```
xksms-cloud
├── docker/                         # ✅ 专用 docker 资源目录
│   ├── docker-compose.yml          # 默认组合编排（如 ELK + SkyWalking）
│   ├── elk/
│   │   └── docker-compose-elk.yml  # 单独部署 ELK 的配置
│   ├── skywalking/
│   │   ├── docker-compose.yml      # SkyWalking OAP + UI
│   │   └── config/                 # agent 或 oap 的额外配置
│   ├── prometheus/
│   │   ├── docker-compose.yml
│   │   ├── prometheus.yml          # Prometheus 采集配置
│   │   └── grafana/                # grafana 配置与 dashboard
│   └── readme.md                   # 每个子系统说明与入口

| 路径                   | 作用                                           |
| -------------------- | -------------------------------------------- |
| `docker/`            | 所有与容器化部署相关的文件集中管理                            |
| `elk/`               | Filebeat + Logstash + Elasticsearch + Kibana |
| `skywalking/`        | SkyWalking OAP + UI，Agent 配置说明也可放入           |
| `prometheus/`        | Prometheus + Grafana 监控系统                    |
| `docker-compose.yml` | 可以是入口组合部署，也可拆分为每个服务独立控制                      |
```
# 📦 XKSMS 基础设施 Docker 启动说明

## 📊 日志平台 ELK

- 目录：`docker/elk/docker-compose-elk.yml`
- 启动：`docker compose -f docker/elk/docker-compose-elk.yml up -d`
- 访问：
  - Kibana: [http://localhost:5601](http://localhost:5601)
  - Elastic: [http://localhost:9200](http://localhost:9200)
- 默认账户：
  - 用户名：elastic
  - 密码：changeme

## 🛰️ 链路追踪 SkyWalking

- 目录：`docker/skywalking/docker-compose.yml`
- 启动：`docker compose -f docker/skywalking/docker-compose.yml up -d`
- 访问：
  - UI: [http://localhost:18080](http://localhost:18080)
- Agent配置：参考 `docker/skywalking/config/README.md`

## 📈 指标监控 Prometheus + Grafana

- 目录：`docker/prometheus/docker-compose.yml`
- 启动：`docker compose -f docker/prometheus/docker-compose.yml up -d`
- 访问：
  - Prometheus: [http://localhost:9090](http://localhost:9090)
  - Grafana: [http://localhost:3000](http://localhost:3000)
- Grafana 默认账号：
  - 用户名：admin
  - 密码：admin
