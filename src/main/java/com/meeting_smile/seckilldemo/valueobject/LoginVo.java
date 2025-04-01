package com.meeting_smile.seckilldemo.valueobject;

import com.meeting_smile.seckilldemo.validator.IsMobile;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

//专门用于接收登录时传递给后端的参数
@Data
public class LoginVo {
    @NotNull
    @IsMobile
    private String mobile;

    @Length(min = 32)   //要求加密后的密码至少为32位
    private String password;
}
