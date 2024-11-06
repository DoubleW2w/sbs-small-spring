package org.springframework.aop;

import java.lang.reflect.Method;

/**
 * {@link Pointcut}切面的一部分——检查目标方法是否有资格获得通知（通知指的是增强的代码逻辑）
 *
 * @author: DoubleW2w
 * @date: 2024/11/6
 * @project: sbs-small-spring
 */
public interface MethodMatcher {
  boolean matches(Method method, Class<?> targetClass);
}
