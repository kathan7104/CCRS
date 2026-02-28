@echo off
cd /d "%~dp0"
echo Starting CCRS Spring Boot application with local profile...
set SPRING_PROFILES_ACTIVE=local
call mvnw.cmd spring-boot:run
pause
