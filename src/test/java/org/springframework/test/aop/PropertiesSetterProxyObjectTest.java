package org.springframework.test.aop;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.service.LoveUService;

/**
 * @author: DoubleW2w
 * @date: 2024/11/10
 * @project: sbs-small-spring
 */
public class PropertiesSetterProxyObjectTest {
  @Test
  public void testPopulateProxyBeanWithPropertyValues() throws Exception {
    ClassPathXmlApplicationContext applicationContext =
        new ClassPathXmlApplicationContext(
            "classpath:properties-setter-proxy-object-1.xml");

    // 获取代理对象
    LoveUService loveUService = (LoveUService) applicationContext.getBean("loveUService", LoveUService.class);
    loveUService.explode();
    assertThat(loveUService.getName()).isEqualTo("earth");
  }
}
