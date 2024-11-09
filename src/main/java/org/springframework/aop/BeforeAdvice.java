package org.springframework.aop;

import org.aopalliance.aop.Advice;

import java.lang.reflect.Method;

/**
 * 前置通知增强
 *
 * @author: DoubleW2w
 * @date: 2024/11/9
 * @project: sbs-small-spring
 */
public interface BeforeAdvice extends Advice {
  void before(Method method, Object[] args, Object target) throws Throwable;
}
