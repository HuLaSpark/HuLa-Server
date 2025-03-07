/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 80031 (8.0.31)
 Source Host           : localhost:13306
 Source Schema         : im

 Target Server Type    : MySQL
 Target Server Version : 80031 (8.0.31)
 File Encoding         : 65001

 Date: 04/03/2025 10:46:02
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for announcements
-- ----------------------------
DROP TABLE IF EXISTS `announcements`;
CREATE TABLE `announcements`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `room_id` bigint NOT NULL COMMENT '群id',
  `u_id` bigint NOT NULL COMMENT '发布者id',
  `content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_ci NOT NULL COMMENT '公告内容',
  `publish_time` datetime NOT NULL COMMENT '发布时间',
  `created_by` bigint NOT NULL COMMENT '创建者',
  `created_time` datetime NOT NULL COMMENT '创建时间',
  `updated_by` bigint NULL DEFAULT NULL COMMENT '更新者',
  `updated_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `is_del` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_ci COMMENT = '聊天公告表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of announcements
-- ----------------------------

-- ----------------------------
-- Table structure for announcements_read_records
-- ----------------------------
DROP TABLE IF EXISTS `announcements_read_records`;
CREATE TABLE `announcements_read_records`  (
  `id` bigint NOT NULL,
  `announcements_id` bigint NOT NULL COMMENT '公告id',
  `u_id` bigint NOT NULL COMMENT '阅读人id',
  `is_check` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否已读 0：未读 1：已读',
  `created_by` bigint NOT NULL COMMENT '创建者',
  `created_time` datetime NOT NULL COMMENT '创建时间',
  `updated_by` bigint NULL DEFAULT NULL COMMENT '更新者',
  `updated_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `is_del` tinyint(1) NOT NULL COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_ci COMMENT = '公告是否已读表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of announcements_read_records
-- ----------------------------

-- ----------------------------
-- Table structure for black
-- ----------------------------
DROP TABLE IF EXISTS `black`;
CREATE TABLE `black`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
  `type` int NOT NULL COMMENT '拉黑目标类型 1.ip 2uid',
  `target` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '拉黑目标',
  `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `update_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_type_target`(`type` ASC, `target` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '黑名单' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of black
-- ----------------------------

-- ----------------------------
-- Table structure for config
-- ----------------------------
DROP TABLE IF EXISTS `config`;
CREATE TABLE `config`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '参数主键',
  `type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '类型',
  `config_name` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT '' COMMENT '参数名称',
  `config_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT '' COMMENT '参数键名',
  `config_value` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '参数键值',
  `is_del` tinyint UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `key`(`config_key` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 333 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '参数配置表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of config
-- ----------------------------
INSERT INTO `config` VALUES (1, 'system', '{\"title\":\"系统名称\",\"componentType\":\"text\",\"value\":\"Hula-IM\",\"configKey\":\"systemName\",\"type\":\"system\"}', 'systemName', 'Hula-IM', 0);
INSERT INTO `config` VALUES (2, 'system', '{\"title\":\"系统Logo\",\"componentType\":\"text\",\"value\":\"/static/img/Iogo.png\",\"configKey\":\"logo\",\"type\":\"system\"}', 'logo', '/static/img/Iogo.png', 0);
INSERT INTO `config` VALUES (3, 'qiniu_up_config', '{\"title\":\"空间域名 Domain\",\"componentType\":\"text\",\"value\":\"https://upload-z2.qiniup.com\",\"configKey\":\"qnUploadUrl\",\"type\":\"qiniu_up_config\"}', 'qnUploadUrl', 'https://upload-z2.qiniup.com', 0);
INSERT INTO `config` VALUES (4, 'qiniu_up_config', '{\"title\":\"accessKey\",\"componentType\":\"text\",\"value\":\"8si6G12t2MG9IOdkNDYmL0vGAYFTW-rHl4LgA5_\",\"configKey\":\"qnAccessKey\",\"type\":\"qiniu_up_config\"}', 'qnAccessKey', '8si6G12t2MG9IOdkNDYmL0vGAYFTW-rHl4LgA5_', 0);
INSERT INTO `config` VALUES (5, 'qiniu_up_config', '{\"title\":\"SecretKey\",\"componentType\":\"text\",\"value\":\"MLzT2U2daTXFDEG9PuAy4TnvfR1oXvK2Yipm_eS9\",\"configKey\":\"qnSecretKey\",\"type\":\"qiniu_up_config\"}', 'qnSecretKey', 'MLzT2U2daTXFDEG9PuAy4TnvfR1oXvK2Yipm_eS9', 0);
INSERT INTO `config` VALUES (6, 'qiniu_up_config', '{\"title\":\"存储空间名称\",\"componentType\":\"text\",\"value\":\"hula\",\"configKey\":\"qnStorageName\",\"type\":\"qiniu_up_config\"}', 'qnStorageName', 'hula', 0);
INSERT INTO `config` VALUES (7, 'qiniu_up_config', '{\"title\":\"七牛云CDN（访问图片用的）\",\"componentType\":\"text\",\"value\":\"https://file.hula.com/\",\"configKey\":\"qnStorageCDN\",\"type\":\"qiniu_up_config\"}', 'qnStorageCDN', 'https://file.hula.com/', 0);
INSERT INTO `config` VALUES (8, 'system', '{\"title\":\"大群ID\",\"componentType\":\"text\",\"value\":\"1\",\"configKey\":\"roomGroupId\",\"type\":\"system\"}', 'roomGroupId', '1', 0);
INSERT INTO `config` VALUES (9, 'qiniu_up_config', '{\"title\":\"超过多少MB开启分片上传\",\"componentType\":\"text\",\"value\":\"500\",\"configKey\":\"turnSharSize\",\"type\":\"qiniu_up_config\"}', 'turnSharSize', '500', 0);
INSERT INTO `config` VALUES (10, 'qiniu_up_config', '{\"title\":\"分片大小\",\"componentType\":\"text\",\"value\":\"50\",\"configKey\":\"fragmentSize\",\"type\":\"shop_config\"}', 'fragmentSize', '50', 0);

-- ----------------------------
-- Table structure for contact
-- ----------------------------
DROP TABLE IF EXISTS `contact`;
CREATE TABLE `contact`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
  `uid` bigint NOT NULL COMMENT 'uid',
  `room_id` bigint NOT NULL COMMENT '房间id',
  `mute_notification` tinyint NOT NULL DEFAULT 0 COMMENT '免打扰',
  `read_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '阅读到的时间',
  `top` tinyint NOT NULL DEFAULT 0 COMMENT '置顶消息',
  `hide` tinyint NOT NULL DEFAULT 0 COMMENT '置顶消息',
  `active_time` datetime(3) NULL DEFAULT NULL COMMENT '会话内消息最后更新的时间(只有普通会话需要维护，全员会话不需要维护)',
  `last_msg_id` bigint NULL DEFAULT NULL COMMENT '会话最新消息id',
  `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `update_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uniq_uid_room_id`(`uid` ASC, `room_id` ASC) USING BTREE,
  INDEX `idx_room_id_read_time`(`room_id` ASC, `read_time` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  INDEX `idx_update_time`(`update_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12053 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '会话列表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of contact
-- ----------------------------
INSERT INTO `contact` VALUES (1, 20018, 1, 0, '2024-11-16 13:10:46.646', 0, 0, NULL, NULL, '2024-11-16 12:12:49.035', '2025-02-25 15:07:03.944');
INSERT INTO `contact` VALUES (2, 20000, 1, 0, '2025-02-27 18:17:15.866', 0, 0, NULL, NULL, '2024-11-16 12:12:49.171', '2025-02-27 18:17:15.903');

-- ----------------------------
-- Table structure for feed
-- ----------------------------
DROP TABLE IF EXISTS `feed`;
CREATE TABLE `feed`  (
  `id` bigint NOT NULL,
  `u_id` bigint NOT NULL COMMENT '用户id',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_ci NOT NULL COMMENT '朋友圈文案',
  `permission` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_ci NOT NULL DEFAULT '1' COMMENT 'privacy -> 私密 open -> 公开 partVisible -> 部分可见 notAnyone -> 不给谁看',
  `media_type` tinyint NULL DEFAULT NULL COMMENT '朋友圈内容类型（0: 纯文字 1: 图片, 2: 视频）',
  `created_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `uid`(`u_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_ci COMMENT = '朋友圈表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of feed
-- ----------------------------

-- ----------------------------
-- Table structure for feed_media
-- ----------------------------
DROP TABLE IF EXISTS `feed_media`;
CREATE TABLE `feed_media`  (
  `id` bigint NOT NULL,
  `feed_id` bigint NOT NULL COMMENT '朋友圈id',
  `url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_ci NOT NULL COMMENT '图片或视频的路径',
  `sort` int NOT NULL COMMENT '排序',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_ci COMMENT = '朋友圈资源表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of feed_media
-- ----------------------------

-- ----------------------------
-- Table structure for feed_target
-- ----------------------------
DROP TABLE IF EXISTS `feed_target`;
CREATE TABLE `feed_target`  (
  `id` bigint NOT NULL,
  `type` tinyint NOT NULL DEFAULT 1 COMMENT '1 -> 关联标签id 2 -> 关联用户id',
  `feed_id` bigint NOT NULL COMMENT '朋友圈id',
  `target_id` bigint NOT NULL COMMENT '标签id',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_ci COMMENT = '朋友圈可见表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of feed_target
-- ----------------------------

-- ----------------------------
-- Table structure for group_member
-- ----------------------------
DROP TABLE IF EXISTS `group_member`;
CREATE TABLE `group_member`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
  `group_id` bigint NOT NULL COMMENT '群组id',
  `uid` bigint NOT NULL COMMENT '成员uid',
  `role` int NOT NULL COMMENT '成员角色 1群主 2管理员 3普通成员',
  `remark` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '群备注',
  `de_friend` tinyint NOT NULL DEFAULT 0 COMMENT '屏蔽群 1 -> 屏蔽 0 -> 正常',
  `my_name` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '我的群昵称',
  `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `update_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_group_id_role`(`group_id` ASC, `role` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  INDEX `idx_update_time`(`update_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 79 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '群成员表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of group_member
-- ----------------------------
INSERT INTO `group_member` VALUES (1, 2, 20000, 1, NULL, 0, NULL, '2024-11-30 13:54:45.747', '2024-11-30 13:54:45.747');
INSERT INTO `group_member` VALUES (2, 2, 20009, 3, NULL, 0, NULL, '2024-11-30 13:54:45.804', '2024-11-30 13:54:45.804');
INSERT INTO `group_member` VALUES (3, 2, 20024, 3, NULL, 0, NULL, '2024-11-30 13:54:45.807', '2024-11-30 13:54:45.807');
INSERT INTO `group_member` VALUES (4, 2, 20021, 3, NULL, 0, NULL, '2024-11-30 13:54:45.809', '2024-11-30 13:54:45.809');
INSERT INTO `group_member` VALUES (5, 2, 20016, 3, NULL, 0, NULL, '2024-11-30 13:54:45.810', '2024-11-30 13:54:45.810');
INSERT INTO `group_member` VALUES (6, 2, 20017, 3, NULL, 0, NULL, '2024-11-30 13:54:45.812', '2024-11-30 13:54:45.812');

-- ----------------------------
-- Table structure for item_config
-- ----------------------------
DROP TABLE IF EXISTS `item_config`;
CREATE TABLE `item_config`  (
  `id` bigint UNSIGNED NOT NULL COMMENT 'id',
  `type` int NOT NULL COMMENT '物品类型 1改名卡 2徽章',
  `img` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '物品图片',
  `describe` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '物品功能描述',
  `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `update_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  INDEX `idx_update_time`(`update_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '功能物品配置表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of item_config
-- ----------------------------
INSERT INTO `item_config` VALUES (1, 1, NULL, '用户可以使用改名卡，更改自己的名字。HuLa名称全局唯一，快抢订你的专属昵称吧', '2023-03-25 22:27:30.511', '2024-05-11 19:37:03.965');
INSERT INTO `item_config` VALUES (2, 2, 'https://hulaspark.com/minio/hula/badge/like.png', '爆赞徽章，单条消息被点赞超过10次，即可获得', '2023-05-07 17:50:31.090', '2024-10-27 12:56:02.777');
INSERT INTO `item_config` VALUES (3, 2, 'https://hulaspark.com/minio/hula/badge/top10.png ', 'HuLa前10名注册的用户才能获得的专属徽章', '2023-05-07 17:50:31.100', '2024-10-27 12:56:04.908');
INSERT INTO `item_config` VALUES (4, 2, 'https://hulaspark.com/minio/hula/badge/top100.png', 'HuLa前100名注册的用户才能获得的专属徽章', '2023-05-07 17:50:31.109', '2024-10-27 12:56:06.990');
INSERT INTO `item_config` VALUES (5, 2, 'https://hulaspark.com/minio/hula/badge/planet.png', 'HuLa知识星球成员的专属徽章', '2023-05-07 17:50:31.109', '2024-10-27 12:56:09.420');
INSERT INTO `item_config` VALUES (6, 2, 'https://hulaspark.com/minio/hula/badge/active.png', 'HuLa项目贡献者专属徽章', '2023-05-07 17:50:31.109', '2024-12-17 17:30:54.943');

-- ----------------------------
-- Table structure for message
-- ----------------------------
DROP TABLE IF EXISTS `message`;
CREATE TABLE `message`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
  `room_id` bigint NOT NULL COMMENT '会话表id',
  `from_uid` bigint NOT NULL COMMENT '消息发送者uid',
  `content` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '消息内容',
  `reply_msg_id` bigint NULL DEFAULT NULL COMMENT '回复的消息内容',
  `status` int NOT NULL COMMENT '消息状态 0正常 1删除',
  `gap_count` int NULL DEFAULT NULL COMMENT '与回复的消息间隔多少条',
  `type` int NULL DEFAULT 1 COMMENT '消息类型 1正常文本 2.撤回消息',
  `extra` json NULL COMMENT '扩展信息',
  `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `update_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_room_id`(`room_id` ASC) USING BTREE,
  INDEX `idx_from_uid`(`from_uid` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  INDEX `idx_update_time`(`update_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5947 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '消息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of message
-- ----------------------------
INSERT INTO `message` VALUES (5946, 1, 20000, '123', NULL, 0, NULL, 1, '{\"recall\": null, \"fileMsg\": null, \"atUidList\": null, \"imgMsgDTO\": null, \"soundMsgDTO\": null, \"videoMsgDTO\": null, \"emojisMsgDTO\": null, \"urlContentMap\": {}}', '2025-02-27 18:17:09.473', '2025-02-27 18:17:09.568');

-- ----------------------------
-- Table structure for message_mark
-- ----------------------------
DROP TABLE IF EXISTS `message_mark`;
CREATE TABLE `message_mark`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
  `msg_id` bigint NOT NULL COMMENT '消息表id',
  `uid` bigint NOT NULL COMMENT '标记人uid',
  `type` int NOT NULL COMMENT '标记类型 1点赞 2举报',
  `status` int NOT NULL COMMENT '消息状态 0正常 1取消',
  `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `update_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_msg_id`(`msg_id` ASC) USING BTREE,
  INDEX `idx_uid`(`uid` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  INDEX `idx_update_time`(`update_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '消息标记表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of message_mark
-- ----------------------------

-- ----------------------------
-- Table structure for role
-- ----------------------------
DROP TABLE IF EXISTS `role`;
CREATE TABLE `role`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色名称',
  `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `update_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  INDEX `idx_update_time`(`update_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '角色表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of role
-- ----------------------------
INSERT INTO `role` VALUES (1, '超级管理员', '2024-07-10 11:17:15.089', '2024-07-10 11:17:15.089');
INSERT INTO `role` VALUES (2, 'HuLa群聊管理员', '2024-07-10 11:17:15.091', '2024-11-26 12:00:22.452');

-- ----------------------------
-- Table structure for room
-- ----------------------------
DROP TABLE IF EXISTS `room`;
CREATE TABLE `room`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
  `type` int NOT NULL COMMENT '房间类型 1群聊 2单聊',
  `hot_flag` int NULL DEFAULT 0 COMMENT '是否全员展示 0否 1是',
  `active_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '群最后消息的更新时间（热点群不需要写扩散，只更新这里）',
  `last_msg_id` bigint NULL DEFAULT NULL COMMENT '会话中的最后一条消息id',
  `ext_json` json NULL COMMENT '额外信息（根据不同类型房间有不同存储的东西）',
  `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `update_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  INDEX `idx_update_time`(`update_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 139 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '房间表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of room
-- ----------------------------
INSERT INTO `room` VALUES (1, 1, 1, '2025-02-27 18:17:09.473', 5946, NULL, '2024-07-10 11:17:15.521', '2025-02-27 18:17:10.150');
INSERT INTO `room` VALUES (2, 2, 0, '2024-11-15 23:41:21.242', 738, NULL, '2024-11-04 05:10:23.402', '2024-11-15 23:41:21.388');
INSERT INTO `room` VALUES (3, 2, 0, '2024-11-13 15:56:49.303', 661, NULL, '2024-11-13 15:56:49.241', '2024-11-13 15:56:49.403');

-- ----------------------------
-- Table structure for room_friend
-- ----------------------------
DROP TABLE IF EXISTS `room_friend`;
CREATE TABLE `room_friend`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
  `room_id` bigint NOT NULL COMMENT '房间id',
  `uid1` bigint NOT NULL COMMENT 'uid1（更小的uid）',
  `uid2` bigint NOT NULL COMMENT 'uid2（更大的uid）',
  `room_key` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '房间key由两个uid拼接，先做排序uid1_uid2',
  `status` int NOT NULL COMMENT '房间状态 0正常 1禁用(删好友了禁用)',
  `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `update_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `room_key`(`room_key` ASC) USING BTREE,
  INDEX `idx_room_id`(`room_id` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  INDEX `idx_update_time`(`update_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 88 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '单聊房间表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of room_friend
-- ----------------------------
INSERT INTO `room_friend` VALUES (3, 12, 20016, 20017, '20016,20017', 0, '2024-11-17 16:50:30.802', '2024-11-17 16:50:30.802');

-- ----------------------------
-- Table structure for room_group
-- ----------------------------
DROP TABLE IF EXISTS `room_group`;
CREATE TABLE `room_group`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
  `room_id` bigint NOT NULL COMMENT '房间id',
  `account_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `name` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '群名称',
  `avatar` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '群头像',
  `ext_json` json NULL COMMENT '额外信息（根据不同类型房间有不同存储的东西）',
  `delete_status` int NOT NULL DEFAULT 0 COMMENT '逻辑删除(0-正常,1-删除)',
  `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `update_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_room_id`(`room_id` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  INDEX `idx_update_time`(`update_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 31 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '群聊房间表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of room_group
-- ----------------------------
INSERT INTO `room_group` VALUES (1, 1, '', 'Hula官方群', 'https://hulaspark.com/minio/hula/avatar/hula.png', NULL, 0, '2024-07-10 11:17:15.523', '2025-02-27 18:18:07.029');
INSERT INTO `room_group` VALUES (2, 30, '', '', 'https://thirdwx.qlogo.cn/mmopen/vi_32/PiajxSqBRaEL7OpdTZYkEeE9oTmZFKL4gIzCr1ibf38OiaPqqcmqlLxTxvw3gskZV5uTma7NSQzCk8yVbIiaN6FV3kmicBWg2CLOKicysib6mDFGEPprQxfUYEupA/132', NULL, 0, '2024-11-30 13:54:45.737', '2024-11-30 13:54:45.737');
INSERT INTO `room_group` VALUES (19, 76, '', '', '010', NULL, 0, '2024-12-30 18:00:06.586', '2024-12-30 18:00:06.586');
INSERT INTO `room_group` VALUES (20, 77, '', '', '010', NULL, 0, '2024-12-31 16:10:09.552', '2024-12-31 16:10:09.552');
INSERT INTO `room_group` VALUES (21, 78, '', '', '010', NULL, 0, '2024-12-31 16:14:07.704', '2024-12-31 16:14:07.704');
INSERT INTO `room_group` VALUES (22, 79, '', '', '010', NULL, 0, '2024-12-31 16:17:34.499', '2024-12-31 16:17:34.499');
INSERT INTO `room_group` VALUES (23, 80, '', '', '001', NULL, 0, '2025-01-01 00:14:48.374', '2025-01-01 00:14:48.374');
INSERT INTO `room_group` VALUES (24, 82, '', '', '001', NULL, 0, '2025-01-01 00:23:21.831', '2025-01-01 00:23:21.831');
INSERT INTO `room_group` VALUES (25, 84, '', '', '006', NULL, 0, '2025-01-01 00:56:39.657', '2025-01-01 00:56:39.657');
INSERT INTO `room_group` VALUES (26, 85, '', '', '001', NULL, 0, '2025-01-02 10:55:49.684', '2025-01-02 10:55:49.684');
INSERT INTO `room_group` VALUES (27, 88, '', '', '001', NULL, 0, '2025-01-09 01:31:51.171', '2025-01-09 01:31:51.171');
INSERT INTO `room_group` VALUES (28, 97, '', '', '001', NULL, 0, '2025-01-18 00:36:57.014', '2025-01-18 00:36:57.014');
INSERT INTO `room_group` VALUES (29, 133, '', '', '001', NULL, 0, '2025-02-17 15:46:02.827', '2025-02-17 15:46:02.827');
INSERT INTO `room_group` VALUES (30, 134, '', '', 'http://111.92.241.175:9000/hula/chat/2025-02/20016/cd9d8c56-3b6f-4ed4-b9ad-7767aa0028ed.png', NULL, 0, '2025-02-17 18:06:48.551', '2025-02-17 18:06:48.551');

-- ----------------------------
-- Table structure for secure_invoke_record
-- ----------------------------
DROP TABLE IF EXISTS `secure_invoke_record`;
CREATE TABLE `secure_invoke_record`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
  `secure_invoke_json` json NOT NULL COMMENT '请求快照参数json',
  `status` tinyint NOT NULL COMMENT '状态 1待执行 2已失败',
  `next_retry_time` datetime(3) NOT NULL COMMENT '下一次重试的时间',
  `retry_times` int NOT NULL COMMENT '已经重试的次数',
  `max_retry_times` int NOT NULL COMMENT '最大重试次数',
  `fail_reason` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '执行失败的堆栈',
  `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `update_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_next_retry_time`(`next_retry_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '本地消息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of secure_invoke_record
-- ----------------------------

-- ----------------------------
-- Table structure for sensitive_word
-- ----------------------------
DROP TABLE IF EXISTS `sensitive_word`;
CREATE TABLE `sensitive_word`  (
  `word` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '敏感词'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '敏感词库' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sensitive_word
-- ----------------------------
INSERT INTO `sensitive_word` VALUES ('TMD');
INSERT INTO `sensitive_word` VALUES ('tmd');

-- ----------------------------
-- Table structure for target
-- ----------------------------
DROP TABLE IF EXISTS `target`;
CREATE TABLE `target`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `u_id` bigint NOT NULL COMMENT '用户id',
  `name` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_ci NOT NULL COMMENT '标签名',
  `icon` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_ci NOT NULL DEFAULT '' COMMENT '标签图标',
  `created_by` bigint NULL DEFAULT NULL COMMENT '创建者',
  `created_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `updated_by` bigint NULL DEFAULT NULL COMMENT '更新者',
  `updated_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `is_del` tinyint(1) NOT NULL COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `employee`(`u_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_ci COMMENT = '聊天的标签' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of target
-- ----------------------------

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '用户id',
  `name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '用户昵称',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '用户头像',
  `account_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `sex` int NULL DEFAULT NULL COMMENT '性别 1为男性，2为女性',
  `open_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '',
  `active_status` int NULL DEFAULT 2 COMMENT '在线状态 1在线 2离线',
  `user_state_id` bigint NOT NULL DEFAULT 0 COMMENT '用户状态id',
  `last_opt_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '最后上下线时间',
  `ip_info` json NULL COMMENT 'ip信息',
  `item_id` bigint NULL DEFAULT NULL COMMENT '佩戴的徽章id',
  `status` int NULL DEFAULT 0 COMMENT '使用状态 0.正常 1拉黑',
  `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `update_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '修改时间',
  `account` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '用户账号',
  `password` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '用户密码',
  `avatar_update_time` datetime(3) NULL DEFAULT NULL COMMENT '头像修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `account_code`(`account_code` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  INDEX `idx_update_time`(`update_time` ASC) USING BTREE,
  INDEX `idx_active_status_last_opt_time`(`active_status` ASC, `last_opt_time` ASC) USING BTREE,
  INDEX `account_UNIQUE`(`account` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 20696 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (20000, 'admin', 'https://thirdwx.qlogo.cn/mmopen/vi_32/PiajxSqBRaEL7OpdTZYkEeE9oTmZFKL4gIzCr1ibf38OiaPqqcmqlLxTxvw3gskZV5uTma7NSQzCk8yVbIiaN6FV3kmicBWg2CLOKicysib6mDFGEPprQxfUYEupA/132', 'ID_5YAN2DXCZF', 0, 'obxlc6xY1EsMyHbbRAAgWDCnOFHE', 1, 2, '2025-02-27 18:20:06.840', '{\"createIp\": \"127.0.0.1\", \"updateIp\": \"127.0.0.1\", \"createIpDetail\": {\"ip\": \"127.0.0.1\", \"isp\": \"内网IP\", \"area\": \"\", \"city\": \"内网IP\", \"isp_id\": \"local\", \"region\": \"XX\", \"city_id\": \"local\", \"country\": \"XX\", \"region_id\": \"xx\", \"country_id\": \"xx\"}, \"updateIpDetail\": {\"ip\": \"127.0.0.1\", \"isp\": \"内网IP\", \"area\": \"\", \"city\": \"内网IP\", \"isp_id\": \"local\", \"region\": \"XX\", \"city_id\": \"local\", \"country\": \"XX\", \"region_id\": \"xx\", \"country_id\": \"xx\"}}', 6, 0, '2024-07-10 15:08:00.607', '2025-02-27 18:20:09.200', 'admin', '123', NULL);

-- ----------------------------
-- Table structure for user_apply
-- ----------------------------
DROP TABLE IF EXISTS `user_apply`;
CREATE TABLE `user_apply`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
  `uid` bigint NOT NULL COMMENT '申请人uid',
  `type` int NOT NULL COMMENT '申请类型 1加好友 2 加群',
  `target_id` bigint NOT NULL COMMENT '接收对象 type: 1 -> uid; type: 2 -> roomGroupId',
  `msg` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '申请信息',
  `status` int NOT NULL COMMENT '申请状态 1待审批 2同意',
  `read_status` int NOT NULL COMMENT '阅读状态 1未读 2已读',
  `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `update_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '修改时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '删除状态 0：未删 1 申请人删除 2 被申请人删除 3都删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_uid_target_id`(`uid` ASC, `target_id` ASC) USING BTREE,
  INDEX `idx_target_id_read_status`(`target_id` ASC, `read_status` ASC) USING BTREE,
  INDEX `idx_target_id`(`target_id` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  INDEX `idx_update_time`(`update_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 159 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户申请表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user_apply
-- ----------------------------

-- ----------------------------
-- Table structure for user_backpack
-- ----------------------------
DROP TABLE IF EXISTS `user_backpack`;
CREATE TABLE `user_backpack`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
  `uid` bigint NOT NULL COMMENT 'uid',
  `item_id` int NOT NULL COMMENT '物品id',
  `status` int NOT NULL COMMENT '使用状态 0.待使用 1已使用',
  `idempotent` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '幂等号',
  `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `update_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uniq_idempotent`(`idempotent` ASC) USING BTREE,
  INDEX `idx_uid`(`uid` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  INDEX `idx_update_time`(`update_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 804 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户背包表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user_backpack
-- ----------------------------

-- ----------------------------
-- Table structure for user_block
-- ----------------------------
DROP TABLE IF EXISTS `user_block`;
CREATE TABLE `user_block`  (
  `id` bigint NOT NULL COMMENT '主键',
  `blocker_uid` bigint NOT NULL COMMENT '屏蔽方用户ID',
  `blocked_uid` bigint NOT NULL COMMENT '被屏蔽方用户ID',
  `created_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '屏蔽时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_blocker_blocked`(`blocker_uid` ASC, `blocked_uid` ASC) USING BTREE,
  INDEX `idx_blocker`(`blocker_uid` ASC) USING BTREE,
  INDEX `idx_blocked`(`blocked_uid` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户屏蔽关系表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user_block
-- ----------------------------

-- ----------------------------
-- Table structure for user_emoji
-- ----------------------------
DROP TABLE IF EXISTS `user_emoji`;
CREATE TABLE `user_emoji`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
  `uid` bigint NOT NULL COMMENT '用户表ID',
  `expression_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '表情地址',
  `delete_status` int NOT NULL DEFAULT 0 COMMENT '逻辑删除(0-正常,1-删除)',
  `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `update_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `IDX_USER_EMOJIS_UID`(`uid` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户表情包' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user_emoji
-- ----------------------------

-- ----------------------------
-- Table structure for user_friend
-- ----------------------------
DROP TABLE IF EXISTS `user_friend`;
CREATE TABLE `user_friend`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
  `uid` bigint NOT NULL COMMENT 'uid',
  `friend_uid` bigint NOT NULL COMMENT '好友uid',
  `delete_status` tinyint(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除(0-正常,1-删除)',
  `mute_notification` tinyint(1) NOT NULL DEFAULT 0 COMMENT '免打扰',
  `hide_my_posts` tinyint(1) NOT NULL DEFAULT 0 COMMENT '不让他看我（0-允许，1-禁止）',
  `hide_their_posts` tinyint(1) NOT NULL DEFAULT 0 COMMENT '不看他（0-允许，1-禁止）',
  `remark` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '好友备注',
  `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `update_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_uid_friend_uid`(`uid` ASC, `friend_uid` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  INDEX `idx_update_time`(`update_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 215 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户联系人表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user_friend
-- ----------------------------

-- ----------------------------
-- Table structure for user_role
-- ----------------------------
DROP TABLE IF EXISTS `user_role`;
CREATE TABLE `user_role`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
  `uid` bigint NOT NULL COMMENT 'uid',
  `role_id` bigint NOT NULL COMMENT '角色id',
  `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `update_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_uid`(`uid` ASC) USING BTREE,
  INDEX `idx_role_id`(`role_id` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  INDEX `idx_update_time`(`update_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户角色关系表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user_role
-- ----------------------------

-- ----------------------------
-- Table structure for user_state
-- ----------------------------
DROP TABLE IF EXISTS `user_state`;
CREATE TABLE `user_state`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_ci NOT NULL COMMENT '状态名',
  `url` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_ci NOT NULL COMMENT '状态图标',
  `created_by` bigint NULL DEFAULT NULL COMMENT '创建者',
  `created_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `updated_by` bigint NULL DEFAULT NULL COMMENT '更新者',
  `updated_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `is_del` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 34 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_ci COMMENT = '聊天用户状态表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user_state
-- ----------------------------
INSERT INTO `user_state` VALUES (1, '在线', '/status/online.png', 1, '2025-02-17 11:39:49', NULL, NULL, 0);
INSERT INTO `user_state` VALUES (2, '离开', '/status/leave.png', 1, '2025-02-17 11:39:49', NULL, NULL, 0);
INSERT INTO `user_state` VALUES (3, '忙碌', '/status/busy.png', 1, '2025-02-17 11:39:49', NULL, NULL, 0);
INSERT INTO `user_state` VALUES (4, '请勿打扰', '/status/IonBan.png', 1, '2025-02-17 11:39:49', NULL, NULL, 0);
INSERT INTO `user_state` VALUES (5, '隐身', '/status/cloaking.png', 1, '2025-02-17 11:39:49', NULL, NULL, 0);
INSERT INTO `user_state` VALUES (6, '离线', '/status/offline.png', 1, '2025-02-17 11:39:49', NULL, NULL, 0);
INSERT INTO `user_state` VALUES (7, '今日天气', '/status/weather_3x.png', 1, '2025-02-17 11:39:49', NULL, NULL, 0);
INSERT INTO `user_state` VALUES (8, '一言难尽', '/status/hardtosay@3x.png', 1, '2025-02-17 11:39:49', NULL, NULL, 0);
INSERT INTO `user_state` VALUES (9, '我太难了', '/status/toohard@3x.png', 1, '2025-02-17 11:39:49', NULL, NULL, 0);
INSERT INTO `user_state` VALUES (10, '难得糊涂', '/status/nandehutu.png', 1, '2025-02-17 11:39:49', NULL, NULL, 0);
INSERT INTO `user_state` VALUES (11, '元气满满', '/status/fullofyuanqi@3x.png', 1, '2025-02-17 11:39:49', NULL, NULL, 0);
INSERT INTO `user_state` VALUES (12, '嗨到飞起', '/status/happytofly@3x.png', 1, '2025-02-17 11:39:49', NULL, NULL, 0);
INSERT INTO `user_state` VALUES (13, '水逆退散', '/status/luck@2x.png', 1, '2025-02-17 11:39:49', NULL, NULL, 0);
INSERT INTO `user_state` VALUES (14, '好运锦鲤', '/status/jinli@2x.png', 1, '2025-02-17 11:39:49', NULL, NULL, 0);
INSERT INTO `user_state` VALUES (15, '恋爱中', '/status/relationship_3x.png', 1, '2025-02-17 11:39:49', NULL, NULL, 0);
INSERT INTO `user_state` VALUES (16, '我crush了', '/status/crush.png', 1, '2025-02-17 11:39:49', NULL, NULL, 0);
INSERT INTO `user_state` VALUES (17, '被掏空', '/status/tkong.png', 1, '2025-02-17 11:39:49', NULL, NULL, 0);
INSERT INTO `user_state` VALUES (18, '听歌中', '/status/music@2x.png', 1, '2025-02-17 11:39:49', NULL, NULL, 0);
INSERT INTO `user_state` VALUES (19, '我没事', '/status/imfine_3x.png', 1, '2025-02-17 11:39:49', NULL, NULL, 0);
INSERT INTO `user_state` VALUES (20, '学习中', '/status/study_3x.png', 1, '2025-02-17 11:39:49', NULL, NULL, 0);
INSERT INTO `user_state` VALUES (21, '睡觉中', '/status/sleeping_3x.png', 1, '2025-02-17 11:39:49', NULL, NULL, 0);
INSERT INTO `user_state` VALUES (22, '搬砖中', '/status/banzhuan.png', 1, '2025-02-17 11:39:49', NULL, NULL, 0);
INSERT INTO `user_state` VALUES (23, '想静静', '/status/bequiet@3x.png', 1, '2025-02-17 11:39:49', NULL, NULL, 0);
INSERT INTO `user_state` VALUES (24, '运动中', '/status/yundongzhong@2x.png', 1, '2025-02-17 11:39:49', NULL, NULL, 0);
INSERT INTO `user_state` VALUES (25, '我想开了', '/status/woxiangkaile.png', 1, '2025-02-17 11:39:49', NULL, NULL, 0);
INSERT INTO `user_state` VALUES (26, '信号弱', '/status/signal_3x.png', 1, '2025-02-17 11:39:49', NULL, NULL, 0);
INSERT INTO `user_state` VALUES (27, '追剧中', '/status/tv_3x.png', 1, '2025-02-17 11:39:49', NULL, NULL, 0);
INSERT INTO `user_state` VALUES (28, '美滋滋', '/status/meizizi@3x.png', 1, '2025-02-17 11:39:49', NULL, NULL, 0);
INSERT INTO `user_state` VALUES (29, '摸鱼中', '/status/fish@2x.png', 1, '2025-02-17 11:39:49', NULL, NULL, 0);
INSERT INTO `user_state` VALUES (30, '无聊中', '/status/boring@3x.png', 1, '2025-02-17 11:39:49', NULL, NULL, 0);
INSERT INTO `user_state` VALUES (31, '悠哉哉', '/status/youzaizai@3x.png', 1, '2025-02-17 11:39:49', NULL, NULL, 0);
INSERT INTO `user_state` VALUES (32, '去旅行', '/status/gototravel.png', 1, '2025-02-17 11:39:49', NULL, NULL, 0);
INSERT INTO `user_state` VALUES (33, '游戏中', '/status/game_3x.png', 1, '2025-02-17 11:39:49', NULL, NULL, 0);

-- ----------------------------
-- Table structure for user_target_rel
-- ----------------------------
DROP TABLE IF EXISTS `user_target_rel`;
CREATE TABLE `user_target_rel`  (
  `id` bigint NOT NULL,
  `u_id` bigint NOT NULL COMMENT '人员id',
  `friend_id` bigint NOT NULL COMMENT '被绑定的人员id',
  `target_id` bigint NOT NULL COMMENT '标签id',
  `created_by` bigint NULL DEFAULT NULL COMMENT '创建者',
  `created_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `updated_by` bigint NULL DEFAULT NULL COMMENT '更新者',
  `updated_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `is_del` tinyint(1) NOT NULL COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `employee`(`u_id` ASC, `friend_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_ci COMMENT = '人员标签关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user_target_rel
-- ----------------------------

-- ----------------------------
-- Table structure for worker_node
-- ----------------------------
DROP TABLE IF EXISTS `worker_node`;
CREATE TABLE `worker_node`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'auto;increment id',
  `host_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '主机名',
  `port` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '端口',
  `type` int NOT NULL COMMENT '节点类型:;ACTUAL 或者 CONTAINER',
  `launch_date` date NOT NULL COMMENT '上线日期',
  `modified` timestamp NULL DEFAULT NULL COMMENT '修改时间',
  `created` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 23 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'DB;WorkerID Assigner for UID Generator' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of worker_node
-- ----------------------------
INSERT INTO `worker_node` VALUES (1, '192.168.1.105', '1740637533651-32328', 2, '2025-02-27', '2025-02-27 14:25:34', '2025-02-27 14:25:34');
INSERT INTO `worker_node` VALUES (2, '192.168.1.105', '1740641806048-38176', 2, '2025-02-27', '2025-02-27 15:36:46', '2025-02-27 15:36:46');
INSERT INTO `worker_node` VALUES (3, '192.168.1.105', '1740641885434-70741', 2, '2025-02-27', '2025-02-27 15:38:05', '2025-02-27 15:38:05');
INSERT INTO `worker_node` VALUES (4, '192.168.1.105', '1740642318943-77237', 2, '2025-02-27', '2025-02-27 15:45:19', '2025-02-27 15:45:19');
INSERT INTO `worker_node` VALUES (5, '192.168.1.105', '1740645602761-28265', 2, '2025-02-27', '2025-02-27 16:40:03', '2025-02-27 16:40:03');
INSERT INTO `worker_node` VALUES (6, '192.168.1.105', '1740645676926-31636', 2, '2025-02-27', '2025-02-27 16:41:17', '2025-02-27 16:41:17');
INSERT INTO `worker_node` VALUES (7, '192.168.1.105', '1740649171235-18216', 2, '2025-02-27', '2025-02-27 17:39:31', '2025-02-27 17:39:31');
INSERT INTO `worker_node` VALUES (8, '192.168.1.105', '1740650592465-20161', 2, '2025-02-27', '2025-02-27 18:03:12', '2025-02-27 18:03:12');
INSERT INTO `worker_node` VALUES (9, '192.168.1.105', '1740651241906-67540', 2, '2025-02-27', '2025-02-27 18:14:02', '2025-02-27 18:14:02');
INSERT INTO `worker_node` VALUES (10, '192.168.1.105', '1740911912923-43299', 2, '2025-03-02', '2025-03-02 18:38:33', '2025-03-02 18:38:33');
INSERT INTO `worker_node` VALUES (11, '192.168.1.105', '1740974696482-23085', 2, '2025-03-03', '2025-03-03 12:04:56', '2025-03-03 12:04:56');
INSERT INTO `worker_node` VALUES (12, '192.168.1.105', '1740974744186-69746', 2, '2025-03-03', '2025-03-03 12:05:44', '2025-03-03 12:05:44');
INSERT INTO `worker_node` VALUES (13, '192.168.1.105', '1740976190991-80068', 2, '2025-03-03', '2025-03-03 12:29:51', '2025-03-03 12:29:51');
INSERT INTO `worker_node` VALUES (14, '192.168.1.105', '1740976446395-17497', 2, '2025-03-03', '2025-03-03 12:34:06', '2025-03-03 12:34:06');
INSERT INTO `worker_node` VALUES (15, '192.168.1.105', '1740976825093-79338', 2, '2025-03-03', '2025-03-03 12:40:25', '2025-03-03 12:40:25');
INSERT INTO `worker_node` VALUES (16, '192.168.1.105', '1740976869970-90188', 2, '2025-03-03', '2025-03-03 12:41:10', '2025-03-03 12:41:10');
INSERT INTO `worker_node` VALUES (17, '192.168.1.105', '1740977125946-60633', 2, '2025-03-03', '2025-03-03 12:45:26', '2025-03-03 12:45:26');
INSERT INTO `worker_node` VALUES (18, '192.168.1.105', '1740977161406-14512', 2, '2025-03-03', '2025-03-03 12:46:01', '2025-03-03 12:46:01');
INSERT INTO `worker_node` VALUES (19, '192.168.1.105', '1740979045821-69473', 2, '2025-03-03', '2025-03-03 13:17:26', '2025-03-03 13:17:26');
INSERT INTO `worker_node` VALUES (20, '192.168.1.105', '1740980020945-56302', 2, '2025-03-03', '2025-03-03 13:33:41', '2025-03-03 13:33:41');
INSERT INTO `worker_node` VALUES (21, '192.168.1.105', '1740994972856-83727', 2, '2025-03-03', '2025-03-03 17:42:53', '2025-03-03 17:42:53');
INSERT INTO `worker_node` VALUES (22, '192.168.1.105', '1740997288040-43007', 2, '2025-03-03', '2025-03-03 18:21:28', '2025-03-03 18:21:28');

-- ----------------------------
-- Table structure for wx_msg
-- ----------------------------
DROP TABLE IF EXISTS `wx_msg`;
CREATE TABLE `wx_msg`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
  `open_id` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '微信openid用户标识',
  `msg` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户消息',
  `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `update_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_open_id`(`open_id` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  INDEX `idx_update_time`(`update_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '微信消息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wx_msg
-- ----------------------------

SET FOREIGN_KEY_CHECKS = 1;
