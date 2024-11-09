package org.springframework.aop;

import org.aopalliance.aop.Advice;

import java.lang.reflect.Method;

/**
 * 返回后置增强
 *
 * @author: DoubleW2w
 * @date: 2024/11/9
 * @project: sbs-small-spring
 */
public interface AfterReturningAdvice extends Advice {
  void afterReturning(Object returnValue, Method method, Object[] args, Object target)
      throws Throwable;
}
