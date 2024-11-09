package org.springframework.test.aop;

import org.junit.jupiter.api.Test;
import org.springframework.beans.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.service.LoveUService;
import org.springframework.test.service.WorldService;

import java.util.Arrays;

/**
 * @author: DoubleW2w
 * @date: 2024/11/9
 * @project: sbs-small-spring
 */
public class AopIntoBeanLifecycleTest {
  @Test
  public void testAutoProxy() throws Exception {
    ClassPathXmlApplicationContext applicationContext =
        new ClassPathXmlApplicationContext("classpath:spring-aop-into-bean-lifecycle.xml");

    // 获取代理对象
    LoveUService loveUService = applicationContext.getBean("loveUService", LoveUService.class);
    loveUService.explode();
  }
}
