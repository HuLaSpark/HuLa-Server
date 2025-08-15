package com.luohuo.flex.oauth.vo.result;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import com.luohuo.flex.base.vo.result.user.VueRouter;

import java.io.Serializable;
import java.util.List;

/**
 * 拥有拥有的权限资源
 *
 * @author zuihou
 * @date 2020/11/28 12:15 下午
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@Schema(description = "权限资源")
public class VisibleResourceVO implements Serializable {
    @Schema(description = "是否启用URI/按钮权限")
    private Boolean enabled;
    @Schema(description = "是否区分大小写")
    private Boolean caseSensitive;
    @Schema(description = "拥有的资源编码")
    private List<String> resourceList;
    @Schema(description = "拥有的菜单路由")
    private List<VueRouter> routerList;
    @Schema(description = "拥有的角色编码")
    private List<String> roleList;
}
