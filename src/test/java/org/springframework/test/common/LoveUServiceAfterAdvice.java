package org.springframework.test.common;

import org.springframework.aop.AfterAdvice;

import java.lang.reflect.Method;

/**
 * @author: DoubleW2w
 * @date: 2024/11/9
 * @project: sbs-small-spring
 */
public class LoveUServiceAfterAdvice implements AfterAdvice {
  @Override
  public void after(Method method, Object[] args, Object target) throws Throwable {
    System.out.println("AfterAdvice: do something after the earth explodes");
  }
}
