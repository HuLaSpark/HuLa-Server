/*
 Navicat Premium Data Transfer

 Source Server         : mysql8.0本地连接
 Source Server Type    : MySQL
 Source Server Version : 80035
 Source Host           : localhost:3306
 Source Schema         : hula-im

 Target Server Type    : MySQL
 Target Server Version : 80035
 File Encoding         : 65001

 Date: 10/07/2024 15:09:46
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

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
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '黑名单' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of black
-- ----------------------------

-- ----------------------------
-- Table structure for contact
-- ----------------------------
DROP TABLE IF EXISTS `contact`;
CREATE TABLE `contact`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
  `uid` bigint NOT NULL COMMENT 'uid',
  `room_id` bigint NOT NULL COMMENT '房间id',
  `read_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '阅读到的时间',
  `active_time` datetime(3) NULL DEFAULT NULL COMMENT '会话内消息最后更新的时间(只有普通会话需要维护，全员会话不需要维护)',
  `last_msg_id` bigint NULL DEFAULT NULL COMMENT '会话最新消息id',
  `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `update_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uniq_uid_room_id`(`uid` ASC, `room_id` ASC) USING BTREE,
  INDEX `idx_room_id_read_time`(`room_id` ASC, `read_time` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  INDEX `idx_update_time`(`update_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '会话列表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of contact
-- ----------------------------
INSERT INTO `contact` VALUES (1, 20000, 1, '2024-07-10 14:54:31.314', NULL, NULL, '2024-07-10 14:54:31.314', '2024-07-10 15:09:05.459');

-- ----------------------------
-- Table structure for group_member
-- ----------------------------
DROP TABLE IF EXISTS `group_member`;
CREATE TABLE `group_member`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
  `group_id` bigint NOT NULL COMMENT '群组id',
  `uid` bigint NOT NULL COMMENT '成员uid',
  `role` int NOT NULL COMMENT '成员角色 1群主 2管理员 3普通成员',
  `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `update_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_group_id_role`(`group_id` ASC, `role` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  INDEX `idx_update_time`(`update_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '群成员表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of group_member
-- ----------------------------

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
INSERT INTO `item_config` VALUES (2, 2, 'http://localhost:9000/hula/badge/like.png', '爆赞徽章，单条消息被点赞超过10次，即可获得', '2023-05-07 17:50:31.090', '2024-05-15 23:49:33.476');
INSERT INTO `item_config` VALUES (3, 2, 'http://localhost:9000/hula/badge/top10.png ', 'HuLa前10名注册的用户才能获得的专属徽章', '2023-05-07 17:50:31.100', '2024-05-15 22:41:47.889');
INSERT INTO `item_config` VALUES (4, 2, 'http://localhost:9000/hula/badge/top100.png', 'HuLa前100名注册的用户才能获得的专属徽章', '2023-05-07 17:50:31.109', '2024-05-15 23:49:42.398');
INSERT INTO `item_config` VALUES (5, 2, 'http://localhost:9000/hula/badge/planet.png', 'HuLa知识星球成员的专属徽章', '2023-05-07 17:50:31.109', '2024-05-15 23:50:00.463');
INSERT INTO `item_config` VALUES (6, 2, 'http://localhost:9000/hula/badge/active.png', 'HuLa项目contributor专属徽章', '2023-05-07 17:50:31.109', '2024-05-15 23:50:20.164');

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
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '消息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of message
-- ----------------------------

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
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '消息标记表' ROW_FORMAT = DYNAMIC;

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
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '角色表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of role
-- ----------------------------
INSERT INTO `role` VALUES (1, '超级管理员', '2024-07-10 11:17:15.089', '2024-07-10 11:17:15.089');
INSERT INTO `role` VALUES (2, '抹茶群聊管理员', '2024-07-10 11:17:15.091', '2024-07-10 11:17:15.091');

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
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '房间表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of room
-- ----------------------------
INSERT INTO `room` VALUES (1, 1, 1, '2024-07-10 11:17:15.521', NULL, NULL, '2024-07-10 11:17:15.521', '2024-07-10 11:17:15.521');

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
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '单聊房间表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of room_friend
-- ----------------------------

-- ----------------------------
-- Table structure for room_group
-- ----------------------------
DROP TABLE IF EXISTS `room_group`;
CREATE TABLE `room_group`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
  `room_id` bigint NOT NULL COMMENT '房间id',
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
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '群聊房间表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of room_group
-- ----------------------------
INSERT INTO `room_group` VALUES (1, 1, '抹茶全员群', 'https://mallchat.cn/assets/logo-e81cd252.jpeg', NULL, 0, '2024-07-10 11:17:15.523', '2024-07-10 11:17:15.523');

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
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '本地消息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of secure_invoke_record
-- ----------------------------

-- ----------------------------
-- Table structure for sensitive_word
-- ----------------------------
DROP TABLE IF EXISTS `sensitive_word`;
CREATE TABLE `sensitive_word`  (
  `word` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '敏感词'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '敏感词库' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sensitive_word
-- ----------------------------
INSERT INTO `sensitive_word` VALUES ('TMD');
INSERT INTO `sensitive_word` VALUES ('tmd');

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '用户id',
  `account` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '用户账号',
  `password` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '用户密码',
  `name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '用户昵称',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '用户头像',
  `sex` int NULL DEFAULT NULL COMMENT '性别 1为男性，2为女性',
  `open_id` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '微信openid用户标识',
  `active_status` int NULL DEFAULT 2 COMMENT '在线状态 1在线 2离线',
  `last_opt_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '最后上下线时间',
  `ip_info` json NULL COMMENT 'ip信息',
  `item_id` bigint NULL DEFAULT NULL COMMENT '佩戴的徽章id',
  `status` int NULL DEFAULT 0 COMMENT '使用状态 0.正常 1拉黑',
  `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `update_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uniq_account`(`account` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  INDEX `idx_update_time`(`update_time` ASC) USING BTREE,
  INDEX `idx_active_status_last_opt_time`(`active_status` ASC, `last_opt_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 20001 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, 'admin','admin','系统消息', 'http://mms1.baidu.com/it/u=1979830414,2984779047&fm=253&app=138&f=JPEG&fmt=auto&q=75?w=500&h=500', NULL, '0', 2, '2023-07-01 11:58:24.605', NULL, NULL, 0, '2023-07-01 11:58:24.605', '2023-07-01 12:02:56.900');
INSERT INTO `user` VALUES (10001,'hula1','hula1', 'ChatGPT', 'https://img1.baidu.com/it/u=3613958228,3522035000&fm=253&fmt=auto&app=120&f=JPEG?w=500&h=500', 0, '??', 2, '2023-06-29 17:03:03.357', NULL, NULL, 0, '2023-06-29 17:03:03.357', '2023-07-01 14:56:10.271');
INSERT INTO `user` VALUES (10002,'hula2','hula2', 'ChatGLM2', 'http://mms1.baidu.com/it/u=1979830414,2984779047&fm=253&app=138&f=JPEG&fmt=auto&q=75?w=500&h=500', NULL, '450', 2, '2023-07-01 11:58:24.605', NULL, NULL, 0, '2023-07-01 11:58:24.605', '2023-07-01 12:02:56.900');
INSERT INTO `user` VALUES (20000,'hula3','hula3', 'Dawn', 'https://thirdwx.qlogo.cn/mmopen/vi_32/PiajxSqBRaEL7OpdTZYkEeE9oTmZFKL4gIzCr1ibf38OiaPqqcmqlLxTxvw3gskZV5uTma7NSQzCk8yVbIiaN6FV3kmicBWg2CLOKicysib6mDFGEPprQxfUYEupA/132', 0, 'obxlc6xY1EsMyHbbRAAgWDCnOFHE', 1, '2024-07-10 15:09:09.086', '{\"createIp\": \"127.0.0.1\", \"updateIp\": \"127.0.0.1\", \"createIpDetail\": null, \"updateIpDetail\": null}', NULL, 0, '2024-07-10 15:08:00.607', '2024-07-10 15:09:09.096');

-- ----------------------------
-- Table structure for user_apply
-- ----------------------------
DROP TABLE IF EXISTS `user_apply`;
CREATE TABLE `user_apply`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
  `uid` bigint NOT NULL COMMENT '申请人uid',
  `type` int NOT NULL COMMENT '申请类型 1加好友',
  `target_id` bigint NOT NULL COMMENT '接收人uid',
  `msg` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '申请信息',
  `status` int NOT NULL COMMENT '申请状态 1待审批 2同意',
  `read_status` int NOT NULL COMMENT '阅读状态 1未读 2已读',
  `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `update_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '修改时间',
  `deleted` tinyint default 0 NOT NULL  COMMENT '删除状态 0：未删 1 申请人删除 2 被申请人删除 3都删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_uid_target_id`(`uid` ASC, `target_id` ASC) USING BTREE,
  INDEX `idx_target_id_read_status`(`target_id` ASC, `read_status` ASC) USING BTREE,
  INDEX `idx_target_id`(`target_id` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  INDEX `idx_update_time`(`update_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户申请表' ROW_FORMAT = Dynamic;

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
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户背包表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user_backpack
-- ----------------------------
INSERT INTO `user_backpack` VALUES (1, 20000, 1, 0, '1_1_20000', '2024-07-10 11:20:32.386', '2024-07-10 11:20:32.386');
INSERT INTO `user_backpack` VALUES (2, 20000, 3, 0, '3_1_20000', '2024-07-10 11:20:32.415', '2024-07-10 11:20:32.415');

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
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户表情包' ROW_FORMAT = DYNAMIC;

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
  `delete_status` int NOT NULL DEFAULT 0 COMMENT '逻辑删除(0-正常,1-删除)',
  `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `update_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_uid_friend_uid`(`uid` ASC, `friend_uid` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  INDEX `idx_update_time`(`update_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户联系人表' ROW_FORMAT = Dynamic;

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
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户角色关系表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_role
-- ----------------------------

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
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '微信消息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wx_msg
-- ----------------------------

SET FOREIGN_KEY_CHECKS = 1;
