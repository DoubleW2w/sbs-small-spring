package org.springframework.aop.aspectj;

import org.aopalliance.aop.Advice;
import org.springframework.aop.Pointcut;
import org.springframework.aop.PointcutAdvisor;

/**
 * @author: DoubleW2w
 * @date: 2024/11/9
 * @project: sbs-small-spring
 */
public class AspectJExpressionPointcutAdvisor implements PointcutAdvisor {

  private AspectJExpressionPointcut pointcut;

  private Advice advice;

  private String expression;

  @Override
  public Advice getAdvice() {
    return advice;
  }

  @Override
  public Pointcut getPointcut() {
    return pointcut;
  }

  public void setAdvice(Advice advice) {
    this.advice = advice;
  }

  public void setExpression(String expression) {
    this.expression = expression;
    pointcut = new AspectJExpressionPointcut(expression);
  }
}
