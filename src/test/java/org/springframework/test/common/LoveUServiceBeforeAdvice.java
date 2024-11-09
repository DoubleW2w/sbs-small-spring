package org.springframework.test.common;

import org.springframework.aop.BeforeAdvice;

import java.lang.reflect.Method;

/**
 * @author: DoubleW2w
 * @date: 2024/11/9
 * @project: sbs-small-spring
 */
public class LoveUServiceBeforeAdvice implements BeforeAdvice {
  @Override
  public void before(Method method, Object[] args, Object target) throws Throwable {
    System.out.println("BeforeAdvice: do something before the earth explodes");
  }
}
