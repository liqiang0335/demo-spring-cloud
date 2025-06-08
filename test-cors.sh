#!/bin/bash

echo "测试 Spring Cloud Gateway CORS 配置"
echo "=================================="

# 启动 Gateway (后台运行)
echo "启动 Gateway..."
cd gateway
./gradlew bootRun --no-daemon &
GATEWAY_PID=$!

# 等待 Gateway 启动
echo "等待 Gateway 启动..."
sleep 10

# 测试 CORS 预检请求
echo ""
echo "测试 CORS 预检请求 (OPTIONS):"
curl -X OPTIONS \
  -H "Origin: http://localhost:3000" \
  -H "Access-Control-Request-Method: POST" \
  -H "Access-Control-Request-Headers: Content-Type" \
  -v http://localhost:9999/server01/test

echo ""
echo ""
echo "测试普通请求:"
curl -X GET \
  -H "Origin: http://localhost:3000" \
  -v http://localhost:9999/health

# 清理
echo ""
echo "清理进程..."
kill $GATEWAY_PID 2>/dev/null

echo "测试完成!"
