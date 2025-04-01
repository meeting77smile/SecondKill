package com.meeting_smile.seckilldemo.exception;

import com.meeting_smile.seckilldemo.valueobject.RespBeanEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 全局异常
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GlobalException extends RuntimeException {
    private RespBeanEnum respBeanEnum;
}
