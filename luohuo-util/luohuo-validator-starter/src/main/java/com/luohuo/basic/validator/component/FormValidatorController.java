package com.luohuo.basic.validator.component;

import cn.hutool.core.util.StrUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.lang.NonNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import com.luohuo.basic.base.R;
import com.luohuo.basic.validator.component.extract.IConstraintExtract;
import com.luohuo.basic.validator.model.FieldValidatorDesc;
import com.luohuo.basic.validator.model.ValidConstraint;
import com.luohuo.basic.validator.wrapper.HttpServletRequestValidatorWrapper;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


/**
 * 统一获取校验规则入口。<br>
 * <br>
 * 加入了一个前端验证接口控制器，专门处理所有的拉取表单验证规则的请求。<br>
 * 在拉取表单验证规则的时候，有两种拉取方式<br>
 * 第一种如下（通过路径变量来传输要拉取的uri路径） ：<br>
 * A表单的保存url为       http://ip:port/porjectName/role/save <br>
 * 那么A表单的验证url：http://ip:port/porjectName<font color="red">/from/validator</font>/role/save <br>
 * <br>
 * 仅仅追加了<font color="red">/from/validator/</font>而已。<br>
 * <br>
 * 第二种如下是（通过参数传递uri路径的方式来拉取）：<br>
 * 表单保存url ：    <br>
 * http://ip:port/porjectName/role/save <br>
 * 那么验证url：<br/>
 * http://ip:port/porjectName<font color="red">/from/validator</font>?fromPath=/porjectName/role/save <br>
 * <br>
 * 固定了验证uri地址，而要验证的表单地址作为参数进行传输。当然，可以一次性拿多个表单验证地址。有些界面可能同时存在多个表单需要提交。
 * <br>
 * <p>
 * <p>
 * Bean Validation 中内置的 constraint
 *
 * <p>
 * <p>
 * {@literal @}Null 被注释的元素必须为 null<br/>
 * {@literal @}NotNull 被注释的元素必须不为 null<br/>
 * {@literal @}AssertTrue 被注释的元素必须为 true<br/>
 * {@literal @}AssertFalse 被注释的元素必须为 false<br/>
 * {@literal @}Min(value) 被注释的元素必须是一个数字，其值必须大于等于指定的最小值<br/>
 * {@literal @}Max(value) 被注释的元素必须是一个数字，其值必须小于等于指定的最大值<br/>
 * {@literal @}DecimalMin(value) 被注释的元素必须是一个数字，其值必须大于等于指定的最小值<br/>
 * {@literal @}DecimalMax(value) 被注释的元素必须是一个数字，其值必须小于等于指定的最大值<br/>
 * {@literal @}Size(max=, min=)   被注释的元素的大小必须在指定的范围内<br/>
 * {@literal @}Digits (integer, fraction)     被注释的元素必须是一个数字，其值必须在可接受的范围内<br/>
 * {@literal @}Past 被注释的元素必须是一个过去的日期<br/>
 * {@literal @}Future 被注释的元素必须是一个将来的日期<br/>
 * {@literal @}Pattern(regex=,flag=) 被注释的元素必须符合指定的正则表达式<br/>
 * {@literal @}NotEmptyPattern(regex=,flag=) 被注释的元素必须符合指定的正则表达式<br/>
 * </p>
 * <p>
 * 注意： 不建议使用 Hibernate Validator  提供的注解！！！
 * Hibernate Validator 附加的 constraint <br/>
 * {@literal @}NotBlank(message =)   验证字符串非null，且长度必须大于0<br/>
 * {@literal @}Email 被注释的元素必须是电子邮箱地址<br/>
 * {@literal @}Size(min=,max=) 被注释的字符串的大小必须在指定的范围内<br/>
 * {@literal @}NotEmpty 被注释的字符串的必须非空<br/>
 * {@literal @}Range(min=,max=,message=) 被注释的元素必须在合适的范围内<br/>
 * <p>
 *
 * @author 乾乾
 * @date 2019-07-12 14:30
 */
@RequestMapping
@RestController
public class FormValidatorController {

    private static final String FORM_VALIDATOR_URL = "/form/validator";
    private final RequestMappingHandlerMapping requestMappingHandlerMapping;
    private final IConstraintExtract constraintExtract;

    public FormValidatorController(IConstraintExtract constraintExtract, RequestMappingHandlerMapping requestMappingHandlerMapping) {
        this.constraintExtract = constraintExtract;
        this.requestMappingHandlerMapping = requestMappingHandlerMapping;
    }

    /**
     * 支持第一种拉取方式
     * 注意： 具体的方法必须在参数上面标注 @Validated 才有效
     *
     * @param request 请求
     * @return 验证规则
     * @throws Exception 异常
     */
    @RequestMapping(FORM_VALIDATOR_URL + "/**")
    @ResponseBody
    public R<Collection<FieldValidatorDesc>> standardByPathVar(HttpServletRequest request) throws Exception {
        String requestUri = request.getRequestURI();
        String formPath = StrUtil.subAfter(requestUri, FORM_VALIDATOR_URL, false);
        return R.success(localFieldValidatorDescribe(request, formPath));
    }

    /**
     * 支持第二种拉取方式
     *
     * @param formPath 表单地址
     * @param request  请求
     * @return 验证规则
     * @throws Exception 异常
     */
    @RequestMapping(FORM_VALIDATOR_URL)
    @ResponseBody
    public R<Collection<FieldValidatorDesc>> standardByQueryParam(@RequestParam(value = "formPath", required = false) String formPath, HttpServletRequest request) throws Exception {
        return R.success(localFieldValidatorDescribe(request, formPath));
    }

    private Collection<FieldValidatorDesc> localFieldValidatorDescribe(HttpServletRequest request, String formPath) throws Exception {
        HandlerExecutionChain chains = requestMappingHandlerMapping.getHandler(new HttpServletRequestValidatorWrapper(request, formPath));
        if (chains == null) {
            return Collections.emptyList();
        }
        HandlerMethod method = (HandlerMethod) chains.getHandler();
        return loadValidatorDescribe(method);
    }

    /**
     * 官方验证规则： （可能还不完整）
     * A, 普通对象形：
     * B、@RequestBody形式：
     * <p>
     * 1，类或方法或参数上有 @Validated
     * 2，参数有 @Valid
     *
     * <p>
     * C、普通参数形式：
     * 类上有 有 @Validated
     * 参数有 任意注解
     *
     * <p>
     * 步骤：
     * 1，先判断类上是否存在
     * 2，判断方法上是否存在
     * 3，判断
     *
     * @param handlerMethod 处理方法
     * @return 验证规则
     * @throws Exception 异常
     */
    private Collection<FieldValidatorDesc> loadValidatorDescribe(HandlerMethod handlerMethod) throws Exception {
        Method method = handlerMethod.getMethod();
        Parameter[] methodParams = method.getParameters();
        if (methodParams == null || methodParams.length < 1) {
            return Collections.emptyList();
        }
        MethodParameter[] methodParameters = handlerMethod.getMethodParameters();
        if (methodParameters.length < 1) {
            return Collections.emptyList();
        }

        // 类上面的验证注解  handlerMethod.getBeanType().getAnnotation(Validated.class)
        Validated classValidated = method.getDeclaringClass().getAnnotation(Validated.class);

        List<ValidConstraint> validatorStandard = getValidConstraints(methodParams, methodParameters, classValidated);
        return constraintExtract.extract(validatorStandard);
    }

    @NonNull
    private List<ValidConstraint> getValidConstraints(Parameter[] methodParams, MethodParameter[] methodParameters, Validated classValidated) {
        List<ValidConstraint> validatorStandard = new ArrayList<>();
        for (int i = 0; i < methodParameters.length; i++) {
            // 方法上的参数 (能正确获取到 当前类和父类Controller上的 参数类型)
            MethodParameter methodParameter = methodParameters[i];
            // 方法上的参数 (能正确获取到 当前类和父类Controller上的 参数注解)
            Parameter methodParam = methodParams[i];

            Validated methodParamValidate = methodParam.getAnnotation(Validated.class);

            //在参数和类上面找注解
            if (methodParamValidate == null && classValidated == null) {
                continue;
            }

            // 优先获取方法上的 验证组，在取类上的验证组
            Class<?>[] group = null;
            if (methodParamValidate != null) {
                group = methodParamValidate.value();
            }
            if (group == null) {
                group = classValidated.value();
            }

            validatorStandard.add(new ValidConstraint(methodParameter.getParameterType(), group));
        }
        return validatorStandard;
    }
}

