@echo off
echo 正在停止所有 Java 进程...
taskkill /F /IM java.exe >nul 2>&1
timeout /t 3 >nul

echo 切换到项目目录...
cd /d "D:\repo\demo\demo-spring-cloud\server01"

echo 清理并重新构建项目...
call gradlew.bat clean build
if errorlevel 1 (
    echo 构建失败！
    pause
    exit /b 1
)

echo 启动 server01 服务...
echo 注意：如果 Nacos 连接失败，服务仍会启动但不会注册到 Nacos
java -jar build\libs\server01-0.0.1-SNAPSHOT.jar

pause
