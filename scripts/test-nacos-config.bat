@echo off
echo === 测试 Nacos 动态配置管理 ===
echo.

REM 检查服务状态
echo 1. 检查服务状态...
echo Gateway 健康检查:
curl -s http://localhost:9999/gateway/health
echo.
echo Server01 健康检查:
curl -s http://localhost:9801/actuator/health
echo.
echo Server02 健康检查:
curl -s http://localhost:9802/actuator/health
echo.

REM 测试配置读取
echo 2. 测试当前配置...
echo Server01 配置:
curl -s http://localhost:9801/api/config
echo.
echo Server02 配置:
curl -s http://localhost:9802/api/config
echo.

REM 测试业务接口
echo 3. 测试业务接口...
echo Server01 Hello:
curl -s "http://localhost:9801/api/hello?name=Nacos"
echo.
echo Server02 Hello:
curl -s "http://localhost:9802/api/hello?name=Config"
echo.

REM 通过网关测试
echo 4. 通过网关测试...
echo 通过网关访问 Server01:
curl -s "http://localhost:9999/server01/hello?name=Gateway"
echo.
echo 通过网关访问 Server02:
curl -s "http://localhost:9999/server02/hello?name=Gateway"
echo.

echo === 测试完成 ===
echo 请在 Nacos 控制台 (http://localhost:18848/nacos) 中修改配置，然后重新运行此脚本查看配置变化
pause
