#!/bin/bash
set -e

echo "=============================================="
echo "  XKSMS-Cloud 一键部署脚本"
echo "=============================================="

# 配置变量
PROJECT_DIR="/opt/xksms-cloud"
LOG_DIR="/opt/xksms-cloud/logs"
ENV_FILE="${PROJECT_DIR}/.env"

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 检查是否以 root 运行
if [ "$EUID" -ne 0 ]; then
  echo -e "${RED}请使用 root 用户运行此脚本${NC}"
  exit 1
fi

# 1. 创建必要目录
echo -e "${YELLOW}[1/5] 创建目录...${NC}"
mkdir -p ${PROJECT_DIR}
mkdir -p ${LOG_DIR}/{gateway,auth,user,system,notify}
mkdir -p ${PROJECT_DIR}/docker/{mysql,redis,nacos}

# 2. 创建环境配置文件
echo -e "${YELLOW}[2/5] 创建环境配置...${NC}"
cat > ${ENV_FILE} <<EOF
# XKSMS-Cloud 环境配置

# 镜像仓库地址（可选，本地部署留空）
REGISTRY=local

# 镜像版本
VERSION=1.0.0

# MySQL 配置
MYSQL_ROOT_PASSWORD=YourStrongPassword123!

# Redis 配置
REDIS_PASSWORD=YourStrongPassword123!
EOF

# 3. 检查 Docker
echo -e "${YELLOW}[3/5] 检查 Docker...${NC}"
if ! command -v docker &> /dev/null; then
    echo -e "${RED}Docker 未安装，请先安装 Docker${NC}"
    exit 1
fi

if ! command -v docker compose &> /dev/null; then
    echo -e "${RED}Docker Compose 未安装${NC}"
    exit 1
fi

echo -e "${GREEN}Docker 版本：$(docker --version)${NC}"
echo -e "${GREEN}Docker Compose 版本：$(docker compose version)${NC}"

# 4. 启动服务
echo -e "${YELLOW}[4/5] 启动服务...${NC}"
cd ${PROJECT_DIR}

# 停止现有服务（如果有）
docker compose -f docker-compose.prod.yml down 2>/dev/null || true

# 启动服务
docker compose -f docker-compose.prod.yml up -d

# 5. 检查服务状态
echo -e "${YELLOW}[5/5] 检查服务状态...${NC}"
sleep 10
docker compose -f docker-compose.prod.yml ps

echo ""
echo -e "${GREEN}=============================================="
echo "  部署完成！"
echo "==============================================${NC}"
echo ""
echo "服务访问地址："
echo "  - Gateway:     http://<服务器 IP>:8080"
echo "  - Auth:        http://<服务器 IP>:8081"
echo "  - User:        http://<服务器 IP>:8082"
echo "  - System:      http://<服务器 IP>:8083"
echo "  - Notify:      http://<服务器 IP>:8084"
echo "  - Nacos:       http://<服务器 IP>:8848  (账号/密码：nacos/nacos)"
echo "  - MySQL:       <服务器 IP>:3306"
echo "  - Redis:       <服务器 IP>:6379"
echo ""
echo "常用命令："
echo "  - 查看日志：docker compose -f docker-compose.prod.yml logs -f gateway"
echo "  - 重启服务：docker compose -f docker-compose.prod.yml restart <服务名>"
echo "  - 停止服务：docker compose -f docker-compose.prod.yml down"
echo ""
