package org.springframework.test.common;

import org.springframework.aop.BeforeAdvice;

import java.lang.reflect.Method;

/**
 * @author: DoubleW2w
 * @date: 2024/11/11
 * @project: sbs-small-spring
 */
public class ABeforeAdvice implements BeforeAdvice {
  @Override
  public void before(Method method, Object[] args, Object target) throws Throwable {
    System.out.println(".........");
  }
}
