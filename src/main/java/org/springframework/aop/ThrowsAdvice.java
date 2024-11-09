package org.springframework.aop;

import org.aopalliance.aop.Advice;

import java.lang.reflect.Method;

/**
 * 抛出异常增强
 *
 * @author: DoubleW2w
 * @date: 2024/11/9
 * @project: sbs-small-spring
 */
public interface ThrowsAdvice extends Advice {
  void throwsHandle(Throwable throwable, Method method, Object[] args, Object target);
}
