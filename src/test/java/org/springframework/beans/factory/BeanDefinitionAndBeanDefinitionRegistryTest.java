package org.springframework.beans.factory;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.NoOp;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

import java.lang.reflect.Constructor;

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

  /** 测试cglib动态代理 */
  @Test
  public void test_cglib() {
    Enhancer enhancer = new Enhancer();
    enhancer.setSuperclass(HelloService.class);
    enhancer.setCallback(
        new NoOp() {
          @Override
          public int hashCode() {
            return super.hashCode();
          }
        });
    Object obj = enhancer.create(new Class[] {String.class}, new Object[] {"zhangsan"});
    System.out.println(obj);
  }

  /**
   * 使用反射来实例化对象
   *
   * @throws Exception
   */
  @Test
  public void test_newInstance() throws Exception {
    Class<HelloService> userServiceClass = HelloService.class;
    Constructor<HelloService> declaredConstructor =
        userServiceClass.getDeclaredConstructor(String.class);
    HelloService helloService = declaredConstructor.newInstance("zhangsan");
    System.out.println(helloService);
  }

  /**
   *
   * @throws Exception
   */
  @Test
  public void test_newInstance2() throws Exception {
    Class<HelloService> beanClass = HelloService.class;
    Constructor<?>[] declaredConstructors = beanClass.getDeclaredConstructors();
    Constructor<?> constructor = declaredConstructors[0];
    Constructor<HelloService> declaredConstructor =
        beanClass.getDeclaredConstructor(constructor.getParameterTypes());
    HelloService helloService = declaredConstructor.newInstance("zhangsan");
    System.out.println(helloService);
  }

  @Test
  public void test_BeanFactoryInstantiationStrategy() {
    // 1.初始化 BeanFactory
    DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();

    // 2.注册 bean
    BeanDefinition beanDefinition = new BeanDefinition(HelloService.class);
    beanFactory.registerBeanDefinition("helloService", beanDefinition);

    // 3.第一次获取 bean
    HelloService userService = (HelloService) beanFactory.getBean("helloService", "zhangsan");
    userService.sayHello();

    // 4.第二次获取 bean from Singleton
    HelloService userService_singleton = (HelloService) beanFactory.getBean("helloService");
    userService_singleton.sayHello();

    System.out.println(userService_singleton == userService);
  }
}
