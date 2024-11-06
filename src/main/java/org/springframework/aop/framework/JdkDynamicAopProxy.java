package org.springframework.aop.framework;

import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.aop.AdvisedSupport;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * SpringAOP框架中AopProxy类的一个实现，基于Jdk中Proxy类的动态代理
 *
 * <p>JDK 动态代理
 *
 * @author: DoubleW2w
 * @date: 2024/11/6
 * @project: sbs-small-spring
 */
public class JdkDynamicAopProxy implements AopProxy, InvocationHandler {

  private final AdvisedSupport advised;

  public JdkDynamicAopProxy(AdvisedSupport advised) {
    this.advised = advised;
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    // 检查方法匹配器是否与当前方法是否匹配
    if (advised
        .getMethodMatcher()
        .matches(method, advised.getTargetSource().getTarget().getClass())) {
      // 代理方法
      MethodInterceptor methodInterceptor = advised.getMethodInterceptor();
      return methodInterceptor.invoke(
          new ReflectiveMethodInvocation(advised.getTargetSource().getTarget(), method, args));
    }
    return method.invoke(advised.getTargetSource().getTarget(), args);
  }

  @Override
  public Object getProxy() {
    return Proxy.newProxyInstance(
        getClass().getClassLoader(), advised.getTargetSource().getTargetClass(), this);
  }
}
