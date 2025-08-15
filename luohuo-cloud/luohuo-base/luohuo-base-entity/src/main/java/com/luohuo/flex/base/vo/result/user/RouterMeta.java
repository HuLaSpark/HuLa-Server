package com.luohuo.flex.base.vo.result.user;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;
import java.util.LinkedHashMap;

/**
 * Vue路由 Meta
 *
 * @author zuihou
 * @date 2019-10-20 15:17
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RouterMeta extends LinkedHashMap<String, Object> implements Serializable {

    private static final long serialVersionUID = 5499925008927195914L;


    @JsonIgnore
    private final RouterMetaConfig routerMetaConfig;

    public RouterMeta() {
        this(null);
    }

    /**
     * 构造
     *
     * @param routerMetaConfig 配置
     */
    public RouterMeta(RouterMetaConfig routerMetaConfig) {
        this.routerMetaConfig = ObjectUtil.defaultIfNull(routerMetaConfig, RouterMetaConfig.DEFAULT_CONFIG);
    }

    @Schema(description = "标题")
    public String getTitle() {
        return Convert.toStr(this.get(routerMetaConfig.getTitleKey()));
    }

    public RouterMeta setTitle(String title) {
        this.put(routerMetaConfig.getTitleKey(), title);
        return this;
    }

    @Schema(description = "图标")
    public String getIcon() {
        return Convert.toStr(this.get(routerMetaConfig.getIconKey()));
    }

    public RouterMeta setIcon(String icon) {
        this.put(routerMetaConfig.getIconKey(), icon);
        return this;
    }

    @Schema(description = "是否固定标签")
    public Boolean getAffix() {
        return Convert.toBool(this.get(routerMetaConfig.getAffixKey()));
    }

    public RouterMeta setAffix(Boolean affix) {
        this.put(routerMetaConfig.getAffixKey(), affix);
        return this;
    }

    @Schema(description = "是否忽略KeepAlive缓存")
    public Boolean getIgnoreKeepAlive() {
        return Convert.toBool(this.get(routerMetaConfig.getIgnoreKeepAliveKey()));
    }

    public RouterMeta setIgnoreKeepAlive(Boolean ignoreKeepAlive) {
        this.put(routerMetaConfig.getIgnoreKeepAliveKey(), ignoreKeepAlive);
        return this;
    }

    @Schema(description = "内嵌iframe的地址")
    public String getFrameSrc() {
        return Convert.toStr(this.get(routerMetaConfig.getFrameSrcKey()));
    }

    public RouterMeta setFrameSrc(String frameSrc) {
        this.put(routerMetaConfig.getFrameSrcKey(), frameSrc);
        return this;
    }

    @Schema(description = "vben5 内嵌iframe的地址")
    public String getIframeSrc() {
        return Convert.toStr(this.get(routerMetaConfig.getIframeSrcKey()));
    }

    public RouterMeta setIframeSrc(String frameSrc) {
        this.put(routerMetaConfig.getIframeSrcKey(), frameSrc);
        return this;
    }

    @Schema(description = "vben5 外链地址")
    public String getLink() {
        return Convert.toStr(this.get(routerMetaConfig.getLinkKey()));
    }

    public RouterMeta setLink(String frameSrc) {
        this.put(routerMetaConfig.getLinkKey(), frameSrc);
        return this;
    }

    @Schema(description = "指定该路由切换的动画名")
    public String getTransitionName() {
        return Convert.toStr(this.get(routerMetaConfig.getTransitionNameKey()));
    }

    public RouterMeta setTransitionName(String transitionName) {
        this.put(routerMetaConfig.getTransitionNameKey(), transitionName);
        return this;
    }

    @Schema(description = "隐藏该路由在面包屑上面的显示")
    public Boolean getHideBreadcrumb() {
        return Convert.toBool(this.get(routerMetaConfig.getHideBreadcrumbKey()));
    }

    public RouterMeta setHideBreadcrumb(Boolean hideBreadcrumb) {
        this.put(routerMetaConfig.getHideBreadcrumbKey(), hideBreadcrumb);
        return this;
    }

    @Schema(description = "如果该路由会携带参数，且需要在tab页上面显示。则需要设置为true")
    public Boolean getCarryParam() {
        return Convert.toBool(this.get(routerMetaConfig.getCarryParamKey()));
    }

    public RouterMeta setCarryParam(Boolean carryParam) {
        this.put(routerMetaConfig.getCarryParamKey(), carryParam);
        return this;
    }

    @Schema(description = "当前激活的菜单。用于配置详情页时左侧激活的菜单路径")
    public String getCurrentActiveMenu() {
        return Convert.toStr(this.get(routerMetaConfig.getCurrentActiveMenuKey()));
    }

    public RouterMeta setCurrentActiveMenu(String currentActiveMenu) {
        this.put(routerMetaConfig.getCurrentActiveMenuKey(), currentActiveMenu);
        return this;
    }

    @Schema(description = "当前路由不再标签页显示")
    public Boolean getHideTab() {
        return Convert.toBool(this.get(routerMetaConfig.getHideTabKey()));
    }

    public RouterMeta setHideTab(Boolean hideTab) {
        this.put(routerMetaConfig.getHideTabKey(), hideTab);
        return this;
    }

    @Schema(description = "当前路由不再菜单显示")
    public Boolean getHideMenu() {
        return Convert.toBool(this.get(routerMetaConfig.getHideMenuKey()));
    }

    public RouterMeta setHideMenu(Boolean hideMenu) {
        this.put(routerMetaConfig.getHideMenuKey(), hideMenu);
        return this;
    }

    @Schema(description = "用于隐藏子菜单")
    public Boolean getHideChildrenInMenu() {
        return Convert.toBool(this.get(routerMetaConfig.getHideChildrenInMenuKey()));
    }

    public RouterMeta setHideChildrenInMenu(Boolean hideChildrenInMenu) {
        this.put(routerMetaConfig.getHideChildrenInMenuKey(), hideChildrenInMenu);
        return this;
    }

    @Schema(description = "菜单小圆点类型")
    public String getType() {
        return Convert.toStr(this.get(routerMetaConfig.getTypeKey()));
    }

    public RouterMeta setType(String type) {
        this.put(routerMetaConfig.getTypeKey(), type);
        return this;
    }

    @Schema(description = "是否显示内容")
    public String getContent() {
        return Convert.toStr(this.get(routerMetaConfig.getContentKey()));
    }

    public RouterMeta setContent(String content) {
        this.put(routerMetaConfig.getContentKey(), content);
        return this;
    }

    @Schema(description = "是否显示小圆点")
    public Boolean getDot() {
        return Convert.toBool(this.get(routerMetaConfig.getDotKey()));
    }

    @Schema(description = "是否显示小圆点")
    public RouterMeta setDot(Boolean dot) {
        this.put(routerMetaConfig.getDotKey(), dot);
        return this;
    }

    @Schema(description = "当前路由不再菜单显示 By Soybean")
    public Boolean getHideInMenu() {
        return Convert.toBool(this.get(routerMetaConfig.getHideInMenuKey()));
    }

    public RouterMeta setHideInMenu(Boolean hideMenu) {
        this.put(routerMetaConfig.getHideInMenuKey(), hideMenu);
        return this;
    }

    @Schema(description = "当前激活的菜单。用于配置详情页时左侧激活的菜单路径 By Soybean")
    public String getActiveMenu() {
        return Convert.toStr(this.get(routerMetaConfig.getActiveMenuKey()));
    }

    public RouterMeta setActiveMenu(String activeMenuKey) {
        this.put(routerMetaConfig.getActiveMenuKey(), activeMenuKey);
        return this;
    }


    @Schema(description = "当前激活的菜单。用于配置详情页时左侧激活的菜单路径 By Soybean")
    public String getActivePath() {
        return Convert.toStr(this.get(routerMetaConfig.getActivePathKey()));
    }

    public RouterMeta setActivePath(String activePath) {
        this.put(routerMetaConfig.getActivePathKey(), activePath);
        return this;
    }


    @Schema(description = "菜单标题 i18n key By Soybean")
    public String getI18nKey() {
        return Convert.toStr(this.get(routerMetaConfig.getI18nKeyKey()));
    }

    public RouterMeta setI18nKey(String val) {
        this.put(routerMetaConfig.getI18nKeyKey(), val);
        return this;
    }

    @Schema(description = "是否缓存路由 By Soybean")
    public String getKeepAlive() {
        return Convert.toStr(this.get(routerMetaConfig.getKeepAliveKey()));
    }

    public RouterMeta setKeepAlive(String val) {
        this.put(routerMetaConfig.getKeepAliveKey(), val);
        return this;
    }

    @Schema(description = "是否缓存路由 By Soybean")
    public Boolean getConstant() {
        return Convert.toBool(this.get(routerMetaConfig.getConstantKey()));
    }

    public RouterMeta setConstant(Boolean val) {
        this.put(routerMetaConfig.getConstantKey(), val);
        return this;
    }

    @Schema(description = "在 src/assets/svg-icon, ，如果设置了该图标，则icon将被忽略 By Soybean")
    public String getLocalIcon() {
        return Convert.toStr(this.get(routerMetaConfig.getLocalIconKey()));
    }

    public RouterMeta setLocalIcon(String val) {
        this.put(routerMetaConfig.getLocalIconKey(), val);
        return this;
    }

    @Schema(description = "排序 By Soybean")
    public Integer getOrder() {
        return Convert.toInt(this.get(routerMetaConfig.getOrderKey()));
    }

    public RouterMeta setOrder(Integer val) {
        this.put(routerMetaConfig.getOrderKey(), val);
        return this;
    }

    @Schema(description = "外部链接 By Soybean")
    public String getHref() {
        return Convert.toStr(this.get(routerMetaConfig.getHrefKey()));
    }

    public RouterMeta setHref(String val) {
        this.put(routerMetaConfig.getHrefKey(), val);
        return this;
    }

    @Schema(description = "默认情况下，同一路由路径将使用一个选项卡，如果设置为true，则将使用多个选项卡 By Soybean")
    public String getMultiTab() {
        return Convert.toStr(this.get(routerMetaConfig.getMultiTabKey()));
    }

    public RouterMeta setMultiTab(String val) {
        this.put(routerMetaConfig.getMultiTabKey(), val);
        return this;
    }

    @Schema(description = "如果设置，则路由将固定在选项卡中，并且该值是固定选项卡的顺序 By Soybean")
    public String getFixedIndexInTab() {
        return Convert.toStr(this.get(routerMetaConfig.getFixedIndexInTabKey()));
    }

    public RouterMeta setFixedIndexInTab(String val) {
        this.put(routerMetaConfig.getFixedIndexInTabKey(), val);
        return this;
    }

    @Schema(description = "组件")
    public String getComponent() {
        return Convert.toStr(this.get(routerMetaConfig.getComponentKey()));
    }

    public RouterMeta setComponent(String val) {
        this.put(routerMetaConfig.getComponentKey(), val);
        return this;
    }
}
