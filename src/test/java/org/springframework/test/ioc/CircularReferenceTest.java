package org.springframework.test.ioc;

import org.junit.jupiter.api.Test;
import org.springframework.beans.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.bean.A;
import org.springframework.test.bean.B;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author: DoubleW2w
 * @date: 2024/11/11
 * @project: sbs-small-spring
 */
public class CircularReferenceTest {
  @Test
  public void testCircularReference() throws Exception {
    ClassPathXmlApplicationContext applicationContext =
        new ClassPathXmlApplicationContext("classpath:spring-circular-reference-1.xml");
    A a = applicationContext.getBean("a", A.class);
    B b = applicationContext.getBean("b", B.class);
    assertThat(a.getB() == b).isTrue();
  }

  @Test
  public void test_circularReferenceWithProxyNotThirdCache() throws Exception {
    ClassPathXmlApplicationContext applicationContext =
        new ClassPathXmlApplicationContext("classpath:spring-circular-reference-2.xml");
    A a = applicationContext.getBean("a", A.class);
    B b = applicationContext.getBean("b", B.class);

    // 增加二级缓存不能解决有代理对象时的循环依赖。
    // a被代理，放进二级缓存earlySingletonObjects中的是实例化后的A，而放进一级缓存singletonObjects中的是被代理后的A，实例化b时从earlySingletonObjects获取a，所以b.getA() != a
    assertThat(b.getA() != a).isTrue();
  }
}
