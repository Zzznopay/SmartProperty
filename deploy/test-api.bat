@echo off
REM API测试脚本
REM 作者: zzz
REM 日期: 2026-07-25

set BASE_URL=http://localhost:8000

echo === 智能物业系统 API测试 ===
echo.

REM 1. 登录
echo 1. 测试登录接口...
curl -s -X POST "%BASE_URL%/api/v1/auth/login" -H "Content-Type: application/json" -d "{\"username\":\"admin\",\"password\":\"admin123\"}"
echo.
echo.

echo 2. 测试获取用户信息...
curl -s -X GET "%BASE_URL%/api/v1/auth/user-info" -H "Authorization: Bearer test_token"
echo.
echo.

echo 3. 测试获取部门树...
curl -s -X GET "%BASE_URL%/api/v1/system/depts/tree" -H "Authorization: Bearer test_token"
echo.
echo.

echo === API测试完成 ===
echo.
echo 请先启动所有服务，然后运行此脚本
pause
