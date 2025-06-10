@echo off
echo Stopping all Java processes...
taskkill /F /IM java.exe >nul 2>&1
timeout /t 2 >nul

cd /d D:\repo\demo\demo-spring-cloud\server01
echo Building project...
call gradlew.bat clean build

echo Starting server01...
java -jar build\libs\server01-0.0.1-SNAPSHOT.jar
