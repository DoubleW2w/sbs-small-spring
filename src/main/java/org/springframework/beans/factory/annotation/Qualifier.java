package org.springframework.beans.factory.annotation;

import java.lang.annotation.*;

/**
 * @author: DoubleW2w
 * @date: 2024/11/10
 * @project: sbs-small-spring
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({
  ElementType.FIELD,
  ElementType.METHOD,
  ElementType.PARAMETER,
  ElementType.TYPE,
  ElementType.ANNOTATION_TYPE
})
@Inherited
@Documented
public @interface Qualifier {
  String value() default "";
}
