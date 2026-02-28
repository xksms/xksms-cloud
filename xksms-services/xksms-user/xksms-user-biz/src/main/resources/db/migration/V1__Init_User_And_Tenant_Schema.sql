-- --------------------------------------------------------------------------------
-- xksms-cloud SaaS 生态核心表结构 (生产环境最终版)
-- 数据库: xksms_user_center (示例)
-- 引擎: InnoDB
-- 字符集: utf8mb4 COLLATE utf8mb4_unicode_ci
-- 设计原则: 租户隔离, 权责分离, 审计追溯, 并发安全。
-- --------------------------------------------------------------------------------

-- ----------------------------
-- 1. 租户信息表
-- ----------------------------
CREATE TABLE `sys_tenant` (
                              `id` bigint NOT NULL AUTO_INCREMENT COMMENT '租户ID',
                              `tenant_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '租户名称',
                              `tenant_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '租户编码, 全局唯一',
                              `status` tinyint NOT NULL DEFAULT '0' COMMENT '租户状态 (0-正常, 1-待审核, 2-已禁用)',
                              `package_id` bigint DEFAULT NULL COMMENT '关联的套餐ID',
                              `expire_time` datetime DEFAULT NULL COMMENT '服务到期时间',
                              `creator_id` bigint DEFAULT NULL COMMENT '创建人ID',
                              `creator_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建人姓名 (快照)',
                              `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                              `updater_id` bigint DEFAULT NULL COMMENT '更新人ID',
                              `updater_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '更新人姓名 (快照)',
                              `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                              `version` int NOT NULL DEFAULT '1' COMMENT '乐观锁版本号',
                              `is_deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除标志 (0-未删除, 1-已删除)',
                              PRIMARY KEY (`id`),
                              UNIQUE KEY `uk_tenant_code` (`tenant_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='租户信息表';

-- ----------------------------
-- 2. 用户账户表 (安全核心)
-- ----------------------------
CREATE TABLE `sys_user_account` (
                                    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID, 全局唯一',
                                    `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '登录用户名',
                                    `phone_number` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '手机号, 全局唯一',
                                    `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '邮箱, 全局唯一',
                                    `password_hash` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'BCrypt加密后的密码哈希',
                                    `status` tinyint NOT NULL DEFAULT '0' COMMENT '账户状态 (0-正常, 1-锁定, 2-禁用)',
                                    `creator_id` bigint DEFAULT NULL COMMENT '创建人ID',
                                    `creator_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建人姓名 (快照)',
                                    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                    `updater_id` bigint DEFAULT NULL COMMENT '更新人ID',
                                    `updater_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '更新人姓名 (快照)',
                                    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                    `version` int NOT NULL DEFAULT '1' COMMENT '乐观锁版本号',
                                    `is_deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除标志',
                                    PRIMARY KEY (`id`),
                                    UNIQUE KEY `uk_phone_number` (`phone_number`),
                                    UNIQUE KEY `uk_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户账户表 (安全凭证)';

-- ----------------------------
-- 3. 用户资料表 (业务画像)
-- ----------------------------
CREATE TABLE `sys_user_profile` (
                                    `user_id` bigint NOT NULL COMMENT '用户ID, 关联sys_user_account.id',
                                    `nickname` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户昵称',
                                    `avatar_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '头像地址',
                                    `gender` tinyint DEFAULT '0' COMMENT '性别 (0-未知, 1-男, 2-女)',
                                    `bio` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '个人简介',
                                    `creator_id` bigint DEFAULT NULL COMMENT '创建人ID',
                                    `creator_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建人姓名 (快照)',
                                    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                    `updater_id` bigint DEFAULT NULL COMMENT '更新人ID',
                                    `updater_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '更新人姓名 (快照)',
                                    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                    PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户资料表 (业务画像)';

-- ----------------------------
-- 4. 用户与租户关系表
-- ----------------------------
CREATE TABLE `sys_user_tenant_relation` (
                                            `id` bigint NOT NULL AUTO_INCREMENT,
                                            `user_id` bigint NOT NULL COMMENT '用户ID',
                                            `tenant_id` bigint NOT NULL COMMENT '租户ID',
                                            `status` tinyint NOT NULL DEFAULT '0' COMMENT '用户在该租户下的状态 (0-待加入, 1-已激活)',
                                            `creator_id` bigint DEFAULT NULL COMMENT '创建人ID',
                                            `creator_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建人姓名 (快照)',
                                            `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                            PRIMARY KEY (`id`),
                                            UNIQUE KEY `uk_user_tenant` (`user_id`,`tenant_id`),
                                            KEY `idx_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户与租户关系表';

-- ----------------------------
-- 5. 角色表
-- ----------------------------
CREATE TABLE `sys_role` (
                            `id` bigint NOT NULL AUTO_INCREMENT COMMENT '角色ID',
                            `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户ID, 0 代表平台通用角色',
                            `role_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色名称',
                            `role_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色编码',
                            `status` tinyint NOT NULL DEFAULT '0' COMMENT '角色状态 (0-正常, 1-禁用)',
                            `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
                            `creator_id` bigint DEFAULT NULL COMMENT '创建人ID',
                            `creator_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建人姓名 (快照)',
                            `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            `updater_id` bigint DEFAULT NULL COMMENT '更新人ID',
                            `updater_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '更新人姓名 (快照)',
                            `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                            `version` int NOT NULL DEFAULT '1' COMMENT '乐观锁版本号',
                            `is_deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除标志',
                            PRIMARY KEY (`id`),
                            UNIQUE KEY `uk_tenant_role_code` (`tenant_id`,`role_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色表';

-- ----------------------------
-- 6. 权限表 (平台级, 无租户概念)
-- ----------------------------
CREATE TABLE `sys_permission` (
                                  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '权限ID',
                                  `parent_id` bigint NOT NULL DEFAULT '0' COMMENT '父权限ID, 用于菜单树',
                                  `permission_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '权限名称/菜单标题',
                                  `permission_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '权限编码 (e.g., user:create)',
                                  `type` tinyint NOT NULL COMMENT '类型 (1-目录, 2-菜单, 3-按钮/API)',
                                  `route_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '前端路由地址',
                                  `icon` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '菜单图标',
                                  `sort_order` int DEFAULT '0' COMMENT '排序',
                                  `status` tinyint NOT NULL DEFAULT '0' COMMENT '状态 (0-正常, 1-禁用)',
                                  `creator_id` bigint DEFAULT NULL COMMENT '创建人ID',
                                  `creator_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建人姓名 (快照)',
                                  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                  `updater_id` bigint DEFAULT NULL COMMENT '更新人ID',
                                  `updater_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '更新人姓名 (快照)',
                                  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                  `is_deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除标志',
                                  PRIMARY KEY (`id`),
                                  KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='权限表(菜单、按钮、API)';

-- ----------------------------
-- 7. 用户角色关联表
-- ----------------------------
CREATE TABLE `sys_user_role` (
                                 `id` bigint NOT NULL AUTO_INCREMENT,
                                 `user_id` bigint NOT NULL COMMENT '用户ID',
                                 `tenant_id` bigint NOT NULL COMMENT '租户ID, 冗余字段, 用于快速查询',
                                 `role_id` bigint NOT NULL COMMENT '角色ID',
                                 `creator_id` bigint DEFAULT NULL COMMENT '创建人ID',
                                 `creator_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建人姓名 (快照)',
                                 `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                 PRIMARY KEY (`id`),
                                 UNIQUE KEY `uk_user_tenant_role` (`user_id`,`tenant_id`,`role_id`),
                                 KEY `idx_role_id` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户在特定租户下的角色关联表';

-- ----------------------------
-- 8. 角色权限关联表
-- ----------------------------
CREATE TABLE `sys_role_permission` (
                                       `id` bigint NOT NULL AUTO_INCREMENT,
                                       `role_id` bigint NOT NULL COMMENT '角色ID',
                                       `permission_id` bigint NOT NULL COMMENT '权限ID',
                                       `creator_id` bigint DEFAULT NULL COMMENT '创建人ID',
                                       `creator_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建人姓名 (快照)',
                                       `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                       PRIMARY KEY (`id`),
                                       UNIQUE KEY `uk_role_permission` (`role_id`,`permission_id`),
                                       KEY `idx_permission_id` (`permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色权限关联表';