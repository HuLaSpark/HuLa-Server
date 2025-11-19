package com.luohuo.flex.im.domain.entity.msg;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.luohuo.flex.im.domain.UrlInfo;
import com.luohuo.flex.im.domain.vo.response.msg.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 消息扩展属性
 *
 * @author nyh
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class MessageExtra implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    //url跳转链接
    private Map<String, UrlInfo> urlContentMap;
    //消息撤回详情
    private MsgRecallDTO recall;
    //艾特的uid
    private List<Long> atUidList;
    //文件消息
    private FileMsgDTO fileMsg;
    //图片消息
    private ImgMsgDTO imgMsgDTO;
    //语音消息
    private SoundMsgDTO soundMsgDTO;
    //文件消息
    private VideoMsgDTO videoMsgDTO;
	//公告消息
	private NoticeMsgDTO noticeMsgDTO;
	//合并的消息
	private MergeMsgDTO mergeMsgDTO;
	//视频扩展消息
	private VideoCallMsgDTO videoCallMsgDTO;
	//音频扩展消息
	private AudioCallMsgDTO audioCallMsgDTO;
	//地图扩展消息
	private MapMsgDTO mapMsgDTO;

	/**
     * 表情图片信息
     */
    private EmojisMsgDTO emojisMsgDTO;
}
