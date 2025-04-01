package com.meeting_smile.seckilldemo.validator;

import com.meeting_smile.seckilldemo.valueobject.IsMobileValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * 自定义注解：验证手机号
 */
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(
        validatedBy = { IsMobileValidator.class }//通过IsMobileValidator类来自定义注解规则
)
public @interface IsMobile {

    boolean required() default true;//是否必填
    String message() default "手机号码格式错误";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
