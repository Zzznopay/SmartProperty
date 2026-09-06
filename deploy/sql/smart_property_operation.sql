-- ============================================================
-- 智能物业系统 - 运营管理服务数据库
-- 数据库: smart_property_operation
-- 作者: zzz
-- 日期: 2026-07-25
-- ============================================================

CREATE DATABASE IF NOT EXISTS `smart_property_operation` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

USE `smart_property_operation`;

SET NAMES utf8mb4;

-- 服务工单表
CREATE TABLE operation_service_order (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '工单ID',
    company_id BIGINT UNSIGNED NOT NULL COMMENT '物业公司ID',
    community_id BIGINT UNSIGNED NOT NULL COMMENT '小区ID',
    order_no VARCHAR(64) NOT NULL COMMENT '工单编号',
    order_type TINYINT UNSIGNED NOT NULL COMMENT '工单类型(1报修 2投诉 3建议 4咨询)',
    title VARCHAR(128) NOT NULL COMMENT '工单标题',
    content TEXT NOT NULL COMMENT '工单内容',
    room_id BIGINT UNSIGNED DEFAULT NULL COMMENT '房间ID',
    owner_id BIGINT UNSIGNED DEFAULT NULL COMMENT '业主ID',
    owner_name VARCHAR(64) DEFAULT NULL COMMENT '业主姓名',
    owner_phone VARCHAR(32) DEFAULT NULL COMMENT '业主电话',
    images VARCHAR(2000) DEFAULT NULL COMMENT '图片URL(JSON数组)',
    priority TINYINT UNSIGNED NOT NULL DEFAULT 2 COMMENT '优先级(1紧急 2普通 3低)',
    status TINYINT UNSIGNED NOT NULL DEFAULT 1 COMMENT '状态(1待分配 2处理中 3待回访 4已完成 5已关闭)',
    assign_user_id BIGINT UNSIGNED DEFAULT NULL COMMENT '处理人ID',
    assign_user_name VARCHAR(64) DEFAULT NULL COMMENT '处理人姓名',
    assign_time DATETIME DEFAULT NULL COMMENT '分配时间',
    handle_content TEXT DEFAULT NULL COMMENT '处理内容',
    handle_time DATETIME DEFAULT NULL COMMENT '处理时间',
    visit_content TEXT DEFAULT NULL COMMENT '回访内容',
    visit_score INT UNSIGNED DEFAULT NULL COMMENT '满意度评分(1-5)',
    visit_time DATETIME DEFAULT NULL COMMENT '回访时间',
    close_time DATETIME DEFAULT NULL COMMENT '关闭时间',
    remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
    create_by VARCHAR(64) DEFAULT NULL COMMENT '创建者',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by VARCHAR(64) DEFAULT NULL COMMENT '更新者',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否删除(0否 1是)',
    PRIMARY KEY (id),
    UNIQUE KEY uk_order_no (order_no),
    KEY idx_company_id (company_id),
    KEY idx_community_id (community_id),
    KEY idx_owner_id (owner_id),
    KEY idx_status (status),
    KEY idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='服务工单表';

-- 工单流转记录表
CREATE TABLE operation_order_flow (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '流转ID',
    company_id BIGINT UNSIGNED NOT NULL COMMENT '物业公司ID',
    order_id BIGINT UNSIGNED NOT NULL COMMENT '工单ID',
    flow_type TINYINT UNSIGNED NOT NULL COMMENT '流转类型(1创建 2分配 3处理 4回访 5关闭)',
    content TEXT DEFAULT NULL COMMENT '流转内容',
    operator_id BIGINT UNSIGNED DEFAULT NULL COMMENT '操作人ID',
    operator_name VARCHAR(64) DEFAULT NULL COMMENT '操作人姓名',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    KEY idx_company_id (company_id),
    KEY idx_order_id (order_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工单流转记录表';

-- 清洁安排表
CREATE TABLE operation_clean_arrange (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '安排ID',
    company_id BIGINT UNSIGNED NOT NULL COMMENT '物业公司ID',
    community_id BIGINT UNSIGNED NOT NULL COMMENT '小区ID',
    area_name VARCHAR(128) NOT NULL COMMENT '清洁区域',
    clean_type TINYINT UNSIGNED NOT NULL COMMENT '清洁类型(1日常 2定期 3专项)',
    arrange_date DATE NOT NULL COMMENT '安排日期',
    start_time TIME DEFAULT NULL COMMENT '开始时间',
    end_time TIME DEFAULT NULL COMMENT '结束时间',
    cleaner_id BIGINT UNSIGNED DEFAULT NULL COMMENT '清洁人员ID',
    cleaner_name VARCHAR(64) DEFAULT NULL COMMENT '清洁人员姓名',
    status TINYINT UNSIGNED NOT NULL DEFAULT 1 COMMENT '状态(1待执行 2执行中 3已完成)',
    complete_time DATETIME DEFAULT NULL COMMENT '完成时间',
    remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
    create_by VARCHAR(64) DEFAULT NULL COMMENT '创建者',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by VARCHAR(64) DEFAULT NULL COMMENT '更新者',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否删除(0否 1是)',
    PRIMARY KEY (id),
    KEY idx_company_id (company_id),
    KEY idx_community_id (community_id),
    KEY idx_arrange_date (arrange_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='清洁安排表';

-- 消防设施表
CREATE TABLE operation_fire_facility (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '设施ID',
    company_id BIGINT UNSIGNED NOT NULL COMMENT '物业公司ID',
    community_id BIGINT UNSIGNED NOT NULL COMMENT '小区ID',
    building_id BIGINT UNSIGNED DEFAULT NULL COMMENT '楼宇ID',
    facility_name VARCHAR(128) NOT NULL COMMENT '设施名称',
    facility_type TINYINT UNSIGNED NOT NULL COMMENT '设施类型(1灭火器 2消防栓 3喷淋 4烟感 5应急灯)',
    facility_no VARCHAR(64) DEFAULT NULL COMMENT '设施编号',
    location VARCHAR(256) DEFAULT NULL COMMENT '安装位置',
    install_date DATE DEFAULT NULL COMMENT '安装日期',
    expire_date DATE DEFAULT NULL COMMENT '有效期至',
    status TINYINT UNSIGNED NOT NULL DEFAULT 1 COMMENT '状态(1正常 2故障 3维修中 4已过期)',
    last_check_date DATE DEFAULT NULL COMMENT '上次检查日期',
    next_check_date DATE DEFAULT NULL COMMENT '下次检查日期',
    remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
    create_by VARCHAR(64) DEFAULT NULL COMMENT '创建者',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by VARCHAR(64) DEFAULT NULL COMMENT '更新者',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否删除(0否 1是)',
    PRIMARY KEY (id),
    KEY idx_company_id (company_id),
    KEY idx_community_id (community_id),
    KEY idx_expire_date (expire_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消防设施表';

-- 保安安排表
CREATE TABLE operation_security_arrange (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '安排ID',
    company_id BIGINT UNSIGNED NOT NULL COMMENT '物业公司ID',
    community_id BIGINT UNSIGNED NOT NULL COMMENT '小区ID',
    arrange_date DATE NOT NULL COMMENT '安排日期',
    shift_type TINYINT UNSIGNED NOT NULL COMMENT '班次(1早班 2中班 3晚班)',
    start_time TIME NOT NULL COMMENT '开始时间',
    end_time TIME NOT NULL COMMENT '结束时间',
    position VARCHAR(64) NOT NULL COMMENT '执勤岗位',
    security_id BIGINT UNSIGNED NOT NULL COMMENT '保安人员ID',
    security_name VARCHAR(64) NOT NULL COMMENT '保安人员姓名',
    status TINYINT UNSIGNED NOT NULL DEFAULT 1 COMMENT '状态(1待执行 2执行中 3已完成)',
    remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
    create_by VARCHAR(64) DEFAULT NULL COMMENT '创建者',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by VARCHAR(64) DEFAULT NULL COMMENT '更新者',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新_time',
    is_deleted TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否删除(0否 1是)',
    PRIMARY KEY (id),
    KEY idx_company_id (company_id),
    KEY idx_community_id (community_id),
    KEY idx_arrange_date (arrange_date),
    KEY idx_security_id (security_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='保安安排表';

-- 来访登记表
CREATE TABLE operation_visit_record (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '记录ID',
    company_id BIGINT UNSIGNED NOT NULL COMMENT '物业公司ID',
    community_id BIGINT UNSIGNED NOT NULL COMMENT '小区ID',
    visitor_name VARCHAR(64) NOT NULL COMMENT '来访人姓名',
    visitor_phone VARCHAR(32) DEFAULT NULL COMMENT '来访人电话',
    visit_reason VARCHAR(256) NOT NULL COMMENT '来访事由',
    visit_target VARCHAR(128) DEFAULT NULL COMMENT '拜访对象',
    room_id BIGINT UNSIGNED DEFAULT NULL COMMENT '拜访房间ID',
    visit_time DATETIME NOT NULL COMMENT '来访时间',
    leave_time DATETIME DEFAULT NULL COMMENT '离开时间',
    visitor_count INT UNSIGNED NOT NULL DEFAULT 1 COMMENT '来访人数',
    plate_no VARCHAR(32) DEFAULT NULL COMMENT '车牌号',
    guard_id BIGINT UNSIGNED DEFAULT NULL COMMENT '门卫ID',
    guard_name VARCHAR(64) DEFAULT NULL COMMENT '门卫姓名',
    status TINYINT UNSIGNED NOT NULL DEFAULT 1 COMMENT '状态(1在访 2已离开)',
    remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
    create_by VARCHAR(64) DEFAULT NULL COMMENT '创建者',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by VARCHAR(64) DEFAULT NULL COMMENT '更新者',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新_time',
    is_deleted TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否删除(0否 1是)',
    PRIMARY KEY (id),
    KEY idx_company_id (company_id),
    KEY idx_community_id (community_id),
    KEY idx_visit_time (visit_time),
    KEY idx_visitor_name (visitor_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='来访登记表';

-- 车辆进出记录表
CREATE TABLE operation_vehicle_record (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '记录ID',
    company_id BIGINT UNSIGNED NOT NULL COMMENT '物业公司ID',
    community_id BIGINT UNSIGNED NOT NULL COMMENT '小区ID',
    plate_no VARCHAR(32) NOT NULL COMMENT '车牌号',
    vehicle_type TINYINT UNSIGNED NOT NULL DEFAULT 1 COMMENT '车辆类型(1小型车 2大型车 3摩托车)',
    record_type TINYINT UNSIGNED NOT NULL COMMENT '记录类型(1入场 2出场)',
    record_time DATETIME NOT NULL COMMENT '记录时间',
    gate_name VARCHAR(64) DEFAULT NULL COMMENT '闸口名称',
    image_url VARCHAR(512) DEFAULT NULL COMMENT '抓拍图片URL',
    parking_id BIGINT UNSIGNED DEFAULT NULL COMMENT '车位ID',
    is_temporary TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否临时车(0否 1是)',
    fee_amount DECIMAL(10,2) DEFAULT NULL COMMENT '停车费(元)',
    pay_status TINYINT UNSIGNED DEFAULT 0 COMMENT '缴费状态(0未缴 1已缴)',
    pay_time DATETIME DEFAULT NULL COMMENT '缴费时间',
    remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否删除(0否 1是)',
    PRIMARY KEY (id),
    KEY idx_company_id (company_id),
    KEY idx_community_id (community_id),
    KEY idx_plate_no (plate_no),
    KEY idx_record_time (record_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='车辆进出记录表';

-- 公告表
CREATE TABLE admin_notice (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '公告ID',
    company_id BIGINT UNSIGNED NOT NULL COMMENT '物业公司ID',
    community_id BIGINT UNSIGNED DEFAULT NULL COMMENT '小区ID(空表示全部)',
    notice_title VARCHAR(128) NOT NULL COMMENT '公告标题',
    notice_content TEXT NOT NULL COMMENT '公告内容',
    notice_type TINYINT UNSIGNED NOT NULL COMMENT '公告类型(1通知 2公告 3温馨提示 4紧急通知)',
    is_top TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否置顶(0否 1是)',
    is_publish TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否发布(0否 1是)',
    publish_time DATETIME DEFAULT NULL COMMENT '发布时间',
    expire_time DATETIME DEFAULT NULL COMMENT '过期时间',
    read_count INT UNSIGNED NOT NULL DEFAULT 0 COMMENT '已读人数',
    images VARCHAR(2000) DEFAULT NULL COMMENT '图片URL(JSON数组)',
    attachments VARCHAR(2000) DEFAULT NULL COMMENT '附件URL(JSON数组)',
    status TINYINT UNSIGNED NOT NULL DEFAULT 1 COMMENT '状态(1草稿 2已发布 3已撤回)',
    remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
    create_by VARCHAR(64) DEFAULT NULL COMMENT '创建者',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by VARCHAR(64) DEFAULT NULL COMMENT '更新者',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'update_time',
    is_deleted TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否删除(0否 1是)',
    PRIMARY KEY (id),
    KEY idx_company_id (company_id),
    KEY idx_community_id (community_id),
    KEY idx_notice_type (notice_type),
    KEY idx_publish_time (publish_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='公告表';

-- 公告已读表
CREATE TABLE admin_notice_read (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'ID',
    company_id BIGINT UNSIGNED NOT NULL COMMENT '物业公司ID',
    notice_id BIGINT UNSIGNED NOT NULL COMMENT '公告ID',
    user_id BIGINT UNSIGNED NOT NULL COMMENT '用户ID',
    read_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '阅读时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_notice_user (notice_id, user_id),
    KEY idx_company_id (company_id),
    KEY idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='公告已读表';

-- 消息表
CREATE TABLE admin_message (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '消息ID',
    company_id BIGINT UNSIGNED NOT NULL COMMENT '物业公司ID',
    message_type TINYINT UNSIGNED NOT NULL COMMENT '消息类型(1站内消息 2短信 3邮件 4微信推送)',
    title VARCHAR(128) NOT NULL COMMENT '消息标题',
    content TEXT NOT NULL COMMENT '消息内容',
    sender_id BIGINT UNSIGNED DEFAULT NULL COMMENT '发送人ID',
    sender_name VARCHAR(64) DEFAULT NULL COMMENT '发送人姓名',
    receiver_id BIGINT UNSIGNED NOT NULL COMMENT '接收人ID',
    receiver_name VARCHAR(64) DEFAULT NULL COMMENT '接收人姓名',
    receiver_phone VARCHAR(32) DEFAULT NULL COMMENT '接收人手机',
    receiver_email VARCHAR(128) DEFAULT NULL COMMENT '接收人邮箱',
    is_read TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否已读(0否 1是)',
    read_time DATETIME DEFAULT NULL COMMENT '阅读时间',
    send_status TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '发送状态(0待发送 1已发送 2发送失败)',
    send_time DATETIME DEFAULT NULL COMMENT '发送时间',
    fail_reason VARCHAR(500) DEFAULT NULL COMMENT '失败原因',
    business_type VARCHAR(64) DEFAULT NULL COMMENT '业务类型',
    business_id BIGINT UNSIGNED DEFAULT NULL COMMENT '业务ID',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'update_time',
    is_deleted TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否删除(0否 1是)',
    PRIMARY KEY (id),
    KEY idx_company_id (company_id),
    KEY idx_receiver_id (receiver_id),
    KEY idx_message_type (message_type),
    KEY idx_is_read (is_read),
    KEY idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消息表';

-- 意见箱表
CREATE TABLE admin_opinion_box (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '意见ID',
    company_id BIGINT UNSIGNED NOT NULL COMMENT '物业公司ID',
    community_id BIGINT UNSIGNED DEFAULT NULL COMMENT '小区ID',
    box_name VARCHAR(64) NOT NULL COMMENT '意见箱名称',
    admin_user_id BIGINT UNSIGNED DEFAULT NULL COMMENT '管理员ID',
    admin_user_name VARCHAR(64) DEFAULT NULL COMMENT '管理员姓名',
    is_anonymous TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '允许匿名(0否 1是)',
    is_active TINYINT UNSIGNED NOT NULL DEFAULT 1 COMMENT '是否启用(0否 1是)',
    remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
    create_by VARCHAR(64) DEFAULT NULL COMMENT '创建者',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by VARCHAR(64) DEFAULT NULL COMMENT '更新者',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'update_time',
    is_deleted TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否删除(0否 1是)',
    PRIMARY KEY (id),
    KEY idx_company_id (company_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='意见箱表';

-- 意见提交表
CREATE TABLE admin_opinion_submit (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '意见ID',
    company_id BIGINT UNSIGNED NOT NULL COMMENT '物业公司ID',
    box_id BIGINT UNSIGNED NOT NULL COMMENT '意见箱ID',
    title VARCHAR(128) NOT NULL COMMENT '意见标题',
    content TEXT NOT NULL COMMENT '意见内容',
    is_anonymous TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否匿名(0否 1是)',
    submit_user_id BIGINT UNSIGNED DEFAULT NULL COMMENT '提交人ID',
    submit_user_name VARCHAR(64) DEFAULT NULL COMMENT '提交人姓名',
    submit_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '提交时间',
    images VARCHAR(2000) DEFAULT NULL COMMENT '图片URL(JSON数组)',
    status TINYINT UNSIGNED NOT NULL DEFAULT 1 COMMENT '状态(1待处理 2处理中 3已回复 4已关闭)',
    reply_content TEXT DEFAULT NULL COMMENT '回复内容',
    reply_user_id BIGINT UNSIGNED DEFAULT NULL COMMENT '回复人ID',
    reply_user_name VARCHAR(64) DEFAULT NULL COMMENT '回复人姓名',
    reply_time DATETIME DEFAULT NULL COMMENT '回复时间',
    satisfaction TINYINT UNSIGNED DEFAULT NULL COMMENT '满意度(1-5)',
    remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
    create_by VARCHAR(64) DEFAULT NULL COMMENT '创建者',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by VARCHAR(64) DEFAULT NULL COMMENT '更新者',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'update_time',
    is_deleted TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否删除(0否 1是)',
    PRIMARY KEY (id),
    KEY idx_company_id (company_id),
    KEY idx_box_id (box_id),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='意见提交表';

-- 投票调查表
CREATE TABLE admin_survey (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '调查ID',
    company_id BIGINT UNSIGNED NOT NULL COMMENT '物业公司ID',
    community_id BIGINT UNSIGNED DEFAULT NULL COMMENT '小区ID',
    survey_title VARCHAR(128) NOT NULL COMMENT '调查标题',
    survey_desc TEXT DEFAULT NULL COMMENT '调查说明',
    survey_type TINYINT UNSIGNED NOT NULL DEFAULT 1 COMMENT '调查类型(1投票 2问卷)',
    start_time DATETIME NOT NULL COMMENT '开始时间',
    end_time DATETIME NOT NULL COMMENT '结束时间',
    is_anonymous TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否匿名(0否 1是)',
    is_multiple TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否多选(0否 1是)',
    max_select INT UNSIGNED DEFAULT NULL COMMENT '最多选择项数',
    participant_count INT UNSIGNED NOT NULL DEFAULT 0 COMMENT '参与人数',
    status TINYINT UNSIGNED NOT NULL DEFAULT 1 COMMENT '状态(1草稿 2进行中 3已结束)',
    remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
    create_by VARCHAR(64) DEFAULT NULL COMMENT '创建者',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by VARCHAR(64) DEFAULT NULL COMMENT '更新者',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'update_time',
    is_deleted TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否删除(0否 1是)',
    PRIMARY KEY (id),
    KEY idx_company_id (company_id),
    KEY idx_community_id (community_id),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='投票调查表';

-- 业委会成员表
CREATE TABLE admin_committee_member (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '成员ID',
    company_id BIGINT UNSIGNED NOT NULL COMMENT '物业公司ID',
    community_id BIGINT UNSIGNED NOT NULL COMMENT '小区ID',
    member_name VARCHAR(64) NOT NULL COMMENT '成员姓名',
    position VARCHAR(64) NOT NULL COMMENT '职务',
    phone VARCHAR(32) DEFAULT NULL COMMENT '联系电话',
    room_id BIGINT UNSIGNED DEFAULT NULL COMMENT '房间ID',
    term_start DATE NOT NULL COMMENT '任期开始',
    term_end DATE NOT NULL COMMENT '任期结束',
    photo VARCHAR(512) DEFAULT NULL COMMENT '照片URL',
    introduction TEXT DEFAULT NULL COMMENT '个人简介',
    status TINYINT UNSIGNED NOT NULL DEFAULT 1 COMMENT '状态(1在任 2已离任)',
    remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
    create_by VARCHAR(64) DEFAULT NULL COMMENT '创建者',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by VARCHAR(64) DEFAULT NULL COMMENT '更新者',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否删除(0否 1是)',
    PRIMARY KEY (id),
    KEY idx_company_id (company_id),
    KEY idx_community_id (community_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='业委会成员表';

-- 业委会会议表
CREATE TABLE admin_committee_meeting (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '会议ID',
    company_id BIGINT UNSIGNED NOT NULL COMMENT '物业公司ID',
    community_id BIGINT UNSIGNED NOT NULL COMMENT '小区ID',
    meeting_title VARCHAR(128) NOT NULL COMMENT '会议标题',
    meeting_date DATE NOT NULL COMMENT '会议日期',
    start_time TIME NOT NULL COMMENT '开始时间',
    end_time TIME NOT NULL COMMENT '结束时间',
    location VARCHAR(256) DEFAULT NULL COMMENT '会议地点',
    meeting_content TEXT NOT NULL COMMENT '会议内容',
    meeting_summary TEXT DEFAULT NULL COMMENT '会议纪要',
    attendees VARCHAR(500) DEFAULT NULL COMMENT '参会人员',
    images VARCHAR(2000) DEFAULT NULL COMMENT '图片URL(JSON数组)',
    status TINYINT UNSIGNED NOT NULL DEFAULT 1 COMMENT '状态(1计划中 2进行中 3已完成)',
    remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
    create_by VARCHAR(64) DEFAULT NULL COMMENT '创建者',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by VARCHAR(64) DEFAULT NULL COMMENT '更新者',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否删除(0否 1是)',
    PRIMARY KEY (id),
    KEY idx_company_id (company_id),
    KEY idx_community_id (community_id),
    KEY idx_meeting_date (meeting_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='业委会会议表';

-- 清洁检查表
CREATE TABLE operation_clean_check (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '检查ID',
    company_id BIGINT UNSIGNED NOT NULL COMMENT '物业公司ID',
    community_id BIGINT UNSIGNED NOT NULL COMMENT '小区ID',
    arrange_id BIGINT UNSIGNED DEFAULT NULL COMMENT '安排ID',
    check_date DATE NOT NULL COMMENT '检查日期',
    area_name VARCHAR(128) NOT NULL COMMENT '检查区域',
    check_result TINYINT UNSIGNED NOT NULL COMMENT '检查结果(1合格 2不合格)',
    score INT UNSIGNED DEFAULT NULL COMMENT '评分(1-100)',
    problems TEXT DEFAULT NULL COMMENT '问题描述',
    checker_id BIGINT UNSIGNED DEFAULT NULL COMMENT '检查人ID',
    checker_name VARCHAR(64) DEFAULT NULL COMMENT '检查人姓名',
    remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
    create_by VARCHAR(64) DEFAULT NULL COMMENT '创建者',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by VARCHAR(64) DEFAULT NULL COMMENT '更新者',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否删除(0否 1是)',
    PRIMARY KEY (id),
    KEY idx_company_id (company_id),
    KEY idx_community_id (community_id),
    KEY idx_check_date (check_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='清洁检查表';

-- 绿化植被表
CREATE TABLE operation_greenery (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '植被ID',
    company_id BIGINT UNSIGNED NOT NULL COMMENT '物业公司ID',
    community_id BIGINT UNSIGNED NOT NULL COMMENT '小区ID',
    greenery_name VARCHAR(64) NOT NULL COMMENT '植被名称',
    greenery_type TINYINT UNSIGNED NOT NULL COMMENT '植被类型(1乔木 2灌木 3草坪 4花卉)',
    location VARCHAR(256) DEFAULT NULL COMMENT '种植位置',
    quantity INT UNSIGNED DEFAULT NULL COMMENT '数量',
    plant_date DATE DEFAULT NULL COMMENT '种植日期',
    status TINYINT UNSIGNED NOT NULL DEFAULT 1 COMMENT '状态(1正常 2枯萎 3已移除)',
    remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
    create_by VARCHAR(64) DEFAULT NULL COMMENT '创建者',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by VARCHAR(64) DEFAULT NULL COMMENT '更新者',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否删除(0否 1是)',
    PRIMARY KEY (id),
    KEY idx_company_id (company_id),
    KEY idx_community_id (community_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='绿化植被表';

-- 绿化检查表
CREATE TABLE operation_greenery_check (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '检查ID',
    company_id BIGINT UNSIGNED NOT NULL COMMENT '物业公司ID',
    community_id BIGINT UNSIGNED NOT NULL COMMENT '小区ID',
    check_date DATE NOT NULL COMMENT '检查日期',
    area_name VARCHAR(128) NOT NULL COMMENT '检查区域',
    check_result TINYINT UNSIGNED NOT NULL COMMENT '检查结果(1合格 2不合格)',
    score INT UNSIGNED DEFAULT NULL COMMENT '评分(1-100)',
    problems TEXT DEFAULT NULL COMMENT '问题描述',
    checker_id BIGINT UNSIGNED DEFAULT NULL COMMENT '检查人ID',
    checker_name VARCHAR(64) DEFAULT NULL COMMENT '检查人姓名',
    remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
    create_by VARCHAR(64) DEFAULT NULL COMMENT '创建者',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by VARCHAR(64) DEFAULT NULL COMMENT '更新者',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否删除(0否 1是)',
    PRIMARY KEY (id),
    KEY idx_company_id (company_id),
    KEY idx_community_id (community_id),
    KEY idx_check_date (check_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='绿化检查表';

-- 消防巡查表
CREATE TABLE operation_fire_patrol (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '巡查ID',
    company_id BIGINT UNSIGNED NOT NULL COMMENT '物业公司ID',
    community_id BIGINT UNSIGNED NOT NULL COMMENT '小区ID',
    patrol_date DATE NOT NULL COMMENT '巡查日期',
    patrol_time TIME NOT NULL COMMENT '巡查时间',
    patrol_area VARCHAR(128) NOT NULL COMMENT '巡查区域',
    patrol_result TINYINT UNSIGNED NOT NULL COMMENT '巡查结果(1正常 2异常)',
    problems TEXT DEFAULT NULL COMMENT '问题描述',
    patrol_user_id BIGINT UNSIGNED DEFAULT NULL COMMENT '巡查人ID',
    patrol_user_name VARCHAR(64) DEFAULT NULL COMMENT '巡查人姓名',
    handle_content TEXT DEFAULT NULL COMMENT '处理情况',
    status TINYINT UNSIGNED NOT NULL DEFAULT 1 COMMENT '状态(1待处理 2已处理)',
    remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
    create_by VARCHAR(64) DEFAULT NULL COMMENT '创建者',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by VARCHAR(64) DEFAULT NULL COMMENT '更新者',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否删除(0否 1是)',
    PRIMARY KEY (id),
    KEY idx_company_id (company_id),
    KEY idx_community_id (community_id),
    KEY idx_patrol_date (patrol_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消防巡查表';

-- 消防演练表
CREATE TABLE operation_fire_drill (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '演练ID',
    company_id BIGINT UNSIGNED NOT NULL COMMENT '物业公司ID',
    community_id BIGINT UNSIGNED NOT NULL COMMENT '小区ID',
    drill_name VARCHAR(128) NOT NULL COMMENT '演练名称',
    drill_type TINYINT UNSIGNED NOT NULL COMMENT '演练类型(1灭火演练 2疏散演练 3综合演练)',
    drill_date DATE NOT NULL COMMENT '演练日期',
    start_time TIME NOT NULL COMMENT '开始时间',
    end_time TIME NOT NULL COMMENT '结束时间',
    location VARCHAR(256) NOT NULL COMMENT '演练地点',
    participant_count INT UNSIGNED DEFAULT NULL COMMENT '参与人数',
    drill_content TEXT NOT NULL COMMENT '演练内容',
    drill_summary TEXT DEFAULT NULL COMMENT '演练总结',
    organizer VARCHAR(64) DEFAULT NULL COMMENT '组织人',
    status TINYINT UNSIGNED NOT NULL DEFAULT 1 COMMENT '状态(1计划中 2进行中 3已完成)',
    remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
    create_by VARCHAR(64) DEFAULT NULL COMMENT '创建者',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by VARCHAR(64) DEFAULT NULL COMMENT '更新者',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否删除(0否 1是)',
    PRIMARY KEY (id),
    KEY idx_company_id (company_id),
    KEY idx_community_id (community_id),
    KEY idx_drill_date (drill_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消防演练表';

-- 执勤记录表
CREATE TABLE operation_duty_record (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '记录ID',
    company_id BIGINT UNSIGNED NOT NULL COMMENT '物业公司ID',
    community_id BIGINT UNSIGNED NOT NULL COMMENT '小区ID',
    duty_date DATE NOT NULL COMMENT '执勤日期',
    start_time TIME NOT NULL COMMENT '开始时间',
    end_time TIME NOT NULL COMMENT '结束时间',
    position VARCHAR(64) NOT NULL COMMENT '执勤岗位',
    security_id BIGINT UNSIGNED NOT NULL COMMENT '保安人员ID',
    security_name VARCHAR(64) NOT NULL COMMENT '保安人员姓名',
    duty_content TEXT DEFAULT NULL COMMENT '执勤内容',
    abnormal_info TEXT DEFAULT NULL COMMENT '异常情况',
    status TINYINT UNSIGNED NOT NULL DEFAULT 1 COMMENT '状态(1正常 2异常)',
    remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
    create_by VARCHAR(64) DEFAULT NULL COMMENT '创建者',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by VARCHAR(64) DEFAULT NULL COMMENT '更新者',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否删除(0否 1是)',
    PRIMARY KEY (id),
    KEY idx_company_id (company_id),
    KEY idx_community_id (community_id),
    KEY idx_duty_date (duty_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='执勤记录表';

-- 物品出入表
CREATE TABLE operation_goods_record (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '记录ID',
    company_id BIGINT UNSIGNED NOT NULL COMMENT '物业公司ID',
    community_id BIGINT UNSIGNED NOT NULL COMMENT '小区ID',
    record_type TINYINT UNSIGNED NOT NULL COMMENT '记录类型(1物品带入 2物品带出)',
    goods_name VARCHAR(128) NOT NULL COMMENT '物品名称',
    goods_desc VARCHAR(500) DEFAULT NULL COMMENT '物品描述',
    quantity INT UNSIGNED NOT NULL DEFAULT 1 COMMENT '数量',
    owner_name VARCHAR(64) DEFAULT NULL COMMENT '业主姓名',
    room_id BIGINT UNSIGNED DEFAULT NULL COMMENT '房间ID',
    operator_name VARCHAR(64) NOT NULL COMMENT '操作人姓名',
    operator_phone VARCHAR(32) DEFAULT NULL COMMENT '操作人电话',
    operate_time DATETIME NOT NULL COMMENT '操作时间',
    guard_id BIGINT UNSIGNED DEFAULT NULL COMMENT '门卫ID',
    guard_name VARCHAR(64) DEFAULT NULL COMMENT '门卫姓名',
    remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
    create_by VARCHAR(64) DEFAULT NULL COMMENT '创建者',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by VARCHAR(64) DEFAULT NULL COMMENT '更新者',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否删除(0否 1是)',
    PRIMARY KEY (id),
    KEY idx_company_id (company_id),
    KEY idx_community_id (community_id),
    KEY idx_operate_time (operate_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='物品出入表';

-- 社区活动表
CREATE TABLE operation_community_activity (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '活动ID',
    company_id BIGINT UNSIGNED NOT NULL COMMENT '物业公司ID',
    community_id BIGINT UNSIGNED NOT NULL COMMENT '小区ID',
    activity_name VARCHAR(128) NOT NULL COMMENT '活动名称',
    activity_type TINYINT UNSIGNED NOT NULL COMMENT '活动类型(1公益 2文体 3节日 4其他)',
    activity_date DATE NOT NULL COMMENT '活动日期',
    start_time TIME NOT NULL COMMENT '开始时间',
    end_time TIME NOT NULL COMMENT '结束时间',
    location VARCHAR(256) NOT NULL COMMENT '活动地点',
    content TEXT NOT NULL COMMENT '活动内容',
    participant_count INT UNSIGNED DEFAULT NULL COMMENT '参与人数',
    budget DECIMAL(10,2) DEFAULT NULL COMMENT '预算(元)',
    actual_cost DECIMAL(10,2) DEFAULT NULL COMMENT '实际花费(元)',
    organizer VARCHAR(64) DEFAULT NULL COMMENT '组织人',
    status TINYINT UNSIGNED NOT NULL DEFAULT 1 COMMENT '状态(1计划中 2进行中 3已完成)',
    remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
    create_by VARCHAR(64) DEFAULT NULL COMMENT '创建者',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by VARCHAR(64) DEFAULT NULL COMMENT '更新者',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否删除(0否 1是)',
    PRIMARY KEY (id),
    KEY idx_company_id (company_id),
    KEY idx_community_id (community_id),
    KEY idx_activity_date (activity_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='社区活动表';

-- 规章制度表
CREATE TABLE admin_regulation (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '制度ID',
    company_id BIGINT UNSIGNED NOT NULL COMMENT '物业公司ID',
    title VARCHAR(128) NOT NULL COMMENT '制度标题',
    content TEXT NOT NULL COMMENT '制度内容',
    category VARCHAR(64) DEFAULT NULL COMMENT '分类',
    file_url VARCHAR(512) DEFAULT NULL COMMENT '附件URL',
    file_name VARCHAR(128) DEFAULT NULL COMMENT '附件名称',
    is_publish TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否发布(0否 1是)',
    publish_time DATETIME DEFAULT NULL COMMENT '发布时间',
    view_count INT UNSIGNED NOT NULL DEFAULT 0 COMMENT '查阅次数',
    status TINYINT UNSIGNED NOT NULL DEFAULT 1 COMMENT '状态(1草稿 2已发布)',
    remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
    create_by VARCHAR(64) DEFAULT NULL COMMENT '创建者',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by VARCHAR(64) DEFAULT NULL COMMENT '更新者',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否删除(0否 1是)',
    PRIMARY KEY (id),
    KEY idx_company_id (company_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='规章制度表';

-- 投票选项表
CREATE TABLE admin_survey_option (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '选项ID',
    company_id BIGINT UNSIGNED NOT NULL COMMENT '物业公司ID',
    survey_id BIGINT UNSIGNED NOT NULL COMMENT '调查ID',
    option_content VARCHAR(500) NOT NULL COMMENT '选项内容',
    option_order INT UNSIGNED NOT NULL DEFAULT 0 COMMENT '选项排序',
    vote_count INT UNSIGNED NOT NULL DEFAULT 0 COMMENT '投票数',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否删除(0否 1是)',
    PRIMARY KEY (id),
    KEY idx_company_id (company_id),
    KEY idx_survey_id (survey_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='投票选项表';

-- 投票记录表
CREATE TABLE admin_survey_vote (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '投票ID',
    company_id BIGINT UNSIGNED NOT NULL COMMENT '物业公司ID',
    survey_id BIGINT UNSIGNED NOT NULL COMMENT '调查ID',
    option_id BIGINT UNSIGNED NOT NULL COMMENT '选项ID',
    user_id BIGINT UNSIGNED DEFAULT NULL COMMENT '用户ID',
    vote_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '投票时间',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否删除(0否 1是)',
    PRIMARY KEY (id),
    KEY idx_company_id (company_id),
    KEY idx_survey_id (survey_id),
    KEY idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='投票记录表';
