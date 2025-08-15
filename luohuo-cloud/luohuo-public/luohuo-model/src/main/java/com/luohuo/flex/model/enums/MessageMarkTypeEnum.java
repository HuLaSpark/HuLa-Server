package com.luohuo.flex.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 消息标记类型
 * @author nyh
 */
@AllArgsConstructor
@Getter
public enum MessageMarkTypeEnum {
	LIKE(1, "点赞", 10),
	DISLIKE(2, "不满", 5),
	// 新增表情互动类型
	HEART(3, "爱心", 15),       // 表达喜爱
	ANGRY(4, "愤怒", 20),      // 表达不满
	CELEBRATE(5, "礼炮", 30),  // 庆祝消息
	ROCKET(6, "火箭", 50),     // 超级认可
	LOL(7, "笑哭", 15),        // 觉得有趣
	APPLAUSE(8, "鼓掌", 25),   // 表示赞赏
	FLOWER(9, "鲜花", 20),    // 送花支持
	BOMB(10, "炸弹", 40),     // 强烈反对
	CONFUSED(11, "疑问", 15),  // 表示疑惑
	VICTORY(12, "胜利", 35),   // 庆祝胜利
	LIGHT(13, "灯光", 30),     // 打call支持
	MONEY(14, "红包", 45);     // 打赏支持

	private final Integer type;
	private final String desc;
	private final Integer riseNum; // 需要多少个标记升级 TODO 达到多少次数之后触发的效果

	private static final Map<Integer, MessageMarkTypeEnum> CACHE;

	static {
		CACHE = Arrays.stream(values()).collect(Collectors.toMap(
				MessageMarkTypeEnum::getType,
				Function.identity()
		));
	}

	public static MessageMarkTypeEnum of(Integer type) {
		return CACHE.get(type);
	}
}