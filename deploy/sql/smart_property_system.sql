-- ============================================================
-- 智能物业系统 - 系统基础服务数据库
-- 数据库: smart_property_system
-- 作者: zzz
-- 日期: 2026-07-25
-- ============================================================

CREATE DATABASE IF NOT EXISTS `smart_property_system` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

USE `smart_property_system`;

SET NAMES utf8mb4;

-- 用户表
CREATE TABLE sys_user (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    company_id BIGINT UNSIGNED NOT NULL COMMENT '物业公司ID',
    dept_id BIGINT UNSIGNED DEFAULT NULL COMMENT '部门ID',
    username VARCHAR(64) NOT NULL COMMENT '用户名',
    password VARCHAR(128) NOT NULL COMMENT '密码(BCrypt加密)',
    real_name VARCHAR(64) DEFAULT NULL COMMENT '真实姓名',
    phone VARCHAR(128) DEFAULT NULL COMMENT '手机号(加密存储)',
    phone_mask VARCHAR(32) DEFAULT NULL COMMENT '手机号(脱敏)',
    email VARCHAR(128) DEFAULT NULL COMMENT '邮箱',
    avatar VARCHAR(512) DEFAULT NULL COMMENT '头像URL',
    gender TINYINT UNSIGNED DEFAULT 0 COMMENT '性别(0未知 1男 2女)',
    status TINYINT UNSIGNED NOT NULL DEFAULT 1 COMMENT '状态(0禁用 1启用)',
    login_ip VARCHAR(64) DEFAULT NULL COMMENT '最后登录IP',
    login_time DATETIME DEFAULT NULL COMMENT '最后登录时间',
    remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
    create_by VARCHAR(64) DEFAULT NULL COMMENT '创建者',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by VARCHAR(64) DEFAULT NULL COMMENT '更新者',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否删除(0否 1是)',
    PRIMARY KEY (id),
    UNIQUE KEY uk_username (username),
    KEY idx_company_id (company_id),
    KEY idx_dept_id (dept_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 角色表
CREATE TABLE sys_role (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '角色ID',
    company_id BIGINT UNSIGNED NOT NULL COMMENT '物业公司ID',
    role_name VARCHAR(64) NOT NULL COMMENT '角色名称',
    role_key VARCHAR(128) NOT NULL COMMENT '角色标识',
    sort INT NOT NULL DEFAULT 0 COMMENT '排序',
    status TINYINT UNSIGNED NOT NULL DEFAULT 1 COMMENT '状态(0禁用 1启用)',
    remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
    create_by VARCHAR(64) DEFAULT NULL COMMENT '创建者',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by VARCHAR(64) DEFAULT NULL COMMENT '更新者',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否删除(0否 1是)',
    PRIMARY KEY (id),
    KEY idx_company_id (company_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 菜单表
CREATE TABLE sys_menu (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
    parent_id BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '父菜单ID',
    menu_name VARCHAR(64) NOT NULL COMMENT '菜单名称',
    path VARCHAR(256) DEFAULT NULL COMMENT '路由路径',
    component VARCHAR(256) DEFAULT NULL COMMENT '组件路径',
    perms VARCHAR(128) DEFAULT NULL COMMENT '权限标识',
    icon VARCHAR(64) DEFAULT NULL COMMENT '菜单图标',
    menu_type CHAR(1) NOT NULL COMMENT '菜单类型(M目录 C菜单 F按钮)',
    sort INT NOT NULL DEFAULT 0 COMMENT '排序',
    visible TINYINT UNSIGNED NOT NULL DEFAULT 1 COMMENT '是否可见(0隐藏 1显示)',
    status TINYINT UNSIGNED NOT NULL DEFAULT 1 COMMENT '状态(0禁用 1启用)',
    create_by VARCHAR(64) DEFAULT NULL COMMENT '创建者',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by VARCHAR(64) DEFAULT NULL COMMENT '更新者',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜单表';

-- 用户角色关联表
CREATE TABLE sys_user_role (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'ID',
    user_id BIGINT UNSIGNED NOT NULL COMMENT '用户ID',
    role_id BIGINT UNSIGNED NOT NULL COMMENT '角色ID',
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_role (user_id, role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- 角色菜单关联表
CREATE TABLE sys_role_menu (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'ID',
    role_id BIGINT UNSIGNED NOT NULL COMMENT '角色ID',
    menu_id BIGINT UNSIGNED NOT NULL COMMENT '菜单ID',
    PRIMARY KEY (id),
    UNIQUE KEY uk_role_menu (role_id, menu_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色菜单关联表';

-- 部门表
CREATE TABLE sys_dept (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '部门ID',
    company_id BIGINT UNSIGNED NOT NULL COMMENT '物业公司ID',
    parent_id BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '父部门ID',
    dept_name VARCHAR(64) NOT NULL COMMENT '部门名称',
    sort INT NOT NULL DEFAULT 0 COMMENT '排序',
    leader VARCHAR(64) DEFAULT NULL COMMENT '负责人',
    phone VARCHAR(32) DEFAULT NULL COMMENT '联系电话',
    email VARCHAR(128) DEFAULT NULL COMMENT '邮箱',
    status TINYINT UNSIGNED NOT NULL DEFAULT 1 COMMENT '状态(0禁用 1启用)',
    create_by VARCHAR(64) DEFAULT NULL COMMENT '创建者',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by VARCHAR(64) DEFAULT NULL COMMENT '更新者',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否删除(0否 1是)',
    PRIMARY KEY (id),
    KEY idx_company_id (company_id),
    KEY idx_parent_id (parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='部门表';

-- 字典类型表
CREATE TABLE sys_dict_type (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '字典类型ID',
    dict_name VARCHAR(128) NOT NULL COMMENT '字典名称',
    dict_type VARCHAR(128) NOT NULL COMMENT '字典类型',
    status TINYINT UNSIGNED NOT NULL DEFAULT 1 COMMENT '状态(0禁用 1启用)',
    remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
    create_by VARCHAR(64) DEFAULT NULL COMMENT '创建者',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by VARCHAR(64) DEFAULT NULL COMMENT '更新者',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否删除(0否 1是)',
    PRIMARY KEY (id),
    UNIQUE KEY uk_dict_type (dict_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='字典类型表';

-- 字典数据表
CREATE TABLE sys_dict_data (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '字典数据ID',
    dict_type VARCHAR(128) NOT NULL COMMENT '字典类型',
    dict_label VARCHAR(128) NOT NULL COMMENT '字典标签',
    dict_value VARCHAR(128) NOT NULL COMMENT '字典值',
    sort INT NOT NULL DEFAULT 0 COMMENT '排序',
    status TINYINT UNSIGNED NOT NULL DEFAULT 1 COMMENT '状态(0禁用 1启用)',
    remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
    create_by VARCHAR(64) DEFAULT NULL COMMENT '创建者',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by VARCHAR(64) DEFAULT NULL COMMENT '更新者',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_dict_type (dict_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='字典数据表';

-- 物业公司表
CREATE TABLE sys_company (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '公司ID',
    parent_id BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '父公司ID',
    company_name VARCHAR(128) NOT NULL COMMENT '公司名称',
    company_code VARCHAR(64) NOT NULL COMMENT '公司编码',
    contact_name VARCHAR(64) DEFAULT NULL COMMENT '联系人',
    contact_phone VARCHAR(128) DEFAULT NULL COMMENT '联系电话(加密存储)',
    address VARCHAR(256) DEFAULT NULL COMMENT '公司地址',
    logo VARCHAR(512) DEFAULT NULL COMMENT '公司Logo',
    status TINYINT UNSIGNED NOT NULL DEFAULT 1 COMMENT '状态(0禁用 1启用)',
    remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
    create_by VARCHAR(64) DEFAULT NULL COMMENT '创建者',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by VARCHAR(64) DEFAULT NULL COMMENT '更新者',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否删除(0否 1是)',
    PRIMARY KEY (id),
    UNIQUE KEY uk_company_code (company_code),
    KEY idx_parent_id (parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='物业公司表';

-- 登录日志表
CREATE TABLE sys_login_log (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '日志ID',
    username VARCHAR(64) DEFAULT NULL COMMENT '用户名',
    ip VARCHAR(64) DEFAULT NULL COMMENT '登录IP',
    location VARCHAR(256) DEFAULT NULL COMMENT '登录地点',
    browser VARCHAR(64) DEFAULT NULL COMMENT '浏览器',
    os VARCHAR(64) DEFAULT NULL COMMENT '操作系统',
    status TINYINT UNSIGNED NOT NULL DEFAULT 1 COMMENT '状态(0失败 1成功)',
    msg VARCHAR(256) DEFAULT NULL COMMENT '提示消息',
    login_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '登录时间',
    PRIMARY KEY (id),
    KEY idx_login_time (login_time),
    KEY idx_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='登录日志表';

-- 操作日志表
CREATE TABLE sys_oper_log (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '日志ID',
    module VARCHAR(64) DEFAULT NULL COMMENT '模块名称',
    business_type TINYINT UNSIGNED DEFAULT 0 COMMENT '业务类型(0其它 1新增 2修改 3删除 4查询 5导出)',
    method VARCHAR(256) DEFAULT NULL COMMENT '方法名称',
    request_method VARCHAR(16) DEFAULT NULL COMMENT '请求方式',
    oper_name VARCHAR(64) DEFAULT NULL COMMENT '操作人',
    oper_url VARCHAR(512) DEFAULT NULL COMMENT '请求URL',
    oper_ip VARCHAR(64) DEFAULT NULL COMMENT '操作IP',
    oper_param TEXT DEFAULT NULL COMMENT '请求参数',
    json_result TEXT DEFAULT NULL COMMENT '返回结果',
    status TINYINT UNSIGNED NOT NULL DEFAULT 1 COMMENT '状态(0失败 1成功)',
    error_msg TEXT DEFAULT NULL COMMENT '错误消息',
    cost_time BIGINT UNSIGNED DEFAULT 0 COMMENT '耗时(毫秒)',
    oper_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    PRIMARY KEY (id),
    KEY idx_oper_time (oper_time),
    KEY idx_oper_name (oper_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';

-- Token黑名单表
CREATE TABLE auth_token_blacklist (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'ID',
    token VARCHAR(512) NOT NULL COMMENT 'Token值',
    expire_time DATETIME NOT NULL COMMENT '过期时间',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    KEY idx_expire_time (expire_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Token黑名单表';

-- 文件表
CREATE TABLE file_info (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '文件ID',
    company_id BIGINT UNSIGNED NOT NULL COMMENT '物业公司ID',
    file_name VARCHAR(256) NOT NULL COMMENT '文件名称',
    original_name VARCHAR(256) NOT NULL COMMENT '原始文件名',
    file_path VARCHAR(512) NOT NULL COMMENT '文件路径',
    file_url VARCHAR(512) NOT NULL COMMENT '文件URL',
    file_size BIGINT UNSIGNED NOT NULL COMMENT '文件大小(字节)',
    file_type VARCHAR(64) NOT NULL COMMENT '文件类型',
    file_ext VARCHAR(32) NOT NULL COMMENT '文件扩展名',
    md5 VARCHAR(64) DEFAULT NULL COMMENT '文件MD5',
    storage_type TINYINT UNSIGNED NOT NULL DEFAULT 1 COMMENT '存储类型(1MinIO 2OSS)',
    bucket_name VARCHAR(64) DEFAULT NULL COMMENT '存储桶名称',
    business_type VARCHAR(64) DEFAULT NULL COMMENT '业务类型',
    business_id BIGINT UNSIGNED DEFAULT NULL COMMENT '业务ID',
    upload_user_id BIGINT UNSIGNED DEFAULT NULL COMMENT '上传用户ID',
    upload_user_name VARCHAR(64) DEFAULT NULL COMMENT '上传用户姓名',
    download_count INT UNSIGNED NOT NULL DEFAULT 0 COMMENT '下载次数',
    status TINYINT UNSIGNED NOT NULL DEFAULT 1 COMMENT '状态(1正常 2已删除)',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否删除(0否 1是)',
    PRIMARY KEY (id),
    KEY idx_company_id (company_id),
    KEY idx_business (business_type, business_id),
    KEY idx_md5 (md5)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文件表';

-- 短信模板表
CREATE TABLE sys_sms_template (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '模板ID',
    company_id BIGINT UNSIGNED NOT NULL COMMENT '物业公司ID',
    template_name VARCHAR(64) NOT NULL COMMENT '模板名称',
    template_content VARCHAR(500) NOT NULL COMMENT '模板内容',
    template_type TINYINT UNSIGNED NOT NULL DEFAULT 1 COMMENT '模板类型(1通知 2提醒 3营销)',
    is_active TINYINT UNSIGNED NOT NULL DEFAULT 1 COMMENT '是否启用(0否 1是)',
    remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
    create_by VARCHAR(64) DEFAULT NULL COMMENT '创建者',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by VARCHAR(64) DEFAULT NULL COMMENT '更新者',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否删除(0否 1是)',
    PRIMARY KEY (id),
    KEY idx_company_id (company_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='短信模板表';

-- 短信发送记录表
CREATE TABLE sys_sms_log (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '记录ID',
    company_id BIGINT UNSIGNED NOT NULL COMMENT '物业公司ID',
    phone VARCHAR(32) NOT NULL COMMENT '手机号',
    content VARCHAR(500) NOT NULL COMMENT '短信内容',
    template_id BIGINT UNSIGNED DEFAULT NULL COMMENT '模板ID',
    send_user_id BIGINT UNSIGNED DEFAULT NULL COMMENT '发送人ID',
    send_user_name VARCHAR(64) DEFAULT NULL COMMENT '发送人姓名',
    send_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发送时间',
    send_status TINYINT UNSIGNED NOT NULL COMMENT '发送状态(1成功 2失败)',
    fail_reason VARCHAR(500) DEFAULT NULL COMMENT '失败原因',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    KEY idx_company_id (company_id),
    KEY idx_phone (phone),
    KEY idx_send_time (send_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='短信发送记录表';

-- 统计快照表
CREATE TABLE report_snapshot (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '快照ID',
    company_id BIGINT UNSIGNED NOT NULL COMMENT '物业公司ID',
    community_id BIGINT UNSIGNED DEFAULT NULL COMMENT '小区ID',
    snapshot_type VARCHAR(64) NOT NULL COMMENT '快照类型',
    snapshot_month VARCHAR(7) NOT NULL COMMENT '快照月份(yyyy-MM)',
    snapshot_data JSON NOT NULL COMMENT '快照数据(JSON)',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_type_month (company_id, community_id, snapshot_type, snapshot_month),
    KEY idx_company_id (company_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='统计快照表';

-- 导出任务表
CREATE TABLE report_export_task (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '任务ID',
    company_id BIGINT UNSIGNED NOT NULL COMMENT '物业公司ID',
    task_name VARCHAR(128) NOT NULL COMMENT '任务名称',
    export_type VARCHAR(64) NOT NULL COMMENT '导出类型',
    export_params JSON DEFAULT NULL COMMENT '导出参数(JSON)',
    file_url VARCHAR(512) DEFAULT NULL COMMENT '文件URL',
    file_name VARCHAR(128) DEFAULT NULL COMMENT '文件名称',
    file_size BIGINT UNSIGNED DEFAULT NULL COMMENT '文件大小(字节)',
    status TINYINT UNSIGNED NOT NULL DEFAULT 1 COMMENT '状态(1处理中 2已完成 3失败)',
    progress INT UNSIGNED NOT NULL DEFAULT 0 COMMENT '进度(0-100)',
    fail_reason VARCHAR(500) DEFAULT NULL COMMENT '失败原因',
    request_user_id BIGINT UNSIGNED NOT NULL COMMENT '请求用户ID',
    request_user_name VARCHAR(64) DEFAULT NULL COMMENT '请求用户姓名',
    request_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '请求时间',
    complete_time DATETIME DEFAULT NULL COMMENT '完成时间',
    expire_time DATETIME DEFAULT NULL COMMENT '过期时间',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_company_id (company_id),
    KEY idx_request_user_id (request_user_id),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='导出任务表';

-- 初始数据: 管理员账号(密码: admin123)
INSERT INTO sys_company (id, company_name, company_code, status, create_by) VALUES
(1, '和家云物业', 'HJY001', 1, 'system');

INSERT INTO sys_dept (id, company_id, parent_id, dept_name, sort, status, create_by) VALUES
(1, 1, 0, '总公司', 0, 1, 'system'),
(2, 1, 1, '技术部', 1, 1, 'system'),
(3, 1, 1, '运营部', 2, 1, 'system');

INSERT INTO sys_user (id, company_id, dept_id, username, password, real_name, status, create_by) VALUES
(1, 1, 1, 'admin', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '系统管理员', 1, 'system');

INSERT INTO sys_role (id, company_id, role_name, role_key, sort, status, create_by) VALUES
(1, 1, '超级管理员', 'admin', 0, 1, 'system');

INSERT INTO sys_user_role (user_id, role_id) VALUES (1, 1);

-- ============================================================
-- MinIO 文件存储相关扩展
-- ============================================================

-- 秒传查询复合索引
CREATE INDEX idx_md5_company_business ON file_info (md5, company_id, business_type);

-- 网盘文件夹表
CREATE TABLE file_folder (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '文件夹ID',
    company_id BIGINT UNSIGNED NOT NULL COMMENT '物业公司ID',
    user_id BIGINT UNSIGNED NOT NULL COMMENT '用户ID',
    parent_id BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '父文件夹ID',
    folder_name VARCHAR(128) NOT NULL COMMENT '文件夹名称',
    folder_path VARCHAR(512) NOT NULL COMMENT '文件夹路径',
    is_shared TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否共享(0否 1是)',
    share_user_ids VARCHAR(2000) DEFAULT NULL COMMENT '共享用户ID列表(JSON)',
    status TINYINT UNSIGNED NOT NULL DEFAULT 1 COMMENT '状态(1正常 2已删除)',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否删除(0否 1是)',
    PRIMARY KEY (id),
    KEY idx_company_id (company_id),
    KEY idx_user_id (user_id),
    KEY idx_parent_id (parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='网盘文件夹表';

-- 网盘文件表
CREATE TABLE file_disk (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '网盘文件ID',
    company_id BIGINT UNSIGNED NOT NULL COMMENT '物业公司ID',
    user_id BIGINT UNSIGNED NOT NULL COMMENT '用户ID',
    folder_id BIGINT UNSIGNED DEFAULT NULL COMMENT '文件夹ID',
    file_id BIGINT UNSIGNED NOT NULL COMMENT '文件ID',
    file_name VARCHAR(256) NOT NULL COMMENT '文件名称',
    is_shared TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否共享(0否 1是)',
    share_user_ids VARCHAR(2000) DEFAULT NULL COMMENT '共享用户ID列表(JSON)',
    status TINYINT UNSIGNED NOT NULL DEFAULT 1 COMMENT '状态(1正常 2已删除)',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否删除(0否 1是)',
    PRIMARY KEY (id),
    KEY idx_company_id (company_id),
    KEY idx_user_id (user_id),
    KEY idx_folder_id (folder_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='网盘文件表';
