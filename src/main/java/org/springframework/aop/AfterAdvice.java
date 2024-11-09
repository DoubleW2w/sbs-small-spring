package org.springframework.aop;

import org.aopalliance.aop.Advice;

import java.lang.reflect.Method;

/**
 * 后置通知进行增强
 *
 * @author: DoubleW2w
 * @date: 2024/11/9
 * @project: sbs-small-spring
 */
public interface AfterAdvice extends Advice {
  void after(Method method, Object[] args, Object target) throws Throwable;
}
