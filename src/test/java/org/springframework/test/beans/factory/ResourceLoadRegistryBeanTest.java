package org.springframework.test.beans.factory;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.test.service.HelloService;

/**
 * @author: DoubleW2w
 * @date: 2024/10/3
 * @project: sbs-small-spring
 */
public class ResourceLoadRegistryBeanTest {
  @Test
  public void test_xml() throws Exception {
    // 1.初始化 BeanFactory
    DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();

    // 2. 读取配置文件&注册Bean
    XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(beanFactory);
    reader.loadBeanDefinitions("classpath:spring-config.xml");
    // 3. 获取Bean对象调用方法
    HelloService helloService = (HelloService) beanFactory.getBean("helloService");
    String result = helloService.sayHello();
    helloService.sayWorld();
    System.out.println("测试结果：" + result);
  }
}
