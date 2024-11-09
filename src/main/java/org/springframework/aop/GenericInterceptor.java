package org.springframework.aop;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

/**
 * @author: DoubleW2w
 * @date: 2024/11/9
 * @project: sbs-small-spring
 */
public class GenericInterceptor implements MethodInterceptor {
  /** 前置通知 */
  private BeforeAdvice beforeAdvice;

  /** 后置通知 */
  private AfterAdvice afterAdvice;

  /** 返回后置通知 */
  private AfterReturningAdvice afterReturningAdvice;

  /** 抛出通知 */
  private ThrowsAdvice throwsAdvice;

  @Override
  public Object invoke(MethodInvocation invocation) throws Throwable {
    Object result = null;
    try {
      // 前置通知
      if (beforeAdvice != null) {
        beforeAdvice.before(
            invocation.getMethod(), invocation.getArguments(), invocation.getThis());
      }
      result = invocation.proceed();
    } catch (Exception throwable) {
      // 异常通知
      if (throwsAdvice != null) {
        throwsAdvice.throwsHandle(
            throwable, invocation.getMethod(), invocation.getArguments(), invocation.getThis());
      }
    } finally {
      // 后置通知
      if (afterAdvice != null) {
        afterAdvice.after(invocation.getMethod(), invocation.getArguments(), invocation.getThis());
      }
    }
    // 返回通知
    if (afterReturningAdvice != null) {
      afterReturningAdvice.afterReturning(
          result, invocation.getMethod(), invocation.getArguments(), invocation.getThis());
    }
    return result;
  }

  public void setBeforeAdvice(BeforeAdvice beforeAdvice) {
    this.beforeAdvice = beforeAdvice;
  }

  public void setAfterReturningAdvice(AfterReturningAdvice afterReturningAdvice) {
    this.afterReturningAdvice = afterReturningAdvice;
  }

  public void setThrowsAdvice(ThrowsAdvice throwsAdvice) {
    this.throwsAdvice = throwsAdvice;
  }

  public void setAfterAdvice(AfterAdvice afterAdvice) {
    this.afterAdvice = afterAdvice;
  }
}
