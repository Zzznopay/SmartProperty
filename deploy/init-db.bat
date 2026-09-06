@echo off
REM 数据库初始化脚本（env 化）
REM 用法: init-db.bat [system^|property^|operation^|all]
REM 环境变量（可覆盖默认值）:
REM   MYSQL_HOST      默认 localhost
REM   MYSQL_PORT      默认 3306
REM   MYSQL_USER      默认 root
REM   MYSQL_PASSWORD  默认 123456

REM 作者: zzz
REM 日期: 2026-07-31

setlocal

if "%MYSQL_HOST%"=="" set MYSQL_HOST=localhost
if "%MYSQL_PORT%"=="" set MYSQL_PORT=3306
if "%MYSQL_USER%"=="" set MYSQL_USER=root
if "%MYSQL_PASSWORD%"=="" set MYSQL_PASSWORD=123456

set TARGET=%1
if "%TARGET%"=="" set TARGET=all

echo =================================
echo   SmartProperty 数据库初始化
echo   MySQL: %MYSQL_USER%@%MYSQL_HOST%:%MYSQL_PORT%
echo   目标: %TARGET%
echo =================================

if "%TARGET%"=="all" goto ALL
if "%TARGET%"=="system" goto SYSTEM
if "%TARGET%"=="property" goto PROPERTY
if "%TARGET%"=="operation" goto OPERATION
echo 未知目标: %TARGET% (支持 system^|property^|operation^|all)
goto END

:ALL
call :RUN "smart_property_system" "smart_property_system.sql"
call :RUN "smart_property_property" "smart_property_property.sql"
call :RUN "smart_property_operation" "smart_property_operation.sql"
goto END

:SYSTEM
call :RUN "smart_property_system" "smart_property_system.sql"
goto END

:PROPERTY
call :RUN "smart_property_property" "smart_property_property.sql"
goto END

:OPERATION
call :RUN "smart_property_operation" "smart_property_operation.sql"
goto END

:RUN
echo   -> 初始化库 %~1 (来自 %~2)
mysql -h %MYSQL_HOST% -P %MYSQL_PORT% -u %MYSQL_USER% -p%MYSQL_PASSWORD% < %~2
echo   OK %~1 初始化完成
goto :EOF

:END
echo.
echo =================================
echo   初始化完成！
echo   默认管理员账号:
echo     用户名: admin
echo     密码:   admin123
echo =================================
endlocal
