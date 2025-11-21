package com.luohuo.basic.validator.config;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.hibernate.validator.HibernateValidator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import com.luohuo.basic.validator.component.FormValidatorController;
import com.luohuo.basic.validator.component.extract.DefaultConstraintExtractImpl;
import com.luohuo.basic.validator.component.extract.IConstraintExtract;
import com.luohuo.basic.validator.utils.AssertUtil;

/**
 * 验证器配置
 *
 * @author 乾乾
 * @date 2019/07/14
 */
@ComponentScan(basePackageClasses = FormValidatorController.class)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class ValidatorConfiguration {

    @Bean
    public Validator validator() {
        ValidatorFactory validatorFactory = AssertUtil.warp(Validation.byProvider(HibernateValidator.class)
                        .configure()
                        //快速失败返回模式
                        .addProperty("hibernate.validator.fail_fast", "true"))
                .buildValidatorFactory();
        return validatorFactory.getValidator();
    }


    /**
     * Method:  开启快速返回
     * Description:
     * 如果参数校验有异常，直接抛异常，不会进入到 controller，使用全局异常拦截进行拦截
     */
    @Bean
    public MethodValidationPostProcessor methodValidationPostProcessor(Validator validator) {
        MethodValidationPostProcessor postProcessor = new MethodValidationPostProcessor();
        postProcessor.setValidator(validator);
        return postProcessor;
    }

    @Bean
    public IConstraintExtract constraintExtract(Validator validator) {
        return new DefaultConstraintExtractImpl(validator);
    }

}
