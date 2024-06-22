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

 Date: 18/06/2024 15:22:20
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
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '黑名单' ROW_FORMAT = DYNAMIC;

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
) ENGINE = InnoDB AUTO_INCREMENT = 425 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '会话列表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of contact
-- ----------------------------
INSERT INTO `contact` VALUES (1, 20000, 1, '2024-06-18 10:45:46.377', NULL, NULL, '2024-05-15 09:41:24.582', '2024-06-18 10:45:46.379');
INSERT INTO `contact` VALUES (2, 20001, 1, '2024-06-13 09:04:43.671', NULL, NULL, '2024-05-17 12:12:14.047', '2024-06-13 09:04:43.674');
INSERT INTO `contact` VALUES (3, 20000, 2, '2024-06-18 10:45:24.293', '2024-06-18 10:39:37.699', 826, '2024-05-18 12:12:02.945', '2024-06-18 10:45:24.295');
INSERT INTO `contact` VALUES (4, 20001, 2, '2024-06-13 09:04:24.944', '2024-06-18 10:39:37.699', 826, '2024-05-18 12:12:02.945', '2024-06-18 10:39:37.742');

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
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '群成员表' ROW_FORMAT = Dynamic;

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
) ENGINE = InnoDB AUTO_INCREMENT = 829 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '消息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of message
-- ----------------------------
INSERT INTO `message` VALUES (1, 1, 20000, '你们好！', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 12:09:04.242', '2024-05-17 12:09:04.248');
INSERT INTO `message` VALUES (2, 1, 20001, '干', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 12:12:13.660', '2024-05-17 12:12:13.666');
INSERT INTO `message` VALUES (3, 1, 20001, '干什么', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 12:12:43.079', '2024-05-17 12:12:43.087');
INSERT INTO `message` VALUES (4, 1, 20000, '干什么', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 12:12:47.968', '2024-05-17 12:12:47.972');
INSERT INTO `message` VALUES (5, 1, 20000, '😃', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 12:13:14.040', '2024-05-17 12:13:14.044');
INSERT INTO `message` VALUES (6, 1, 20000, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 12:14:01.441', '2024-05-17 12:14:01.450');
INSERT INTO `message` VALUES (7, 1, 20001, '干', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 12:14:33.119', '2024-05-17 12:14:33.122');
INSERT INTO `message` VALUES (8, 1, 20001, '干噶', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 12:15:09.573', '2024-05-17 12:15:09.582');
INSERT INTO `message` VALUES (9, 1, 20001, '看看', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 12:16:07.355', '2024-05-17 12:16:07.361');
INSERT INTO `message` VALUES (10, 1, 20001, '你看看', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 12:16:42.314', '2024-05-17 12:16:42.324');
INSERT INTO `message` VALUES (11, 1, 20001, '😀', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 12:17:56.005', '2024-05-17 12:17:56.014');
INSERT INTO `message` VALUES (12, 1, 20001, '什么鬼', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 12:18:58.439', '2024-05-17 12:18:58.444');
INSERT INTO `message` VALUES (13, 1, 20001, '🫥', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 12:19:12.360', '2024-05-17 12:19:12.365');
INSERT INTO `message` VALUES (14, 1, 20001, '再试试看', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 12:19:58.757', '2024-05-17 12:19:58.762');
INSERT INTO `message` VALUES (15, 1, 20000, '好', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 12:37:37.583', '2024-05-17 12:37:37.590');
INSERT INTO `message` VALUES (16, 1, 20000, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 12:37:57.980', '2024-05-17 12:37:57.983');
INSERT INTO `message` VALUES (17, 1, 20000, '嘟嘟', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 12:38:09.185', '2024-05-17 12:38:09.189');
INSERT INTO `message` VALUES (18, 1, 20001, '法克', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 13:46:23.565', '2024-05-17 13:46:23.577');
INSERT INTO `message` VALUES (19, 1, 20000, '🤗', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 16:24:51.523', '2024-05-17 16:24:51.565');
INSERT INTO `message` VALUES (20, 1, 20001, '还行', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 16:49:05.353', '2024-05-17 16:49:05.358');
INSERT INTO `message` VALUES (21, 1, 20001, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 16:50:01.577', '2024-05-17 16:50:01.582');
INSERT INTO `message` VALUES (22, 1, 20001, '男男女女', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 16:56:07.864', '2024-05-17 16:56:07.872');
INSERT INTO `message` VALUES (23, 1, 20001, '哈哈哈哈', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 16:56:12.410', '2024-05-17 16:56:12.413');
INSERT INTO `message` VALUES (24, 1, 20001, '她她她', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 16:56:37.244', '2024-05-17 16:56:37.248');
INSERT INTO `message` VALUES (25, 1, 20001, '消失了', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 16:56:48.147', '2024-05-17 16:56:48.151');
INSERT INTO `message` VALUES (26, 1, 20001, '还行', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 16:57:14.591', '2024-05-17 16:57:14.596');
INSERT INTO `message` VALUES (27, 1, 20001, '步步', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 17:05:06.394', '2024-05-17 17:05:06.401');
INSERT INTO `message` VALUES (28, 1, 20001, '一个', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 17:05:11.240', '2024-05-17 17:05:11.244');
INSERT INTO `message` VALUES (29, 1, 20001, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 17:05:26.898', '2024-05-17 17:05:26.903');
INSERT INTO `message` VALUES (30, 1, 20000, 'ttt ', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 17:05:40.170', '2024-05-17 17:05:40.174');
INSERT INTO `message` VALUES (31, 1, 20001, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 17:09:17.441', '2024-05-17 17:09:17.448');
INSERT INTO `message` VALUES (32, 1, 20001, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 17:09:20.413', '2024-05-17 17:09:20.417');
INSERT INTO `message` VALUES (33, 1, 20001, '看看', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 17:13:08.753', '2024-05-17 17:13:08.758');
INSERT INTO `message` VALUES (34, 1, 20001, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 17:13:52.086', '2024-05-17 17:13:52.091');
INSERT INTO `message` VALUES (35, 1, 20001, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 17:14:04.215', '2024-05-17 17:14:04.221');
INSERT INTO `message` VALUES (36, 1, 20001, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 17:14:04.983', '2024-05-17 17:14:04.989');
INSERT INTO `message` VALUES (37, 1, 20001, 'fkl', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 17:14:36.199', '2024-05-17 17:14:36.203');
INSERT INTO `message` VALUES (38, 1, 20001, '斤斤计较', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 17:16:57.275', '2024-05-17 17:16:57.281');
INSERT INTO `message` VALUES (39, 1, 20001, '打算', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 17:18:01.409', '2024-05-17 17:18:01.415');
INSERT INTO `message` VALUES (40, 1, 20001, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 17:21:06.600', '2024-05-17 17:21:06.610');
INSERT INTO `message` VALUES (41, 1, 20001, '啦啦啦', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 17:21:15.177', '2024-05-17 17:21:15.181');
INSERT INTO `message` VALUES (42, 1, 20001, '看看', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 17:21:23.528', '2024-05-17 17:21:23.532');
INSERT INTO `message` VALUES (43, 1, 20001, '哈哈哈', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 17:23:13.555', '2024-05-17 17:23:13.564');
INSERT INTO `message` VALUES (44, 1, 20001, '😀', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 17:23:20.958', '2024-05-17 17:23:20.963');
INSERT INTO `message` VALUES (45, 1, 20001, '啊', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 17:23:23.916', '2024-05-17 17:23:23.922');
INSERT INTO `message` VALUES (46, 1, 20001, '你说啥', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 17:23:26.928', '2024-05-17 17:23:26.938');
INSERT INTO `message` VALUES (47, 1, 20001, '快点出发', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 17:23:32.333', '2024-05-17 17:23:32.337');
INSERT INTO `message` VALUES (48, 1, 20000, '什么东西', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 17:23:47.188', '2024-05-17 17:23:47.192');
INSERT INTO `message` VALUES (49, 1, 20000, '出来了吗', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 17:23:52.213', '2024-05-17 17:23:52.218');
INSERT INTO `message` VALUES (50, 1, 20001, '😀', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 17:25:51.200', '2024-05-17 17:25:51.207');
INSERT INTO `message` VALUES (51, 1, 20001, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 17:25:53.582', '2024-05-17 17:25:53.586');
INSERT INTO `message` VALUES (52, 1, 20001, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 17:25:56.770', '2024-05-17 17:25:56.775');
INSERT INTO `message` VALUES (53, 1, 20001, '😀', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 17:26:03.218', '2024-05-17 17:26:03.222');
INSERT INTO `message` VALUES (54, 1, 20001, '😘', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 17:26:08.311', '2024-05-17 17:26:08.315');
INSERT INTO `message` VALUES (55, 1, 20001, '爱你', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 17:29:59.539', '2024-05-17 17:29:59.546');
INSERT INTO `message` VALUES (56, 1, 20001, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 17:30:01.881', '2024-05-17 17:30:01.885');
INSERT INTO `message` VALUES (57, 1, 20001, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 17:30:04.407', '2024-05-17 17:30:04.412');
INSERT INTO `message` VALUES (58, 1, 20001, '😘', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 17:30:08.459', '2024-05-17 17:30:08.462');
INSERT INTO `message` VALUES (59, 1, 20001, '🫡', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 17:30:12.608', '2024-05-17 17:30:12.615');
INSERT INTO `message` VALUES (60, 1, 20001, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 17:31:00.221', '2024-05-17 17:31:00.226');
INSERT INTO `message` VALUES (61, 1, 20001, '阿迪斯', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 17:31:05.518', '2024-05-17 17:31:05.522');
INSERT INTO `message` VALUES (62, 1, 20001, '的撒', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 17:31:09.552', '2024-05-17 17:31:09.556');
INSERT INTO `message` VALUES (63, 1, 20000, '的撒', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 17:31:18.692', '2024-05-17 17:31:18.695');
INSERT INTO `message` VALUES (64, 1, 20001, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 17:31:22.302', '2024-05-17 17:31:22.307');
INSERT INTO `message` VALUES (65, 1, 20001, '萨达', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 17:31:25.830', '2024-05-17 17:31:25.835');
INSERT INTO `message` VALUES (66, 1, 20001, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 17:31:46.395', '2024-05-17 17:31:46.398');
INSERT INTO `message` VALUES (67, 1, 20001, '打算', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 17:31:49.084', '2024-05-17 17:31:49.088');
INSERT INTO `message` VALUES (68, 1, 20001, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 17:31:51.665', '2024-05-17 17:31:51.670');
INSERT INTO `message` VALUES (69, 1, 20001, 'dsa的', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 17:31:53.094', '2024-05-17 17:31:53.099');
INSERT INTO `message` VALUES (70, 1, 20001, '萨达', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 17:31:56.925', '2024-05-17 17:31:56.929');
INSERT INTO `message` VALUES (71, 1, 20001, '还行', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 17:32:10.209', '2024-05-17 17:32:10.212');
INSERT INTO `message` VALUES (72, 1, 20001, '出来了吗', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 17:32:13.993', '2024-05-17 17:32:13.999');
INSERT INTO `message` VALUES (73, 1, 20001, '的撒', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 17:32:54.121', '2024-05-17 17:32:54.126');
INSERT INTO `message` VALUES (74, 1, 20001, '出来了', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 17:32:57.133', '2024-05-17 17:32:57.138');
INSERT INTO `message` VALUES (75, 1, 20001, '从', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 17:33:02.925', '2024-05-17 17:33:02.929');
INSERT INTO `message` VALUES (76, 1, 20001, '从', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 17:33:05.847', '2024-05-17 17:33:05.851');
INSERT INTO `message` VALUES (77, 1, 20001, 'c', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 17:33:07.243', '2024-05-17 17:33:07.249');
INSERT INTO `message` VALUES (78, 1, 20001, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 17:33:10.772', '2024-05-17 17:33:10.776');
INSERT INTO `message` VALUES (79, 1, 20001, '花花', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 17:33:48.475', '2024-05-17 17:33:48.479');
INSERT INTO `message` VALUES (80, 1, 20001, '画虎', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 17:33:50.743', '2024-05-17 17:33:50.747');
INSERT INTO `message` VALUES (81, 1, 20001, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 17:33:56.075', '2024-05-17 17:33:56.079');
INSERT INTO `message` VALUES (82, 1, 20001, '天地', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 17:34:48.233', '2024-05-17 17:34:48.240');
INSERT INTO `message` VALUES (83, 1, 20001, '异地', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 17:34:50.003', '2024-05-17 17:34:50.007');
INSERT INTO `message` VALUES (84, 1, 20001, 'zhen\'kai', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 17:34:52.451', '2024-05-17 17:34:52.456');
INSERT INTO `message` VALUES (85, 1, 20001, '1123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 17:35:46.971', '2024-05-17 17:35:46.976');
INSERT INTO `message` VALUES (86, 1, 20001, '的', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 17:35:54.492', '2024-05-17 17:35:54.497');
INSERT INTO `message` VALUES (87, 1, 20001, '好', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 17:37:06.876', '2024-05-17 17:37:06.884');
INSERT INTO `message` VALUES (88, 1, 20001, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 17:37:08.201', '2024-05-17 17:37:08.205');
INSERT INTO `message` VALUES (89, 1, 20001, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 17:37:09.380', '2024-05-17 17:37:09.385');
INSERT INTO `message` VALUES (90, 1, 20001, '🫡', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 17:37:13.956', '2024-05-17 17:37:13.964');
INSERT INTO `message` VALUES (91, 1, 20001, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 17:37:18.984', '2024-05-17 17:37:18.988');
INSERT INTO `message` VALUES (92, 1, 20001, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 17:37:28.227', '2024-05-17 17:37:28.231');
INSERT INTO `message` VALUES (93, 1, 20001, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 17:37:29.466', '2024-05-17 17:37:29.470');
INSERT INTO `message` VALUES (94, 1, 20001, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 17:37:32.655', '2024-05-17 17:37:32.659');
INSERT INTO `message` VALUES (95, 1, 20001, 'ka\'k\'k', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 17:37:39.280', '2024-05-17 17:37:39.283');
INSERT INTO `message` VALUES (96, 1, 20001, '蝴蝶卡', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 17:38:33.368', '2024-05-17 17:38:33.374');
INSERT INTO `message` VALUES (97, 1, 20001, '哈喽', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 17:38:35.093', '2024-05-17 17:38:35.097');
INSERT INTO `message` VALUES (98, 1, 20001, '哈萨克和的', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 17:38:36.571', '2024-05-17 17:38:36.575');
INSERT INTO `message` VALUES (99, 1, 20001, '的撒', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 17:38:39.877', '2024-05-17 17:38:39.881');
INSERT INTO `message` VALUES (100, 1, 20001, '大家撒看', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 17:38:42.748', '2024-05-17 17:38:42.752');
INSERT INTO `message` VALUES (101, 1, 20001, '打算', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 17:38:46.341', '2024-05-17 17:38:46.345');
INSERT INTO `message` VALUES (102, 1, 20001, '打算', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 17:38:57.808', '2024-05-17 17:38:57.812');
INSERT INTO `message` VALUES (103, 1, 20001, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 17:38:59.010', '2024-05-17 17:38:59.014');
INSERT INTO `message` VALUES (104, 1, 20001, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 17:39:18.312', '2024-05-17 17:39:18.316');
INSERT INTO `message` VALUES (105, 1, 20001, '初级', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 17:39:23.974', '2024-05-17 17:39:23.979');
INSERT INTO `message` VALUES (106, 1, 20001, '11', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 17:42:19.227', '2024-05-17 17:42:19.234');
INSERT INTO `message` VALUES (107, 1, 20001, '11', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 17:42:20.956', '2024-05-17 17:42:20.959');
INSERT INTO `message` VALUES (108, 1, 20001, '1', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 17:42:23.010', '2024-05-17 17:42:23.014');
INSERT INTO `message` VALUES (109, 1, 20001, '1', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 17:42:24.404', '2024-05-17 17:42:24.408');
INSERT INTO `message` VALUES (110, 1, 20001, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 17:45:21.964', '2024-05-17 17:45:21.972');
INSERT INTO `message` VALUES (111, 1, 20001, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 17:45:23.564', '2024-05-17 17:45:23.567');
INSERT INTO `message` VALUES (112, 1, 20001, '你好', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 17:45:26.189', '2024-05-17 17:45:26.193');
INSERT INTO `message` VALUES (113, 1, 20001, '霓虹', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 17:47:14.189', '2024-05-17 17:47:14.197');
INSERT INTO `message` VALUES (114, 1, 20001, '你会', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 17:47:19.491', '2024-05-17 17:47:19.496');
INSERT INTO `message` VALUES (115, 1, 20001, '看看', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 17:47:22.354', '2024-05-17 17:47:22.358');
INSERT INTO `message` VALUES (116, 1, 20001, '看看', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 17:47:23.643', '2024-05-17 17:47:23.647');
INSERT INTO `message` VALUES (117, 1, 20001, '再看一下', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 17:47:53.990', '2024-05-17 17:47:53.995');
INSERT INTO `message` VALUES (118, 1, 20001, '看看', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 17:47:56.486', '2024-05-17 17:47:56.489');
INSERT INTO `message` VALUES (119, 1, 20001, '有什么de', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 17:47:59.021', '2024-05-17 17:47:59.026');
INSERT INTO `message` VALUES (120, 1, 20001, '为什么会这样你饿', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 17:48:06.834', '2024-05-17 17:48:06.838');
INSERT INTO `message` VALUES (121, 1, 20001, '为什么会出现这样', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 17:48:13.439', '2024-05-17 17:48:13.443');
INSERT INTO `message` VALUES (122, 1, 20001, '？', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 17:48:19.931', '2024-05-17 17:48:19.936');
INSERT INTO `message` VALUES (123, 1, 20001, '现在n', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 17:49:05.548', '2024-05-17 17:49:05.553');
INSERT INTO `message` VALUES (124, 1, 20001, '是吗', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 17:49:07.984', '2024-05-17 17:49:07.989');
INSERT INTO `message` VALUES (125, 1, 20001, '？', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 17:49:20.220', '2024-05-17 17:49:20.225');
INSERT INTO `message` VALUES (126, 1, 20001, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 17:51:39.472', '2024-05-17 17:51:39.479');
INSERT INTO `message` VALUES (127, 1, 20001, '什么', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 18:12:46.136', '2024-05-17 18:12:46.144');
INSERT INTO `message` VALUES (128, 1, 20001, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 18:12:53.025', '2024-05-17 18:12:53.029');
INSERT INTO `message` VALUES (129, 1, 20001, '12344444', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 18:12:56.269', '2024-05-17 18:12:56.273');
INSERT INTO `message` VALUES (130, 1, 20001, '出现了', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 18:12:59.272', '2024-05-17 18:12:59.277');
INSERT INTO `message` VALUES (131, 1, 20001, '看看', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 18:13:09.887', '2024-05-17 18:13:09.891');
INSERT INTO `message` VALUES (132, 1, 20001, '怎么', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 18:13:14.069', '2024-05-17 18:13:14.074');
INSERT INTO `message` VALUES (133, 1, 20001, '😀', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 18:13:19.792', '2024-05-17 18:13:19.796');
INSERT INTO `message` VALUES (134, 1, 20001, '😆', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 18:35:05.318', '2024-05-17 18:35:05.329');
INSERT INTO `message` VALUES (135, 1, 20001, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 19:16:17.473', '2024-05-17 19:16:17.479');
INSERT INTO `message` VALUES (136, 1, 20001, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 19:16:21.214', '2024-05-17 19:16:21.218');
INSERT INTO `message` VALUES (137, 1, 20001, '消息', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 19:16:24.676', '2024-05-17 19:16:24.681');
INSERT INTO `message` VALUES (138, 1, 20001, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 19:21:50.585', '2024-05-17 19:21:50.591');
INSERT INTO `message` VALUES (139, 1, 20001, '让人', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 19:22:01.356', '2024-05-17 19:22:01.362');
INSERT INTO `message` VALUES (140, 1, 20001, '😀', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 19:22:28.136', '2024-05-17 19:22:28.140');
INSERT INTO `message` VALUES (141, 1, 20001, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 19:26:22.158', '2024-05-17 19:26:22.165');
INSERT INTO `message` VALUES (142, 1, 20001, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 19:26:25.078', '2024-05-17 19:26:25.082');
INSERT INTO `message` VALUES (143, 1, 20001, '你好', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 19:27:21.464', '2024-05-17 19:27:21.469');
INSERT INTO `message` VALUES (144, 1, 20001, '的', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 19:27:32.331', '2024-05-17 19:27:32.335');
INSERT INTO `message` VALUES (145, 1, 20001, '好', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 19:27:55.150', '2024-05-17 19:27:55.155');
INSERT INTO `message` VALUES (146, 1, 20001, 'hao\'d', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 19:29:05.445', '2024-05-17 19:29:05.458');
INSERT INTO `message` VALUES (147, 1, 20001, '反思了', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 19:38:51.021', '2024-05-17 19:38:51.030');
INSERT INTO `message` VALUES (148, 1, 20001, 'shu\'ba', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 19:38:54.952', '2024-05-17 19:38:54.956');
INSERT INTO `message` VALUES (149, 1, 20001, '书吧', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 19:38:58.467', '2024-05-17 19:38:58.472');
INSERT INTO `message` VALUES (150, 1, 20001, '书吧', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 19:39:12.375', '2024-05-17 19:39:12.384');
INSERT INTO `message` VALUES (151, 1, 20001, '输掉', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 19:39:16.189', '2024-05-17 19:39:16.195');
INSERT INTO `message` VALUES (152, 1, 20001, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 19:39:30.838', '2024-05-17 19:39:30.843');
INSERT INTO `message` VALUES (153, 1, 20001, '看', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 19:40:19.001', '2024-05-17 19:40:19.005');
INSERT INTO `message` VALUES (154, 1, 20001, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 19:40:20.530', '2024-05-17 19:40:20.534');
INSERT INTO `message` VALUES (155, 1, 20001, '等待', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 19:40:22.717', '2024-05-17 19:40:22.721');
INSERT INTO `message` VALUES (156, 1, 20001, '什么东西', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 19:40:41.672', '2024-05-17 19:40:41.677');
INSERT INTO `message` VALUES (157, 1, 20001, '😀', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 21:24:34.716', '2024-05-17 21:24:34.723');
INSERT INTO `message` VALUES (158, 1, 20001, '笑死', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 21:24:48.024', '2024-05-17 21:24:48.029');
INSERT INTO `message` VALUES (159, 1, 20001, '下撒按时啊', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 21:29:11.737', '2024-05-17 21:29:11.748');
INSERT INTO `message` VALUES (160, 1, 20001, '大厦的看啥肯定会看撒谎肯定会大厦的看啥肯定会看撒谎肯定会大厦的看啥肯定会看撒谎肯定会大厦的看啥肯定会看撒谎肯定会大厦的看啥肯定会看撒谎肯定会大厦的看啥肯定会看撒谎肯定会大厦的看啥肯定会看撒谎肯定会大厦的看啥肯定会看撒谎肯定会大厦的看啥肯定会看撒谎肯定会大厦的看啥肯定会看撒谎肯定会大厦的看啥肯定会看撒谎肯定会大厦的看啥肯定会看撒谎肯定会大厦的看啥肯定会看撒谎肯定会大厦的看啥肯定会看撒谎肯定会大厦的看啥肯定会看撒谎肯定会大厦的看啥肯定会看撒谎肯定会大厦的看啥肯定会看撒谎肯定会大厦的看啥肯定会看撒谎肯定会大厦的看啥肯定会看撒谎肯定会大厦的看啥肯定会看撒谎肯定会大厦的看啥肯定会看撒谎肯定会大厦的看啥肯定会看撒谎肯定会大厦的看啥肯定会看撒谎肯定会大厦的看啥肯定会看撒谎肯定会大厦的看啥肯定会看撒谎肯定会大厦的看啥肯定会看撒谎肯定会大厦的看啥肯定会看撒谎肯定会', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 21:29:46.545', '2024-05-17 21:29:46.552');
INSERT INTO `message` VALUES (161, 1, 20001, '还行', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 21:30:30.445', '2024-05-17 21:30:30.450');
INSERT INTO `message` VALUES (162, 1, 20001, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 21:33:06.434', '2024-05-17 21:33:06.441');
INSERT INTO `message` VALUES (163, 1, 20001, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 21:34:00.757', '2024-05-17 21:34:00.762');
INSERT INTO `message` VALUES (164, 1, 20001, '还行', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 21:34:05.319', '2024-05-17 21:34:05.325');
INSERT INTO `message` VALUES (165, 1, 20001, '更改', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 21:35:23.633', '2024-05-17 21:35:23.638');
INSERT INTO `message` VALUES (166, 1, 20001, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 21:35:30.782', '2024-05-17 21:35:30.786');
INSERT INTO `message` VALUES (167, 1, 20001, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 21:35:31.779', '2024-05-17 21:35:31.782');
INSERT INTO `message` VALUES (168, 1, 20001, '444', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 21:35:34.106', '2024-05-17 21:35:34.111');
INSERT INTO `message` VALUES (169, 1, 20001, '666', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 21:35:39.113', '2024-05-17 21:35:39.119');
INSERT INTO `message` VALUES (170, 1, 20001, '111', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 21:38:00.028', '2024-05-17 21:38:00.035');
INSERT INTO `message` VALUES (171, 1, 20001, '笑死', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 21:38:14.604', '2024-05-17 21:38:14.609');
INSERT INTO `message` VALUES (172, 1, 20001, '怎么回事啊', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 21:38:21.692', '2024-05-17 21:38:21.696');
INSERT INTO `message` VALUES (173, 1, 20001, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 21:40:39.304', '2024-05-17 21:40:39.311');
INSERT INTO `message` VALUES (174, 1, 20001, '零零撒旦', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 21:42:52.391', '2024-05-17 21:42:52.399');
INSERT INTO `message` VALUES (175, 1, 20001, '的撒', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 21:42:54.521', '2024-05-17 21:42:54.527');
INSERT INTO `message` VALUES (176, 1, 20001, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 21:46:09.788', '2024-05-17 21:46:09.799');
INSERT INTO `message` VALUES (177, 1, 20001, '零零撒旦', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 21:46:26.128', '2024-05-17 21:46:26.134');
INSERT INTO `message` VALUES (178, 1, 20001, '日日日', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 21:46:56.928', '2024-05-17 21:46:56.933');
INSERT INTO `message` VALUES (179, 1, 20001, '效率', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 21:47:00.836', '2024-05-17 21:47:00.842');
INSERT INTO `message` VALUES (180, 1, 20001, '四傲松i啊', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 21:49:14.803', '2024-05-17 21:49:14.809');
INSERT INTO `message` VALUES (181, 1, 20001, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 21:56:37.539', '2024-05-17 21:56:37.548');
INSERT INTO `message` VALUES (182, 1, 20001, '打算', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-17 21:56:41.451', '2024-05-17 21:56:41.456');
INSERT INTO `message` VALUES (183, 1, 20000, '早上好', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 06:47:00.305', '2024-05-18 06:47:00.335');
INSERT INTO `message` VALUES (184, 1, 20000, '撒', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 06:47:06.595', '2024-05-18 06:47:06.598');
INSERT INTO `message` VALUES (185, 1, 20000, '啦啦啦', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 06:47:39.598', '2024-05-18 06:47:39.604');
INSERT INTO `message` VALUES (186, 1, 20000, '看一下', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 06:47:43.972', '2024-05-18 06:47:43.977');
INSERT INTO `message` VALUES (187, 1, 20000, '啪啪啪', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 06:48:40.193', '2024-05-18 06:48:40.199');
INSERT INTO `message` VALUES (188, 1, 20000, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 06:49:49.058', '2024-05-18 06:49:49.063');
INSERT INTO `message` VALUES (189, 1, 20000, '😀', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 06:49:55.959', '2024-05-18 06:49:55.963');
INSERT INTO `message` VALUES (190, 1, 20000, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 07:05:57.476', '2024-05-18 07:05:57.484');
INSERT INTO `message` VALUES (191, 1, 20000, '看看吧', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 07:06:03.404', '2024-05-18 07:06:03.409');
INSERT INTO `message` VALUES (192, 1, 20000, '这都行', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 07:06:11.526', '2024-05-18 07:06:11.531');
INSERT INTO `message` VALUES (193, 1, 20000, '什么鬼', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 07:06:15.144', '2024-05-18 07:06:15.148');
INSERT INTO `message` VALUES (194, 1, 20000, '看一下', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 07:32:24.582', '2024-05-18 07:32:24.588');
INSERT INTO `message` VALUES (195, 1, 20000, '为什么滚动会出问题呢', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 07:56:12.560', '2024-05-18 07:56:12.578');
INSERT INTO `message` VALUES (196, 1, 20000, '老师这样', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 07:56:16.739', '2024-05-18 07:56:16.742');
INSERT INTO `message` VALUES (197, 1, 20000, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 07:56:24.523', '2024-05-18 07:56:24.528');
INSERT INTO `message` VALUES (198, 1, 20000, '我服了', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 08:43:38.382', '2024-05-18 08:43:38.391');
INSERT INTO `message` VALUES (199, 1, 20000, '再看看', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 08:51:58.916', '2024-05-18 08:51:58.928');
INSERT INTO `message` VALUES (200, 1, 20000, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 09:13:40.832', '2024-05-18 09:13:40.839');
INSERT INTO `message` VALUES (201, 1, 20000, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 09:21:27.227', '2024-05-18 09:21:27.235');
INSERT INTO `message` VALUES (202, 1, 20000, '试试', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 09:21:31.047', '2024-05-18 09:21:31.052');
INSERT INTO `message` VALUES (203, 1, 20000, '喀喀喀', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 09:22:15.240', '2024-05-18 09:22:15.248');
INSERT INTO `message` VALUES (204, 1, 20000, '卡卡是', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 09:22:17.412', '2024-05-18 09:22:17.417');
INSERT INTO `message` VALUES (205, 1, 20000, '的撒', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 09:22:20.425', '2024-05-18 09:22:20.430');
INSERT INTO `message` VALUES (206, 1, 20000, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 09:26:03.055', '2024-05-18 09:26:03.063');
INSERT INTO `message` VALUES (207, 1, 20000, '为什么', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 09:26:08.046', '2024-05-18 09:26:08.052');
INSERT INTO `message` VALUES (208, 1, 20000, '出现了', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 09:26:12.262', '2024-05-18 09:26:12.267');
INSERT INTO `message` VALUES (209, 1, 20000, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 09:29:25.145', '2024-05-18 09:29:25.152');
INSERT INTO `message` VALUES (210, 1, 20000, '看看看看', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 09:34:58.735', '2024-05-18 09:34:58.743');
INSERT INTO `message` VALUES (211, 1, 20000, '出来', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 09:36:41.497', '2024-05-18 09:36:41.505');
INSERT INTO `message` VALUES (212, 1, 20000, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 09:39:12.803', '2024-05-18 09:39:12.810');
INSERT INTO `message` VALUES (213, 1, 20000, '哦哦哦', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 09:39:29.519', '2024-05-18 09:39:29.524');
INSERT INTO `message` VALUES (214, 1, 20000, '我看看', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 09:39:34.090', '2024-05-18 09:39:34.099');
INSERT INTO `message` VALUES (215, 1, 20000, '怎么回事啊', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 09:39:38.314', '2024-05-18 09:39:38.319');
INSERT INTO `message` VALUES (216, 1, 20000, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 09:40:45.043', '2024-05-18 09:40:45.049');
INSERT INTO `message` VALUES (217, 1, 20000, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 09:40:46.786', '2024-05-18 09:40:46.791');
INSERT INTO `message` VALUES (218, 1, 20000, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 09:40:48.537', '2024-05-18 09:40:48.541');
INSERT INTO `message` VALUES (219, 1, 20000, '啊啊？', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 09:40:54.162', '2024-05-18 09:40:54.167');
INSERT INTO `message` VALUES (220, 1, 20000, '为什么呢', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 09:41:29.151', '2024-05-18 09:41:29.157');
INSERT INTO `message` VALUES (221, 1, 20000, '😀', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 09:42:29.658', '2024-05-18 09:42:29.665');
INSERT INTO `message` VALUES (222, 1, 20000, '再试试', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 09:42:34.463', '2024-05-18 09:42:34.468');
INSERT INTO `message` VALUES (223, 1, 20000, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 09:43:33.713', '2024-05-18 09:43:33.718');
INSERT INTO `message` VALUES (224, 1, 20000, '看看', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 09:43:36.563', '2024-05-18 09:43:36.568');
INSERT INTO `message` VALUES (225, 1, 20000, '为什么', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 09:43:38.489', '2024-05-18 09:43:38.493');
INSERT INTO `message` VALUES (226, 1, 20000, '这样的吗', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 09:43:41.824', '2024-05-18 09:43:41.829');
INSERT INTO `message` VALUES (227, 1, 20000, '啊？', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 09:43:44.768', '2024-05-18 09:43:44.772');
INSERT INTO `message` VALUES (228, 1, 20000, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 09:44:14.553', '2024-05-18 09:44:14.558');
INSERT INTO `message` VALUES (229, 1, 20000, '啊啊', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 09:44:16.469', '2024-05-18 09:44:16.473');
INSERT INTO `message` VALUES (230, 1, 20000, '出现了', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 09:44:18.627', '2024-05-18 09:44:18.630');
INSERT INTO `message` VALUES (231, 1, 20000, '看一下', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 09:44:24.125', '2024-05-18 09:44:24.131');
INSERT INTO `message` VALUES (232, 1, 20000, '这样的吗', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 09:44:26.469', '2024-05-18 09:44:26.473');
INSERT INTO `message` VALUES (233, 1, 20000, '😀', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 09:45:08.673', '2024-05-18 09:45:08.678');
INSERT INTO `message` VALUES (234, 1, 20001, '什么鬼', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 09:46:24.868', '2024-05-18 09:46:24.875');
INSERT INTO `message` VALUES (235, 1, 20001, '12313', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 09:46:53.820', '2024-05-18 09:46:53.824');
INSERT INTO `message` VALUES (236, 1, 20001, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 09:46:54.474', '2024-05-18 09:46:54.478');
INSERT INTO `message` VALUES (237, 1, 20001, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 09:46:55.011', '2024-05-18 09:46:55.013');
INSERT INTO `message` VALUES (238, 1, 20001, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 09:46:55.643', '2024-05-18 09:46:55.646');
INSERT INTO `message` VALUES (239, 1, 20001, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 09:46:56.297', '2024-05-18 09:46:56.301');
INSERT INTO `message` VALUES (240, 1, 20001, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 09:46:56.762', '2024-05-18 09:46:56.764');
INSERT INTO `message` VALUES (241, 1, 20001, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 09:46:57.391', '2024-05-18 09:46:57.395');
INSERT INTO `message` VALUES (242, 1, 20001, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 09:46:57.827', '2024-05-18 09:46:57.829');
INSERT INTO `message` VALUES (243, 1, 20001, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 09:46:58.484', '2024-05-18 09:46:58.487');
INSERT INTO `message` VALUES (244, 1, 20001, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 09:46:58.881', '2024-05-18 09:46:58.883');
INSERT INTO `message` VALUES (245, 1, 20001, '123sa', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 09:46:59.880', '2024-05-18 09:46:59.883');
INSERT INTO `message` VALUES (246, 1, 20001, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 09:47:00.895', '2024-05-18 09:47:00.898');
INSERT INTO `message` VALUES (247, 1, 20001, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 09:47:01.981', '2024-05-18 09:47:01.984');
INSERT INTO `message` VALUES (248, 1, 20001, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 09:47:25.882', '2024-05-18 09:47:25.886');
INSERT INTO `message` VALUES (249, 1, 20001, '哈哈哈', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 09:47:27.343', '2024-05-18 09:47:27.347');
INSERT INTO `message` VALUES (250, 1, 20001, '和发放', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 09:47:28.852', '2024-05-18 09:47:28.856');
INSERT INTO `message` VALUES (251, 1, 20001, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 09:47:29.733', '2024-05-18 09:47:29.736');
INSERT INTO `message` VALUES (252, 1, 20001, 'sad ', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 09:47:31.162', '2024-05-18 09:47:31.166');
INSERT INTO `message` VALUES (253, 1, 20001, 'sdad', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 09:47:32.599', '2024-05-18 09:47:32.603');
INSERT INTO `message` VALUES (254, 1, 20001, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 09:47:33.674', '2024-05-18 09:47:33.678');
INSERT INTO `message` VALUES (255, 1, 20001, '当年萨蒂阿松大蝴蝶卡换卡当年萨蒂阿松大蝴蝶卡换卡当年萨蒂阿松大蝴蝶卡换卡当年萨蒂阿松大蝴蝶卡换卡当年萨蒂阿松大蝴蝶卡换卡当年萨蒂阿松大蝴蝶卡换卡当年萨蒂阿松大蝴蝶卡换卡当年萨蒂阿松大蝴蝶卡换卡当年萨蒂阿松大蝴蝶卡换卡当年萨蒂阿松大蝴蝶卡换卡当年萨蒂阿松大蝴蝶卡换卡当年萨蒂阿松大蝴蝶卡换卡当年萨蒂阿松大蝴蝶卡换卡当年萨蒂阿松大蝴蝶卡换卡当年萨蒂阿松大蝴蝶卡换卡当年萨蒂阿松大蝴蝶卡换卡当年萨蒂阿松大蝴蝶卡换卡当年萨蒂阿松大蝴蝶卡换卡当年萨蒂阿松大蝴蝶卡换卡当年萨蒂阿松大蝴蝶卡换卡', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 09:47:40.990', '2024-05-18 09:47:40.993');
INSERT INTO `message` VALUES (256, 1, 20001, '😀', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 09:48:41.612', '2024-05-18 09:48:41.620');
INSERT INTO `message` VALUES (257, 1, 20001, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 09:49:37.417', '2024-05-18 09:49:37.425');
INSERT INTO `message` VALUES (258, 1, 20001, '嘎嘎嘎', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 09:52:59.238', '2024-05-18 09:52:59.245');
INSERT INTO `message` VALUES (259, 1, 20001, '哈哈哈', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 09:54:56.095', '2024-05-18 09:54:56.102');
INSERT INTO `message` VALUES (260, 1, 20001, '笑死了', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 09:55:06.172', '2024-05-18 09:55:06.176');
INSERT INTO `message` VALUES (261, 1, 20001, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 09:55:17.314', '2024-05-18 09:55:17.318');
INSERT INTO `message` VALUES (262, 1, 20001, '打算', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 09:55:19.996', '2024-05-18 09:55:20.000');
INSERT INTO `message` VALUES (263, 1, 20001, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 09:57:00.836', '2024-05-18 09:57:00.841');
INSERT INTO `message` VALUES (264, 1, 20001, '笑了', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 10:00:22.887', '2024-05-18 10:00:22.894');
INSERT INTO `message` VALUES (265, 1, 20001, '看一下', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 10:00:42.240', '2024-05-18 10:00:42.245');
INSERT INTO `message` VALUES (266, 1, 20001, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 10:00:57.021', '2024-05-18 10:00:57.026');
INSERT INTO `message` VALUES (267, 1, 20001, '天天', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 10:01:25.781', '2024-05-18 10:01:25.786');
INSERT INTO `message` VALUES (268, 1, 20001, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 10:01:47.606', '2024-05-18 10:01:47.610');
INSERT INTO `message` VALUES (269, 1, 20001, '123555', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 10:03:57.553', '2024-05-18 10:03:57.560');
INSERT INTO `message` VALUES (270, 1, 20001, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 10:06:57.796', '2024-05-18 10:06:57.807');
INSERT INTO `message` VALUES (271, 1, 20001, '这样', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 10:07:00.622', '2024-05-18 10:07:00.626');
INSERT INTO `message` VALUES (272, 1, 20001, '的吗', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 10:07:02.101', '2024-05-18 10:07:02.105');
INSERT INTO `message` VALUES (273, 1, 20001, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 10:07:31.717', '2024-05-18 10:07:31.722');
INSERT INTO `message` VALUES (274, 1, 20001, '😀', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 10:07:43.609', '2024-05-18 10:07:43.614');
INSERT INTO `message` VALUES (275, 1, 20001, '123123123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 10:08:52.183', '2024-05-18 10:08:52.191');
INSERT INTO `message` VALUES (276, 1, 20001, '的事实', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 10:09:51.721', '2024-05-18 10:09:51.726');
INSERT INTO `message` VALUES (277, 1, 20001, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 10:10:23.874', '2024-05-18 10:10:23.878');
INSERT INTO `message` VALUES (278, 1, 20001, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 10:11:21.437', '2024-05-18 10:11:21.445');
INSERT INTO `message` VALUES (279, 1, 20001, '😀', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 10:11:24.776', '2024-05-18 10:11:24.782');
INSERT INTO `message` VALUES (280, 1, 20001, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 10:11:47.935', '2024-05-18 10:11:47.940');
INSERT INTO `message` VALUES (281, 1, 20001, '大撒大撒', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 10:12:11.156', '2024-05-18 10:12:11.167');
INSERT INTO `message` VALUES (282, 1, 20001, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 10:12:43.242', '2024-05-18 10:12:43.248');
INSERT INTO `message` VALUES (283, 1, 20001, '怎么回事', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 10:13:28.188', '2024-05-18 10:13:28.195');
INSERT INTO `message` VALUES (284, 1, 20000, '😀', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 10:14:39.434', '2024-05-18 10:14:39.440');
INSERT INTO `message` VALUES (285, 1, 20000, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 10:17:19.840', '2024-05-18 10:17:19.847');
INSERT INTO `message` VALUES (286, 1, 20001, '花花u啊', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 10:17:24.171', '2024-05-18 10:17:24.175');
INSERT INTO `message` VALUES (287, 1, 20001, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 10:17:26.653', '2024-05-18 10:17:26.659');
INSERT INTO `message` VALUES (288, 1, 20001, '这样吗', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 10:17:29.513', '2024-05-18 10:17:29.516');
INSERT INTO `message` VALUES (289, 1, 20000, '怎么又有啦', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 10:17:34.783', '2024-05-18 10:17:34.790');
INSERT INTO `message` VALUES (290, 1, 20001, '啊？', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 10:17:37.850', '2024-05-18 10:17:37.855');
INSERT INTO `message` VALUES (291, 1, 20001, '😀', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 10:17:48.409', '2024-05-18 10:17:48.414');
INSERT INTO `message` VALUES (292, 1, 20000, '1', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 10:17:54.090', '2024-05-18 10:17:54.096');
INSERT INTO `message` VALUES (293, 1, 20001, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 10:18:45.962', '2024-05-18 10:18:45.968');
INSERT INTO `message` VALUES (294, 1, 20001, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 10:18:52.009', '2024-05-18 10:18:52.013');
INSERT INTO `message` VALUES (295, 1, 20001, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 10:19:20.591', '2024-05-18 10:19:20.596');
INSERT INTO `message` VALUES (296, 1, 20001, '啊啊啊', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 10:19:23.100', '2024-05-18 10:19:23.105');
INSERT INTO `message` VALUES (297, 1, 20001, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 10:19:28.854', '2024-05-18 10:19:28.858');
INSERT INTO `message` VALUES (298, 1, 20000, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 10:19:58.435', '2024-05-18 10:19:58.440');
INSERT INTO `message` VALUES (299, 1, 20000, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 10:20:19.868', '2024-05-18 10:20:19.874');
INSERT INTO `message` VALUES (300, 1, 20001, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 10:20:30.391', '2024-05-18 10:20:30.396');
INSERT INTO `message` VALUES (301, 1, 20000, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 10:20:33.982', '2024-05-18 10:20:33.986');
INSERT INTO `message` VALUES (302, 1, 20000, '看', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 10:21:18.854', '2024-05-18 10:21:18.858');
INSERT INTO `message` VALUES (303, 1, 20000, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 10:23:06.480', '2024-05-18 10:23:06.489');
INSERT INTO `message` VALUES (304, 1, 20001, '😀', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 10:23:16.598', '2024-05-18 10:23:16.601');
INSERT INTO `message` VALUES (305, 1, 20001, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 10:27:28.297', '2024-05-18 10:27:28.304');
INSERT INTO `message` VALUES (306, 1, 20000, '好的卡号的开始', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 10:31:25.786', '2024-05-18 10:31:25.797');
INSERT INTO `message` VALUES (307, 1, 20000, '😀', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 10:31:39.928', '2024-05-18 10:31:39.933');
INSERT INTO `message` VALUES (308, 1, 20000, '***', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 11:45:59.086', '2024-05-18 11:45:59.095');
INSERT INTO `message` VALUES (309, 1, 20001, '***', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 11:46:07.372', '2024-05-18 11:46:07.376');
INSERT INTO `message` VALUES (310, 1, 20001, '***', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 11:47:11.406', '2024-05-18 11:47:11.415');
INSERT INTO `message` VALUES (311, 2, 20001, '我们已经成为好友了，开始聊天吧', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 12:12:02.884', '2024-05-18 12:12:02.887');
INSERT INTO `message` VALUES (312, 2, 20000, '😀', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 12:12:55.439', '2024-05-18 12:12:55.448');
INSERT INTO `message` VALUES (313, 2, 20000, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 12:12:58.485', '2024-05-18 12:12:58.489');
INSERT INTO `message` VALUES (314, 2, 20000, '还行把', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 12:13:02.857', '2024-05-18 12:13:02.862');
INSERT INTO `message` VALUES (315, 2, 20001, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 12:13:07.383', '2024-05-18 12:13:07.387');
INSERT INTO `message` VALUES (316, 2, 20001, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 12:13:14.245', '2024-05-18 12:13:14.249');
INSERT INTO `message` VALUES (317, 2, 20001, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 12:13:15.325', '2024-05-18 12:13:15.328');
INSERT INTO `message` VALUES (318, 2, 20001, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 12:13:16.380', '2024-05-18 12:13:16.382');
INSERT INTO `message` VALUES (319, 2, 20001, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 12:13:17.947', '2024-05-18 12:13:17.950');
INSERT INTO `message` VALUES (320, 2, 20000, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 12:13:21.176', '2024-05-18 12:13:21.180');
INSERT INTO `message` VALUES (321, 2, 20000, '😀', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 12:13:25.874', '2024-05-18 12:13:25.877');
INSERT INTO `message` VALUES (322, 2, 20000, '🫡', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 12:13:33.786', '2024-05-18 12:13:33.790');
INSERT INTO `message` VALUES (323, 2, 20000, '笑死了', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 12:13:37.540', '2024-05-18 12:13:37.542');
INSERT INTO `message` VALUES (324, 2, 20000, '大厦看到好卡还是卡还贷款', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 12:14:03.038', '2024-05-18 12:14:03.049');
INSERT INTO `message` VALUES (325, 2, 20000, '大厦看', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 12:14:06.354', '2024-05-18 12:14:06.358');
INSERT INTO `message` VALUES (326, 2, 20000, '的话凯撒好看', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 12:14:08.224', '2024-05-18 12:14:08.228');
INSERT INTO `message` VALUES (327, 2, 20000, '的撒好看', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 12:14:10.090', '2024-05-18 12:14:10.093');
INSERT INTO `message` VALUES (328, 2, 20000, '电话是卡卡', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 12:14:11.773', '2024-05-18 12:14:11.777');
INSERT INTO `message` VALUES (329, 2, 20000, '的深刻理解案例', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 12:14:13.433', '2024-05-18 12:14:13.438');
INSERT INTO `message` VALUES (330, 2, 20000, '的撒娇', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 12:14:15.123', '2024-05-18 12:14:15.127');
INSERT INTO `message` VALUES (331, 2, 20000, '白瞎了', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 12:16:18.479', '2024-05-18 12:16:18.485');
INSERT INTO `message` VALUES (332, 2, 20000, '西萨的j', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 12:16:20.946', '2024-05-18 12:16:20.950');
INSERT INTO `message` VALUES (333, 2, 20000, '觉得萨科', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 12:16:22.199', '2024-05-18 12:16:22.203');
INSERT INTO `message` VALUES (334, 2, 20000, '好的健身卡', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 12:16:23.375', '2024-05-18 12:16:23.378');
INSERT INTO `message` VALUES (335, 2, 20000, '和库萨克', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 12:16:24.597', '2024-05-18 12:16:24.603');
INSERT INTO `message` VALUES (336, 2, 20000, '还是卡换卡', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 12:16:25.673', '2024-05-18 12:16:25.678');
INSERT INTO `message` VALUES (337, 2, 20000, '贺卡上打开', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 12:16:26.876', '2024-05-18 12:16:26.880');
INSERT INTO `message` VALUES (338, 2, 20000, '回家卡刷点卡', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 12:16:27.982', '2024-05-18 12:16:27.986');
INSERT INTO `message` VALUES (339, 2, 20000, '好的凯撒好看', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 12:16:29.115', '2024-05-18 12:16:29.119');
INSERT INTO `message` VALUES (340, 2, 20000, '哈卡刷点卡', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 12:16:30.235', '2024-05-18 12:16:30.238');
INSERT INTO `message` VALUES (341, 2, 20000, '很快就撒谎的看', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 12:16:31.295', '2024-05-18 12:16:31.298');
INSERT INTO `message` VALUES (342, 2, 20000, '哈斯卡就会打开', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 12:16:32.387', '2024-05-18 12:16:32.390');
INSERT INTO `message` VALUES (343, 2, 20000, '还是卡还贷款', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 12:16:33.471', '2024-05-18 12:16:33.476');
INSERT INTO `message` VALUES (344, 2, 20000, '哈卡刷点卡', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 12:16:34.585', '2024-05-18 12:16:34.589');
INSERT INTO `message` VALUES (345, 1, 20001, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 12:17:58.820', '2024-05-18 12:17:58.826');
INSERT INTO `message` VALUES (346, 2, 20001, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 12:25:46.668', '2024-05-18 12:25:46.679');
INSERT INTO `message` VALUES (347, 2, 20001, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 12:25:48.778', '2024-05-18 12:25:48.782');
INSERT INTO `message` VALUES (348, 2, 20001, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 12:25:56.050', '2024-05-18 12:25:56.054');
INSERT INTO `message` VALUES (349, 1, 20001, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 12:26:15.869', '2024-05-18 12:26:15.874');
INSERT INTO `message` VALUES (350, 1, 20001, '在这', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 12:26:50.920', '2024-05-18 12:26:50.927');
INSERT INTO `message` VALUES (351, 2, 20001, '出现把', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 12:26:56.258', '2024-05-18 12:26:56.262');
INSERT INTO `message` VALUES (352, 2, 20001, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 12:27:24.257', '2024-05-18 12:27:24.264');
INSERT INTO `message` VALUES (353, 1, 20001, '哈哈哈', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 12:27:46.722', '2024-05-18 12:27:46.732');
INSERT INTO `message` VALUES (354, 1, 20001, '😀', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 12:28:02.732', '2024-05-18 12:28:02.736');
INSERT INTO `message` VALUES (355, 1, 20001, '1', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 12:28:15.559', '2024-05-18 12:28:15.568');
INSERT INTO `message` VALUES (356, 1, 20001, '😂', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 12:28:21.458', '2024-05-18 12:28:21.462');
INSERT INTO `message` VALUES (357, 1, 20001, '1111', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 12:28:32.240', '2024-05-18 12:28:32.245');
INSERT INTO `message` VALUES (358, 2, 20001, '😀', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 12:28:45.417', '2024-05-18 12:28:45.422');
INSERT INTO `message` VALUES (359, 2, 20000, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 12:30:02.165', '2024-05-18 12:30:02.174');
INSERT INTO `message` VALUES (360, 2, 20000, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 12:36:43.173', '2024-05-18 12:36:43.193');
INSERT INTO `message` VALUES (361, 2, 20000, '的撒旦', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 12:36:47.332', '2024-05-18 12:36:47.336');
INSERT INTO `message` VALUES (362, 1, 20000, '还好', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 12:37:36.815', '2024-05-18 12:37:36.820');
INSERT INTO `message` VALUES (363, 2, 20001, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 12:40:11.875', '2024-05-18 12:40:11.879');
INSERT INTO `message` VALUES (364, 2, 20000, '还行', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 13:00:13.842', '2024-05-18 13:00:13.848');
INSERT INTO `message` VALUES (365, 1, 20000, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 13:01:13.609', '2024-05-18 13:01:13.617');
INSERT INTO `message` VALUES (366, 1, 20000, '出现', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 13:01:29.597', '2024-05-18 13:01:29.602');
INSERT INTO `message` VALUES (367, 1, 20001, '😰', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 13:01:44.351', '2024-05-18 13:01:44.356');
INSERT INTO `message` VALUES (368, 2, 20001, '😃', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 13:01:57.060', '2024-05-18 13:01:57.063');
INSERT INTO `message` VALUES (369, 1, 20000, '出现了', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 13:02:31.187', '2024-05-18 13:02:31.195');
INSERT INTO `message` VALUES (370, 2, 20000, '怎么样', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 13:02:36.549', '2024-05-18 13:02:36.554');
INSERT INTO `message` VALUES (371, 2, 20000, '还行把', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 13:02:49.123', '2024-05-18 13:02:49.127');
INSERT INTO `message` VALUES (372, 2, 20001, '@💨', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 13:03:05.339', '2024-05-18 13:03:05.343');
INSERT INTO `message` VALUES (373, 1, 20001, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 13:03:16.953', '2024-05-18 13:03:16.959');
INSERT INTO `message` VALUES (374, 2, 20001, '花花', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 13:03:25.658', '2024-05-18 13:03:25.664');
INSERT INTO `message` VALUES (375, 2, 20000, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 13:06:21.470', '2024-05-18 13:06:21.478');
INSERT INTO `message` VALUES (376, 1, 20000, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 13:06:37.042', '2024-05-18 13:06:37.045');
INSERT INTO `message` VALUES (377, 2, 20001, '😲', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 13:06:47.222', '2024-05-18 13:06:47.225');
INSERT INTO `message` VALUES (378, 1, 20000, '😀', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 13:07:04.947', '2024-05-18 13:07:04.951');
INSERT INTO `message` VALUES (379, 2, 20000, '🤑', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 13:07:18.751', '2024-05-18 13:07:18.755');
INSERT INTO `message` VALUES (380, 1, 20000, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 13:31:52.323', '2024-05-18 13:31:52.330');
INSERT INTO `message` VALUES (381, 1, 20000, '🤑', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 13:48:00.287', '2024-05-18 13:48:00.293');
INSERT INTO `message` VALUES (382, 2, 20000, '🤑123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 13:48:09.907', '2024-05-18 13:48:09.912');
INSERT INTO `message` VALUES (383, 2, 20000, '😉', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 13:48:20.290', '2024-05-18 13:48:20.293');
INSERT INTO `message` VALUES (384, 1, 20000, '😘的就是多久啊圣诞节', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 13:48:27.519', '2024-05-18 13:48:27.524');
INSERT INTO `message` VALUES (385, 1, 20000, '😘', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 13:49:34.189', '2024-05-18 13:49:34.198');
INSERT INTO `message` VALUES (386, 1, 20000, '大萨达萨达了', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 13:53:44.569', '2024-05-18 13:53:44.577');
INSERT INTO `message` VALUES (387, 2, 20000, '132', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 13:55:38.161', '2024-05-18 13:55:38.165');
INSERT INTO `message` VALUES (388, 2, 20000, '大概有擦伤给', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 13:55:42.389', '2024-05-18 13:55:42.393');
INSERT INTO `message` VALUES (389, 1, 20000, '大撒大撒就好看的好看', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 13:55:48.818', '2024-05-18 13:55:48.822');
INSERT INTO `message` VALUES (390, 1, 20001, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 13:56:41.895', '2024-05-18 13:56:41.903');
INSERT INTO `message` VALUES (391, 1, 20001, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 13:56:43.024', '2024-05-18 13:56:43.033');
INSERT INTO `message` VALUES (392, 1, 20001, '大撒大撒', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 13:56:44.687', '2024-05-18 13:56:44.691');
INSERT INTO `message` VALUES (393, 1, 20001, '的哈德克', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 14:02:43.800', '2024-05-18 14:02:43.807');
INSERT INTO `message` VALUES (394, 1, 20001, '😒', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 14:02:52.161', '2024-05-18 14:02:52.165');
INSERT INTO `message` VALUES (395, 1, 20001, '的撒', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 14:02:55.829', '2024-05-18 14:02:55.832');
INSERT INTO `message` VALUES (396, 1, 20001, '的撒', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 14:02:57.316', '2024-05-18 14:02:57.321');
INSERT INTO `message` VALUES (397, 1, 20001, '的撒', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 14:02:58.506', '2024-05-18 14:02:58.511');
INSERT INTO `message` VALUES (398, 1, 20001, '萨达', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 14:02:59.386', '2024-05-18 14:02:59.392');
INSERT INTO `message` VALUES (399, 1, 20001, '阿斯顿', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 14:03:00.309', '2024-05-18 14:03:00.312');
INSERT INTO `message` VALUES (400, 1, 20001, '阿斯顿', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 14:03:01.154', '2024-05-18 14:03:01.158');
INSERT INTO `message` VALUES (401, 1, 20001, ' sdads啊', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 14:03:02.936', '2024-05-18 14:03:02.940');
INSERT INTO `message` VALUES (402, 1, 20001, ' asdas的', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 14:03:05.273', '2024-05-18 14:03:05.277');
INSERT INTO `message` VALUES (403, 1, 20001, '萨达', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 14:03:06.937', '2024-05-18 14:03:06.941');
INSERT INTO `message` VALUES (404, 1, 20001, '的撒和健康的', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 14:03:09.521', '2024-05-18 14:03:09.525');
INSERT INTO `message` VALUES (405, 1, 20001, '的撒娇看', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 14:03:11.959', '2024-05-18 14:03:11.964');
INSERT INTO `message` VALUES (406, 2, 20001, '😀', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 14:04:30.930', '2024-05-18 14:04:30.936');
INSERT INTO `message` VALUES (407, 2, 20001, '的话卡刷卡', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 14:04:35.320', '2024-05-18 14:04:35.323');
INSERT INTO `message` VALUES (408, 1, 20001, '1', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 14:14:12.802', '2024-05-18 14:14:12.809');
INSERT INTO `message` VALUES (409, 1, 20001, '2123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 14:14:17.697', '2024-05-18 14:14:17.702');
INSERT INTO `message` VALUES (410, 2, 20001, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 14:14:22.056', '2024-05-18 14:14:22.060');
INSERT INTO `message` VALUES (411, 1, 20001, '大萨达萨达', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 14:15:51.425', '2024-05-18 14:15:51.432');
INSERT INTO `message` VALUES (412, 2, 20001, '大撒大撒', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 14:15:55.368', '2024-05-18 14:15:55.372');
INSERT INTO `message` VALUES (413, 2, 20001, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 14:16:48.210', '2024-05-18 14:16:48.215');
INSERT INTO `message` VALUES (414, 2, 20001, '的撒觉得萨科', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 14:16:55.010', '2024-05-18 14:16:55.014');
INSERT INTO `message` VALUES (415, 2, 20001, '多花时间卡卡', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 14:16:56.422', '2024-05-18 14:16:56.426');
INSERT INTO `message` VALUES (416, 2, 20001, '活动就看撒', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 14:16:57.586', '2024-05-18 14:16:57.590');
INSERT INTO `message` VALUES (417, 1, 20000, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 14:18:31.810', '2024-05-18 14:18:31.821');
INSERT INTO `message` VALUES (418, 2, 20001, '123123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 14:18:37.660', '2024-05-18 14:18:37.664');
INSERT INTO `message` VALUES (419, 1, 20000, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 14:18:49.425', '2024-05-18 14:18:49.430');
INSERT INTO `message` VALUES (420, 2, 20001, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 14:18:56.310', '2024-05-18 14:18:56.314');
INSERT INTO `message` VALUES (421, 1, 20001, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 14:19:13.159', '2024-05-18 14:19:13.164');
INSERT INTO `message` VALUES (422, 2, 20000, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 14:19:18.229', '2024-05-18 14:19:18.232');
INSERT INTO `message` VALUES (423, 2, 20001, '很多事卡号看到', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 14:19:42.990', '2024-05-18 14:19:42.997');
INSERT INTO `message` VALUES (424, 2, 20001, '大厦尽快和', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 14:19:44.876', '2024-05-18 14:19:44.881');
INSERT INTO `message` VALUES (425, 2, 20001, '的回家卡刷点卡', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 14:19:45.956', '2024-05-18 14:19:45.960');
INSERT INTO `message` VALUES (426, 2, 20001, '好的手机卡好看', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 14:19:47.067', '2024-05-18 14:19:47.071');
INSERT INTO `message` VALUES (427, 2, 20001, NULL, NULL, 0, NULL, 5, '{\"soundMsgDTO\": {\"url\": \"http://192.168.1.13:9000/hula/chat/2024-05/20001/26f080ec-4713-4e27-abf5-009667e57b40.wav\", \"size\": 86928, \"second\": 5}}', '2024-05-18 14:20:26.417', '2024-05-18 14:20:26.424');
INSERT INTO `message` VALUES (428, 1, 20000, '都很健康撒谎的看撒谎卡圣诞贺卡都很健康撒谎的看撒谎卡圣诞贺卡都很健康撒谎的看撒谎卡圣诞贺卡都很健康撒谎的看撒谎卡圣诞贺卡都很健康撒谎的看撒谎卡圣诞贺卡都很健康撒谎的看撒谎卡圣诞贺卡都很健康撒谎的看撒谎卡圣诞贺卡都很健康撒谎的看撒谎卡圣诞贺卡都很健康撒谎的看撒谎卡圣诞贺卡都很健康撒谎的看撒谎卡圣诞贺卡都很健康撒谎的看撒谎卡圣诞贺卡都很健康撒谎的看撒谎卡圣诞贺卡都很健康撒谎的看撒谎卡圣诞贺卡都很健康撒谎的看撒谎卡圣诞贺卡都很健康撒谎的看撒谎卡圣诞贺卡都很健康撒谎的看撒谎卡圣诞贺卡都很健康撒谎的看撒谎卡圣诞贺卡都很健康撒谎的看撒谎卡圣诞贺卡都很健康撒谎的看撒谎卡圣诞贺卡都很健康撒谎的看撒谎卡圣诞贺卡都很健康撒谎的看撒谎卡圣诞贺卡都很健康撒谎的看撒谎卡圣诞贺卡都很健康撒谎的看撒谎卡圣诞贺卡都很健康撒谎的看撒谎卡圣诞贺卡', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 14:56:19.953', '2024-05-18 14:56:19.962');
INSERT INTO `message` VALUES (429, 1, 20001, '都很健康撒谎的看撒谎卡圣诞贺卡都很健康撒谎的看撒谎卡圣诞贺卡都很健康撒谎的看撒谎卡圣诞贺卡都很健康撒谎的看撒谎卡圣诞贺卡都很健康撒谎的看撒谎卡圣诞贺卡都很健康撒谎的看撒谎卡圣诞贺卡都很健康撒谎的看撒谎卡圣诞贺卡都很健康撒谎的看撒谎卡圣诞贺卡都很健康撒谎的看撒谎卡圣诞贺卡都很健康撒谎的看撒谎卡圣诞贺卡都很健康撒谎的看撒谎卡圣诞贺卡都很健康撒谎的看撒谎卡圣诞贺卡都很健康撒谎的看撒谎卡圣诞贺卡都很健康撒谎的看撒谎卡圣诞贺卡都很健康撒谎的看撒谎卡圣诞贺卡都很健康撒谎的看撒谎卡圣诞贺卡', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 14:56:33.272', '2024-05-18 14:56:33.276');
INSERT INTO `message` VALUES (430, 1, 20001, '都很健康撒谎的看撒谎卡圣诞贺卡都很健康撒谎的看撒谎卡圣诞贺卡都很健康撒谎的看撒谎卡圣诞贺卡都很健康撒谎的看撒谎卡圣诞贺卡都很健康撒谎的看撒谎卡圣诞贺卡都很健康撒谎的看撒谎卡圣诞贺卡都很健康撒谎的看撒谎卡圣诞贺卡都很健康撒谎的看撒谎卡圣诞贺卡都很健康撒谎的看撒谎卡圣诞贺卡都很健康撒谎的看撒谎卡圣诞贺卡', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 14:57:18.687', '2024-05-18 14:57:18.702');
INSERT INTO `message` VALUES (431, 1, 20001, '都很健康撒谎的看撒谎卡圣诞贺卡都很健康撒谎的看撒谎卡圣诞贺卡都很健康撒谎的看撒谎卡圣诞贺卡都很健康撒谎的看撒谎卡圣诞贺卡都很健康撒谎的看撒谎卡圣诞贺卡都很健康撒谎的看撒谎卡圣诞贺卡都很健康撒谎的看撒谎卡圣诞贺卡都很健康撒谎的看撒谎卡圣诞贺卡都很健康撒谎的看撒谎卡圣诞贺卡都很健康撒谎的看撒谎卡圣诞贺卡都很健康撒谎的看撒谎卡圣诞贺卡都很健康撒谎的看撒谎卡圣诞贺卡', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 14:57:47.845', '2024-05-18 14:57:47.851');
INSERT INTO `message` VALUES (432, 1, 20000, '都很健康撒谎的看撒谎卡圣诞贺卡都很健康撒谎的看撒谎卡圣诞贺卡都很健康撒谎的看撒谎卡圣诞贺卡都很健康撒谎的看撒谎卡圣诞贺卡都很健康撒谎的看撒谎卡圣诞贺卡都很健康撒谎的看撒谎卡圣诞贺卡都很健康撒谎的看撒谎卡圣诞贺卡都很健康撒谎的看撒谎卡圣诞贺卡都很健康撒谎的看撒谎卡圣诞贺卡都很健康撒谎的看撒谎卡圣诞贺卡', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 14:57:54.370', '2024-05-18 14:57:54.374');
INSERT INTO `message` VALUES (433, 1, 20000, '啥空间', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 14:57:57.706', '2024-05-18 14:57:57.710');
INSERT INTO `message` VALUES (434, 1, 20000, '都很健康撒谎的看撒谎卡圣诞贺卡都很健康撒谎的看撒谎卡圣诞贺卡都很健康撒谎的看撒谎卡圣诞贺卡都很健康撒谎的看撒谎卡圣诞贺卡都很健康撒谎的看撒谎卡圣诞贺卡都很健康撒谎的看撒谎卡圣诞贺卡都很健康撒谎的看撒谎卡圣诞贺卡都很健康撒谎的看撒谎卡圣诞贺卡都很健康撒谎的看撒谎卡圣诞贺卡都很健康撒谎的看撒谎卡圣诞贺卡', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 15:04:41.020', '2024-05-18 15:04:41.026');
INSERT INTO `message` VALUES (435, 1, 20000, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 15:15:42.375', '2024-05-18 15:15:42.382');
INSERT INTO `message` VALUES (436, 1, 20000, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 15:15:43.885', '2024-05-18 15:15:43.889');
INSERT INTO `message` VALUES (437, 2, 20000, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 15:15:49.107', '2024-05-18 15:15:49.111');
INSERT INTO `message` VALUES (438, 2, 20000, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 15:15:50.490', '2024-05-18 15:15:50.494');
INSERT INTO `message` VALUES (439, 2, 20000, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 15:15:51.474', '2024-05-18 15:15:51.478');
INSERT INTO `message` VALUES (440, 2, 20000, '的杀菌', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 15:15:53.730', '2024-05-18 15:15:53.734');
INSERT INTO `message` VALUES (441, 2, 20000, '大厦尽快', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 15:15:55.014', '2024-05-18 15:15:55.018');
INSERT INTO `message` VALUES (442, 2, 20000, '的撒', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 15:16:03.967', '2024-05-18 15:16:03.970');
INSERT INTO `message` VALUES (443, 2, 20000, '的撒娇看了', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 15:16:05.203', '2024-05-18 15:16:05.206');
INSERT INTO `message` VALUES (444, 2, 20000, '的健身卡', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 15:16:06.099', '2024-05-18 15:16:06.101');
INSERT INTO `message` VALUES (445, 2, 20000, '杀菌灯k活动就看撒', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 15:16:08.411', '2024-05-18 15:16:08.415');
INSERT INTO `message` VALUES (446, 2, 20000, '好的手机卡', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 15:16:09.788', '2024-05-18 15:16:09.792');
INSERT INTO `message` VALUES (447, 2, 20000, '活动就看撒', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 15:16:10.876', '2024-05-18 15:16:10.878');
INSERT INTO `message` VALUES (448, 1, 20000, '的撒娇看了', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 15:16:14.161', '2024-05-18 15:16:14.171');
INSERT INTO `message` VALUES (449, 1, 20000, '和江苏快达', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 15:16:15.970', '2024-05-18 15:16:15.974');
INSERT INTO `message` VALUES (450, 2, 20000, '的撒大和jk', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 15:16:39.564', '2024-05-18 15:16:39.568');
INSERT INTO `message` VALUES (451, 2, 20000, '的话就看撒', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 15:16:40.796', '2024-05-18 15:16:40.800');
INSERT INTO `message` VALUES (452, 2, 20000, '的话就看撒', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 15:16:41.854', '2024-05-18 15:16:41.857');
INSERT INTO `message` VALUES (453, 2, 20000, '活动就看撒', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 15:16:42.895', '2024-05-18 15:16:42.898');
INSERT INTO `message` VALUES (454, 2, 20000, '的话就看撒', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 15:16:44.042', '2024-05-18 15:16:44.044');
INSERT INTO `message` VALUES (455, 2, 20000, '活动就看撒', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 15:16:45.108', '2024-05-18 15:16:45.111');
INSERT INTO `message` VALUES (456, 2, 20000, '很多凯撒', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 15:16:46.377', '2024-05-18 15:16:46.380');
INSERT INTO `message` VALUES (457, 1, 20000, '的萨科', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 15:16:50.204', '2024-05-18 15:16:50.213');
INSERT INTO `message` VALUES (458, 1, 20000, '大厦尽快', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 15:16:51.635', '2024-05-18 15:16:51.639');
INSERT INTO `message` VALUES (459, 1, 20000, '好的健身卡', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 15:16:52.897', '2024-05-18 15:16:52.901');
INSERT INTO `message` VALUES (460, 1, 20000, '很多凯撒', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 15:16:54.109', '2024-05-18 15:16:54.112');
INSERT INTO `message` VALUES (461, 1, 20000, '的话就看撒', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 15:16:55.256', '2024-05-18 15:16:55.259');
INSERT INTO `message` VALUES (462, 1, 20000, '很多凯撒', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 15:16:56.506', '2024-05-18 15:16:56.509');
INSERT INTO `message` VALUES (463, 1, 20000, '很多刷卡', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 15:16:57.633', '2024-05-18 15:16:57.637');
INSERT INTO `message` VALUES (464, 1, 20000, '的手机卡还贷款撒的手机卡还贷款撒的手机卡还贷款撒的手机卡还贷款撒的手机卡还贷款撒的手机卡还贷款撒的手机卡还贷款撒的手机卡还贷款撒的手机卡还贷款撒的手机卡还贷款撒的手机卡还贷款撒的手机卡还贷款撒的手机卡还贷款撒的手机卡还贷款撒的手机卡还贷款撒的手机卡还贷款撒的手机卡还贷款撒的手机卡还贷款撒的手机卡还贷款撒的手机卡还贷款撒的手机卡还贷款撒的手机卡还贷款撒的手机卡还贷款撒的手机卡还贷款撒的手机卡还贷款撒的手机卡还贷款撒的手机卡还贷款撒的手机卡还贷款撒的手机卡还贷款撒', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 15:17:07.180', '2024-05-18 15:17:07.190');
INSERT INTO `message` VALUES (465, 1, 20000, '的撒', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 15:17:26.459', '2024-05-18 15:17:26.466');
INSERT INTO `message` VALUES (466, 1, 20000, '的撒好看', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 15:17:28.058', '2024-05-18 15:17:28.063');
INSERT INTO `message` VALUES (467, 1, 20000, '导航手机卡', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 15:17:28.949', '2024-05-18 15:17:28.952');
INSERT INTO `message` VALUES (468, 1, 20000, '德国汉莎', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 15:17:29.793', '2024-05-18 15:17:29.796');
INSERT INTO `message` VALUES (469, 1, 20000, '的敢撒谎就d\'', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 15:17:31.675', '2024-05-18 15:17:31.679');
INSERT INTO `message` VALUES (470, 1, 20000, '的金卡是', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 15:17:32.801', '2024-05-18 15:17:32.805');
INSERT INTO `message` VALUES (471, 1, 20000, '大家撒看', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 16:02:59.982', '2024-05-18 16:02:59.988');
INSERT INTO `message` VALUES (472, 1, 20000, '大厦尽快', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 16:03:01.932', '2024-05-18 16:03:01.936');
INSERT INTO `message` VALUES (473, 1, 20000, '的撒娇看', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 16:03:03.421', '2024-05-18 16:03:03.425');
INSERT INTO `message` VALUES (474, 1, 20000, '打卡萨', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 16:03:04.858', '2024-05-18 16:03:04.862');
INSERT INTO `message` VALUES (475, 1, 20000, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 16:03:16.550', '2024-05-18 16:03:16.555');
INSERT INTO `message` VALUES (476, 2, 20000, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 16:03:19.309', '2024-05-18 16:03:19.313');
INSERT INTO `message` VALUES (477, 2, 20000, '😘', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 18:19:01.973', '2024-05-18 18:19:01.985');
INSERT INTO `message` VALUES (478, 2, 20001, '😮💨', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 18:19:20.855', '2024-05-18 18:19:20.860');
INSERT INTO `message` VALUES (479, 2, 20000, '😮💨', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 19:12:23.827', '2024-05-18 19:12:23.835');
INSERT INTO `message` VALUES (480, 1, 20001, '***', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 19:17:06.696', '2024-05-18 19:17:06.708');
INSERT INTO `message` VALUES (481, 2, 20001, 'weism', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 19:17:29.729', '2024-05-18 19:17:29.733');
INSERT INTO `message` VALUES (482, 1, 20001, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 19:17:40.863', '2024-05-18 19:17:40.869');
INSERT INTO `message` VALUES (483, 1, 20001, 'dasdsa ', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 19:22:57.080', '2024-05-18 19:22:57.087');
INSERT INTO `message` VALUES (484, 1, 20001, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 19:26:24.610', '2024-05-18 19:26:24.615');
INSERT INTO `message` VALUES (485, 2, 20001, 'dsadsa ', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 19:26:29.292', '2024-05-18 19:26:29.296');
INSERT INTO `message` VALUES (486, 2, 20001, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 19:29:14.421', '2024-05-18 19:29:14.429');
INSERT INTO `message` VALUES (487, 1, 20001, 'dsad ', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 19:29:19.011', '2024-05-18 19:29:19.023');
INSERT INTO `message` VALUES (488, 2, 20001, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 20:18:28.545', '2024-05-18 20:18:28.551');
INSERT INTO `message` VALUES (489, 1, 20001, 'dsasda ', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 20:18:33.313', '2024-05-18 20:18:33.319');
INSERT INTO `message` VALUES (490, 1, 20001, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 20:24:21.908', '2024-05-18 20:24:21.915');
INSERT INTO `message` VALUES (491, 2, 20001, 'dsadsa ', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 20:24:25.643', '2024-05-18 20:24:25.647');
INSERT INTO `message` VALUES (492, 1, 20001, 'dsadsa', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 20:24:35.087', '2024-05-18 20:24:35.091');
INSERT INTO `message` VALUES (493, 2, 20001, 'dsadsas', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 20:24:39.923', '2024-05-18 20:24:39.927');
INSERT INTO `message` VALUES (494, 1, 20001, 'dsadsa', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 20:27:39.962', '2024-05-18 20:27:39.968');
INSERT INTO `message` VALUES (495, 2, 20001, 'dsas ', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 20:27:45.849', '2024-05-18 20:27:45.855');
INSERT INTO `message` VALUES (496, 2, 20001, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 20:27:59.601', '2024-05-18 20:27:59.604');
INSERT INTO `message` VALUES (497, 1, 20001, 'dsadsa', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 20:28:05.823', '2024-05-18 20:28:05.828');
INSERT INTO `message` VALUES (498, 2, 20001, 'dsadsa', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 20:28:13.539', '2024-05-18 20:28:13.543');
INSERT INTO `message` VALUES (499, 2, 20001, 'dsadsa', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 20:28:18.064', '2024-05-18 20:28:18.070');
INSERT INTO `message` VALUES (500, 1, 20001, 'dsadsa', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 20:28:22.996', '2024-05-18 20:28:23.002');
INSERT INTO `message` VALUES (501, 2, 20001, 'dsadsad', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 20:28:31.525', '2024-05-18 20:28:31.528');
INSERT INTO `message` VALUES (502, 1, 20001, 'dsasda', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-18 20:28:39.646', '2024-05-18 20:28:39.652');
INSERT INTO `message` VALUES (503, 1, 20000, '你们好呀出来聊聊天', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-20 14:59:46.864', '2024-05-20 14:59:46.908');
INSERT INTO `message` VALUES (504, 1, 20000, '😘', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-20 14:59:52.366', '2024-05-20 14:59:52.371');
INSERT INTO `message` VALUES (505, 1, 20000, '😉', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-20 14:59:55.780', '2024-05-20 14:59:55.784');
INSERT INTO `message` VALUES (506, 1, 20000, '🤯', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-20 15:00:01.299', '2024-05-20 15:00:01.305');
INSERT INTO `message` VALUES (507, 1, 20000, '出来聊天', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-20 15:00:59.023', '2024-05-20 15:00:59.030');
INSERT INTO `message` VALUES (508, 1, 20000, '什么鬼', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-20 15:01:02.314', '2024-05-20 15:01:02.317');
INSERT INTO `message` VALUES (509, 1, 20000, '啊？', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-20 15:01:04.570', '2024-05-20 15:01:04.572');
INSERT INTO `message` VALUES (510, 1, 20000, '笑死了', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-20 15:01:07.858', '2024-05-20 15:01:07.860');
INSERT INTO `message` VALUES (511, 1, 20000, '出来', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-20 15:01:10.239', '2024-05-20 15:01:10.242');
INSERT INTO `message` VALUES (512, 1, 20000, '说什么ne', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-20 15:01:12.994', '2024-05-20 15:01:12.996');
INSERT INTO `message` VALUES (513, 1, 20000, '***', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-20 15:01:15.517', '2024-05-20 15:01:15.520');
INSERT INTO `message` VALUES (514, 1, 20000, '干', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-20 15:01:18.430', '2024-05-20 15:01:18.432');
INSERT INTO `message` VALUES (515, 1, 20000, '出来', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-20 15:01:20.620', '2024-05-20 15:01:20.623');
INSERT INTO `message` VALUES (516, 1, 20000, '说什么呢😀', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-20 15:01:27.773', '2024-05-20 15:01:27.776');
INSERT INTO `message` VALUES (517, 2, 20000, '😀', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-20 15:01:56.518', '2024-05-20 15:01:56.522');
INSERT INTO `message` VALUES (518, 1, 20000, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-20 15:02:13.736', '2024-05-20 15:02:13.740');
INSERT INTO `message` VALUES (519, 2, 20000, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-20 15:02:18.092', '2024-05-20 15:02:18.094');
INSERT INTO `message` VALUES (520, 2, 20000, '我看看', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-20 15:11:00.525', '2024-05-20 15:11:00.533');
INSERT INTO `message` VALUES (521, 1, 20000, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-20 16:03:07.130', '2024-05-20 16:03:07.134');
INSERT INTO `message` VALUES (522, 2, 20000, 'zaikankan', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-20 16:05:33.198', '2024-05-20 16:05:33.205');
INSERT INTO `message` VALUES (523, 2, 20000, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-20 16:06:55.744', '2024-05-20 16:06:55.750');
INSERT INTO `message` VALUES (524, 2, 20000, 'dsad ', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-20 16:07:00.744', '2024-05-20 16:07:00.748');
INSERT INTO `message` VALUES (525, 1, 20000, 'zaikankna', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-20 16:13:54.714', '2024-05-20 16:13:54.722');
INSERT INTO `message` VALUES (526, 1, 20000, 'yiw', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-20 16:13:59.950', '2024-05-20 16:13:59.955');
INSERT INTO `message` VALUES (527, 1, 20000, 'dsadsa', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-20 16:14:04.854', '2024-05-20 16:14:04.856');
INSERT INTO `message` VALUES (528, 1, 20000, 'zenmhuis', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-20 16:14:08.554', '2024-05-20 16:14:08.557');
INSERT INTO `message` VALUES (529, 1, 20000, 'youxiaoguo', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-20 16:14:11.706', '2024-05-20 16:14:11.708');
INSERT INTO `message` VALUES (530, 1, 20000, 'a ?', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-20 16:14:14.225', '2024-05-20 16:14:14.227');
INSERT INTO `message` VALUES (531, 1, 20000, 'sm ', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-20 16:14:17.606', '2024-05-20 16:14:17.608');
INSERT INTO `message` VALUES (532, 1, 20000, 'chux ', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-20 16:14:19.795', '2024-05-20 16:14:19.797');
INSERT INTO `message` VALUES (533, 1, 20000, 'dzdsahd ', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-20 16:14:22.231', '2024-05-20 16:14:22.233');
INSERT INTO `message` VALUES (534, 1, 20000, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-20 16:14:23.553', '2024-05-20 16:14:23.557');
INSERT INTO `message` VALUES (535, 1, 20000, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-20 16:14:24.321', '2024-05-20 16:14:24.323');
INSERT INTO `message` VALUES (536, 1, 20000, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-20 16:14:25.167', '2024-05-20 16:14:25.169');
INSERT INTO `message` VALUES (537, 1, 20000, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-20 16:14:25.987', '2024-05-20 16:14:25.994');
INSERT INTO `message` VALUES (538, 1, 20000, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-20 16:14:26.857', '2024-05-20 16:14:26.859');
INSERT INTO `message` VALUES (539, 1, 20000, 'haoxkey ', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-20 16:14:29.598', '2024-05-20 16:14:29.601');
INSERT INTO `message` VALUES (540, 1, 20000, 'zenmhuisne ', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-20 16:14:34.197', '2024-05-20 16:14:34.199');
INSERT INTO `message` VALUES (541, 1, 20000, 'chuxian', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-20 16:14:38.869', '2024-05-20 16:14:38.871');
INSERT INTO `message` VALUES (542, 1, 20000, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-20 16:15:09.863', '2024-05-20 16:15:09.869');
INSERT INTO `message` VALUES (543, 1, 20000, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-20 16:15:11.008', '2024-05-20 16:15:11.011');
INSERT INTO `message` VALUES (544, 2, 20000, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-20 16:15:14.657', '2024-05-20 16:15:14.659');
INSERT INTO `message` VALUES (545, 2, 20000, 'a ?', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-20 16:15:18.043', '2024-05-20 16:15:18.045');
INSERT INTO `message` VALUES (546, 1, 20000, 'das', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-20 16:15:45.784', '2024-05-20 16:15:45.792');
INSERT INTO `message` VALUES (547, 2, 20000, '😀', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-20 16:19:52.218', '2024-05-20 16:19:52.226');
INSERT INTO `message` VALUES (548, 2, 20000, 'wokankan', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-20 16:32:12.432', '2024-05-20 16:32:12.437');
INSERT INTO `message` VALUES (549, 2, 20000, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-20 17:35:15.130', '2024-05-20 17:35:15.142');
INSERT INTO `message` VALUES (550, 2, 20000, '橡树', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-20 17:35:21.776', '2024-05-20 17:35:21.780');
INSERT INTO `message` VALUES (551, 2, 20000, '大厦看好看', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-20 17:40:41.520', '2024-05-20 17:40:41.529');
INSERT INTO `message` VALUES (552, 2, 20000, '的撒旦', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-20 17:44:38.203', '2024-05-20 17:44:38.209');
INSERT INTO `message` VALUES (553, 2, 20000, '的哈卡斯技术大会', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-20 19:00:52.433', '2024-05-20 19:00:52.439');
INSERT INTO `message` VALUES (554, 2, 20000, '杀手', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-20 19:00:55.602', '2024-05-20 19:00:55.605');
INSERT INTO `message` VALUES (555, 2, 20000, '的杀菌', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-20 19:00:57.307', '2024-05-20 19:00:57.311');
INSERT INTO `message` VALUES (556, 1, 20001, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-20 19:13:14.892', '2024-05-20 19:13:14.909');
INSERT INTO `message` VALUES (557, 1, 20001, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-20 19:13:29.471', '2024-05-20 19:13:29.477');
INSERT INTO `message` VALUES (558, 1, 20000, '的声卡好看', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-20 23:29:24.399', '2024-05-20 23:29:24.407');
INSERT INTO `message` VALUES (559, 1, 20000, '死死死', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-20 23:48:40.958', '2024-05-20 23:48:40.966');
INSERT INTO `message` VALUES (560, 1, 20000, '还想试试', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-20 23:51:35.937', '2024-05-20 23:51:35.944');
INSERT INTO `message` VALUES (561, 1, 20000, '针对', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-20 23:51:38.479', '2024-05-20 23:51:38.484');
INSERT INTO `message` VALUES (562, 1, 20000, '话说第哦啊是', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-20 23:53:11.027', '2024-05-20 23:53:11.034');
INSERT INTO `message` VALUES (563, 1, 20000, '的撒', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-20 23:53:27.318', '2024-05-20 23:53:27.325');
INSERT INTO `message` VALUES (564, 1, 20000, '的撒啊', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-20 23:56:18.510', '2024-05-20 23:56:18.516');
INSERT INTO `message` VALUES (565, 1, 20000, '糯米和', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-21 00:02:13.794', '2024-05-21 00:02:13.805');
INSERT INTO `message` VALUES (566, 1, 20000, '单卡双卡', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-21 00:05:42.426', '2024-05-21 00:05:42.435');
INSERT INTO `message` VALUES (567, 1, 20000, '好的凯撒好的卡', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-21 00:10:32.807', '2024-05-21 00:10:32.818');
INSERT INTO `message` VALUES (568, 1, 20000, '大飒飒', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-21 00:14:11.001', '2024-05-21 00:14:11.008');
INSERT INTO `message` VALUES (569, 1, 20000, '打撒十大', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-21 00:14:27.052', '2024-05-21 00:14:27.057');
INSERT INTO `message` VALUES (570, 1, 20000, '大撒大撒', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-21 00:15:25.137', '2024-05-21 00:15:25.141');
INSERT INTO `message` VALUES (571, 1, 20001, '吃什么呢', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-21 12:06:08.322', '2024-05-21 12:06:08.344');
INSERT INTO `message` VALUES (572, 1, 20001, '城市卡卡', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-21 12:06:27.958', '2024-05-21 12:06:27.963');
INSERT INTO `message` VALUES (573, 1, 20001, '的萨科', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-21 12:06:30.071', '2024-05-21 12:06:30.075');
INSERT INTO `message` VALUES (574, 1, 20001, '的萨科', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-21 12:06:31.756', '2024-05-21 12:06:31.760');
INSERT INTO `message` VALUES (575, 1, 20000, '很多刷卡', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-21 12:06:36.095', '2024-05-21 12:06:36.098');
INSERT INTO `message` VALUES (576, 1, 20000, '德国撒娇', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-21 12:06:37.793', '2024-05-21 12:06:37.795');
INSERT INTO `message` VALUES (577, 1, 20000, '的婚纱', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-21 12:06:39.055', '2024-05-21 12:06:39.058');
INSERT INTO `message` VALUES (578, 1, 20000, '大厦', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-21 12:06:40.141', '2024-05-21 12:06:40.143');
INSERT INTO `message` VALUES (579, 1, 20000, '大厦', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-21 12:06:41.177', '2024-05-21 12:06:41.179');
INSERT INTO `message` VALUES (580, 1, 20000, '大厦', NULL, 0, NULL, 2, '{\"recall\": {\"recallUid\": 20001, \"recallTime\": 1716265298741}, \"urlContentMap\": {}}', '2024-05-21 12:06:42.544', '2024-05-21 12:21:38.745');
INSERT INTO `message` VALUES (581, 1, 20001, '🌫️', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-21 12:06:51.985', '2024-05-21 12:06:51.988');
INSERT INTO `message` VALUES (582, 1, 20001, '💨', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-21 12:06:58.456', '2024-05-21 12:06:58.460');
INSERT INTO `message` VALUES (583, 2, 20001, '的撒', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-21 12:07:13.290', '2024-05-21 12:07:13.294');
INSERT INTO `message` VALUES (584, 2, 20001, '的就是拉拉', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-21 12:07:42.187', '2024-05-21 12:07:42.191');
INSERT INTO `message` VALUES (585, 2, 20001, '的撒大就l', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-21 12:07:49.427', '2024-05-21 12:07:49.429');
INSERT INTO `message` VALUES (586, 1, 20001, '的健康撒谎', NULL, 0, NULL, 2, '{\"recall\": {\"recallUid\": 20001, \"recallTime\": 1716265232660}, \"urlContentMap\": {}}', '2024-05-21 12:07:54.894', '2024-05-21 12:20:32.663');
INSERT INTO `message` VALUES (587, 1, 20001, '的哈萨克', NULL, 0, NULL, 2, '{\"recall\": {\"recallUid\": 20001, \"recallTime\": 1716265230160}, \"urlContentMap\": {}}', '2024-05-21 12:07:57.423', '2024-05-21 12:20:30.171');
INSERT INTO `message` VALUES (588, 1, 20001, '电话是卡卡', NULL, 0, NULL, 2, '{\"recall\": {\"recallUid\": 20001, \"recallTime\": 1716265260220}, \"urlContentMap\": {}}', '2024-05-21 12:07:59.373', '2024-05-21 12:21:00.224');
INSERT INTO `message` VALUES (589, 1, 20000, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-21 12:36:14.229', '2024-05-21 12:36:14.240');
INSERT INTO `message` VALUES (590, 1, 20000, '大厦尽快和扩大', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-21 12:36:20.444', '2024-05-21 12:36:20.448');
INSERT INTO `message` VALUES (591, 1, 20000, '的撒好看的', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-21 12:36:30.062', '2024-05-21 12:36:30.067');
INSERT INTO `message` VALUES (592, 1, 20000, '大厦尽快', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-21 12:36:31.465', '2024-05-21 12:36:31.471');
INSERT INTO `message` VALUES (593, 1, 20000, '的还是卡', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-21 12:36:32.810', '2024-05-21 12:36:32.814');
INSERT INTO `message` VALUES (594, 1, 20000, '大厦看收到', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-21 12:41:07.724', '2024-05-21 12:41:07.735');
INSERT INTO `message` VALUES (595, 1, 20000, '的急啊离开', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-21 12:41:09.957', '2024-05-21 12:41:09.962');
INSERT INTO `message` VALUES (596, 1, 20000, '的骄傲来得及', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-21 12:41:14.222', '2024-05-21 12:41:14.227');
INSERT INTO `message` VALUES (597, 1, 20000, '德哈卡', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-21 12:41:15.523', '2024-05-21 12:41:15.527');
INSERT INTO `message` VALUES (598, 1, 20000, '打开', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-21 12:41:16.879', '2024-05-21 12:41:16.886');
INSERT INTO `message` VALUES (599, 1, 20000, '的萨科', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-21 12:41:18.749', '2024-05-21 12:41:18.753');
INSERT INTO `message` VALUES (600, 1, 20000, '的萨科', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-21 12:41:20.226', '2024-05-21 12:41:20.233');
INSERT INTO `message` VALUES (601, 1, 20000, '轻易饿我去以恶', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-21 12:41:22.385', '2024-05-21 12:41:22.392');
INSERT INTO `message` VALUES (602, 1, 20000, '😀现在就去啊', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-21 12:41:28.272', '2024-05-21 12:41:28.276');
INSERT INTO `message` VALUES (603, 1, 20000, '等什么呢', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-21 12:41:32.916', '2024-05-21 12:41:32.921');
INSERT INTO `message` VALUES (604, 1, 20000, '😙', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-21 12:41:42.706', '2024-05-21 12:41:42.710');
INSERT INTO `message` VALUES (605, 1, 20000, '【】【】速度', NULL, 0, NULL, 2, '{\"recall\": {\"recallUid\": 20000, \"recallTime\": 1716272037340}, \"urlContentMap\": {}}', '2024-05-21 12:41:46.389', '2024-05-21 14:13:57.344');
INSERT INTO `message` VALUES (606, 1, 20000, '自己卡刷点卡', NULL, 0, NULL, 2, '{\"recall\": {\"recallUid\": 20000, \"recallTime\": 1716272031593}, \"urlContentMap\": {}}', '2024-05-21 12:41:49.395', '2024-05-21 14:13:51.603');
INSERT INTO `message` VALUES (607, 1, 20000, '的萨科', NULL, 0, NULL, 2, '{\"recall\": {\"recallUid\": 20000, \"recallTime\": 1716269517794}, \"urlContentMap\": {}}', '2024-05-21 12:41:51.397', '2024-05-21 13:31:57.797');
INSERT INTO `message` VALUES (608, 1, 20000, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-21 14:14:13.946', '2024-05-21 14:14:13.955');
INSERT INTO `message` VALUES (609, 2, 20000, '大撒大撒', NULL, 0, NULL, 2, '{\"recall\": {\"recallUid\": 20000, \"recallTime\": 1716724957712}, \"urlContentMap\": {}}', '2024-05-23 16:59:40.160', '2024-05-26 20:02:37.716');
INSERT INTO `message` VALUES (610, 1, 20000, '你们撒旦', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-23 16:59:46.173', '2024-05-23 16:59:46.178');
INSERT INTO `message` VALUES (611, 1, 20000, '的撒大和健身卡', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-23 17:32:19.274', '2024-05-23 17:32:19.289');
INSERT INTO `message` VALUES (612, 1, 20000, '大厦看看', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-23 17:32:20.781', '2024-05-23 17:32:20.784');
INSERT INTO `message` VALUES (613, 1, 20000, '很多凯撒看', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-23 17:32:21.768', '2024-05-23 17:32:21.771');
INSERT INTO `message` VALUES (614, 1, 20000, '很多刷卡', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-23 17:32:22.754', '2024-05-23 17:32:22.757');
INSERT INTO `message` VALUES (615, 1, 20000, '的还是卡', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-23 17:32:23.693', '2024-05-23 17:32:23.696');
INSERT INTO `message` VALUES (616, 1, 20000, '的哈萨克', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-23 17:32:24.883', '2024-05-23 17:32:24.888');
INSERT INTO `message` VALUES (617, 1, 20000, '的健身卡', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-23 17:32:26.724', '2024-05-23 17:32:26.728');
INSERT INTO `message` VALUES (618, 1, 20000, '电话卡', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-23 17:32:28.252', '2024-05-23 17:32:28.257');
INSERT INTO `message` VALUES (619, 1, 20000, '的哈萨克', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-23 17:32:29.380', '2024-05-23 17:32:29.384');
INSERT INTO `message` VALUES (620, 1, 20000, '的还是卡', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-23 17:32:30.374', '2024-05-23 17:32:30.378');
INSERT INTO `message` VALUES (621, 1, 20000, '你们好', NULL, 0, NULL, 2, '{\"recall\": {\"recallUid\": 20000, \"recallTime\": 1716757853297}, \"urlContentMap\": {}}', '2024-05-23 17:32:33.653', '2024-05-27 05:10:53.302');
INSERT INTO `message` VALUES (622, 2, 20000, '😙', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-26 19:11:23.309', '2024-05-26 19:11:23.354');
INSERT INTO `message` VALUES (623, 2, 20000, '雷猴', NULL, 0, NULL, 2, '{\"recall\": {\"recallUid\": 20000, \"recallTime\": 1716724942840}, \"urlContentMap\": {}}', '2024-05-26 19:11:37.218', '2024-05-26 20:02:22.858');
INSERT INTO `message` VALUES (624, 2, 20000, '的还是卡对话框', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-26 20:03:03.730', '2024-05-26 20:03:03.761');
INSERT INTO `message` VALUES (625, 2, 20000, '大厦尽快', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-26 20:03:06.685', '2024-05-26 20:03:06.690');
INSERT INTO `message` VALUES (626, 2, 20000, '啊可是你', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-26 20:03:08.639', '2024-05-26 20:03:08.644');
INSERT INTO `message` VALUES (627, 2, 20000, '怎么可是', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-26 20:03:11.605', '2024-05-26 20:03:11.609');
INSERT INTO `message` VALUES (628, 2, 20000, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-27 05:20:00.101', '2024-05-27 05:20:00.112');
INSERT INTO `message` VALUES (629, 1, 20000, '😀', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-27 05:34:11.648', '2024-05-27 05:34:11.661');
INSERT INTO `message` VALUES (630, 1, 20000, '🫡', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-27 05:34:26.598', '2024-05-27 05:34:26.602');
INSERT INTO `message` VALUES (631, 1, 20000, '大会的看', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-27 05:34:29.552', '2024-05-27 05:34:29.558');
INSERT INTO `message` VALUES (632, 1, 20000, '大厦尽快', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-27 05:34:31.143', '2024-05-27 05:34:31.145');
INSERT INTO `message` VALUES (633, 1, 20000, '的萨科就', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-27 05:34:32.512', '2024-05-27 05:34:32.515');
INSERT INTO `message` VALUES (634, 1, 20000, '大厦看', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-27 05:34:33.581', '2024-05-27 05:34:33.583');
INSERT INTO `message` VALUES (635, 1, 20000, '大厦看', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-27 05:34:34.707', '2024-05-27 05:34:34.709');
INSERT INTO `message` VALUES (636, 1, 20000, '圣诞贺卡', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-27 05:34:36.302', '2024-05-27 05:34:36.308');
INSERT INTO `message` VALUES (637, 1, 20000, '大厦看', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-27 05:34:37.538', '2024-05-27 05:34:37.542');
INSERT INTO `message` VALUES (638, 1, 20000, '的萨拉', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-27 05:34:42.346', '2024-05-27 05:34:42.351');
INSERT INTO `message` VALUES (639, 1, 20000, '大厦看', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-27 05:34:43.605', '2024-05-27 05:34:43.611');
INSERT INTO `message` VALUES (640, 1, 20000, '的萨科', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-27 05:34:45.238', '2024-05-27 05:34:45.245');
INSERT INTO `message` VALUES (641, 1, 20000, '的撒大大', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-27 05:41:39.525', '2024-05-27 05:41:39.532');
INSERT INTO `message` VALUES (642, 1, 20000, '的撒大就哈', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-27 05:41:41.579', '2024-05-27 05:41:41.583');
INSERT INTO `message` VALUES (643, 1, 20000, '大厦', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-27 05:41:42.893', '2024-05-27 05:41:42.900');
INSERT INTO `message` VALUES (644, 1, 20000, '的还是卡就', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-27 05:41:44.017', '2024-05-27 05:41:44.019');
INSERT INTO `message` VALUES (645, 1, 20000, '的哈萨克', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-27 05:41:44.987', '2024-05-27 05:41:44.990');
INSERT INTO `message` VALUES (646, 1, 20000, '沙克', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-27 05:41:46.087', '2024-05-27 05:41:46.091');
INSERT INTO `message` VALUES (647, 1, 20000, '的撒', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-27 05:41:55.585', '2024-05-27 05:41:55.591');
INSERT INTO `message` VALUES (648, 1, 20000, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-27 05:46:04.260', '2024-05-27 05:46:04.267');
INSERT INTO `message` VALUES (649, 1, 20000, '的撒好看', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-27 05:46:06.060', '2024-05-27 05:46:06.065');
INSERT INTO `message` VALUES (650, 1, 20000, '很多凯撒', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-27 05:46:07.188', '2024-05-27 05:46:07.194');
INSERT INTO `message` VALUES (651, 1, 20000, '对巴萨尽快', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-27 05:46:08.339', '2024-05-27 05:46:08.344');
INSERT INTO `message` VALUES (652, 1, 20000, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-27 05:46:16.175', '2024-05-27 05:46:16.180');
INSERT INTO `message` VALUES (653, 1, 20000, '大厦看', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-27 05:46:17.945', '2024-05-27 05:46:17.951');
INSERT INTO `message` VALUES (654, 1, 20000, '的贺卡上', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-27 05:46:19.461', '2024-05-27 05:46:19.466');
INSERT INTO `message` VALUES (655, 1, 20000, '的萨科', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-27 05:46:43.264', '2024-05-27 05:46:43.269');
INSERT INTO `message` VALUES (656, 1, 20000, '大厦看', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-27 05:46:45.139', '2024-05-27 05:46:45.145');
INSERT INTO `message` VALUES (657, 1, 20000, '沙克', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-27 05:46:46.155', '2024-05-27 05:46:46.158');
INSERT INTO `message` VALUES (658, 1, 20000, '很多刷卡', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-27 05:46:47.297', '2024-05-27 05:46:47.302');
INSERT INTO `message` VALUES (659, 2, 20000, '12312撒大大撒', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-27 05:51:59.728', '2024-05-27 05:51:59.733');
INSERT INTO `message` VALUES (660, 2, 20000, '的撒好看', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-27 05:52:02.721', '2024-05-27 05:52:02.725');
INSERT INTO `message` VALUES (661, 2, 20000, '的哈萨克', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-27 05:52:03.944', '2024-05-27 05:52:03.949');
INSERT INTO `message` VALUES (662, 2, 20000, '🫡', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-27 05:52:08.719', '2024-05-27 05:52:08.723');
INSERT INTO `message` VALUES (663, 1, 20000, '大撒大撒', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-27 05:53:33.230', '2024-05-27 05:53:33.239');
INSERT INTO `message` VALUES (664, 1, 20000, '的撒大就', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-27 06:37:09.363', '2024-05-27 06:37:09.372');
INSERT INTO `message` VALUES (665, 1, 20000, '盛大互动空间', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-27 06:37:11.286', '2024-05-27 06:37:11.290');
INSERT INTO `message` VALUES (666, 1, 20000, '的撒', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-27 06:37:15.097', '2024-05-27 06:37:15.101');
INSERT INTO `message` VALUES (667, 1, 20000, '大家撒离开', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-27 06:37:18.600', '2024-05-27 06:37:18.604');
INSERT INTO `message` VALUES (668, 1, 20000, '的撒回到家看啥肯定会卡还贷款哈客户斯卡哈看啥计划就看啥客户端撒谎的看啥客户按时的客户凯撒好看的话凯撒好看的话凯撒好看哈卡刷点卡还是卡号啥客户端抗沙迪克哈卡刷点卡哈萨克的话空间哈萨克和打开杀害的空间还是卡号的就看啥肯定会刷卡机好的看啥肯定会萨克的还是卡号的是收到客户喀什看哈萨克的话卡刷点卡啥客户端声卡的沙皇的撒谎的刷卡啊哈萨克和的卡省的卡死了电话就ask和看哈萨克的凯撒看啥都', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-27 06:40:51.141', '2024-05-27 06:40:51.152');
INSERT INTO `message` VALUES (669, 1, 20000, '的撒好看', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-27 06:40:54.757', '2024-05-27 06:40:54.758');
INSERT INTO `message` VALUES (670, 1, 20000, '很多凯撒', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-27 06:40:56.747', '2024-05-27 06:40:56.749');
INSERT INTO `message` VALUES (671, 1, 20000, '的哈萨克', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-27 06:40:58.449', '2024-05-27 06:40:58.452');
INSERT INTO `message` VALUES (672, 1, 20000, '的撒', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-27 06:50:02.554', '2024-05-27 06:50:02.563');
INSERT INTO `message` VALUES (673, 1, 20000, '大厦看了', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-27 06:50:08.438', '2024-05-27 06:50:08.442');
INSERT INTO `message` VALUES (674, 1, 20000, '的撒', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-27 06:50:48.500', '2024-05-27 06:50:48.507');
INSERT INTO `message` VALUES (675, 1, 20000, '的撒后即可', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-27 06:50:49.991', '2024-05-27 06:50:49.995');
INSERT INTO `message` VALUES (676, 1, 20000, '的婚纱看', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-27 06:50:51.445', '2024-05-27 06:50:51.451');
INSERT INTO `message` VALUES (677, 1, 20000, '的环境撒', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-27 06:51:22.838', '2024-05-27 06:51:22.843');
INSERT INTO `message` VALUES (678, 1, 20000, '的还是卡', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-27 06:51:24.034', '2024-05-27 06:51:24.040');
INSERT INTO `message` VALUES (679, 1, 20000, '电视卡', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-27 06:51:25.653', '2024-05-27 06:51:25.657');
INSERT INTO `message` VALUES (680, 1, 20000, '电视卡', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-27 06:51:26.802', '2024-05-27 06:51:26.806');
INSERT INTO `message` VALUES (681, 1, 20000, '电视卡', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-27 06:51:27.862', '2024-05-27 06:51:27.866');
INSERT INTO `message` VALUES (682, 1, 20000, '的还是卡', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-27 06:51:28.935', '2024-05-27 06:51:28.939');
INSERT INTO `message` VALUES (683, 1, 20000, '按时打卡', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-27 06:51:30.029', '2024-05-27 06:51:30.033');
INSERT INTO `message` VALUES (684, 1, 20000, '按时打卡', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-27 06:51:30.999', '2024-05-27 06:51:31.002');
INSERT INTO `message` VALUES (685, 1, 20000, '阿斯顿哈卡斯', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-27 06:51:31.937', '2024-05-27 06:51:31.941');
INSERT INTO `message` VALUES (686, 1, 20000, '***', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-27 06:58:21.347', '2024-05-27 06:58:21.358');
INSERT INTO `message` VALUES (687, 1, 20000, '还是卡的很', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-27 06:58:36.979', '2024-05-27 06:58:36.984');
INSERT INTO `message` VALUES (688, 1, 20000, '怎么说', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-27 06:58:39.566', '2024-05-27 06:58:39.569');
INSERT INTO `message` VALUES (689, 1, 20000, '的撒娇和', NULL, 0, NULL, 2, '{\"recall\": {\"recallUid\": 20000, \"recallTime\": 1716889519579}, \"urlContentMap\": {}}', '2024-05-28 17:24:24.451', '2024-05-28 17:45:19.581');
INSERT INTO `message` VALUES (690, 2, 20000, '的还是卡换卡', NULL, 0, NULL, 2, '{\"recall\": {\"recallUid\": 20000, \"recallTime\": 1716889488183}, \"urlContentMap\": {}}', '2024-05-28 17:30:42.057', '2024-05-28 17:44:48.202');
INSERT INTO `message` VALUES (691, 1, 20000, '的撒', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-28 17:50:10.721', '2024-05-28 17:50:10.728');
INSERT INTO `message` VALUES (692, 1, 20000, '萨达', NULL, 0, NULL, 2, '{\"recall\": {\"recallUid\": 20000, \"recallTime\": 1716889834606}, \"urlContentMap\": {}}', '2024-05-28 17:50:14.417', '2024-05-28 17:50:34.610');
INSERT INTO `message` VALUES (693, 1, 20000, '的撒大', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-28 17:50:37.487', '2024-05-28 17:50:37.492');
INSERT INTO `message` VALUES (694, 1, 20000, '德国汉莎感觉', NULL, 0, NULL, 2, '{\"recall\": {\"recallUid\": 20000, \"recallTime\": 1716889867445}, \"urlContentMap\": {}}', '2024-05-28 17:51:03.670', '2024-05-28 17:51:07.449');
INSERT INTO `message` VALUES (695, 2, 20000, '的撒', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-28 17:51:22.168', '2024-05-28 17:51:22.176');
INSERT INTO `message` VALUES (696, 1, 20000, '大厦看', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-28 18:02:36.455', '2024-05-28 18:02:36.463');
INSERT INTO `message` VALUES (697, 1, 20000, '大家撒了解到了撒娇了到家啦是假的啦', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-28 18:02:39.828', '2024-05-28 18:02:39.832');
INSERT INTO `message` VALUES (698, 2, 20000, '大厦尽快的话', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-28 18:02:47.349', '2024-05-28 18:02:47.353');
INSERT INTO `message` VALUES (699, 2, 20000, '的撒回家', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-28 18:02:51.123', '2024-05-28 18:02:51.127');
INSERT INTO `message` VALUES (700, 1, 20000, '大厦看', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-28 18:02:56.014', '2024-05-28 18:02:56.019');
INSERT INTO `message` VALUES (701, 2, 20000, '的撒好看', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-28 18:08:14.022', '2024-05-28 18:08:14.029');
INSERT INTO `message` VALUES (702, 1, 20000, '123撒大大撒', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-28 18:13:08.958', '2024-05-28 18:13:08.966');
INSERT INTO `message` VALUES (703, 2, 20000, '的撒好看的', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-28 18:13:12.646', '2024-05-28 18:13:12.651');
INSERT INTO `message` VALUES (704, 2, 20000, '123128圣诞贺卡是', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-28 18:13:16.816', '2024-05-28 18:13:16.820');
INSERT INTO `message` VALUES (705, 2, 20000, '的撒好的凯撒好看', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-28 19:03:17.672', '2024-05-28 19:03:17.678');
INSERT INTO `message` VALUES (706, 1, 20000, '大厦看到好卡', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-28 19:03:22.695', '2024-05-28 19:03:22.701');
INSERT INTO `message` VALUES (707, 1, 20000, '的撒谎的看是', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-28 19:03:36.983', '2024-05-28 19:03:36.988');
INSERT INTO `message` VALUES (708, 1, 20000, '的啥客户端', NULL, 0, NULL, 2, '{\"recall\": {\"recallUid\": 20000, \"recallTime\": 1716894245500}, \"urlContentMap\": {}}', '2024-05-28 19:03:39.646', '2024-05-28 19:04:05.503');
INSERT INTO `message` VALUES (709, 2, 20000, '的撒韩国的金沙江', NULL, 0, NULL, 2, '{\"recall\": {\"recallUid\": 20000, \"recallTime\": 1716894240690}, \"urlContentMap\": {}}', '2024-05-28 19:03:44.648', '2024-05-28 19:04:00.693');
INSERT INTO `message` VALUES (710, 1, 20000, '大厦的还是卡', NULL, 0, NULL, 2, '{\"recall\": {\"recallUid\": 20000, \"recallTime\": 1716894290810}, \"urlContentMap\": {}}', '2024-05-28 19:04:47.647', '2024-05-28 19:04:50.813');
INSERT INTO `message` VALUES (711, 2, 20000, '大撒大撒', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-28 19:31:38.040', '2024-05-28 19:31:38.047');
INSERT INTO `message` VALUES (712, 2, 20000, '的撒大', NULL, 0, NULL, 2, '{\"recall\": {\"recallUid\": 20000, \"recallTime\": 1716896431098}, \"urlContentMap\": {}}', '2024-05-28 19:31:42.766', '2024-05-28 19:40:31.101');
INSERT INTO `message` VALUES (713, 2, 20000, '的撒大厦尽快和', NULL, 0, NULL, 2, '{\"recall\": {\"recallUid\": 20000, \"recallTime\": 1716896428401}, \"urlContentMap\": {}}', '2024-05-28 19:31:47.472', '2024-05-28 19:40:28.405');
INSERT INTO `message` VALUES (714, 1, 20000, '大厦看到', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-28 19:31:51.300', '2024-05-28 19:31:51.304');
INSERT INTO `message` VALUES (715, 2, 20000, '大吉大利撒娇了', NULL, 0, NULL, 2, '{\"recall\": {\"recallUid\": 20000, \"recallTime\": 1716896426299}, \"urlContentMap\": {}}', '2024-05-28 19:32:22.657', '2024-05-28 19:40:26.306');
INSERT INTO `message` VALUES (716, 1, 20000, '大厦的卡', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-28 19:32:25.667', '2024-05-28 19:32:25.671');
INSERT INTO `message` VALUES (717, 2, 20000, 'd\'g\'sja', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-28 21:47:47.098', '2024-05-28 21:47:47.103');
INSERT INTO `message` VALUES (718, 2, 20000, '的杀菌就', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-28 21:47:49.569', '2024-05-28 21:47:49.572');
INSERT INTO `message` VALUES (719, 2, 20000, '都干啥', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-28 21:47:50.752', '2024-05-28 21:47:50.756');
INSERT INTO `message` VALUES (720, 2, 20000, '德国撒娇', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-28 21:47:52.001', '2024-05-28 21:47:52.003');
INSERT INTO `message` VALUES (721, 2, 20000, '德国撒娇', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-28 21:47:53.105', '2024-05-28 21:47:53.109');
INSERT INTO `message` VALUES (722, 2, 20000, '的杀菌', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-28 21:47:54.263', '2024-05-28 21:47:54.266');
INSERT INTO `message` VALUES (723, 2, 20000, '的杀菌', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-28 21:47:55.403', '2024-05-28 21:47:55.406');
INSERT INTO `message` VALUES (724, 2, 20000, '的杀菌', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-28 21:47:56.438', '2024-05-28 21:47:56.442');
INSERT INTO `message` VALUES (725, 2, 20000, '都干啥', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-28 21:47:58.021', '2024-05-28 21:47:58.026');
INSERT INTO `message` VALUES (726, 2, 20000, '大厦', NULL, 0, NULL, 2, '{\"recall\": {\"recallUid\": 20000, \"recallTime\": 1716904082693}, \"urlContentMap\": {}}', '2024-05-28 21:47:59.521', '2024-05-28 21:48:02.696');
INSERT INTO `message` VALUES (727, 2, 20000, '大家撒感觉', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-28 21:52:39.569', '2024-05-28 21:52:39.580');
INSERT INTO `message` VALUES (728, 2, 20001, '的哈萨克和的', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-28 22:12:11.645', '2024-05-28 22:12:11.654');
INSERT INTO `message` VALUES (729, 2, 20001, '大厦看', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-28 22:12:35.091', '2024-05-28 22:12:35.099');
INSERT INTO `message` VALUES (730, 2, 20001, '很多刷卡', NULL, 0, NULL, 2, '{\"recall\": {\"recallUid\": 20000, \"recallTime\": 1716905563482}, \"urlContentMap\": {}}', '2024-05-28 22:12:37.471', '2024-05-28 22:12:43.484');
INSERT INTO `message` VALUES (731, 1, 20001, '大厦看的还是卡', NULL, 0, NULL, 2, '{\"recall\": {\"recallUid\": 20000, \"recallTime\": 1716905995785}, \"urlContentMap\": {}}', '2024-05-28 22:19:44.685', '2024-05-28 22:19:55.788');
INSERT INTO `message` VALUES (732, 1, 20001, '的哈萨克', NULL, 0, NULL, 2, '{\"recall\": {\"recallUid\": 20000, \"recallTime\": 1716908314292}, \"urlContentMap\": {}}', '2024-05-28 22:19:49.156', '2024-05-28 22:58:34.298');
INSERT INTO `message` VALUES (733, 2, 20001, '的撒大', NULL, 0, NULL, 2, '{\"recall\": {\"recallUid\": 20001, \"recallTime\": 1716906665900}, \"urlContentMap\": {}}', '2024-05-28 22:31:03.075', '2024-05-28 22:31:05.904');
INSERT INTO `message` VALUES (734, 2, 20001, '很多刷卡和', 727, 0, 5, 1, '{\"urlContentMap\": {}}', '2024-05-28 23:43:21.893', '2024-05-28 23:43:21.917');
INSERT INTO `message` VALUES (735, 2, 20000, '大师卡？', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-28 23:58:20.333', '2024-05-28 23:58:20.344');
INSERT INTO `message` VALUES (736, 2, 20000, '大撒大撒就l', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-28 23:58:42.282', '2024-05-28 23:58:42.286');
INSERT INTO `message` VALUES (737, 2, 20001, '大撒大撒', 736, 0, 1, 1, '{\"urlContentMap\": {}}', '2024-05-29 00:01:23.845', '2024-05-29 00:01:23.859');
INSERT INTO `message` VALUES (738, 2, 20001, '大撒大撒的撒大', 736, 0, 2, 1, '{\"urlContentMap\": {}}', '2024-05-29 00:01:49.123', '2024-05-29 00:01:49.129');
INSERT INTO `message` VALUES (739, 2, 20001, '都会受到卡卡', 717, 0, 20, 1, '{\"urlContentMap\": {}}', '2024-05-29 09:32:09.944', '2024-05-29 09:32:09.972');
INSERT INTO `message` VALUES (740, 2, 20001, '的话卡刷卡的', 717, 0, 21, 1, '{\"urlContentMap\": {}}', '2024-05-29 09:32:23.998', '2024-05-29 09:32:24.004');
INSERT INTO `message` VALUES (741, 2, 20001, '好看好看', 703, 0, 30, 1, '{\"urlContentMap\": {}}', '2024-05-29 09:34:16.594', '2024-05-29 09:34:16.605');
INSERT INTO `message` VALUES (742, 2, 20001, '大撒大撒', 727, 0, 13, 1, '{\"urlContentMap\": {}}', '2024-05-29 09:36:59.037', '2024-05-29 09:36:59.043');
INSERT INTO `message` VALUES (743, 2, 20001, '大撒大撒', 717, 0, 24, 1, '{\"urlContentMap\": {}}', '2024-05-29 09:37:12.431', '2024-05-29 09:37:12.439');
INSERT INTO `message` VALUES (744, 1, 20000, '<span id=\"aitSpan\" contenteditable=\"false\" class=\"text-#13987f select-none cursor-default\" style=\"user-select: text;\">@码外狂徒</span>&nbsp;你好', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-30 16:21:44.523', '2024-05-30 16:21:44.571');
INSERT INTO `message` VALUES (745, 1, 20001, '@码外狂徒 北师大', NULL, 0, NULL, 1, '{\"atUidList\": [20000], \"urlContentMap\": {}}', '2024-05-30 16:59:27.986', '2024-05-30 16:59:28.007');
INSERT INTO `message` VALUES (746, 1, 20000, '<span id=\"aitSpan\" contenteditable=\"false\" class=\"text-#13987f select-none cursor-default\" style=\"user-select: text;\">@养三百个嫩男模</span>&nbsp;萨达', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-30 17:12:02.514', '2024-05-30 17:12:02.521');
INSERT INTO `message` VALUES (747, 1, 20000, '大撒大的撒大<div>132撒旦撒</div><div>的撒大</div><div>撒大大啊</div>', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-30 18:49:30.305', '2024-05-30 18:49:30.318');
INSERT INTO `message` VALUES (748, 1, 20000, '<span id=\"aitSpan\" contenteditable=\"false\" class=\"text-#13987f select-none cursor-default\" style=\"user-select: text;\">@码外狂徒</span>&nbsp;<span id=\"aitSpan\" contenteditable=\"false\" class=\"text-#13987f select-none cursor-default\" style=\"user-select: text;\">@养三百个嫩男模</span>&nbsp;123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-30 20:23:12.763', '2024-05-30 20:23:12.802');
INSERT INTO `message` VALUES (749, 1, 20000, '<span id=\"aitSpan\" contenteditable=\"false\" class=\"text-#13987f select-none cursor-default\" style=\"user-select: text;\">@码外狂徒</span>&nbsp;萨达撒', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-30 20:23:54.473', '2024-05-30 20:23:54.479');
INSERT INTO `message` VALUES (750, 1, 20000, '的撒大<span id=\"aitSpan\" contenteditable=\"false\" class=\"text-#13987f select-none cursor-default\" style=\"user-select: text;\">@养三百个嫩男模</span>&nbsp;123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-30 20:24:19.918', '2024-05-30 20:24:19.923');
INSERT INTO `message` VALUES (751, 1, 20000, '<span id=\"aitSpan\" contenteditable=\"false\" class=\"text-#13987f select-none cursor-default\" style=\"user-select: text;\">@码外狂徒</span>&nbsp;<span id=\"aitSpan\" contenteditable=\"false\" class=\"text-#13987f select-none cursor-default\" style=\"user-select: text;\">@养三百个嫩男模</span>&nbsp;打撒十大', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-30 20:24:55.046', '2024-05-30 20:24:55.051');
INSERT INTO `message` VALUES (752, 1, 20000, '<span id=\"aitSpan\" contenteditable=\"false\" class=\"text-#13987f select-none cursor-default\" style=\"user-select: text;\">@码外狂徒</span>&nbsp;<span id=\"aitSpan\" contenteditable=\"false\" class=\"text-#13987f select-none cursor-default\" style=\"user-select: text;\">@养三百个嫩男模</span>&nbsp;色打撒打撒', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-30 20:25:10.247', '2024-05-30 20:25:10.252');
INSERT INTO `message` VALUES (753, 1, 20000, '<span id=\"aitSpan\" contenteditable=\"false\" class=\"text-#13987f select-none cursor-default\" style=\"user-select: text;\">@码外狂徒</span>&nbsp;<span id=\"aitSpan\" contenteditable=\"false\" class=\"text-#13987f select-none cursor-default\" style=\"user-select: text;\">@养三百个嫩男模</span>&nbsp;啊倒萨', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-30 20:25:51.489', '2024-05-30 20:25:51.496');
INSERT INTO `message` VALUES (754, 1, 20000, '<span id=\"aitSpan\" contenteditable=\"false\" class=\"text-#13987f select-none cursor-default\" style=\"user-select: text;\">@码外狂徒</span>&nbsp;<span id=\"aitSpan\" contenteditable=\"false\" class=\"text-#13987f select-none cursor-default\" style=\"user-select: text;\">@养三百个嫩男模</span>&nbsp;大撒大撒', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-30 20:26:27.511', '2024-05-30 20:26:27.518');
INSERT INTO `message` VALUES (755, 1, 20000, '<span id=\"aitSpan\" contenteditable=\"false\" class=\"text-#13987f select-none cursor-default\" style=\"user-select: text;\">@码外狂徒</span>&nbsp;<span id=\"aitSpan\" contenteditable=\"false\" class=\"text-#13987f select-none cursor-default\" style=\"user-select: text;\">@养三百个嫩男模</span>&nbsp;大撒大撒', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-30 20:27:09.297', '2024-05-30 20:27:09.305');
INSERT INTO `message` VALUES (756, 1, 20000, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-30 20:27:48.393', '2024-05-30 20:27:48.406');
INSERT INTO `message` VALUES (757, 1, 20000, '的<span id=\"aitSpan\" contenteditable=\"false\" class=\"text-#13987f select-none cursor-default\" style=\"user-select: text;\">@码外狂徒</span>&nbsp;<span id=\"aitSpan\" contenteditable=\"false\" class=\"text-#13987f select-none cursor-default\" style=\"user-select: text;\">@养三百个嫩男模</span>&nbsp;的撒大', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-30 20:27:55.430', '2024-05-30 20:27:55.435');
INSERT INTO `message` VALUES (758, 1, 20000, '<span id=\"aitSpan\" contenteditable=\"false\" class=\"text-#13987f select-none cursor-default\" style=\"user-select: text;\">@养三百个嫩男模</span>&nbsp;的撒大<span id=\"aitSpan\" contenteditable=\"false\" class=\"text-#13987f select-none cursor-default\" style=\"user-select: text;\">@养三百个嫩男模</span>&nbsp;<span id=\"aitSpan\" contenteditable=\"false\" class=\"text-#13987f select-none cursor-default\" style=\"user-select: text;\">@码外狂徒</span>&nbsp;1231231', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-30 20:28:24.912', '2024-05-30 20:28:24.918');
INSERT INTO `message` VALUES (759, 1, 20000, '123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-30 20:28:39.740', '2024-05-30 20:28:39.744');
INSERT INTO `message` VALUES (760, 1, 20000, '@养三百个嫩男模 @码外狂徒 12312萨达萨达', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-05-30 20:29:05.011', '2024-05-30 20:29:05.016');
INSERT INTO `message` VALUES (761, 2, 20001, '的代价爱国', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-06-09 13:48:55.677', '2024-06-09 13:48:55.713');
INSERT INTO `message` VALUES (762, 2, 20001, '的沙皇的', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-06-09 13:49:00.094', '2024-06-09 13:49:00.099');
INSERT INTO `message` VALUES (763, 2, 20001, '大厦尽快', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-06-09 13:49:01.760', '2024-06-09 13:49:01.764');
INSERT INTO `message` VALUES (764, 2, 20001, '大厦看', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-06-09 13:49:03.130', '2024-06-09 13:49:03.132');
INSERT INTO `message` VALUES (765, 2, 20001, '的还是卡', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-06-09 13:49:04.207', '2024-06-09 13:49:04.209');
INSERT INTO `message` VALUES (766, 2, 20001, '的还是卡', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-06-09 13:49:05.249', '2024-06-09 13:49:05.252');
INSERT INTO `message` VALUES (767, 2, 20001, '的还是卡', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-06-09 13:49:06.292', '2024-06-09 13:49:06.293');
INSERT INTO `message` VALUES (768, 2, 20001, '你上面', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-06-09 13:49:08.177', '2024-06-09 13:49:08.182');
INSERT INTO `message` VALUES (769, 2, 20001, '萨拉', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-06-09 13:49:09.980', '2024-06-09 13:49:09.984');
INSERT INTO `message` VALUES (770, 2, 20001, '大厦看', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-06-09 13:49:11.248', '2024-06-09 13:49:11.250');
INSERT INTO `message` VALUES (771, 2, 20001, '大厦看', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-06-09 13:49:12.486', '2024-06-09 13:49:12.488');
INSERT INTO `message` VALUES (772, 2, 20001, '导航手机卡', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-06-09 13:49:13.778', '2024-06-09 13:49:13.780');
INSERT INTO `message` VALUES (773, 2, 20001, '的还是卡', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-06-09 13:49:15.123', '2024-06-09 13:49:15.126');
INSERT INTO `message` VALUES (774, 1, 20001, '出发', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-06-09 13:54:36.326', '2024-06-09 13:54:36.333');
INSERT INTO `message` VALUES (775, 1, 20001, '出发', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-06-09 13:54:38.666', '2024-06-09 13:54:38.668');
INSERT INTO `message` VALUES (776, 1, 20001, '的啥空间', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-06-09 13:54:39.969', '2024-06-09 13:54:39.971');
INSERT INTO `message` VALUES (777, 2, 20001, '出门', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-06-09 13:57:58.212', '2024-06-09 13:57:58.219');
INSERT INTO `message` VALUES (778, 1, 20001, '的撒', NULL, 0, NULL, 2, '{\"recall\": {\"recallUid\": 20000, \"recallTime\": 1718071675905}, \"urlContentMap\": {}}', '2024-06-09 13:58:10.955', '2024-06-11 10:07:55.909');
INSERT INTO `message` VALUES (779, 2, 20000, '很受打击啊', NULL, 0, NULL, 2, '{\"recall\": {\"recallUid\": 20000, \"recallTime\": 1718071660972}, \"urlContentMap\": {}}', '2024-06-11 10:00:59.136', '2024-06-11 10:07:40.979');
INSERT INTO `message` VALUES (780, 2, 20000, '大厦', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-06-11 10:01:03.826', '2024-06-11 10:01:03.830');
INSERT INTO `message` VALUES (781, 2, 20000, '的撒娇的', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-06-11 10:01:05.659', '2024-06-11 10:01:05.663');
INSERT INTO `message` VALUES (782, 2, 20000, '大厦酒店', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-06-11 10:01:07.563', '2024-06-11 10:01:07.567');
INSERT INTO `message` VALUES (783, 2, 20000, '说什么呢', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-06-11 10:01:10.969', '2024-06-11 10:01:10.974');
INSERT INTO `message` VALUES (784, 2, 20000, '还不是这样吗', NULL, 0, NULL, 2, '{\"recall\": {\"recallUid\": 20000, \"recallTime\": 1718071685191}, \"urlContentMap\": {}}', '2024-06-11 10:01:16.012', '2024-06-11 10:08:05.194');
INSERT INTO `message` VALUES (785, 1, 20000, '还是', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-06-11 10:08:32.954', '2024-06-11 10:08:32.963');
INSERT INTO `message` VALUES (786, 1, 20000, '@码外狂徒 123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-06-11 10:09:07.308', '2024-06-11 10:09:07.316');
INSERT INTO `message` VALUES (787, 1, 20000, '@养三百个嫩男模 123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-06-11 10:09:25.930', '2024-06-11 10:09:25.937');
INSERT INTO `message` VALUES (788, 1, 20000, NULL, NULL, 0, NULL, 3, '{\"imgMsgDTO\": {\"url\": null, \"size\": null, \"width\": null, \"height\": null}}', '2024-06-11 10:16:30.318', '2024-06-11 10:16:30.331');
INSERT INTO `message` VALUES (789, 1, 20000, '@养三百个嫩男模 123', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-06-11 11:33:52.230', '2024-06-11 11:33:52.236');
INSERT INTO `message` VALUES (790, 1, 20000, '的杀菌好的', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-06-11 12:00:24.127', '2024-06-11 12:00:24.134');
INSERT INTO `message` VALUES (791, 1, 20000, '的杀菌', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-06-11 12:00:25.618', '2024-06-11 12:00:25.623');
INSERT INTO `message` VALUES (792, 1, 20000, '的婚纱', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-06-11 12:00:27.208', '2024-06-11 12:00:27.216');
INSERT INTO `message` VALUES (793, 1, 20000, '的婚纱看', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-06-11 12:00:28.614', '2024-06-11 12:00:28.620');
INSERT INTO `message` VALUES (794, 1, 20000, '大厦看', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-06-11 12:00:29.999', '2024-06-11 12:00:30.004');
INSERT INTO `message` VALUES (795, 1, 20000, '大师卡', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-06-11 12:00:31.257', '2024-06-11 12:00:31.261');
INSERT INTO `message` VALUES (796, 1, 20000, '大厦看的', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-06-11 12:00:32.606', '2024-06-11 12:00:32.610');
INSERT INTO `message` VALUES (797, 1, 20000, '你好呀', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-06-11 14:06:49.592', '2024-06-11 14:06:49.602');
INSERT INTO `message` VALUES (798, 1, 20000, '下午号', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-06-11 14:07:55.003', '2024-06-11 14:07:55.009');
INSERT INTO `message` VALUES (799, 1, 20000, '吃饭了吗', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-06-11 14:07:57.742', '2024-06-11 14:07:57.746');
INSERT INTO `message` VALUES (800, 1, 20000, '快出来聊聊天', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-06-11 14:08:07.611', '2024-06-11 14:08:07.615');
INSERT INTO `message` VALUES (801, 2, 20000, '你好呀', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-06-11 14:09:17.973', '2024-06-11 14:09:17.985');
INSERT INTO `message` VALUES (802, 2, 20000, '什么什么的', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-06-11 14:09:21.577', '2024-06-11 14:09:21.581');
INSERT INTO `message` VALUES (803, 2, 20000, '什么什么的', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-06-11 14:09:32.275', '2024-06-11 14:09:32.279');
INSERT INTO `message` VALUES (804, 2, 20000, '你好呀', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-06-11 14:09:36.741', '2024-06-11 14:09:36.745');
INSERT INTO `message` VALUES (805, 2, 20000, '跟我一起聊聊天', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-06-11 14:09:46.426', '2024-06-11 14:09:46.431');
INSERT INTO `message` VALUES (806, 2, 20000, '你好呀', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-06-11 14:09:48.392', '2024-06-11 14:09:48.396');
INSERT INTO `message` VALUES (807, 2, 20000, '时代开始', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-06-11 14:09:50.762', '2024-06-11 14:09:50.768');
INSERT INTO `message` VALUES (808, 2, 20000, '出来聊聊天', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-06-11 14:09:59.588', '2024-06-11 14:09:59.593');
INSERT INTO `message` VALUES (809, 1, 20000, '你好呀', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-06-11 14:18:31.884', '2024-06-11 14:18:31.895');
INSERT INTO `message` VALUES (810, 2, 20001, '你好', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-06-13 08:58:13.827', '2024-06-13 08:58:13.860');
INSERT INTO `message` VALUES (811, 2, 20000, '你好', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-06-13 08:58:25.122', '2024-06-13 08:58:25.129');
INSERT INTO `message` VALUES (812, 2, 20001, '你好呀', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-06-13 08:58:36.539', '2024-06-13 08:58:36.544');
INSERT INTO `message` VALUES (813, 2, 20000, '早上好', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-06-13 09:04:18.474', '2024-06-13 09:04:18.482');
INSERT INTO `message` VALUES (814, 1, 20000, '早上好', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-06-13 09:04:39.482', '2024-06-13 09:04:39.492');
INSERT INTO `message` VALUES (815, 1, 20000, '你好', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-06-18 10:24:03.811', '2024-06-18 10:24:03.854');
INSERT INTO `message` VALUES (816, 1, 20000, '的杀菌还是打', NULL, 0, NULL, 2, '{\"recall\": {\"recallUid\": 20000, \"recallTime\": 1718678461108}, \"urlContentMap\": {}}', '2024-06-18 10:24:09.877', '2024-06-18 10:41:01.125');
INSERT INTO `message` VALUES (817, 2, 20000, '好的卡刷点卡', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-06-18 10:26:45.250', '2024-06-18 10:26:45.258');
INSERT INTO `message` VALUES (818, 2, 20000, '大厦大家撒', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-06-18 10:26:47.482', '2024-06-18 10:26:47.486');
INSERT INTO `message` VALUES (819, 2, 20000, '你什么', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-06-18 10:26:50.615', '2024-06-18 10:26:50.621');
INSERT INTO `message` VALUES (820, 1, 20000, '💩😋🤗', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-06-18 10:27:54.647', '2024-06-18 10:27:54.657');
INSERT INTO `message` VALUES (821, 1, 20000, '😤', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-06-18 10:28:14.822', '2024-06-18 10:28:14.829');
INSERT INTO `message` VALUES (822, 2, 20000, '🤑', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-06-18 10:31:22.841', '2024-06-18 10:31:22.850');
INSERT INTO `message` VALUES (823, 2, 20000, '😝', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-06-18 10:39:20.667', '2024-06-18 10:39:20.673');
INSERT INTO `message` VALUES (824, 2, 20000, '回到家大厦看', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-06-18 10:39:33.831', '2024-06-18 10:39:33.834');
INSERT INTO `message` VALUES (825, 2, 20000, '大厦', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-06-18 10:39:35.833', '2024-06-18 10:39:35.837');
INSERT INTO `message` VALUES (826, 2, 20000, '大厦看', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-06-18 10:39:37.699', '2024-06-18 10:39:37.704');
INSERT INTO `message` VALUES (827, 1, 20000, '😤', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-06-18 10:45:37.298', '2024-06-18 10:45:37.305');
INSERT INTO `message` VALUES (828, 1, 20000, '🫥', NULL, 0, NULL, 1, '{\"urlContentMap\": {}}', '2024-06-18 10:45:45.968', '2024-06-18 10:45:45.972');

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
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '消息标记表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of message_mark
-- ----------------------------
INSERT INTO `message_mark` VALUES (1, 469, 20001, 1, 0, '2024-05-18 15:56:54.089', '2024-05-18 15:56:54.089');
INSERT INTO `message_mark` VALUES (2, 468, 20001, 1, 0, '2024-05-18 15:56:56.780', '2024-05-18 15:56:56.780');
INSERT INTO `message_mark` VALUES (3, 466, 20001, 1, 0, '2024-05-18 15:57:00.566', '2024-05-18 15:57:00.566');
INSERT INTO `message_mark` VALUES (4, 467, 20001, 1, 0, '2024-05-18 15:57:02.213', '2024-05-18 15:57:02.213');
INSERT INTO `message_mark` VALUES (5, 464, 20001, 1, 0, '2024-05-18 15:57:05.394', '2024-05-18 15:57:05.394');
INSERT INTO `message_mark` VALUES (6, 465, 20001, 1, 0, '2024-05-18 15:57:12.452', '2024-05-18 15:57:42.682');
INSERT INTO `message_mark` VALUES (7, 463, 20001, 1, 0, '2024-05-18 15:57:15.087', '2024-05-18 15:57:15.087');
INSERT INTO `message_mark` VALUES (8, 462, 20001, 1, 0, '2024-05-18 15:57:16.561', '2024-05-18 15:57:16.561');
INSERT INTO `message_mark` VALUES (9, 461, 20001, 1, 0, '2024-05-18 15:57:18.130', '2024-05-18 15:57:18.130');
INSERT INTO `message_mark` VALUES (10, 745, 20001, 1, 0, '2024-05-30 18:45:10.697', '2024-05-30 18:45:10.697');

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
INSERT INTO `role` VALUES (1, '超级管理员', '2024-05-09 03:41:03.363', '2024-05-09 03:41:03.363');
INSERT INTO `role` VALUES (2, 'HuLa群聊管理员', '2024-05-09 03:41:03.364', '2024-05-11 19:36:33.609');

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
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '房间表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of room
-- ----------------------------
INSERT INTO `room` VALUES (1, 1, 1, '2024-06-18 10:45:45.968', 828, NULL, '2024-05-09 03:41:03.785', '2024-06-18 10:45:46.006');
INSERT INTO `room` VALUES (2, 2, 0, '2024-06-18 10:39:37.699', 826, NULL, '2024-05-18 12:12:02.861', '2024-06-18 10:39:37.733');

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
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '单聊房间表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of room_friend
-- ----------------------------
INSERT INTO `room_friend` VALUES (1, 2, 20000, 20001, '20000,20001', 0, '2024-05-18 12:12:02.868', '2024-05-18 12:12:02.868');

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
INSERT INTO `room_group` VALUES (1, 1, 'HuLa全员群', 'https://mallchat.cn/assets/logo-e81cd252.jpeg', NULL, 0, '2024-05-09 03:41:03.787', '2024-05-11 19:26:39.964');

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
) ENGINE = InnoDB AUTO_INCREMENT = 937 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '本地消息表' ROW_FORMAT = Dynamic;

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
  UNIQUE INDEX `uniq_open_id`(`open_id` ASC) USING BTREE,
  UNIQUE INDEX `uniq_name`(`name` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  INDEX `idx_update_time`(`update_time` ASC) USING BTREE,
  INDEX `idx_active_status_last_opt_time`(`active_status` ASC, `last_opt_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 20002 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, '系统消息', 'http://mms1.baidu.com/it/u=1979830414,2984779047&fm=253&app=138&f=JPEG&fmt=auto&q=75?w=500&h=500', NULL, '0', 2, '2023-07-01 11:58:24.605', NULL, NULL, 0, '2023-07-01 11:58:24.605', '2023-07-01 12:02:56.900');
INSERT INTO `user` VALUES (10001, 'ChatGPT', 'https://img1.baidu.com/it/u=3613958228,3522035000&fm=253&fmt=auto&app=120&f=JPEG?w=500&h=500', 0, '??', 2, '2023-06-29 17:03:03.357', NULL, NULL, 0, '2023-06-29 17:03:03.357', '2023-07-01 14:56:10.271');
INSERT INTO `user` VALUES (10002, 'ChatGLM2', 'http://mms1.baidu.com/it/u=1979830414,2984779047&fm=253&app=138&f=JPEG&fmt=auto&q=75?w=500&h=500', NULL, '450', 2, '2023-07-01 11:58:24.605', NULL, NULL, 0, '2023-07-01 11:58:24.605', '2023-07-01 12:02:56.900');
INSERT INTO `user` VALUES (20000, '原来是我创建的', 'https://thirdwx.qlogo.cn/mmopen/vi_32/LKA3sYSqmKHNNcVOAYiaaIh9kliar0Sr3QGVvtPiad2IO5PmCk8ojxGmtQnQW2u5N6SIUaSRgGIUd95jmr79JVsEg/132', 0, 'obxlc6xY1EsMyHbbRAAgWDCnOFHE', 2, '2024-06-18 10:47:26.354', '{\"createIp\": \"0:0:0:0:0:0:0:1\", \"updateIp\": \"0:0:0:0:0:0:0:1\", \"createIpDetail\": null, \"updateIpDetail\": null}', 3, 0, '2024-05-10 04:41:18.940', '2024-06-18 10:47:26.365');
INSERT INTO `user` VALUES (20001, '代码小白', 'https://thirdwx.qlogo.cn/mmopen/vi_32/OU9Y8o9bWLQZdsKDwx29sibVxicib1z9hicmbibejd3aIqNQibHftXyiahaMLxVyViaJJTGtqPiaicRLflAAVx4ibtxgkfMmQ/132', 0, 'obxlc6zI2SG1VwcdyKSzL_u4o1bg', 2, '2024-06-13 09:12:40.886', '{\"createIp\": \"0:0:0:0:0:0:0:1\", \"updateIp\": \"0:0:0:0:0:0:0:1\", \"createIpDetail\": null, \"updateIpDetail\": null}', 3, 0, '2024-05-10 04:50:25.407', '2024-06-13 09:12:40.959');

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
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_uid_target_id`(`uid` ASC, `target_id` ASC) USING BTREE,
  INDEX `idx_target_id_read_status`(`target_id` ASC, `read_status` ASC) USING BTREE,
  INDEX `idx_target_id`(`target_id` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  INDEX `idx_update_time`(`update_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户申请表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_apply
-- ----------------------------
INSERT INTO `user_apply` VALUES (1, 20000, 1, 20001, '加个好友吧', 2, 2, '2024-05-15 09:51:18.874', '2024-05-18 12:12:02.795');

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
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户背包表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user_backpack
-- ----------------------------
INSERT INTO `user_backpack` VALUES (1, 20000, 3, 0, '3_1_20000', '2024-05-10 03:36:14.682', '2024-05-10 03:36:14.682');
INSERT INTO `user_backpack` VALUES (2, 20000, 1, 1, '1_1_20000', '2024-05-10 03:36:14.716', '2024-06-11 14:07:30.278');
INSERT INTO `user_backpack` VALUES (3, 20002, 1, 0, '1_1_20002', '2024-05-10 04:29:44.208', '2024-05-10 04:29:44.208');
INSERT INTO `user_backpack` VALUES (4, 20002, 3, 0, '3_1_20002', '2024-05-10 04:29:44.244', '2024-05-10 04:29:44.244');
INSERT INTO `user_backpack` VALUES (5, 20001, 1, 1, '1_1_20001', '2024-05-10 04:50:25.674', '2024-05-14 21:42:07.902');
INSERT INTO `user_backpack` VALUES (6, 20001, 3, 0, '3_1_20001', '2024-05-10 04:50:25.674', '2024-05-10 04:50:25.674');

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
  `delete_status` int NOT NULL DEFAULT 0 COMMENT '逻辑删除(0-正常,1-删除)',
  `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `update_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_uid_friend_uid`(`uid` ASC, `friend_uid` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  INDEX `idx_update_time`(`update_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户联系人表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_friend
-- ----------------------------
INSERT INTO `user_friend` VALUES (1, 20001, 20000, 0, '2024-05-18 12:12:02.848', '2024-05-18 12:12:02.848');
INSERT INTO `user_friend` VALUES (2, 20000, 20001, 0, '2024-05-18 12:12:02.851', '2024-05-18 12:12:02.851');

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
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户角色关系表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_role
-- ----------------------------
INSERT INTO `user_role` VALUES (1, 20000, 1, '2024-05-18 12:00:29.550', '2024-05-26 20:06:56.869');

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
