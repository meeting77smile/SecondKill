package com.meeting_smile.seckilldemo.valueobject;

import com.meeting_smile.seckilldemo.utils.ValidatorUtil;
import com.meeting_smile.seckilldemo.validator.IsMobile;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.thymeleaf.util.StringUtils;

/**
 * 自定义手机号码校验规则
 */
public class IsMobileValidator implements ConstraintValidator<IsMobile,String> {
    private boolean required = false;
    @Override
    public void initialize(IsMobile constraintAnnotation) {//初始化
        required = constraintAnnotation.required();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if(required){
            return ValidatorUtil.isMobile(value);//此时value若为空值，传入就会返回非法，即为必填时不允许空值出现
        }
        else{//非必填
            if(StringUtils.isEmpty(value)){//value为传进来的值，如mobile
                return true;//非必填时允许空值出现
            }
            else{
                return ValidatorUtil.isMobile(value);//校验电话号码是否合法
            }
        }
    }
}
