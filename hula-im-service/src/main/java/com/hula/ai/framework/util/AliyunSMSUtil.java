package com.hula.ai.framework.util;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.QuerySendDetailsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.QuerySendDetailsResponse;
import com.aliyuncs.dysmsapi.model.v20170525.SendBatchSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendBatchSmsResponse;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.hula.ai.common.constant.StringPoolConstant;
import com.hula.ai.config.dto.ExtraInfoDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 阿里云短信工具类
 *
 * @author: 云裂痕
 * @date: 2023/01/31
 * @version: 1.0.0
 * 得其道
 * 乾乾
 */
@Slf4j
@Component
public class AliyunSMSUtil {

    /**
     * 此处需要替换成开发者自己的AK(在阿里云访问控制台寻找)
     */
    private static String ACCESS_KEY_ID;

    private static String ACCESS_KEY_SECRET;

    /**
     * 短信签名
     */
    private static String SMS_SIGN;

    /**
     * 产品名称:云通信短信API产品,开发者无需替换
     */
    private static final String PRODUCT;

    /**
     * 产品域名,开发者无需替换
     */
    private static final String DOMAIN;


    /**
     * 静态块
     */
    static {
        //初始化PRODUCT
        PRODUCT = "Dysmsapi";
        //初始化产品域名
        DOMAIN = "dysmsapi.aliyuncs.com";
    }

    /**
     * 初始化配置信息
     */
    public static void initSmsInfo(ExtraInfoDTO smsInfo) {
        ACCESS_KEY_ID = smsInfo.getSmsKeyId();
        ACCESS_KEY_SECRET = smsInfo.getSmsKeySecret();
        SMS_SIGN = smsInfo.getSmsSign();
    }

    /**
     * 发送短信
     *
     * @param tel          手机号
     * @param templateCode 短信模板
     * @param map          短信参数
     */
    public static SendSmsResponse sendSms(ExtraInfoDTO smsInfo, String tel, String templateCode, Map<String, Object> map) {
        initSmsInfo(smsInfo);
        try {
            // 可自助调整超时时间
            System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
            System.setProperty("sun.net.client.defaultReadTimeout", "10000");

            // 初始化acsClient,暂不支持region化
            IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", ACCESS_KEY_ID, ACCESS_KEY_SECRET);
            DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", PRODUCT, DOMAIN);
            IAcsClient acsClient = new DefaultAcsClient(profile);

            // 组装请求对象-具体描述见控制台-文档部分内容
            SendSmsRequest request = new SendSmsRequest();
            // 必填:待发送手机号
            request.setPhoneNumbers(tel);
            request.setSignName(SMS_SIGN);
            // 必填:短信模板-可在短信控制台中找到
            request.setTemplateCode(templateCode);
            // 可选:模板中的变量替换JSON串,如模板内容为"亲爱的用户,您的验证码为${code}"时,此处的值为
            // request.setTemplateParam("{\"name\":\"Tom\", \"code\":\"123\"}");

            if (null != map && !map.isEmpty()) {
                StringBuffer sb = new StringBuffer();
                for (String key : map.keySet()) {
                    sb.append("'" + key + "':").append("'" + map.get(key) + "',");
                }
                String param = sb.substring(0, sb.length() - 1);
                request.setTemplateParam(StringPoolConstant.LEFT_BRACE + param + "}");
            }

            // 选填-上行短信扩展码(无特殊需求用户请忽略此字段)
            // request.setSmsUpExtendCode("90997");

            // 可选:outId为提供给业务方扩展字段,最终在短信回执消息中将此值带回给调用者
            request.setOutId("yourOutId");

            // hint 此处可能会抛出异常，注意catch
            SendSmsResponse smsResponse = acsClient.getAcsResponse(request);
            if (null != smsResponse && "OK".equals(smsResponse.getCode())) {
                log.info(tel + "短信发送成功！");
            } else {
                log.error(tel + "短信发送失败！" + (null == smsResponse ? StringPoolConstant.EMPTY : smsResponse.getMessage()));
            }
            return smsResponse;
        } catch (ClientException e) {
            e.printStackTrace();
            log.error("发送短信异常", e);
            return null;
        }
    }

    /**
     * 批量发送短信
     *
     * @param smsInfo
     * @param templateCode
     * @param signNameJson
     * @param templateParam
     * @return
     */
    public static SendBatchSmsResponse sendBatchSms(ExtraInfoDTO smsInfo, String phoneNumberJson, String templateCode, String signNameJson, String templateParam) {
        initSmsInfo(smsInfo);
        try {
            // 可自助调整超时时间
            System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
            System.setProperty("sun.net.client.defaultReadTimeout", "10000");

            // 初始化acsClient,暂不支持region化
            IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", ACCESS_KEY_ID, ACCESS_KEY_SECRET);
            DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", PRODUCT, DOMAIN);
            IAcsClient acsClient = new DefaultAcsClient(profile);

            // 组装请求对象-具体描述见控制台-文档部分内容
            SendBatchSmsRequest request = new SendBatchSmsRequest();
            //使用post提交
            request.setMethod(MethodType.POST);
            //必填:待发送手机号。支持JSON格式的批量调用，批量上限为100个手机号码,批量调用相对于单条调用及时性稍有延迟,验证码类型的短信推荐使用单条调用的方式
            request.setPhoneNumberJson(phoneNumberJson);
            request.setSignNameJson(signNameJson);
            // 必填:短信模板-可在短信控制台中找到
            request.setTemplateCode(templateCode);
            // 可选:模板中的变量替换JSON串,如模板内容为"亲爱的用户,您的验证码为${code}"时,此处的值为
            // request.setTemplateParam("{\"name\":\"Tom\", \"code\":\"123\"}");

           /* if (null != map && !map.isEmpty()) {
                StringBuffer sb = new StringBuffer();
                for (String key : map.keySet()) {
                    sb.append("'" + key + "':").append("'" + map.get(key) + "',");
                }
                String param = sb.substring(0, sb.length() - 1);
                request.setTemplateParamJson("[{" + param + "}]");
            }*/
            /*request.setTemplateParamJson("[{\"code\":\"123321\"},{\"code\":\"456654\"}]");*/
            request.setTemplateParamJson(templateParam);
            // 选填-上行短信扩展码(无特殊需求用户请忽略此字段)
            // request.setSmsUpExtendCode("90997");

            // hint 此处可能会抛出异常，注意catch
            SendBatchSmsResponse sendSmsResponse = acsClient.getAcsResponse(request);
            if (sendSmsResponse.getCode() != null && "OK".equals(sendSmsResponse.getCode())) {
                //请求成功
                log.info("短信批量发送成功！");
            }
            return sendSmsResponse;
        } catch (ClientException e) {
            e.printStackTrace();
            log.error("发送短信异常", e);
            return null;
        }
    }

    /**
     * 查询发送短信用户
     *
     * @param tel
     * @param bizId
     * @param createTime
     * @return
     */
    public static QuerySendDetailsResponse querySend(ExtraInfoDTO smsInfo, String tel, String bizId, String createTime) {
        initSmsInfo(smsInfo);
        try {
            // 可自助调整超时时间
            System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
            System.setProperty("sun.net.client.defaultReadTimeout", "10000");

            // 初始化acsClient,暂不支持region化
            IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", ACCESS_KEY_ID, ACCESS_KEY_SECRET);
            DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", PRODUCT, DOMAIN);
            IAcsClient acsClient = new DefaultAcsClient(profile);

            //组装请求对象
            QuerySendDetailsRequest request = new QuerySendDetailsRequest();
            /*//必填-号码
            request.setPhoneNumber("150000000");
            //可选-调用发送短信接口时返回的BizId
            request.setBizId("1234567^8901234");
            //必填-短信发送的日期 支持30天内记录查询（可查其中一天的发送数据），格式yyyyMMdd
            request.setSendDate("20170513");*/
            //必填-号码
            request.setPhoneNumber(tel);
            //可选-调用发送短信接口时返回的BizId
            request.setBizId(bizId);
            //必填-短信发送的日期 支持30天内记录查询（可查其中一天的发送数据），格式yyyyMMdd
            request.setSendDate(createTime);
            //必填-页大小
            request.setPageSize(10L);
            //必填-当前页码从1开始计数
            request.setCurrentPage(1L);
            //hint 此处可能会抛出异常，注意catch
            QuerySendDetailsResponse querySendDetailsResponse = acsClient.getAcsResponse(request);
            //获取返回结果
            if (querySendDetailsResponse.getCode() != null && "OK".equals(querySendDetailsResponse.getCode())) {
                //代表请求成功
                log.info("短信查询成功！");
            }
            return querySendDetailsResponse;
        } catch (ClientException e) {
            e.printStackTrace();
            log.error("查询短信异常", e);
            return null;
        }
    }

}
