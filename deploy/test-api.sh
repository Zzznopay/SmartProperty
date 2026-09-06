#!/bin/bash

# API测试脚本
# 作者: zzz
# 日期: 2026-07-25

BASE_URL="http://localhost:8000"

echo "=== 智能物业系统 API测试 ==="
echo ""

# 1. 登录
echo "1. 测试登录接口..."
LOGIN_RESULT=$(curl -s -X POST "$BASE_URL/api/v1/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}')

echo "$LOGIN_RESULT" | head -c 200
echo ""

# 提取Token
TOKEN=$(echo "$LOGIN_RESULT" | grep -o '"accessToken":"[^"]*"' | cut -d'"' -f4)

if [ -z "$TOKEN" ]; then
    echo "登录失败，请检查服务是否启动"
    exit 1
fi

echo "登录成功，Token: ${TOKEN:0:50}..."
echo ""

# 2. 获取用户信息
echo "2. 测试获取用户信息..."
curl -s -X GET "$BASE_URL/api/v1/auth/user-info" \
  -H "Authorization: Bearer $TOKEN" | head -c 200
echo ""
echo ""

# 3. 获取菜单树
echo "3. 测试获取菜单树..."
curl -s -X GET "$BASE_URL/api/v1/auth/menus" \
  -H "Authorization: Bearer $TOKEN" | head -c 200
echo ""
echo ""

# 4. 获取用户列表
echo "4. 测试获取用户列表..."
curl -s -X GET "$BASE_URL/api/v1/system/users?pageNum=1&pageSize=10" \
  -H "Authorization: Bearer $TOKEN" | head -c 200
echo ""
echo ""

# 5. 获取部门树
echo "5. 测试获取部门树..."
curl -s -X GET "$BASE_URL/api/v1/system/depts/tree" \
  -H "Authorization: Bearer $TOKEN" | head -c 200
echo ""
echo ""

# 6. 创建小区
echo "6. 测试创建小区..."
curl -s -X POST "$BASE_URL/api/v1/property/communitys" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"communityName":"测试小区","communityCode":"TEST001","address":"测试地址","status":1}' | head -c 200
echo ""
echo ""

# 7. 获取小区列表
echo "7. 测试获取小区列表..."
curl -s -X GET "$BASE_URL/api/v1/property/communitys?pageNum=1&pageSize=10" \
  -H "Authorization: Bearer $TOKEN" | head -c 200
echo ""
echo ""

# 8. 创建工单
echo "8. 测试创建工单..."
curl -s -X POST "$BASE_URL/api/v1/operation/service-orders" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"communityId":1,"orderType":1,"title":"测试报修","content":"水管漏水","priority":2}' | head -c 200
echo ""
echo ""

# 9. 获取工单列表
echo "9. 测试获取工单列表..."
curl -s -X GET "$BASE_URL/api/v1/operation/service-orders?pageNum=1&pageSize=10" \
  -H "Authorization: Bearer $TOKEN" | head -c 200
echo ""
echo ""

# 10. 创建公告
echo "10. 测试创建公告..."
curl -s -X POST "$BASE_URL/api/v1/admin/notices" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"noticeTitle":"测试公告","noticeContent":"公告内容","noticeType":1}' | head -c 200
echo ""
echo ""

echo "=== API测试完成 ==="
