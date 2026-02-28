-- V1.0.0__初始化系统管理模块数据库
-- 创建时间：2026-02-28
-- 描述：创建系统管理模块的基础表结构

-- ==================== 租户表 ====================
CREATE TABLE IF NOT EXISTS `system_tenant` (
    `id` BIGINT(20) NOT NULL COMMENT '租户 ID',
    `tenant_code` VARCHAR(64) NOT NULL COMMENT '租户编码',
    `tenant_name` VARCHAR(128) NOT NULL COMMENT '租户名称',
    `contact_name` VARCHAR(64) DEFAULT NULL COMMENT '联系人姓名',
    `contact_phone` VARCHAR(20) DEFAULT NULL COMMENT '联系人电话',
    `contact_email` VARCHAR(128) DEFAULT NULL COMMENT '联系人邮箱',
    `status` TINYINT(2) NOT NULL DEFAULT 1 COMMENT '状态（0-停用 1-正常）',
    `expire_time` DATETIME DEFAULT NULL COMMENT '过期时间',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `creator` VARCHAR(64) DEFAULT NULL COMMENT '创建者',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater` VARCHAR(64) DEFAULT NULL COMMENT '更新者',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT(2) NOT NULL DEFAULT 0 COMMENT '删除标志（0-未删除 1-已删除）',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_tenant_code` (`tenant_code`),
    KEY `idx_status` (`status`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='租户表';

-- ==================== 系统菜单表 ====================
CREATE TABLE IF NOT EXISTS `system_menu` (
    `id` BIGINT(20) NOT NULL COMMENT '菜单 ID',
    `parent_id` BIGINT(20) NOT NULL DEFAULT 0 COMMENT '父菜单 ID',
    `menu_name` VARCHAR(64) NOT NULL COMMENT '菜单名称',
    `menu_type` TINYINT(2) NOT NULL COMMENT '菜单类型（1-目录 2-菜单 3-按钮）',
    `menu_icon` VARCHAR(128) DEFAULT NULL COMMENT '菜单图标',
    `route_path` VARCHAR(256) DEFAULT NULL COMMENT '路由路径',
    `route_param` VARCHAR(256) DEFAULT NULL COMMENT '路由参数',
    `component` VARCHAR(256) DEFAULT NULL COMMENT '组件路径',
    `permission` VARCHAR(128) DEFAULT NULL COMMENT '权限标识',
    `visible` TINYINT(2) NOT NULL DEFAULT 1 COMMENT '是否可见（0-隐藏 1-显示）',
    `sort_order` INT(11) NOT NULL DEFAULT 0 COMMENT '排序',
    `creator` VARCHAR(64) DEFAULT NULL COMMENT '创建者',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater` VARCHAR(64) DEFAULT NULL COMMENT '更新者',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT(2) NOT NULL DEFAULT 0 COMMENT '删除标志（0-未删除 1-已删除）',
    PRIMARY KEY (`id`),
    KEY `idx_parent_id` (`parent_id`),
    KEY `idx_menu_type` (`menu_type`),
    KEY `idx_visible` (`visible`),
    KEY `idx_sort_order` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜单表';

-- ==================== 系统角色表 ====================
CREATE TABLE IF NOT EXISTS `system_role` (
    `id` BIGINT(20) NOT NULL COMMENT '角色 ID',
    `tenant_id` BIGINT(20) NOT NULL COMMENT '租户 ID',
    `role_code` VARCHAR(64) NOT NULL COMMENT '角色编码',
    `role_name` VARCHAR(64) NOT NULL COMMENT '角色名称',
    `role_level` INT(11) DEFAULT NULL COMMENT '角色层级',
    `data_scope` TINYINT(2) NOT NULL DEFAULT 1 COMMENT '数据范围（1-全部 2-本部门 3-本部门及以下 4-仅本人）',
    `status` TINYINT(2) NOT NULL DEFAULT 1 COMMENT '状态（0-停用 1-正常）',
    `sort_order` INT(11) NOT NULL DEFAULT 0 COMMENT '排序',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `creator` VARCHAR(64) DEFAULT NULL COMMENT '创建者',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater` VARCHAR(64) DEFAULT NULL COMMENT '更新者',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT(2) NOT NULL DEFAULT 0 COMMENT '删除标志（0-未删除 1-已删除）',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_tenant_role_code` (`tenant_id`, `role_code`),
    KEY `idx_status` (`status`),
    KEY `idx_sort_order` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- ==================== 角色菜单关联表 ====================
CREATE TABLE IF NOT EXISTS `system_role_menu` (
    `id` BIGINT(20) NOT NULL COMMENT 'ID',
    `role_id` BIGINT(20) NOT NULL COMMENT '角色 ID',
    `menu_id` BIGINT(20) NOT NULL COMMENT '菜单 ID',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_menu` (`role_id`, `menu_id`),
    KEY `idx_menu_id` (`menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色菜单关联表';

-- ==================== 系统部门表 ====================
CREATE TABLE IF NOT EXISTS `system_department` (
    `id` BIGINT(20) NOT NULL COMMENT '部门 ID',
    `tenant_id` BIGINT(20) NOT NULL COMMENT '租户 ID',
    `parent_id` BIGINT(20) NOT NULL DEFAULT 0 COMMENT '父部门 ID',
    `dept_name` VARCHAR(64) NOT NULL COMMENT '部门名称',
    `dept_code` VARCHAR(64) DEFAULT NULL COMMENT '部门编码',
    `leader` VARCHAR(64) DEFAULT NULL COMMENT '负责人',
    `contact_phone` VARCHAR(20) DEFAULT NULL COMMENT '联系电话',
    `contact_email` VARCHAR(128) DEFAULT NULL COMMENT '联系邮箱',
    `sort_order` INT(11) NOT NULL DEFAULT 0 COMMENT '排序',
    `status` TINYINT(2) NOT NULL DEFAULT 1 COMMENT '状态（0-停用 1-正常）',
    `creator` VARCHAR(64) DEFAULT NULL COMMENT '创建者',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater` VARCHAR(64) DEFAULT NULL COMMENT '更新者',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT(2) NOT NULL DEFAULT 0 COMMENT '删除标志（0-未删除 1-已删除）',
    PRIMARY KEY (`id`),
    KEY `idx_tenant_parent_id` (`tenant_id`, `parent_id`),
    KEY `idx_status` (`status`),
    KEY `idx_sort_order` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='部门表';

-- ==================== 系统字典表 ====================
CREATE TABLE IF NOT EXISTS `system_dict` (
    `id` BIGINT(20) NOT NULL COMMENT '字典 ID',
    `tenant_id` BIGINT(20) NOT NULL COMMENT '租户 ID',
    `dict_code` VARCHAR(64) NOT NULL COMMENT '字典编码',
    `dict_name` VARCHAR(64) NOT NULL COMMENT '字典名称',
    `dict_type` TINYINT(2) NOT NULL DEFAULT 1 COMMENT '字典类型（1-系统内置 2-用户自定义）',
    `status` TINYINT(2) NOT NULL DEFAULT 1 COMMENT '状态（0-停用 1-正常）',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `creator` VARCHAR(64) DEFAULT NULL COMMENT '创建者',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater` VARCHAR(64) DEFAULT NULL COMMENT '更新者',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT(2) NOT NULL DEFAULT 0 COMMENT '删除标志（0-未删除 1-已删除）',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_tenant_dict_code` (`tenant_id`, `dict_code`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='字典表';

-- ==================== 系统字典项表 ====================
CREATE TABLE IF NOT EXISTS `system_dict_item` (
    `id` BIGINT(20) NOT NULL COMMENT '字典项 ID',
    `dict_id` BIGINT(20) NOT NULL COMMENT '字典 ID',
    `item_code` VARCHAR(64) NOT NULL COMMENT '字典项编码',
    `item_name` VARCHAR(64) NOT NULL COMMENT '字典项名称',
    `item_value` VARCHAR(256) DEFAULT NULL COMMENT '字典项值',
    `sort_order` INT(11) NOT NULL DEFAULT 0 COMMENT '排序',
    `status` TINYINT(2) NOT NULL DEFAULT 1 COMMENT '状态（0-停用 1-正常）',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `creator` VARCHAR(64) DEFAULT NULL COMMENT '创建者',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater` VARCHAR(64) DEFAULT NULL COMMENT '更新者',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_dict_id` (`dict_id`),
    KEY `idx_sort_order` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='字典项表';
