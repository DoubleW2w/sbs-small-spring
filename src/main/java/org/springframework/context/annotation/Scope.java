package org.springframework.context.annotation;

import java.lang.annotation.*;

/**
 * @author: DoubleW2w
 * @date: 2024/11/10
 * @project: sbs-small-spring
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Scope {
  String value() default "singleton";
}
