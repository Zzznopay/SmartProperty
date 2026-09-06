#!/bin/bash

# 数据库初始化脚本（env 化）
# 用法: bash init-db.sh [system|property|operation|all]
# 环境变量（可覆盖默认值）:
#   MYSQL_HOST      默认 localhost
#   MYSQL_PORT      默认 3306
#   MYSQL_USER      默认 root
#   MYSQL_PASSWORD  默认 123456
#   SQL_DIR         SQL 文件目录，默认脚本所在 sql/

# 作者: zzz
# 日期: 2026-07-31

set -e

SQL_DIR="${SQL_DIR:-$(dirname "$0")/sql}"
TARGET="${1:-all}"

MYSQL_HOST="${MYSQL_HOST:-localhost}"
MYSQL_PORT="${MYSQL_PORT:-3306}"
MYSQL_USER="${MYSQL_USER:-root}"
MYSQL_PASSWORD="${MYSQL_PASSWORD:-123456}"

run_sql() {
  local db="$1" file="$2"
  if [ ! -f "$file" ]; then
    echo "  ✗ SQL 文件不存在: $file"
    return 1
  fi
  echo "  → 初始化库 $db (来自 $file)"
  mysql --default-character-set=utf8mb4 -h "$MYSQL_HOST" -P "$MYSQL_PORT" -u "$MYSQL_USER" -p"$MYSQL_PASSWORD" < "$file"
  echo "  ✓ 库 $db 初始化完成"
}

# 是否加载演示种子数据（默认加载；LOAD_SEED=0 跳过）
LOAD_SEED="${LOAD_SEED:-1}"

run_seed() {
  local db="$1"
  local seed_file="$SQL_DIR/seed/${db}-seed.sql"
  if [ "$LOAD_SEED" != "1" ]; then
    echo "  - 跳过种子数据 (LOAD_SEED=0): $db"
    return 0
  fi
  if [ ! -f "$seed_file" ]; then
    echo "  - 无种子数据文件: $seed_file"
    return 0
  fi
  echo "  → 加载演示种子数据 $db (来自 $seed_file)"
  mysql --default-character-set=utf8mb4 -h "$MYSQL_HOST" -P "$MYSQL_PORT" -u "$MYSQL_USER" -p"$MYSQL_PASSWORD" < "$seed_file"
  echo "  ✓ 种子数据加载完成 $db"
}

run_one() {
  local svc="$1"
  case "$svc" in
    system)   run_sql smart_property_system   "$SQL_DIR/smart_property_system.sql";   run_seed smart_property_system ;;
    property) run_sql smart_property_property "$SQL_DIR/smart_property_property.sql"; run_seed smart_property_property ;;
    operation) run_sql smart_property_operation "$SQL_DIR/smart_property_operation.sql"; run_seed smart_property_operation ;;
    *)
      echo "未知服务: $svc (支持 system|property|operation|all)"
      return 1
      ;;
  esac
}

echo "================================="
echo "  SmartProperty 数据库初始化"
echo "  MySQL: ${MYSQL_USER}@${MYSQL_HOST}:${MYSQL_PORT}"
echo "  目标: $TARGET"
echo "  SQL_DIR: $SQL_DIR"
echo "================================="

case "$TARGET" in
  all)
    run_one system
    run_one property
    run_one operation
    ;;
  *) run_one "$TARGET" ;;
esac

echo ""
echo "================================="
echo "  初始化完成！"
echo "  默认管理员账号:"
echo "    用户名: admin"
echo "    密码:   admin123"
echo "================================="
