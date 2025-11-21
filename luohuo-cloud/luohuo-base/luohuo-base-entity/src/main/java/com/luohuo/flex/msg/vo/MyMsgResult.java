package com.luohuo.flex.msg.vo;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import com.luohuo.flex.msg.vo.result.ExtendNoticeResultVO;

import java.io.Serializable;

/**
 * 我的消息
 *
 * @author 乾乾
 * @date 2021/8/8 23:48
 */
@Data
@EqualsAndHashCode
@Schema(description = "我的消息")
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MyMsgResult implements Serializable {
    /** 待办 */
    private IPage<ExtendNoticeResultVO> todoList;
    /** 提醒 */
    private IPage<ExtendNoticeResultVO> noticeList;
    /** 预警 */
    private IPage<ExtendNoticeResultVO> earlyWarningList;
}
