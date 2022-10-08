package com.netty.study.annotation;

import java.lang.annotation.*;

/**
 * @author Steven
 * @date 2022年10月08日 16:55
 */
@Documented
@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ResponseBody {
}
