package org.springframework.test.aop;

import org.junit.jupiter.api.Test;
import org.springframework.aop.AdvisedSupport;
import org.springframework.aop.MethodMatcher;
import org.springframework.aop.TargetSource;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.framework.JdkDynamicAopProxy;
import org.springframework.test.common.LoveUServiceInterceptor;
import org.springframework.test.service.LoveUService;
import org.springframework.test.service.LoveUServiceImpl;

/**
 * @author: DoubleW2w
 * @date: 2024/11/6
 * @project: sbs-small-spring
 */
public class DynamicProxyTest {
  @Test
  public void test_jdkDynamic() throws Exception {
    LoveUService loveUService = new LoveUServiceImpl();

    AdvisedSupport advisedSupport = new AdvisedSupport();
    TargetSource targetSource = new TargetSource(loveUService);
    LoveUServiceInterceptor methodInterceptor = new LoveUServiceInterceptor();
    AspectJExpressionPointcut pointcut =
        new AspectJExpressionPointcut(
            "execution(* org.springframework.test.service.LoveUService.explode(..))");
    MethodMatcher methodMatcher = pointcut.getMethodMatcher();
    advisedSupport.setTargetSource(targetSource);
    advisedSupport.setMethodInterceptor(methodInterceptor);
    advisedSupport.setMethodMatcher(methodMatcher);

    LoveUService proxy = (LoveUService) new JdkDynamicAopProxy(advisedSupport).getProxy();
    proxy.explode();
  }
}
