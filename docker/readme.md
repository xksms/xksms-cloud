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
