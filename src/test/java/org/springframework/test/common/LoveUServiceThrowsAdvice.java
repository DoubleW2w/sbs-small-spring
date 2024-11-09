package org.springframework.test.common;

import org.springframework.aop.ThrowsAdvice;

import java.lang.reflect.Method;

/**
 * @author: DoubleW2w
 * @date: 2024/11/9
 * @project: sbs-small-spring
 */
public class LoveUServiceThrowsAdvice implements ThrowsAdvice {
  @Override
  public void throwsHandle(Throwable throwable, Method method, Object[] args, Object target) {
    System.out.println(
        "ThrowsAdvice: do something when the earth explodes function throw an exception");
  }
}
