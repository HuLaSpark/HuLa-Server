/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 80030 (8.0.30)
 Source Host           : 127.0.0.1:13306
 Source Schema         : luohuo_im_01

 Target Server Type    : MySQL
 Target Server Version : 80030 (8.0.30)
 File Encoding         : 65001

 Date: 21/11/2025 14:56:13
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for base_config
-- ----------------------------
DROP TABLE IF EXISTS `base_config`;
CREATE TABLE `base_config`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '参数主键',
  `type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '类型',
  `config_name` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT '' COMMENT '参数名称',
  `config_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT '' COMMENT '参数键名',
  `config_value` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL COMMENT '参数键值',
  `is_del` tinyint UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否删除',
  `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `update_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '修改时间',
  `create_by` bigint NOT NULL DEFAULT 3 COMMENT '创建人',
  `update_by` bigint NULL DEFAULT NULL COMMENT '最后更新人',
  `tenant_id` bigint NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `key`(`config_key` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 19 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '参数配置表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of base_config
-- ----------------------------
INSERT INTO `base_config` VALUES (1, 'system', '{\"title\":\"系统名称\",\"componentType\":\"text\",\"value\":\"Hula-IM\",\"configKey\":\"systemName\",\"type\":\"system\"}', 'systemName', 'HuLa', 0, '2025-06-26 07:42:06.404', '2025-06-26 07:42:06.494', 3, NULL, 0);
INSERT INTO `base_config` VALUES (2, 'system', '{\"title\":\"系统Logo\",\"componentType\":\"text\",\"value\":\"/static/img/Iogo.png\",\"configKey\":\"logo\",\"type\":\"system\"}', 'logo', '/static/img/Iogo.png', 0, '2025-06-26 07:42:06.404', '2025-06-26 07:42:06.494', 3, NULL, 0);
INSERT INTO `base_config` VALUES (3, 'qiniu_up_config', '{\"title\":\"空间域名 Domain\",\"componentType\":\"text\",\"value\":\"https://upload-z2.qiniup.com\",\"configKey\":\"qnUploadUrl\",\"type\":\"qiniu_up_config\"}', 'qnUploadUrl', 'https://up-z2.qiniup.com', 0, '2025-06-26 07:42:06.404', '2025-08-18 04:29:56.616', 3, NULL, 0);
INSERT INTO `base_config` VALUES (4, 'qiniu_up_config', '{\"title\":\"accessKey\",\"componentType\":\"text\",\"value\":\"231YmL0vGAYFTW-rHl4LgA5_\",\"configKey\":\"qnAccessKey\",\"type\":\"qiniu_up_config\"}', 'qnAccessKey', 'LXrRo6YhT22hy5yScLzQJAQaUGUJ', 0, '2025-06-26 07:42:06.404', '2025-10-11 03:43:10.679', 3, NULL, 0);
INSERT INTO `base_config` VALUES (5, 'qiniu_up_config', '{\"title\":\"SecretKey\",\"componentType\":\"text\",\"value\":\"2daTXFDEG9PuAy4TnvfR1o2\",\"configKey\":\"qnSecretKey\",\"type\":\"qiniu_up_config\"}', 'qnSecretKey', 'BYKKz6n222111VS-llemF2Hg', 0, '2025-06-26 07:42:06.404', '2025-10-11 03:43:05.090', 3, NULL, 0);
INSERT INTO `base_config` VALUES (6, 'qiniu_up_config', '{\"title\":\"存储空间名称\",\"componentType\":\"text\",\"value\":\"hula\",\"configKey\":\"qnStorageName\",\"type\":\"qiniu_up_config\"}', 'qnStorageName', 'hula-spark', 0, '2025-06-26 07:42:06.404', '2025-06-26 07:42:06.494', 3, NULL, 0);
INSERT INTO `base_config` VALUES (7, 'qiniu_up_config', '{\"title\":\"七牛云CDN（访问图片用的）\",\"componentType\":\"text\",\"value\":\"https://file.hula.com/\",\"configKey\":\"qnStorageCDN\",\"type\":\"qiniu_up_config\"}', 'qnStorageCDN', 'https://cdn.hulaspark.com', 0, '2025-06-26 07:42:06.404', '2025-06-26 07:42:06.494', 3, NULL, 0);
INSERT INTO `base_config` VALUES (8, 'system', '{\"title\":\"大群ID\",\"componentType\":\"text\",\"value\":\"1\",\"configKey\":\"roomGroupId\",\"type\":\"system\"}', 'roomGroupId', '1', 0, '2025-06-26 07:42:06.404', '2025-06-26 07:42:06.494', 3, NULL, 0);
INSERT INTO `base_config` VALUES (9, 'qiniu_up_config', '{\"title\":\"超过多少MB开启分片上传\",\"componentType\":\"text\",\"value\":\"500\",\"configKey\":\"turnSharSize\",\"type\":\"qiniu_up_config\"}', 'turnSharSize', '4', 0, '2025-06-26 07:42:06.404', '2025-06-26 07:42:06.494', 3, NULL, 0);
INSERT INTO `base_config` VALUES (10, 'qiniu_up_config', '{\"title\":\"分片大小\",\"componentType\":\"text\",\"value\":\"50\",\"configKey\":\"fragmentSize\",\"type\":\"shop_config\"}', 'fragmentSize', '2', 0, '2025-06-26 07:42:06.404', '2025-06-26 07:42:06.494', 3, NULL, 0);
INSERT INTO `base_config` VALUES (11, 'qiniu_up_config', '{\"title\":\"OSS引擎\",\"componentType\":\"text\",\"value\":\"qiniu\",\"configKey\":\"storageDefault\",\"type\":\"shop_config\"}', 'storageDefault', 'qiniu', 0, '2025-06-26 07:42:06.404', '2025-06-26 07:42:06.494', 3, NULL, 0);
INSERT INTO `base_config` VALUES (12, 'system', '{\"title\":\"Hula管理员邮箱\",\"componentType\":\"text\",\"value\":\"\",\"configKey\":\"masterEmail\",\"type\":\"system\"}', 'masterEmail', 'nongyehong919@163.com', 0, '2025-06-26 07:42:06.404', '2025-07-16 19:58:20.816', 3, NULL, 0);
INSERT INTO `base_config` VALUES (13, 'system', '{\"title\":\"AI基础信息\",\"componentType\":\"text\",\"value\":\"system/material/20250305/aX3YYCCpDf.png\",\"configKey\":\"baseInfo\",\"type\":\"site_config\"}', 'baseInfo', '{\"contentSecurity\":0,\"copyright\":\"© 2025 luohuo 粤ICP备2025455944号 深圳市络火科技有限公司\",\"descrip\":\"HulaAi，基于AI大模型api实现的ChatGPT服务，支持ChatGPT(3.5、4.0)模型，同时也支持国内文心一言(支持Stable-Diffusion-XL作图)、通义千问、讯飞星火、智谱清言(ChatGLM)等主流模型，支出同步响应及流式响应，完美呈现打印机效果。\",\"keywords\":[\"通义千问\",\"ChatGPT\",\"文心一言\",\"智谱清言\"],\"proxyServer\":\"\",\"siteTitle\":\"HulaAi\",\"domain\":\"https://gpt.panday94.xyz\",\"proxyType\":2,\"siteLogo\":\"\"}', 0, '2025-06-26 07:42:06.404', '2025-08-18 04:32:58.114', 3, NULL, 0);
INSERT INTO `base_config` VALUES (14, 'system', '{\"title\":\"AI 扩展配置\",\"componentType\":\"text\",\"value\":\"system/material/20250305/aX3YYCCpDf.png\",\"configKey\":\"extraInfo\",\"type\":\"site_config\"}', 'extraInfo', '{\"ossType\":1,\"smsType\":0}', 0, '2025-06-26 07:42:06.404', '2025-07-16 19:58:23.711', 3, NULL, 0);
INSERT INTO `base_config` VALUES (15, 'system', '{\"title\":\"AI AppInfo\",\"componentType\":\"text\",\"value\":\"system/material/20250305/aX3YYCCpDf.png\",\"configKey\":\"appInfo\",\"type\":\"site_config\"}', 'appInfo', '{\"h5Url\":\"https://gpt.panday94.xyz/h5\",\"isSms\":1,\"homeNotice\":\"确保合法合规使用，在运营过程中产生的一切问题后果自负，与作者无关。!\",\"isGptLimit\":0,\"isShare\":1,\"shareRewardNum\":\"20\",\"freeNum\":\"5\",\"isRedemption\":1}', 0, '2025-06-26 07:42:06.404', '2025-07-16 19:58:24.711', 3, NULL, 0);
INSERT INTO `base_config` VALUES (16, 'system', '{\"title\":\"AI 微信配置\",\"componentType\":\"text\",\"value\":\"system/material/20250305/aX3YYCCpDf.png\",\"configKey\":\"wxInfo\",\"type\":\"site_config\"}', 'wxInfo', '{\"mpLogin\":0,\"mpPay\":0,\"maAppId\":\"xx\",\"maSecret\":\"xx\",\"mpAppId\":\"xx\",\"mpSecret\":\"xx\",\"mchNo\":\"xx\",\"v3Secret\":\"xx\"}', 0, '2025-06-26 07:42:06.404', '2025-07-16 19:58:26.063', 3, NULL, 0);

-- ----------------------------
-- Table structure for im_announcements
-- ----------------------------
DROP TABLE IF EXISTS `im_announcements`;
CREATE TABLE `im_announcements`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `room_id` bigint NOT NULL COMMENT '群id',
  `uid` bigint NOT NULL COMMENT '发布者id',
  `content` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_ci NOT NULL COMMENT '公告内容',
  `top` tinyint(1) NOT NULL DEFAULT 0 COMMENT '顶置公告',
  `create_by` bigint NOT NULL DEFAULT 0 COMMENT '创建者',
  `create_time` datetime NOT NULL COMMENT '发布时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `is_del` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT 1,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 94625993966593 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_ci COMMENT = '聊天公告表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of im_announcements
-- ----------------------------

-- ----------------------------
-- Table structure for im_announcements_read_records
-- ----------------------------
DROP TABLE IF EXISTS `im_announcements_read_records`;
CREATE TABLE `im_announcements_read_records`  (
  `id` bigint NOT NULL,
  `announcements_id` bigint NOT NULL COMMENT '公告id',
  `uid` bigint NOT NULL COMMENT '阅读人id',
  `is_check` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否已读 0：未读 1：已读',
  `create_by` bigint NOT NULL DEFAULT 0 COMMENT '创建者',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `is_del` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT 1,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_ci COMMENT = '公告是否已读表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of im_announcements_read_records
-- ----------------------------

-- ----------------------------
-- Table structure for im_black
-- ----------------------------
DROP TABLE IF EXISTS `im_black`;
CREATE TABLE `im_black`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
  `type` int NOT NULL COMMENT '拉黑目标类型 1.ip 2uid',
  `target` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '拉黑目标',
  `deadline` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '截止时间',
  `create_by` bigint NOT NULL DEFAULT 0 COMMENT '创建者',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新者',
  `is_del` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
  `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `update_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '修改时间',
  `tenant_id` bigint NOT NULL DEFAULT 1,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_type_target`(`type` ASC, `target` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '黑名单' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of im_black
-- ----------------------------
INSERT INTO `im_black` VALUES (1, 2, '13191075985', '2026-05-24 21:14:07.000', 0, NULL, 0, '2025-05-22 21:57:36.198', '2025-05-25 00:12:24.291', 1);

-- ----------------------------
-- Table structure for im_contact
-- ----------------------------
DROP TABLE IF EXISTS `im_contact`;
CREATE TABLE `im_contact`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
  `uid` bigint NOT NULL COMMENT 'uid',
  `room_id` bigint NOT NULL COMMENT '房间id',
  `mute_notification` tinyint NOT NULL DEFAULT 0 COMMENT '免打扰',
  `shield` tinyint NOT NULL DEFAULT 0 COMMENT '屏蔽会话',
  `read_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '阅读到的时间',
  `top` tinyint NOT NULL DEFAULT 0 COMMENT '置顶消息',
  `hide` tinyint NOT NULL DEFAULT 0 COMMENT '隐藏会话',
  `active_time` datetime(3) NULL DEFAULT NULL COMMENT '会话内消息最后更新的时间(只有普通会话需要维护，全员会话不需要维护)',
  `last_msg_id` bigint NULL DEFAULT NULL COMMENT '会话最新消息id',
  `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `update_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '修改时间',
  `tenant_id` bigint NOT NULL DEFAULT 1,
  `create_by` bigint NOT NULL DEFAULT 0 COMMENT '创建者',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新者',
  `is_del` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uniq_uid_room_id`(`uid` ASC, `room_id` ASC) USING BTREE,
  INDEX `idx_room_id_read_time`(`room_id` ASC, `read_time` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  INDEX `idx_update_time`(`update_time` ASC) USING BTREE,
  INDEX `idx_contact_room_uid_hide`(`room_id` ASC, `uid` ASC, `hide` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 69082079603081 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '会话列表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of im_contact
-- ----------------------------
INSERT INTO `im_contact` VALUES (10937855681025, 10937855681024, 1, 0, 0, '2025-10-11 11:38:14.092', 1, 0, '2025-10-10 18:46:37.156', 82907955820032, '2025-03-27 04:23:08.420', '2025-10-11 11:38:14.092', 1, 0, 61170828519937, 0);
INSERT INTO `im_contact` VALUES (10937855681525, 1, 1, 0, 0, '2025-07-07 14:49:04.239', 1, 0, '2025-10-10 18:46:37.156', 82907955820032, '2025-03-27 04:23:08.420', '2025-10-10 10:46:38.197', 1, 0, NULL, 0);
INSERT INTO `im_contact` VALUES (10937855681526, 10937855681024, 11229133317122, 0, 0, '2025-10-11 11:38:15.691', 1, 0, '2025-10-10 18:46:37.156', 82907955820032, '2025-03-27 04:23:08.420', '2025-10-11 11:38:15.692', 1, 0, 61170828519937, 0);

-- ----------------------------
-- Table structure for im_feed
-- ----------------------------
DROP TABLE IF EXISTS `im_feed`;
CREATE TABLE `im_feed`  (
  `id` bigint NOT NULL,
  `uid` bigint NOT NULL COMMENT '用户id',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_ci NOT NULL COMMENT '朋友圈文案',
  `permission` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_ci NOT NULL DEFAULT '1' COMMENT 'privacy -> 私密 open -> 公开 partVisible -> 部分可见 notAnyone -> 不给谁看',
  `media_type` tinyint NULL DEFAULT NULL COMMENT '朋友圈内容类型（0: 纯文字 1: 图片, 2: 视频）',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `tenant_id` bigint NOT NULL DEFAULT 1,
  `create_by` bigint NOT NULL DEFAULT 1,
  `is_del` tinyint NOT NULL DEFAULT 0,
  `update_by` bigint NOT NULL DEFAULT 1,
  `update_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `uid`(`uid` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_ci COMMENT = '朋友圈表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of im_feed
-- ----------------------------

-- ----------------------------
-- Table structure for im_feed_comment
-- ----------------------------
DROP TABLE IF EXISTS `im_feed_comment`;
CREATE TABLE `im_feed_comment`  (
  `id` bigint NOT NULL,
  `feed_id` bigint NOT NULL COMMENT '朋友圈id',
  `uid` bigint NOT NULL COMMENT '评论人uid',
  `reply_comment_id` bigint NULL DEFAULT NULL COMMENT '回复的评论ID（如果是回复评论，则有值；如果是直接评论朋友圈，则为null）',
  `reply_uid` bigint NULL DEFAULT NULL COMMENT '被回复人的uid（如果是回复评论，则有值）',
  `content` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_ci NOT NULL COMMENT '评论内容',
  `tenant_id` bigint NOT NULL DEFAULT 1,
  `create_by` bigint NOT NULL DEFAULT 0 COMMENT '创建者',
  `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `update_by` bigint NOT NULL DEFAULT 0 COMMENT '更新者',
  `update_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '更新时间',
  `is_del` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_feed_id`(`feed_id` ASC) USING BTREE,
  INDEX `idx_uid`(`uid` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_ci COMMENT = '朋友圈评论表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of im_feed_comment
-- ----------------------------

-- ----------------------------
-- Table structure for im_feed_like
-- ----------------------------
DROP TABLE IF EXISTS `im_feed_like`;
CREATE TABLE `im_feed_like`  (
  `id` bigint NOT NULL,
  `feed_id` bigint NOT NULL COMMENT '朋友圈id',
  `uid` bigint NOT NULL COMMENT '点赞人uid',
  `tenant_id` bigint NOT NULL DEFAULT 1,
  `create_by` bigint NOT NULL DEFAULT 0 COMMENT '创建者',
  `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_feed_uid`(`feed_id` ASC, `uid` ASC) USING BTREE,
  INDEX `idx_uid`(`uid` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_ci COMMENT = '朋友圈点赞表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of im_feed_like
-- ----------------------------

-- ----------------------------
-- Table structure for im_feed_media
-- ----------------------------
DROP TABLE IF EXISTS `im_feed_media`;
CREATE TABLE `im_feed_media`  (
  `id` bigint NOT NULL,
  `feed_id` bigint NOT NULL COMMENT '朋友圈id',
  `url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_ci NOT NULL COMMENT '图片或视频的路径',
  `sort` int NOT NULL COMMENT '排序',
  `tenant_id` bigint NOT NULL DEFAULT 1,
  `create_by` bigint NOT NULL DEFAULT 0 COMMENT '创建者',
  `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `is_del` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
  `update_by` bigint NULL DEFAULT 0 COMMENT '更新者',
  `update_time` datetime(3) NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_ci COMMENT = '朋友圈资源表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of im_feed_media
-- ----------------------------

-- ----------------------------
-- Table structure for im_feed_target
-- ----------------------------
DROP TABLE IF EXISTS `im_feed_target`;
CREATE TABLE `im_feed_target`  (
  `id` bigint NOT NULL,
  `type` tinyint NOT NULL DEFAULT 1 COMMENT '1 -> 关联标签id 2 -> 关联用户id',
  `feed_id` bigint NOT NULL COMMENT '朋友圈id',
  `target_id` bigint NOT NULL COMMENT '标签id',
  `tenant_id` bigint NOT NULL DEFAULT 1,
  `create_by` bigint NOT NULL DEFAULT 0 COMMENT '创建者',
  `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `is_del` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
  `update_by` bigint NULL DEFAULT 0 COMMENT '更新者',
  `update_time` datetime(3) NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_ci COMMENT = '朋友圈可见表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of im_feed_target
-- ----------------------------

-- ----------------------------
-- Table structure for im_group_invite
-- ----------------------------
DROP TABLE IF EXISTS `im_group_invite`;
CREATE TABLE `im_group_invite`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `group_id` bigint NOT NULL COMMENT '群组ID',
  `inviter_uid` bigint NOT NULL COMMENT '邀请人ID',
  `invitee_uid` bigint NOT NULL COMMENT '被邀请人ID',
  `tenant_id` bigint NOT NULL DEFAULT 1,
  `state` tinyint NOT NULL DEFAULT 0 COMMENT '0=待审批,1=已同意,2=已拒绝',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `create_by` bigint NOT NULL DEFAULT 1 COMMENT '创建者',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新者',
  `is_del` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_invitee_state`(`invitee_uid` ASC, `state` ASC) USING BTREE,
  INDEX `idx_group`(`group_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 62581624628737 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '群组邀请记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of im_group_invite
-- ----------------------------
INSERT INTO `im_group_invite` VALUES (62581624628736, 26248738827266, 10937855681024, 11013365735936, 1, 0, '2025-08-15 16:37:03', '2025-08-15 16:37:03', 10937855681024, 61847994706433, 0);

-- ----------------------------
-- Table structure for im_group_member
-- ----------------------------
DROP TABLE IF EXISTS `im_group_member`;
CREATE TABLE `im_group_member`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
  `group_id` bigint NOT NULL COMMENT '群组id',
  `uid` bigint NOT NULL COMMENT '成员uid',
  `role_id` int NOT NULL COMMENT '成员角色 1群主 2管理员 3普通成员',
  `remark` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '群备注',
  `de_friend` tinyint NOT NULL DEFAULT 0 COMMENT '屏蔽群 1 -> 屏蔽 0 -> 正常',
  `my_name` varchar(48) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '我的群昵称',
  `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `update_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '修改时间',
  `tenant_id` bigint NOT NULL DEFAULT 1,
  `create_by` bigint NOT NULL DEFAULT 0 COMMENT '创建者',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新者',
  `is_del` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_group_id_role`(`group_id` ASC, `role_id` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  INDEX `idx_update_time`(`update_time` ASC) USING BTREE,
  INDEX `idx_group_member_uid_isdel_groupid`(`uid` ASC, `is_del` ASC, `group_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 98068775162882 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '群成员表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of im_group_member
-- ----------------------------
INSERT INTO `im_group_member` VALUES (10937855681026, 1, 10937855681024, 1, '', 0, 'Dawn', '2025-03-27 04:23:08.435', '2025-09-12 06:47:19.108', 1, 0, 61170828519937, 0);
INSERT INTO `im_group_member` VALUES (10937855681626, 1, 1, 3, '', 0, '', '2025-03-27 04:23:08.435', '2025-03-27 04:23:08.435', 1, 0, NULL, 0);

-- ----------------------------
-- Table structure for im_item_config
-- ----------------------------
DROP TABLE IF EXISTS `im_item_config`;
CREATE TABLE `im_item_config`  (
  `id` bigint UNSIGNED NOT NULL COMMENT 'id',
  `type` int NOT NULL COMMENT '物品类型 1改名卡 2徽章',
  `img` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '物品图片',
  `describe` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '物品功能描述',
  `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `update_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '修改时间',
  `tenant_id` bigint NOT NULL DEFAULT 1,
  `create_by` bigint NOT NULL DEFAULT 1 COMMENT '创建人',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新者',
  `is_del` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  INDEX `idx_update_time`(`update_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '功能物品配置表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of im_item_config
-- ----------------------------
INSERT INTO `im_item_config` VALUES (1, 1, NULL, '用户可以使用改名卡，更改自己的名字。HuLa名称全局唯一，快抢订你的专属昵称吧', '2023-03-25 22:27:30.511', '2024-05-11 19:37:03.965', 1, 1, NULL, 0);
INSERT INTO `im_item_config` VALUES (2, 2, 'https://cdn.hulaspark.com/badge/like.png', '爆赞徽章，单条消息被点赞超过10次，即可获得', '2023-05-07 17:50:31.090', '2025-03-26 17:47:00.273', 1, 1, NULL, 0);
INSERT INTO `im_item_config` VALUES (3, 2, 'https://cdn.hulaspark.com/badge/top10.png ', 'HuLa前10名注册的用户才能获得的专属徽章', '2023-05-07 17:50:31.100', '2025-03-26 17:47:04.452', 1, 1, NULL, 0);
INSERT INTO `im_item_config` VALUES (4, 2, 'https://cdn.hulaspark.com/badge/top100.png', 'HuLa前100名注册的用户才能获得的专属徽章', '2023-05-07 17:50:31.109', '2025-03-26 17:47:07.734', 1, 1, NULL, 0);
INSERT INTO `im_item_config` VALUES (5, 2, 'https://cdn.hulaspark.com/badge/planet.png', 'HuLa团队核心成员专属徽章', '2023-05-07 17:50:31.109', '2025-09-19 10:41:01.216', 1, 1, NULL, 0);
INSERT INTO `im_item_config` VALUES (6, 2, 'https://cdn.hulaspark.com/badge/active.png', 'HuLa项目贡献者专属徽章', '2023-05-07 17:50:31.109', '2025-03-26 17:47:13.855', 1, 1, NULL, 0);

-- ----------------------------
-- Table structure for im_message
-- ----------------------------
DROP TABLE IF EXISTS `im_message`;
CREATE TABLE `im_message`  (
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
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建人id',
  `update_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '修改时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新人id',
  `tenant_id` bigint NOT NULL DEFAULT 1,
  `is_del` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_room_id`(`room_id` ASC) USING BTREE,
  INDEX `idx_from_uid`(`from_uid` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  INDEX `idx_update_time`(`update_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 98069286867969 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '消息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of im_message
-- ----------------------------
INSERT INTO `im_message` VALUES (10939168498176, 1, 10937855681024, NULL, NULL, 0, NULL, 7, '{\"recall\": null, \"fileMsg\": null, \"atUidList\": null, \"imgMsgDTO\": null, \"soundMsgDTO\": null, \"videoMsgDTO\": null, \"emojisMsgDTO\": {\"url\": \"https://bbs-static.miyoushe.com/static/2024/10/24/1b9f7abf6e8338291f02e882a1ffec64_7988382128756120451.png\"}, \"urlContentMap\": null}', '2025-03-27 04:28:21.410', NULL, '2025-03-27 04:28:21.431', NULL, 1, 0);
INSERT INTO `im_message` VALUES (10939189469696, 1, 10937855681024, '更新咯', NULL, 0, NULL, 1, '{\"recall\": null, \"fileMsg\": null, \"atUidList\": null, \"imgMsgDTO\": null, \"soundMsgDTO\": null, \"videoMsgDTO\": null, \"emojisMsgDTO\": null, \"urlContentMap\": {}}', '2025-03-27 04:28:26.276', NULL, '2025-03-27 04:28:26.295', NULL, 1, 0);
INSERT INTO `im_message` VALUES (10939243995648, 1, 10937855681024, '大家快来占沙发', NULL, 0, NULL, 1, '{\"recall\": null, \"fileMsg\": null, \"atUidList\": null, \"imgMsgDTO\": null, \"soundMsgDTO\": null, \"videoMsgDTO\": null, \"emojisMsgDTO\": null, \"urlContentMap\": {}}', '2025-03-27 04:28:39.792', NULL, '2025-03-27 04:28:39.800', NULL, 1, 0);

-- ----------------------------
-- Table structure for im_message_mark
-- ----------------------------
DROP TABLE IF EXISTS `im_message_mark`;
CREATE TABLE `im_message_mark`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
  `msg_id` bigint NOT NULL COMMENT '消息表id',
  `uid` bigint NOT NULL COMMENT '标记人uid',
  `type` int NOT NULL COMMENT '标记类型 1点赞 2举报',
  `status` int NOT NULL COMMENT '消息状态 0正常 1取消',
  `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `update_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '修改时间',
  `tenant_id` bigint NOT NULL DEFAULT 1,
  `create_by` bigint NOT NULL DEFAULT 1 COMMENT '创建人',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新者',
  `is_del` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_msg_id`(`msg_id` ASC) USING BTREE,
  INDEX `idx_uid`(`uid` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  INDEX `idx_update_time`(`update_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 96691856460801 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '消息标记表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of im_message_mark
-- ----------------------------

-- ----------------------------
-- Table structure for im_notice
-- ----------------------------
DROP TABLE IF EXISTS `im_notice`;
CREATE TABLE `im_notice`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `event_type` tinyint NOT NULL COMMENT '通知类型:1-好友申请;2-群申请;3-群邀请;5-移除群成员;6-好友被申请;7-被邀请进群',
  `type` tinyint NOT NULL DEFAULT 1 COMMENT '通知类型 1群聊 2加好友',
  `sender_id` bigint NOT NULL COMMENT '发起人UID',
  `receiver_id` bigint NOT NULL COMMENT '接收人UID',
  `room_id` bigint NOT NULL COMMENT '房间ID',
  `apply_id` bigint NULL DEFAULT NULL COMMENT '申请ID',
  `operate_id` bigint NOT NULL COMMENT '房间ID',
  `content` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT '0' COMMENT '通知消息 [申请时填写]',
  `status` tinyint(1) NOT NULL DEFAULT 0 COMMENT '处理状态:0-未处理;1-已同意;2-已拒绝',
  `is_read` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否已读',
  `tenant_id` bigint NOT NULL COMMENT '租户id',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` bigint NOT NULL COMMENT '创建人',
  `update_by` bigint NOT NULL COMMENT '创建人',
  `is_del` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_receiver_type`(`receiver_id` ASC, `event_type` ASC) USING BTREE,
  INDEX `idx_sender`(`sender_id` ASC) USING BTREE,
  INDEX `idx_related`(`apply_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 98015092265987 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '统一通知表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of im_notice
-- ----------------------------

-- ----------------------------
-- Table structure for im_role
-- ----------------------------
DROP TABLE IF EXISTS `im_role`;
CREATE TABLE `im_role`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色名称',
  `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `update_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '修改时间',
  `tenant_id` bigint NOT NULL DEFAULT 1,
  `create_by` bigint NOT NULL DEFAULT 1 COMMENT '创建人',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新者',
  `is_del` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  INDEX `idx_update_time`(`update_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '角色表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of im_role
-- ----------------------------
INSERT INTO `im_role` VALUES (1, '超级管理员', '2024-07-10 11:17:15.089', '2024-07-10 11:17:15.089', 1, 1, NULL, 0);
INSERT INTO `im_role` VALUES (2, 'HuLa群聊管理员', '2024-07-10 11:17:15.091', '2024-11-26 12:00:22.452', 1, 1, NULL, 0);

-- ----------------------------
-- Table structure for im_room
-- ----------------------------
DROP TABLE IF EXISTS `im_room`;
CREATE TABLE `im_room`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
  `type` int NOT NULL COMMENT '房间类型 1群聊 2单聊',
  `hot_flag` int NULL DEFAULT 0 COMMENT '是否全员展示 0否 1是',
  `active_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '群最后消息的更新时间（热点群不需要写扩散，只更新这里）',
  `last_msg_id` bigint NULL DEFAULT NULL COMMENT '会话中的最后一条消息id',
  `ext_json` json NULL COMMENT '额外信息（根据不同类型房间有不同存储的东西）',
  `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `update_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '修改时间',
  `tenant_id` bigint NOT NULL DEFAULT 1,
  `create_by` bigint NOT NULL DEFAULT 1 COMMENT '创建人',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新者',
  `is_del` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  INDEX `idx_update_time`(`update_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 98068775162883 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '房间表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of im_room
-- ----------------------------
INSERT INTO `im_room` VALUES (1, 1, 1, '2025-11-21 14:52:20.024', 98069286867968, NULL, '2024-07-10 11:17:15.521', '2025-11-21 06:52:20.138', 1, 1, NULL, 0);

-- ----------------------------
-- Table structure for im_room_friend
-- ----------------------------
DROP TABLE IF EXISTS `im_room_friend`;
CREATE TABLE `im_room_friend`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
  `room_id` bigint NOT NULL COMMENT '房间id',
  `uid1` bigint NOT NULL COMMENT 'uid1（更小的uid）',
  `uid2` bigint NOT NULL COMMENT 'uid2（更大的uid）',
  `room_key` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '房间key由两个uid拼接，先做排序uid1_uid2',
  `de_friend1` int NOT NULL COMMENT '房间状态 0正常 1屏蔽  uid1 屏蔽 uid2',
  `de_friend2` int NOT NULL COMMENT '房间状态 0正常 1屏蔽  uid2 屏蔽 uid1',
  `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `update_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '修改时间',
  `tenant_id` bigint NOT NULL DEFAULT 1,
  `create_by` bigint NOT NULL DEFAULT 1 COMMENT '创建人',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新者',
  `is_del` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `room_key`(`room_key` ASC) USING BTREE,
  INDEX `idx_room_id`(`room_id` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  INDEX `idx_update_time`(`update_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 98068775162884 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '单聊房间表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of im_room_friend
-- ----------------------------
INSERT INTO `im_room_friend` VALUES (11229133317123, 11229133317122, 10937855681024, 1, '1,10937855681024', 0, 0, '2025-03-27 23:40:34.548', '2025-10-11 03:34:48.606', 1, 1, NULL, 0);

-- ----------------------------
-- Table structure for im_room_group
-- ----------------------------
DROP TABLE IF EXISTS `im_room_group`;
CREATE TABLE `im_room_group`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
  `room_id` bigint NOT NULL COMMENT '房间id',
  `account` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '群号',
  `name` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '群名称',
  `avatar` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '群头像',
  `allow_scan_enter` tinyint NOT NULL DEFAULT 1 COMMENT '允许扫码直接进群',
  `ext_json` json NULL COMMENT '额外信息（根据不同类型房间有不同存储的东西）',
  `is_del` int NOT NULL DEFAULT 0 COMMENT '逻辑删除(0-正常,1-删除)',
  `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `update_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '修改时间',
  `tenant_id` bigint NOT NULL DEFAULT 1,
  `create_by` bigint NOT NULL DEFAULT 1 COMMENT '创建人',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新者',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_room_id`(`room_id` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  INDEX `idx_update_time`(`update_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 97720639541763 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '群聊房间表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of im_room_group
-- ----------------------------
INSERT INTO `im_room_group` VALUES (1, 1, 'hula-ds240401', 'HuLa官方频道', 'https://cdn.hulaspark.com/avatar/hula.png', 1, NULL, 0, '2024-07-10 11:17:15.523', '2025-10-06 03:57:29.384', 1, 1, 61170828519937);

-- ----------------------------
-- Table structure for im_sensitive_word
-- ----------------------------
DROP TABLE IF EXISTS `im_sensitive_word`;
CREATE TABLE `im_sensitive_word`  (
  `word` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '敏感词',
  `tenant_id` bigint NOT NULL DEFAULT 1
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '敏感词库' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of im_sensitive_word
-- ----------------------------
INSERT INTO `im_sensitive_word` VALUES ('TMD', 1);
INSERT INTO `im_sensitive_word` VALUES ('tmd', 1);
INSERT INTO `im_sensitive_word` VALUES ('毒品', 1);
INSERT INTO `im_sensitive_word` VALUES ('卖淫', 1);
INSERT INTO `im_sensitive_word` VALUES ('习近平', 1);

-- ----------------------------
-- Table structure for im_target
-- ----------------------------
DROP TABLE IF EXISTS `im_target`;
CREATE TABLE `im_target`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `u_id` bigint NOT NULL COMMENT '用户id',
  `name` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_ci NOT NULL COMMENT '标签名',
  `icon` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_ci NOT NULL DEFAULT '' COMMENT '标签图标',
  `create_by` bigint NOT NULL COMMENT '创建者',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `is_del` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT 1,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `employee`(`u_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_ci COMMENT = '聊天的标签' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of im_target
-- ----------------------------

-- ----------------------------
-- Table structure for im_user
-- ----------------------------
DROP TABLE IF EXISTS `im_user`;
CREATE TABLE `im_user`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '用户id',
  `user_id` bigint NOT NULL DEFAULT 3 COMMENT 'def_user 的id',
  `user_type` tinyint NOT NULL DEFAULT 3 COMMENT '1: 系统用户，2：机器人，3：普通用户',
  `name` varchar(48) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '用户昵称',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '用户头像',
  `email` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '用户邮箱',
  `account` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '用户账号',
  `sex` int NULL DEFAULT NULL COMMENT '性别 1为男性，2为女性',
  `open_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '',
  `user_state_id` bigint NOT NULL DEFAULT 0 COMMENT '用户状态id',
  `last_opt_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '最后上下线时间',
  `ip_info` json NULL COMMENT 'ip信息',
  `item_id` bigint NULL DEFAULT NULL COMMENT '佩戴的徽章id',
  `state` int NULL DEFAULT 0 COMMENT '使用状态 0.正常 1拉黑',
  `resume` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '个人简介',
  `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `update_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '修改时间',
  `password` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '用户密码',
  `create_by` bigint NOT NULL DEFAULT 1 COMMENT '创建者',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新者',
  `is_del` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
  `avatar_update_time` datetime(3) NULL DEFAULT NULL COMMENT '头像修改时间',
  `context` tinyint NOT NULL DEFAULT 0 COMMENT '是否开启上下文[AI模块]',
  `tenant_id` bigint NOT NULL DEFAULT 1,
  `num` bigint NOT NULL DEFAULT 10 COMMENT '是否开启上下文[AI模块]',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  INDEX `idx_update_time`(`update_time` ASC) USING BTREE,
  INDEX `idx_active_status_last_opt_time`(`last_opt_time` ASC) USING BTREE,
  INDEX `account_UNIQUE`(`account` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 98068775162881 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of im_user
-- ----------------------------
INSERT INTO `im_user` VALUES (1, 61170828519936, 2, 'HuLa小管家', '022', '', 'bot', NULL, '', 0, '2025-07-07 15:27:01.711', '{\"createIp\": \"206.237.119.215\", \"updateIp\": \"120.231.232.41\", \"createIpDetail\": {\"ip\": \"206.237.119.215\", \"isp\": \"\", \"area\": \"\", \"city\": \"\", \"isp_id\": \"\", \"region\": \"\", \"city_id\": \"\", \"country\": \"美国\", \"region_id\": \"\", \"country_id\": \"US\"}, \"updateIpDetail\": {\"ip\": \"120.231.232.41\", \"isp\": \"移动\", \"area\": \"\", \"city\": \"\", \"isp_id\": \"100025\", \"region\": \"广东\", \"city_id\": \"\", \"country\": \"中国\", \"region_id\": \"440000\", \"country_id\": \"CN\"}}', 6, 0, '', '2025-03-27 04:23:08.393', '2025-11-14 08:39:22.859', 'k.23772439646234', 0, NULL, 0, '2025-05-09 18:24:37.089', 0, 1, 10);
INSERT INTO `im_user` VALUES (10937855681024, 61170828519937, 3, 'Dawn', 'https://cdn.hulaspark.com/avatar/2439646234/97320189485dca88dcc7a70054445a56.webp', '2439646234@qq.com', '13275346112', NULL, '', 23, '2025-07-30 15:31:57.651', '{\"createIp\": \"206.237.119.215\", \"updateIp\": \"121.35.47.142\", \"createIpDetail\": null, \"updateIpDetail\": {\"ip\": \"121.35.47.142\", \"isp\": \"电信\", \"area\": \"\", \"city\": \"深圳\", \"isp_id\": \"100017\", \"region\": \"广东\", \"city_id\": \"440300\", \"country\": \"中国\", \"region_id\": \"440000\", \"country_id\": \"CN\"}}', 6, 0, '', '2025-03-27 04:23:08.393', '2025-11-20 11:10:32.943', 'k.2439646234', 0, NULL, 0, '2025-09-20 21:35:31.415', 0, 1, 10);

-- ----------------------------
-- Table structure for im_user_apply
-- ----------------------------
DROP TABLE IF EXISTS `im_user_apply`;
CREATE TABLE `im_user_apply`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
  `uid` bigint NOT NULL COMMENT '申请人uid',
  `type` int NOT NULL COMMENT '申请类型 1群聊 2加好友',
  `room_id` bigint NOT NULL COMMENT '房间id',
  `target_id` bigint NOT NULL COMMENT '接收对象 type: 1 -> uid; type: 2 -> roomGroupId',
  `msg` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '申请信息',
  `status` int NOT NULL COMMENT '申请状态 1待审批 2同意',
  `apply_for` tinyint NOT NULL DEFAULT 0 COMMENT '主动申请加群',
  `read_status` int NOT NULL COMMENT '阅读状态 1未读 2已读',
  `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `update_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '修改时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '删除状态 0：未删 1 申请人删除 2 被申请人删除 3都删除',
  `tenant_id` bigint NOT NULL DEFAULT 1,
  `create_by` bigint NOT NULL COMMENT '创建者',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新者',
  `is_del` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_uid_target_id`(`uid` ASC, `target_id` ASC) USING BTREE,
  INDEX `idx_target_id_read_status`(`target_id` ASC, `read_status` ASC) USING BTREE,
  INDEX `idx_target_id`(`target_id` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  INDEX `idx_update_time`(`update_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 98015092265985 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户申请表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of im_user_apply
-- ----------------------------

-- ----------------------------
-- Table structure for im_user_backpack
-- ----------------------------
DROP TABLE IF EXISTS `im_user_backpack`;
CREATE TABLE `im_user_backpack`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
  `uid` bigint NOT NULL COMMENT 'uid',
  `item_id` bigint NOT NULL COMMENT '物品id',
  `status` int NOT NULL COMMENT '使用状态 0.待使用 1已使用',
  `idempotent` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '幂等号',
  `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `update_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '修改时间',
  `tenant_id` bigint NOT NULL DEFAULT 1,
  `create_by` bigint NOT NULL COMMENT '创建者',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新者',
  `is_del` tinyint NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uniq_idempotent`(`idempotent` ASC) USING BTREE,
  INDEX `idx_uid`(`uid` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  INDEX `idx_update_time`(`update_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 98068775162891 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户背包表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of im_user_backpack
-- ----------------------------
INSERT INTO `im_user_backpack` VALUES (10937855681029, 10937855681024, 6, 0, '6_1_10937855681024', '2025-03-27 04:27:34.443', '2025-03-27 04:27:34.443', 1, 0, NULL, 0);

-- ----------------------------
-- Table structure for im_user_block
-- ----------------------------
DROP TABLE IF EXISTS `im_user_block`;
CREATE TABLE `im_user_block`  (
  `id` bigint NOT NULL COMMENT '主键',
  `blocker_uid` bigint NOT NULL COMMENT '屏蔽方用户ID',
  `blocked_uid` bigint NOT NULL COMMENT '被屏蔽方用户ID',
  `created_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '屏蔽时间',
  `tenant_id` bigint NOT NULL DEFAULT 1,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_blocker_blocked`(`blocker_uid` ASC, `blocked_uid` ASC) USING BTREE,
  INDEX `idx_blocker`(`blocker_uid` ASC) USING BTREE,
  INDEX `idx_blocked`(`blocked_uid` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户屏蔽关系表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of im_user_block
-- ----------------------------

-- ----------------------------
-- Table structure for im_user_emoji
-- ----------------------------
DROP TABLE IF EXISTS `im_user_emoji`;
CREATE TABLE `im_user_emoji`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
  `uid` bigint NOT NULL COMMENT '用户表ID',
  `expression_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '表情地址',
  `is_del` int NOT NULL DEFAULT 0 COMMENT '逻辑删除(0-正常,1-删除)',
  `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `update_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '修改时间',
  `tenant_id` bigint NOT NULL DEFAULT 1,
  `create_by` bigint NOT NULL COMMENT '创建者',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新者',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `IDX_USER_EMOJIS_UID`(`uid` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 97749185974785 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户表情包' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of im_user_emoji
-- ----------------------------
INSERT INTO `im_user_emoji` VALUES (10951726244352, 10937855681024, 'https://cdn.hulaspark.com/chat/2439646234/c64611c3bbbcd197552b7f84ac4b709b.jpg', 0, '2025-03-27 05:18:15.512', '2025-03-27 05:18:15.512', 1, 0, NULL);

-- ----------------------------
-- Table structure for im_user_friend
-- ----------------------------
DROP TABLE IF EXISTS `im_user_friend`;
CREATE TABLE `im_user_friend`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
  `room_id` bigint NOT NULL DEFAULT 0 COMMENT '房间id',
  `uid` bigint NOT NULL COMMENT 'uid',
  `friend_uid` bigint NOT NULL COMMENT '好友uid',
  `is_temp` tinyint NOT NULL DEFAULT 0 COMMENT '临时会话',
  `temp_msg_count` tinyint(1) NOT NULL DEFAULT 0 COMMENT '临时消息计数',
  `temp_status` tinyint(1) NOT NULL DEFAULT 0 COMMENT 'false-未回复 true-已回复',
  `is_del` tinyint(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除(0-正常,1-删除)',
  `mute_notification` tinyint(1) NOT NULL DEFAULT 0 COMMENT '免打扰',
  `hide_my_posts` tinyint(1) NOT NULL DEFAULT 0 COMMENT '不让他看我（0-允许，1-禁止）',
  `hide_their_posts` tinyint(1) NOT NULL DEFAULT 0 COMMENT '不看他（0-允许，1-禁止）',
  `remark` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '好友备注',
  `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `update_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '修改时间',
  `tenant_id` bigint NOT NULL DEFAULT 1,
  `create_by` bigint NOT NULL COMMENT '创建者',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新者',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_uid_friend_uid`(`uid` ASC, `friend_uid` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  INDEX `idx_update_time`(`update_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 98068775162886 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户联系人表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of im_user_friend
-- ----------------------------
INSERT INTO `im_user_friend` VALUES (11367692149761, 11229133317122, 1, 10937855681024, 0, 0, 0, 0, 0, 0, 0, NULL, '2025-03-28 08:51:09.399', '2025-10-11 03:34:59.682', 1, 0, NULL);
INSERT INTO `im_user_friend` VALUES (11515012883456, 11229133317122, 10937855681024, 1, 0, 0, 0, 0, 0, 0, 0, NULL, '2025-03-28 18:36:33.214', '2025-10-11 03:35:00.200', 1, 0, NULL);

-- ----------------------------
-- Table structure for im_user_privacy
-- ----------------------------
DROP TABLE IF EXISTS `im_user_privacy`;
CREATE TABLE `im_user_privacy`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `uid` bigint NOT NULL COMMENT '用户ID',
  `is_private` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否私密账号',
  `allow_temp_session` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否允许临时会话',
  `searchable_by_phone` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否允许通过手机号搜索',
  `searchable_by_account` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否允许通过账号搜索',
  `searchable_by_username` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否允许通过用户名搜索',
  `show_online_status` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否显示在线状态',
  `allow_add_friend` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否允许添加好友',
  `allow_group_invite` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否允许群邀请',
  `hide_profile` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否隐藏个人资料',
  `create_by` bigint NOT NULL COMMENT '创建者',
  `create_time` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `update_time` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新者',
  `is_del` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT 1 COMMENT '租户ID',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_uid`(`uid` ASC) USING BTREE COMMENT '用户ID唯一索引'
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户隐私设置表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of im_user_privacy
-- ----------------------------

-- ----------------------------
-- Table structure for im_user_role
-- ----------------------------
DROP TABLE IF EXISTS `im_user_role`;
CREATE TABLE `im_user_role`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
  `uid` bigint NOT NULL COMMENT 'uid',
  `role_id` bigint NOT NULL COMMENT '角色id',
  `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `update_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '修改时间',
  `tenant_id` bigint NOT NULL DEFAULT 1,
  `create_by` bigint NOT NULL COMMENT '创建者',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新者',
  `is_del` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_uid`(`uid` ASC) USING BTREE,
  INDEX `idx_role_id`(`role_id` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  INDEX `idx_update_time`(`update_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户角色关系表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of im_user_role
-- ----------------------------

-- ----------------------------
-- Table structure for im_user_state
-- ----------------------------
DROP TABLE IF EXISTS `im_user_state`;
CREATE TABLE `im_user_state`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_ci NOT NULL COMMENT '状态名',
  `url` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_ci NOT NULL COMMENT '状态图标',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `is_del` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT 1,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 34 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_ci COMMENT = '聊天用户状态表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of im_user_state
-- ----------------------------
INSERT INTO `im_user_state` VALUES (1, '离开', '/status/leave.png', 1, '2025-02-17 11:39:49', NULL, NULL, 0, 1);
INSERT INTO `im_user_state` VALUES (2, '忙碌', '/status/busy.png', 1, '2025-02-17 11:39:49', NULL, NULL, 0, 1);
INSERT INTO `im_user_state` VALUES (3, '请勿打扰', '/status/IonBan.png', 1, '2025-02-17 11:39:49', NULL, NULL, 0, 1);
INSERT INTO `im_user_state` VALUES (4, '隐身', '/status/cloaking.png', 1, '2025-02-17 11:39:49', NULL, NULL, 0, 1);
INSERT INTO `im_user_state` VALUES (5, '今日天气', '/status/weather_3x.png', 1, '2025-02-17 11:39:49', NULL, NULL, 0, 1);
INSERT INTO `im_user_state` VALUES (6, '一言难尽', '/status/hardtosay@3x.png', 1, '2025-02-17 11:39:49', NULL, NULL, 0, 1);
INSERT INTO `im_user_state` VALUES (7, '我太难了', '/status/toohard@3x.png', 1, '2025-02-17 11:39:49', NULL, NULL, 0, 1);
INSERT INTO `im_user_state` VALUES (8, '难得糊涂', '/status/nandehutu.png', 1, '2025-02-17 11:39:49', NULL, NULL, 0, 1);
INSERT INTO `im_user_state` VALUES (9, '元气满满', '/status/fullofyuanqi@3x.png', 1, '2025-02-17 11:39:49', NULL, NULL, 0, 1);
INSERT INTO `im_user_state` VALUES (10, '嗨到飞起', '/status/happytofly@3x.png', 1, '2025-02-17 11:39:49', NULL, NULL, 0, 1);
INSERT INTO `im_user_state` VALUES (11, '水逆退散', '/status/luck@2x.png', 1, '2025-02-17 11:39:49', NULL, NULL, 0, 1);
INSERT INTO `im_user_state` VALUES (12, '好运锦鲤', '/status/jinli@2x.png', 1, '2025-02-17 11:39:49', NULL, NULL, 0, 1);
INSERT INTO `im_user_state` VALUES (13, '恋爱中', '/status/relationship_3x.png', 1, '2025-02-17 11:39:49', NULL, NULL, 0, 1);
INSERT INTO `im_user_state` VALUES (14, '我crush了', '/status/crush.png', 1, '2025-02-17 11:39:49', NULL, NULL, 0, 1);
INSERT INTO `im_user_state` VALUES (15, '被掏空', '/status/tkong.png', 1, '2025-02-17 11:39:49', NULL, NULL, 0, 1);
INSERT INTO `im_user_state` VALUES (16, '听歌中', '/status/music@2x.png', 1, '2025-02-17 11:39:49', NULL, NULL, 0, 1);
INSERT INTO `im_user_state` VALUES (17, '我没事', '/status/imfine_3x.png', 1, '2025-02-17 11:39:49', NULL, NULL, 0, 1);
INSERT INTO `im_user_state` VALUES (18, '学习中', '/status/study_3x.png', 1, '2025-02-17 11:39:49', NULL, NULL, 0, 1);
INSERT INTO `im_user_state` VALUES (19, '睡觉中', '/status/sleeping_3x.png', 1, '2025-02-17 11:39:49', NULL, NULL, 0, 1);
INSERT INTO `im_user_state` VALUES (20, '搬砖中', '/status/banzhuan.png', 1, '2025-02-17 11:39:49', NULL, NULL, 0, 1);
INSERT INTO `im_user_state` VALUES (21, '想静静', '/status/bequiet@3x.png', 1, '2025-02-17 11:39:49', NULL, NULL, 0, 1);
INSERT INTO `im_user_state` VALUES (22, '运动中', '/status/yundongzhong@2x.png', 1, '2025-02-17 11:39:49', NULL, NULL, 0, 1);
INSERT INTO `im_user_state` VALUES (23, '我想开了', '/status/woxiangkaile.png', 1, '2025-02-17 11:39:49', NULL, NULL, 0, 1);
INSERT INTO `im_user_state` VALUES (24, '信号弱', '/status/signal_3x.png', 1, '2025-02-17 11:39:49', NULL, NULL, 0, 1);
INSERT INTO `im_user_state` VALUES (25, '追剧中', '/status/tv_3x.png', 1, '2025-02-17 11:39:49', NULL, NULL, 0, 1);
INSERT INTO `im_user_state` VALUES (26, '美滋滋', '/status/meizizi@3x.png', 1, '2025-02-17 11:39:49', NULL, NULL, 0, 1);
INSERT INTO `im_user_state` VALUES (27, '摸鱼中', '/status/fish@2x.png', 1, '2025-02-17 11:39:49', NULL, NULL, 0, 1);
INSERT INTO `im_user_state` VALUES (28, '无聊中', '/status/boring@3x.png', 1, '2025-02-17 11:39:49', NULL, NULL, 0, 1);
INSERT INTO `im_user_state` VALUES (29, '悠哉哉', '/status/youzaizai@3x.png', 1, '2025-02-17 11:39:49', NULL, NULL, 0, 1);
INSERT INTO `im_user_state` VALUES (30, '去旅行', '/status/gototravel.png', 1, '2025-02-17 11:39:49', NULL, NULL, 0, 1);
INSERT INTO `im_user_state` VALUES (31, '游戏中', '/status/game_3x.png', 1, '2025-02-17 11:39:49', NULL, NULL, 0, 1);

-- ----------------------------
-- Table structure for im_user_target_rel
-- ----------------------------
DROP TABLE IF EXISTS `im_user_target_rel`;
CREATE TABLE `im_user_target_rel`  (
  `id` bigint NOT NULL,
  `u_id` bigint NOT NULL COMMENT '人员id',
  `friend_id` bigint NOT NULL COMMENT '被绑定的人员id',
  `target_id` bigint NOT NULL COMMENT '标签id',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `is_del` tinyint(1) NOT NULL COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT 1,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `employee`(`u_id` ASC, `friend_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_as_ci COMMENT = '人员标签关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of im_user_target_rel
-- ----------------------------

-- ----------------------------
-- Table structure for im_wx_msg
-- ----------------------------
DROP TABLE IF EXISTS `im_wx_msg`;
CREATE TABLE `im_wx_msg`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
  `open_id` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '微信openid用户标识',
  `msg` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户消息',
  `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `update_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '修改时间',
  `tenant_id` bigint NOT NULL DEFAULT 1,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_open_id`(`open_id` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  INDEX `idx_update_time`(`update_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '微信消息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of im_wx_msg
-- ----------------------------

-- ----------------------------
-- Table structure for secure_invoke_record
-- ----------------------------
DROP TABLE IF EXISTS `secure_invoke_record`;
CREATE TABLE `secure_invoke_record`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
  `secure_invoke_json` json NOT NULL COMMENT '请求快照参数json',
  `state` tinyint NOT NULL COMMENT '状态 1待执行 2已失败',
  `next_retry_time` datetime(3) NOT NULL COMMENT '下一次重试的时间',
  `retry_times` int NOT NULL COMMENT '已经重试的次数',
  `max_retry_times` int NOT NULL COMMENT '最大重试次数',
  `fail_reason` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '执行失败的堆栈',
  `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `update_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '修改时间',
  `create_by` bigint NOT NULL COMMENT '创建人',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新人',
  `is_del` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_next_retry_time`(`next_retry_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 98069286867970 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '本地消息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of secure_invoke_record
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
) ENGINE = InnoDB AUTO_INCREMENT = 235 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'DB;WorkerID Assigner for UID Generator' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of worker_node
-- ----------------------------

SET FOREIGN_KEY_CHECKS = 1;
