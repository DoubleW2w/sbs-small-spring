package org.springframework.beans.factory;

import org.junit.jupiter.api.Test;
import org.springframework.beans.context.support.ClassPathXmlApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 分支：aware-interface 测试类
 *
 * @author: DoubleW2w
 * @date: 2024/10/8
 * @project: sbs-small-spring
 */
public class AwareInterfaceTest {
  @Test
  public void test() throws Exception {
    ClassPathXmlApplicationContext applicationContext =
        new ClassPathXmlApplicationContext("classpath:spring-config-aware-interface.xml");
    HelloWorldService helloService = applicationContext.getBean("helloWorldService", HelloWorldService.class);
    assertThat(helloService.getApplicationContext()).isNotNull();
    assertThat(helloService.getBeanFactory()).isNotNull();
  }
}
