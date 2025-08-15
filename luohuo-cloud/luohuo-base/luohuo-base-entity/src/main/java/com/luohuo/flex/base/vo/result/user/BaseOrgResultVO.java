package com.luohuo.flex.base.vo.result.user;


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
import com.luohuo.basic.base.entity.TreeEntity;
import com.luohuo.basic.interfaces.echo.EchoVO;
import com.luohuo.flex.model.constant.EchoApi;
import com.luohuo.flex.model.constant.EchoDictType;

import java.io.Serializable;
import java.util.Map;

/**
 * <p>
 * 实体类
 * 组织
 * </p>
 *
 * @author zuihou
 * @since 2021-10-18
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@Schema(description = "组织")
public class BaseOrgResultVO extends TreeEntity<BaseOrgResultVO, Long> implements Serializable, EchoVO {

    private static final long serialVersionUID = 1L;
    @Builder.Default
    private Map<String, Object> echoMap = MapUtil.newHashMap();

    @Schema(description = "主键")
    private Long id;

    /**
     * 名称
     */
    @Schema(description = "名称")
    
    private String name;
    /**
     * 类型;[10-单位 20-部门]@Echo(api = EchoApi.DICTIONARY_ITEM_FEIGN_CLASS, dictType = EchoDictType.Base.ORG_TYPE)
     */
    @Schema(description = "类型")
    @Echo(api = EchoApi.DICTIONARY_ITEM_FEIGN_CLASS, dictType = EchoDictType.Base.ORG_TYPE)
    
    private String type;
    /**
     * 简称
     */
    @Schema(description = "简称")
    
    private String shortName;
    /**
     * 父ID
     */
    @Schema(description = "父ID")
    
    private Long parentId;
    /**
     * 树层级
     */
    @Schema(description = "树层级")
    
    private Integer treeGrade;
    /**
     * 树路径;用id拼接树结构
     */
    @Schema(description = "树路径")
    
    private String treePath;
    /**
     * 排序
     */
    @Schema(description = "排序")
    
    private Integer sortValue;
    /**
     * 状态;[0-禁用 1-启用]
     */
    @Schema(description = "状态")
    
    private Boolean state;
    /**
     * 备注
     */
    @Schema(description = "备注")
    
    private String remarks;
}
