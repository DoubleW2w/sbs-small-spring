package org.springframework.test.beans.factory;

import org.junit.jupiter.api.Test;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanReference;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.test.service.HelloService;
import org.springframework.test.service.WorldService;

/**
 * @author: DoubleW2w
 * @date: 2024/9/30
 * @project: sbs-small-spring
 */
public class PopulateBeanWithPropertyValuesTest {
  @Test
  public void test_BeanFactoryInstantiationStrategy() {
    // 1.初始化 BeanFactory
    DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();

    // 2.注册 bean
    beanFactory.registerBeanDefinition("worldService", new BeanDefinition(WorldService.class));

    // 3. helloService 设置属性[name、worldService]
    PropertyValues propertyValues = new PropertyValues();
    propertyValues.addPropertyValue(
        new PropertyValue("worldService", new BeanReference("worldService")));

    // 4. 注册 bean
    beanFactory.registerBeanDefinition(
        "helloService", new BeanDefinition(HelloService.class, propertyValues));

    // 5.获取 bean
    HelloService userService = (HelloService) beanFactory.getBean("helloService","zhangsan");
    userService.sayHello();
    userService.sayWorld();
  }
}
