@echo off
cd /d "%~dp0"
echo Starting CCRS Spring Boot application...
call mvnw.cmd spring-boot:run
pause
