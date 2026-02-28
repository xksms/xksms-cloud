# ====================================================================
# XKSMS-Cloud 统一基础 Dockerfile
# 使用多阶段构建，基于 Eclipse Temurin JDK 21
# ====================================================================

# ----------------------
# 构建阶段
# ----------------------
FROM maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /app

# 复制父 POM 和依赖 BOM（利用 Docker 缓存层）
COPY pom.xml .
COPY xksms-dependencies/pom.xml xksms-dependencies/
COPY xksms-common/pom.xml xksms-common/
COPY xksms-starters/pom.xml xksms-starters/
COPY xksms-starters/*/pom.xml xksms-starters/
COPY xksms-modules/pom.xml xksms-modules/
COPY xksms-modules/*/pom.xml xksms-modules/
COPY xksms-platform/pom.xml xksms-platform/
COPY xksms-platform/*/pom.xml xksms-platform/
COPY xksms-platform/*/*/pom.xml xksms-platform/
COPY xksms-gateway/pom.xml xksms-gateway/
COPY xksms-gateway/*/pom.xml xksms-gateway/
COPY xksms-auth/pom.xml xksms-auth/

# 下载依赖（利用 Docker 缓存层）
RUN mvn dependency:go-offline -B

# 复制源代码并构建
COPY src /app/src
COPY xksms-common/src /app/xksms-common/src
COPY xksms-starters/*/src /app/xksms-starters/
COPY xksms-modules/*/src /app/xksms-modules/
COPY xksms-platform/*/src /app/xksms-platform/
COPY xksms-gateway/*/src /app/xksms-gateway/
COPY xksms-auth/src /app/xksms-auth/

ARG MODULE_NAME
ARG JAR_FILE=target/*.jar

RUN mvn clean package -DskipTests -B -pl ${MODULE_NAME} -am

# ----------------------
# 运行阶段
# ----------------------
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# 创建非 root 用户运行应用
RUN addgroup -S xksms && adduser -S xksms -G xksms

# 复制构建产物
COPY --from=builder /app/${MODULE_NAME}/target/*.jar app.jar

# 设置时区
ENV TZ=Asia/Shanghai
RUN apk add --no-cache tzdata && cp /usr/share/zoneinfo/${TZ} /etc/localtime && echo ${TZ} > /etc/timezone

# 暴露端口（默认 8080，实际由应用配置决定）
EXPOSE 8080

# 健康检查
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# 使用非 root 用户运行
USER xksms:xksms

# JVM 参数优化
ENV JAVA_OPTS="-Xms512m -Xmx512m -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/app/logs"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
