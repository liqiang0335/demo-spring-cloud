#!/bin/bash

# Spring Cloud Gateway 测试脚本
echo "=== Spring Cloud Gateway 测试 ==="
echo ""

GATEWAY_URL="http://localhost:9999"
SERVER01_URL="http://localhost:9801"
SERVER02_URL="http://localhost:9802"

echo "1. 测试网关健康检查..."
curl -s "$GATEWAY_URL/gateway/health" | jq '.' 2>/dev/null || curl -s "$GATEWAY_URL/gateway/health"
echo ""

echo "2. 测试网关信息..."
curl -s "$GATEWAY_URL/gateway/info" | jq '.' 2>/dev/null || curl -s "$GATEWAY_URL/gateway/info"
echo ""

echo "3. 通过网关访问 Server01..."
curl -s "$GATEWAY_URL/server01/api/hello?name=Gateway" | jq '.' 2>/dev/null || curl -s "$GATEWAY_URL/server01/api/hello?name=Gateway"
echo ""

echo "4. 通过网关访问 Server02..."
curl -s "$GATEWAY_URL/server02/api/hello?name=Gateway" | jq '.' 2>/dev/null || curl -s "$GATEWAY_URL/server02/api/hello?name=Gateway"
echo ""

echo "5. 测试 API 路由 (路由到 Server01)..."
curl -s "$GATEWAY_URL/api/info" | jq '.' 2>/dev/null || curl -s "$GATEWAY_URL/api/info"
echo ""

echo "6. 测试 Server02 用户 API..."
curl -s "$GATEWAY_URL/server02/api/users" | jq '.' 2>/dev/null || curl -s "$GATEWAY_URL/server02/api/users"
echo ""

echo "7. 测试负载均衡路由..."
curl -s "$GATEWAY_URL/lb/api/info" | jq '.' 2>/dev/null || curl -s "$GATEWAY_URL/lb/api/info"
echo ""

echo "8. 测试 POST 请求..."
curl -s -X POST "$GATEWAY_URL/server01/api/data" \
     -H "Content-Type: application/json" \
     -d '{"test": "data", "from": "gateway"}' | jq '.' 2>/dev/null || \
curl -s -X POST "$GATEWAY_URL/server01/api/data" \
     -H "Content-Type: application/json" \
     -d '{"test": "data", "from": "gateway"}'
echo ""

echo "=== 测试完成 ==="
echo ""
echo "网关路由规则："
echo "- /server01/** → http://localhost:9801"
echo "- /server02/** → http://localhost:9802"
echo "- /api/** → http://localhost:9801"
echo "- /lb/** → http://localhost:9801 (负载均衡)"
echo "- /gateway/* → 网关管理接口"
