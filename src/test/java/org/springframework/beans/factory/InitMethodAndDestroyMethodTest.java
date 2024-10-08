package org.springframework.beans.factory;

import org.junit.jupiter.api.Test;
import org.springframework.beans.context.support.ClassPathXmlApplicationContext;

/**
 * @author: DoubleW2w
 * @date: 2024/10/8
 * @project: sbs-small-spring
 */
public class InitMethodAndDestroyMethodTest {
  @Test
  public void test_ApplicationContext() {
    // 1.初始化 BeanFactory
    ClassPathXmlApplicationContext applicationContext =
        new ClassPathXmlApplicationContext("classpath:spring-config-init-method-destroy-method.xml");
    applicationContext.registerShutdownHook();

    // 2. 获取Bean对象调用方法
    HelloService helloService = applicationContext.getBean("helloService", HelloService.class);
    String result = helloService.sayHello();
    System.out.println("测试结果：" + result);
  }
}
