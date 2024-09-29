package org.springframework.beans.factory;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

/**
 * @author: DoubleW2w
 * @date: 2024/9/29
 * @project: sbs-small-spring
 */
public class BeanDefinitionAndBeanDefinitionRegistryTest {

  @Test
  public void test_BeanFactory() {
    // 1.初始化 BeanFactory
    DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();

    // 2.注册 bean
    BeanDefinition beanDefinition = new BeanDefinition(HelloService.class);
    beanFactory.registerBeanDefinition("helloService", beanDefinition);

    // 3.第一次获取 bean
    HelloService userService = (HelloService) beanFactory.getBean("helloService");
    userService.sayHello();

    // 4.第二次获取 bean from Singleton
    HelloService userService_singleton = (HelloService) beanFactory.getBean("helloService");
    userService_singleton.sayHello();
  }
}
