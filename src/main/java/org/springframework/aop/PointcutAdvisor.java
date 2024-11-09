package org.springframework.aop;

/**
 * @author: DoubleW2w
 * @date: 2024/11/9
 * @project: sbs-small-spring
 */
public interface PointcutAdvisor extends Advisor {
  Pointcut getPointcut();
}
