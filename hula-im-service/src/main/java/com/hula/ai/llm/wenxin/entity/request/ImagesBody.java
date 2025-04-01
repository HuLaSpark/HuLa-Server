package com.hula.ai.llm.wenxin.entity.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

/**
 * 文本生成图片请求
 *
 * @author: 云裂痕
 * @date: 2024/01/06
 * @version: 1.0.0
 * 得其道
 * 乾乾
 */
@Data
@Builder
@Slf4j
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class ImagesBody implements Serializable {

    /**
     * 提示词，即用户希望图片包含的元素
     */
    @NonNull
    private String prompt;

    /**
     * 反向提示词，即用户希望图片不包含的元素
     */
    @JsonProperty("negative_prompt")
    private String negativePrompt;

    /**
     * 生成图片长宽，默认值 1024x1024，取值范围如下：
     * ["768x768", "768x1024", "1024x768", "576x1024", "1024x576", "1024x1024"]
     */
    private String size = "1024*1024";

    /**
     * 生成图片数量，说明：
     * · 默认值为1
     * · 取值范围为1-4
     * · 单次生成的图片较多及请求较频繁可能导致请求超时
     */
    @Builder.Default
    private Integer n = 1;

    /**
     * 迭代轮次，说明：
     * · 默认值为20
     * · 取值范围为10-50
     */
    @Builder.Default
    private Integer steps = 20;

    /**
     * 采样方式，默认值：Euler a
     * 可选值如下：
     * · Euler
     * · Euler a
     * · DPM++ 2M
     * · DPM++ 2M Karras
     * · LMS Karras
     * · DPM++ SDE
     * · DPM++ SDE Karras
     * · DPM2 a Karras
     * · Heun
     * · DPM++ 2M SDE
     * · DPM++ 2M SDE Karras
     * · DPM2
     * · DPM2 Karras
     * · DPM2 a
     * · LMS
     */
    @JsonProperty("sampler_index")
    private String samplerIndex = "Euler a";

    /**
     * 随机种子，
     * 不设置时，自动生成随机数
     * 取值范围 [0, 4294967295]
     */
    private Integer seed;

    /**
     * 提示词相关性，说明：默认值为5，取值范围0-30
     */
    @JsonProperty("cfg_scale")
    @Builder.Default
    private Float cfgScale = 5f;

    /**
     * 生成风格。说明：
     * （1）可选值：
     * · Base：基础风格
     * · 3D Model：3D模型
     * · Analog Film：模拟胶片
     * · Anime：动漫
     * · Cinematic：电影
     * · Comic Book：漫画
     * · Craft Clay：工艺黏土
     * · Digital Art：数字艺术
     * · Enhance：增强
     * · Fantasy Art：幻想艺术
     * · lsometric：等距风格
     * · Line Art：线条艺术
     * · Lowpoly：低多边形
     * · Neonpunk：霓虹朋克
     * · Origami：折纸
     * · Photographic：摄影
     * · Pixel Art：像素艺术
     * · Texture：纹理
     * （2）默认值为Base
     */
    @Builder.Default
    private String style = "Base";


    @JsonProperty("user_id")
    private String userId;
}


