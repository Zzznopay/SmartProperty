-- 自动建库（仅在 MySQL 数据卷为空时执行一次）
CREATE DATABASE IF NOT EXISTS `smart_property_property` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS `smart_property_operation` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
