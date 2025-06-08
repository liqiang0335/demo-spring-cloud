#!/bin/bash

# 前端通过网关调用服务的测试脚本
echo "==================================="
echo "前端通过网关调用服务01测试脚本"
echo "==================================="

# 检查网关是否运行
echo "1. 检查网关状态..."
curl -s http://localhost:9999/gateway/info || echo "❌ 网关未启动 (端口9999)"

echo -e "\n"

# 检查服务01是否运行
echo "2. 检查服务01状态..."
curl -s http://localhost:9801/api/hello || echo "❌ 服务01未启动 (端口9801)"

echo -e "\n"

# 测试通过网关调用服务01 - 方式一
echo "3. 通过网关调用服务01 (方式一: /server01/api/hello)..."
curl -s -w "HTTP状态码: %{http_code}\n" http://localhost:9999/server01/api/hello

echo -e "\n"

# 测试通过网关调用服务01 - 方式二
echo "4. 通过网关调用服务01 (方式二: /api/hello)..."
curl -s -w "HTTP状态码: %{http_code}\n" http://localhost:9999/api/hello

echo -e "\n"

# 测试带参数的请求
echo "5. 通过网关调用服务01 (带参数)..."
curl -s -w "HTTP状态码: %{http_code}\n" "http://localhost:9999/server01/api/hello?name=测试用户"

echo -e "\n"

# 测试POST请求
echo "6. 通过网关发送POST请求到服务01..."
curl -s -X POST \
  -H "Content-Type: application/json" \
  -d '{"message":"来自测试脚本的数据","user":"测试用户"}' \
  -w "HTTP状态码: %{http_code}\n" \
  http://localhost:9999/server01/api/data

echo -e "\n"

# 测试服务01的info接口
echo "7. 通过网关调用服务01的info接口..."
curl -s -w "HTTP状态码: %{http_code}\n" http://localhost:9999/server01/api/info

echo -e "\n"

echo "==================================="
echo "测试完成！"
echo "==================================="
echo "如果看到JSON响应和200状态码，说明网关正常工作"
echo "前端可以使用以下URL调用服务01："
echo "- http://localhost:9999/server01/api/hello (推荐)"
echo "- http://localhost:9999/api/hello (兼容方式)"
echo "- http://localhost:9999/server01/api/info"
echo "- http://localhost:9999/server01/api/data (POST)"
