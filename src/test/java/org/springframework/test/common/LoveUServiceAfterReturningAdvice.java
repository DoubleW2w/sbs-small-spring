package org.springframework.test.common;

import org.springframework.aop.AfterReturningAdvice;

import java.lang.reflect.Method;

/**
 * @author: DoubleW2w
 * @date: 2024/11/9
 * @project: sbs-small-spring
 */
public class LoveUServiceAfterReturningAdvice implements AfterReturningAdvice {
  @Override
  public void afterReturning(Object returnValue, Method method, Object[] args, Object target)
      throws Throwable {
    System.out.println("AfterReturningAdvice: do something after the earth explodes return");
  }
}
