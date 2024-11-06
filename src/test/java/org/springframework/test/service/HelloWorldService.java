package org.springframework.test.service;

import org.springframework.beans.BeansException;
import org.springframework.beans.context.ApplicationContext;
import org.springframework.beans.context.ApplicationContextAware;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanNameAware;

/**
 * @author: DoubleW2w
 * @date: 2024/10/8
 * @project: sbs-small-spring
 */
public class HelloWorldService
    implements BeanNameAware, BeanClassLoaderAware, ApplicationContextAware, BeanFactoryAware {

  private ApplicationContext applicationContext;

  private BeanFactory beanFactory;

  public String sayHelloWorld() {
    System.out.println("hello world");
    return "hello world";
  }

  @Override
  public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
    this.beanFactory = beanFactory;
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.applicationContext = applicationContext;
  }

  public ApplicationContext getApplicationContext() {
    return applicationContext;
  }

  public BeanFactory getBeanFactory() {
    return beanFactory;
  }

  @Override
  public void setBeanClassLoader(ClassLoader classLoader) {
    System.out.println("ClassLoader：" + classLoader);
  }

  @Override
  public void setBeanName(String name) {
    System.out.println("Bean Name is：" + name);
  }
}
