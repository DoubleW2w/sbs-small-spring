package org.springframework.test.aop;

import org.junit.jupiter.api.Test;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.test.service.HelloService;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 切点表达式测试类
 *
 * @author: DoubleW2w
 * @date: 2024/11/6
 * @project: sbs-small-spring
 */
public class PointcutExpressionTest {
  @Test
  public void testPointcutExpression() throws Exception {
    // 简单实现，只实现了 execution 方式，表示匹配 HelloService和他子类 下面的所有方法
    AspectJExpressionPointcut pointcut =
        new AspectJExpressionPointcut(
            "execution(* org.springframework.test.service.HelloService.*(..))");
    Class<HelloService> clazz = HelloService.class;
    Method method = clazz.getDeclaredMethod("sayHello");
    assertThat(pointcut.matches(clazz)).isTrue();
    assertThat(pointcut.matches(method, clazz)).isTrue();
  }
}
