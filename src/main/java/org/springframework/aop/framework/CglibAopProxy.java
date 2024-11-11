package org.springframework.aop.framework;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.springframework.aop.AdvisedSupport;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Method;

/**
 * SpringAOP框中基于CGLIB实现的代理{@link AopProxy}
 *
 * <p>这种类型的对象应该通过代理工厂获得，由AdvisedSupport对象配置。这个类是Spring AOP框架的内部类，不需要由客户端代码直接使用。
 *
 * @author: DoubleW2w
 * @date: 2024/11/7
 * @project: sbs-small-spring
 */
public class CglibAopProxy implements AopProxy {

  private final AdvisedSupport advised;

  public CglibAopProxy(AdvisedSupport advised) {
    this.advised = advised;
  }

  /**
   * 获取代理对象
   *
   * <p>通过cglib库创建一个代理对象，该对象可以是目标类的子类
   */
  @Override
  public Object getProxy() {
    Enhancer enhancer = new Enhancer();
    // 获取目标对象的类
    Class<?> aClass = advised.getTargetSource().getTarget().getClass();
    // 如果是CGLIB代理类，就获取父类，否则直接使用目标类
    aClass = ClassUtils.isCglibProxyClass(aClass) ? aClass.getSuperclass() : aClass;
    // 设置Enhancer对象的父类
    enhancer.setSuperclass(aClass);
    // 设置代理类实现的接口
    enhancer.setInterfaces(advised.getTargetSource().getTargetClass());
    // 设置代理类的回调方法，用于处理代理类的方法调用
    enhancer.setCallback(new DynamicAdvisedInterceptor(advised));
    return enhancer.create();
  }

  /** 注意此处的MethodInterceptor是cglib中的接口，advised中的MethodInterceptor的AOP联盟中定义的接口，因此定义此类做适配 */
  private static class DynamicAdvisedInterceptor implements MethodInterceptor {
    private final AdvisedSupport advised;

    public DynamicAdvisedInterceptor(AdvisedSupport advised) {
      this.advised = advised;
    }

    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy)
        throws Throwable {
      // cglib方法调用
      CglibMethodInvocation methodInvocation =
          new CglibMethodInvocation(
              advised.getTargetSource().getTarget(), method, objects, methodProxy);
      // 检查目标方法是否符合通知条件
      if (advised
          .getMethodMatcher()
          .matches(method, advised.getTargetSource().getTarget().getClass())) {
        return advised.getMethodInterceptor().invoke(methodInvocation);
      }
      return methodInvocation.proceed();
    }
  }

  private static class CglibMethodInvocation extends ReflectiveMethodInvocation {
    private final MethodProxy methodProxy;

    public CglibMethodInvocation(
        Object target, Method method, Object[] arguments, MethodProxy methodProxy) {
      super(target, method, arguments);
      this.methodProxy = methodProxy;
    }

    @Override
    public Object proceed() throws Throwable {
      return this.methodProxy.invoke(this.target, this.arguments);
    }
  }
}
