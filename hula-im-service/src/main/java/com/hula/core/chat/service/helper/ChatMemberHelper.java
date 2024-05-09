package com.hula.core.chat.service.helper;

import cn.hutool.core.lang.Pair;
import cn.hutool.core.util.StrUtil;
import com.hula.core.user.domain.enums.ChatActiveStatusEnum;

/**
 * 成员列表工具类
 * @author nyh
 */
public class ChatMemberHelper {
    private static final String SEPARATOR = "_";

    public static Pair<ChatActiveStatusEnum, String> getCursorPair(String cursor) {
        ChatActiveStatusEnum activeStatusEnum = ChatActiveStatusEnum.ONLINE;
        String timeCursor = null;
        if (StrUtil.isNotBlank(cursor)) {
            String activeStr = cursor.split(SEPARATOR)[0];
            String timeStr = cursor.split(SEPARATOR)[1];
            activeStatusEnum = ChatActiveStatusEnum.of(Integer.parseInt(activeStr));
            timeCursor = timeStr;
        }
        return Pair.of(activeStatusEnum, timeCursor);
    }

    public static String generateCursor(ChatActiveStatusEnum activeStatusEnum, String timeCursor) {
        return activeStatusEnum.getStatus() + SEPARATOR + timeCursor;
    }
}
