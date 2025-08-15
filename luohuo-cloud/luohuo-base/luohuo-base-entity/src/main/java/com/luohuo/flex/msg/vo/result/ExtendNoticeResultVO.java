package com.luohuo.flex.msg.vo.result;

import cn.hutool.core.map.MapUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import com.luohuo.basic.annotation.echo.Echo;
import com.luohuo.basic.base.entity.Entity;
import com.luohuo.basic.interfaces.echo.EchoVO;
import com.luohuo.flex.model.constant.EchoApi;
import com.luohuo.flex.model.constant.EchoDictType;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * <p>
 * 表单查询方法返回值VO
 * 通知表
 * </p>
 *
 * @author zuihou
 * @date 2022-07-04 15:51:37
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@Builder
@Schema(description = "通知表")
public class ExtendNoticeResultVO extends Entity<Long> implements Serializable, EchoVO {

    private static final long serialVersionUID = 1L;

    @Builder.Default
    private Map<String, Object> echoMap = MapUtil.newHashMap();

    @Schema(description = "ID")
    private Long id;
    /**
     * 消息ID
     */
    @Schema(description = "消息ID")
    private Long msgId;

    /**
     * 业务ID
     */
    @Schema(description = "业务ID")
    private String bizId;
    /**
     * 业务类型
     */
    @Schema(description = "业务类型")
    private String bizType;
    /**
     * 接收人
     */
    @Schema(description = "接收人")
    private Long recipientId;
    /**
     * 提醒方式;
     *
     * @Echo(api = EchoApi.DICTIONARY_ITEM_FEIGN_CLASS, dictType = EchoDictType.Base.NOTICE_REMIND_MODE)
     * [01-待办 02-预警 03-提醒]
     */
    @Schema(description = "提醒方式")
    @Echo(api = EchoApi.DICTIONARY_ITEM_FEIGN_CLASS, dictType = EchoDictType.Base.NOTICE_REMIND_MODE)
    private String remindMode;
    /**
     * 标题
     */
    @Schema(description = "标题")
    private String title;
    /**
     * 内容
     */
    @Schema(description = "内容")
    private String content;
    /**
     * 发布人
     */
    @Schema(description = "发布人")
    private String author;
    /**
     * 处理地址
     */
    @Schema(description = "处理地址")
    private String url;
    /**
     * 打开方式;
     *
     * @Echo(api = EchoApi.DICTIONARY_ITEM_FEIGN_CLASS, dictType = EchoDictType.Base.NOTICE_TARGET)
     * [01-页面 02-弹窗 03-新开窗口]
     */
    @Schema(description = "打开方式")
    @Echo(api = EchoApi.DICTIONARY_ITEM_FEIGN_CLASS, dictType = EchoDictType.Base.NOTICE_TARGET)
    private String target;
    /**
     * 自动已读
     */
    @Schema(description = "自动已读")
    private Boolean autoRead;
    /**
     * 处理时间
     */
    @Schema(description = "处理时间")
    private LocalDateTime handleTime;
    /**
     * 读取时间
     */
    @Schema(description = "读取时间")
    private LocalDateTime readTime;
    /**
     * 是否已读
     */
    @Schema(description = "是否已读")
    private Boolean isRead;
    /**
     * 是否处理
     */
    @Schema(description = "是否处理")
    private Boolean isHandle;
    /**
     * 所属组织
     */
    @Schema(description = "所属组织")
    private Long createdOrgId;


}
