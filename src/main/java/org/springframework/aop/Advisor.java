package org.springframework.aop;

import org.aopalliance.aop.Advice;

/**
 * @author: DoubleW2w
 * @date: 2024/11/9
 * @project: sbs-small-spring
 */
public interface Advisor {
  Advice getAdvice();
}
