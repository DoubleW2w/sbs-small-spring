package org.springframework.test.aop;

import org.aopalliance.intercept.MethodInterceptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.aop.AdvisedSupport;
import org.springframework.aop.MethodMatcher;
import org.springframework.aop.TargetSource;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.framework.CglibAopProxy;
import org.springframework.aop.framework.JdkDynamicAopProxy;
import org.springframework.aop.framework.ReflectiveMethodInvocation;
import org.springframework.test.common.LoveUServiceInterceptor;
import org.springframework.test.service.LoveUService;
import org.springframework.test.service.LoveUServiceImpl;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author: DoubleW2w
 * @date: 2024/11/6
 * @project: sbs-small-spring
 */
public class DynamicProxyTest {

  private AdvisedSupport advisedSupport;

  @BeforeEach
  public void setup() throws Exception {
    LoveUService loveUService = new LoveUServiceImpl();

    advisedSupport = new AdvisedSupport();
    TargetSource targetSource = new TargetSource(loveUService);
    LoveUServiceInterceptor methodInterceptor = new LoveUServiceInterceptor();
    AspectJExpressionPointcut pointcut =
        new AspectJExpressionPointcut(
            "execution(* org.springframework.test.service.LoveUService.explode(..))");
    MethodMatcher methodMatcher = pointcut.getMethodMatcher();
    advisedSupport.setTargetSource(targetSource);
    advisedSupport.setMethodInterceptor(methodInterceptor);
    advisedSupport.setMethodMatcher(methodMatcher);
  }

  @Test
  public void test_jdkDynamic() throws Exception {
    LoveUService proxy = (LoveUService) new JdkDynamicAopProxy(advisedSupport).getProxy();
    proxy.explode();
  }

  @Test
  public void test_cglibDynamic() throws Exception {
    LoveUService proxy = (LoveUService) new CglibAopProxy(advisedSupport).getProxy();
    proxy.explode();
  }

  @Test
  public void test_proxy_class() {
    LoveUService loveUService =
        (LoveUService)
            Proxy.newProxyInstance(
                Thread.currentThread().getContextClassLoader(),
                new Class[] {LoveUService.class},
                (proxy, method, args) -> "你被代理了！");
    loveUService.explode();
  }

  @Test
  public void test_proxy_method() {
    // 目标对象(可以替换成任何的目标对象)
    Object targetObj = new LoveUServiceImpl();

    // AOP 代理
    LoveUService proxy =
        (LoveUService)
            Proxy.newProxyInstance(
                Thread.currentThread().getContextClassLoader(),
                targetObj.getClass().getInterfaces(),
                new InvocationHandler() {
                  // 方法匹配器
                  MethodMatcher methodMatcher =
                      new AspectJExpressionPointcut(
                          "execution(* org.springframework.test.service.LoveUService.explode(..))");

                  @Override
                  public Object invoke(Object proxy, Method method, Object[] args)
                      throws Throwable {
                    if (methodMatcher.matches(method, targetObj.getClass())) {
                      // 方法拦截器
                      MethodInterceptor methodInterceptor =
                          invocation -> {
                            long start = System.currentTimeMillis();
                            try {
                              return invocation.proceed();
                            } finally {
                              System.out.println("监控 - Begin By AOP");
                              System.out.println("方法名称：" + invocation.getMethod().getName());
                              System.out.println(
                                  "方法耗时：" + (System.currentTimeMillis() - start) + "ms");
                              System.out.println("监控 - End\r\n");
                            }
                          };
                      // 反射调用
                      return methodInterceptor.invoke(
                          new ReflectiveMethodInvocation(targetObj, method, args));
                    }
                    return method.invoke(targetObj, args);
                  }
                });

    proxy.explode();
  }
}
