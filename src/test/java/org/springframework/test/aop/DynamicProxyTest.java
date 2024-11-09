package org.springframework.test.aop;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import org.aopalliance.intercept.MethodInterceptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.aop.*;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.aspectj.AspectJExpressionPointcutAdvisor;
import org.springframework.aop.framework.CglibAopProxy;
import org.springframework.aop.framework.JdkDynamicAopProxy;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.framework.ReflectiveMethodInvocation;
import org.springframework.test.common.*;
import org.springframework.test.service.LoveUService;
import org.springframework.test.service.LoveUServiceImpl;
import org.springframework.test.service.LoveUServiceWithExceptionImpl;
import org.springframework.test.service.WorldService;

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
    GenericInterceptor methodInterceptor = new GenericInterceptor();
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
  public void test_proxyFactory() throws Exception {
    // 使用JDK动态代理
    advisedSupport.setProxyTargetClass(false);
    LoveUService proxy = (LoveUService) new ProxyFactory(advisedSupport).getProxy();
    proxy.explode();
    System.out.println("---------------------------------------------------------------");
    // 使用CGLIB动态代理
    advisedSupport.setProxyTargetClass(true);
    proxy = (LoveUService) new ProxyFactory(advisedSupport).getProxy();
    proxy.explode();
  }

  @Test
  public void test_beforeAdvice() throws Exception {
    // 设置BeforeAdvice
    LoveUServiceBeforeAdvice beforeAdvice = new LoveUServiceBeforeAdvice();
    GenericInterceptor methodInterceptor = new GenericInterceptor();
    methodInterceptor.setBeforeAdvice(beforeAdvice);
    advisedSupport.setMethodInterceptor(methodInterceptor);

    LoveUService proxy = (LoveUService) new ProxyFactory(advisedSupport).getProxy();
    proxy.explode();
  }

  @Test
  public void test_AfterAdvice() throws Exception {
    LoveUServiceAfterAdvice afterAdvice = new LoveUServiceAfterAdvice();
    GenericInterceptor methodInterceptor = new GenericInterceptor();
    methodInterceptor.setAfterAdvice(afterAdvice);
    advisedSupport.setMethodInterceptor(methodInterceptor);
    LoveUService proxy = (LoveUService) new ProxyFactory(advisedSupport).getProxy();
    proxy.explode();
  }

  @Test
  public void test_afterReturningAdvice() throws Exception {
    LoveUServiceAfterReturningAdvice afterReturningAdvice = new LoveUServiceAfterReturningAdvice();
    GenericInterceptor methodInterceptor = new GenericInterceptor();
    methodInterceptor.setAfterReturningAdvice(afterReturningAdvice);

    advisedSupport.setMethodInterceptor(methodInterceptor);
    LoveUService proxy = (LoveUService) new ProxyFactory(advisedSupport).getProxy();
    proxy.explode();
  }

  @Test
  public void test_throwsAdvice() throws Exception {
    LoveUService loveUService = new LoveUServiceWithExceptionImpl();
    // 设置ThrowsAdvice
    LoveUServiceThrowsAdvice throwsAdvice = new LoveUServiceThrowsAdvice();
    GenericInterceptor methodInterceptor = new GenericInterceptor();
    methodInterceptor.setThrowsAdvice(throwsAdvice);

    advisedSupport.setMethodInterceptor(methodInterceptor);
    advisedSupport.setTargetSource(new TargetSource(loveUService));

    LoveUService proxy = (LoveUService) new ProxyFactory(advisedSupport).getProxy();
    proxy.explode();
  }

  @Test
  public void test_allAdvice() throws Exception {
    GenericInterceptor methodInterceptor = new GenericInterceptor();
    methodInterceptor.setBeforeAdvice(new LoveUServiceBeforeAdvice());
    methodInterceptor.setAfterReturningAdvice(new LoveUServiceAfterReturningAdvice());
    methodInterceptor.setThrowsAdvice(new LoveUServiceThrowsAdvice());
    methodInterceptor.setAfterAdvice(new LoveUServiceAfterAdvice());
    advisedSupport.setMethodInterceptor(methodInterceptor);
    LoveUService proxy = (LoveUService) new ProxyFactory(advisedSupport).getProxy();
    proxy.explode();
  }

  @Test
  public void test_allAdviceWithException() throws Exception {
    LoveUService loveUService = new LoveUServiceWithExceptionImpl();

    // 拦截器并设置好各种通知
    GenericInterceptor methodInterceptor = new GenericInterceptor();
    methodInterceptor.setBeforeAdvice(new LoveUServiceBeforeAdvice());
    methodInterceptor.setAfterReturningAdvice(new LoveUServiceAfterReturningAdvice());
    methodInterceptor.setThrowsAdvice(new LoveUServiceThrowsAdvice());
    methodInterceptor.setAfterAdvice(new LoveUServiceAfterAdvice());
    advisedSupport.setMethodInterceptor(methodInterceptor);

    advisedSupport.setTargetSource(new TargetSource(loveUService));
    LoveUService proxy = (LoveUService) new ProxyFactory(advisedSupport).getProxy();
    proxy.explode();
  }

  @Test
  public void test_advisor() throws Exception {
    LoveUService loveUService = new LoveUServiceImpl();
    // Advisor是Pointcut和Advice的组合
    AspectJExpressionPointcutAdvisor advisor = new AspectJExpressionPointcutAdvisor();
    advisor.setExpression("execution(* org.springframework.test.service.LoveUService.explode(..))");
    GenericInterceptor methodInterceptor = new GenericInterceptor();
    methodInterceptor.setBeforeAdvice(new LoveUServiceBeforeAdvice());
    advisor.setAdvice(methodInterceptor);

    AdvisedSupport advisedSupport = new AdvisedSupport();
    TargetSource targetSource = new TargetSource(loveUService);
    advisedSupport.setTargetSource(targetSource);
    advisedSupport.setMethodInterceptor((MethodInterceptor) advisor.getAdvice());
    advisedSupport.setMethodMatcher(advisor.getPointcut().getMethodMatcher());
    //			advisedSupport.setProxyTargetClass(true);   //JDK or CGLIB

    LoveUService proxy = (LoveUService) new ProxyFactory(advisedSupport).getProxy();
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
