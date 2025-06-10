#!/bin/bash

echo "停止现有的 Java 进程..."
pkill -f "server01-0.0.1-SNAPSHOT.jar" || true
sleep 2

echo "检查 Nacos 服务器状态..."
cd /d/repo/demo/demo-spring-cloud/docker

# 重启 Nacos 服务器以确保正常运行
echo "重启 Nacos 服务器..."
docker-compose down
sleep 3
docker-compose up -d

echo "等待 Nacos 启动..."
sleep 10

# 检查 Nacos 是否正常运行
echo "检查 Nacos 健康状态..."
curl -s http://127.0.0.1:18848/nacos/actuator/health || echo "Nacos 健康检查失败"

echo "尝试登录 Nacos..."
curl -X POST 'http://127.0.0.1:18848/nacos/v1/auth/login' -d 'username=nacos&password=nacos' || echo "Nacos 登录失败"

echo "构建并启动 server01..."
cd /d/repo/demo/demo-spring-cloud/server01

./gradlew clean build

echo "启动 server01 服务..."
java -jar build/libs/server01-0.0.1-SNAPSHOT.jar
