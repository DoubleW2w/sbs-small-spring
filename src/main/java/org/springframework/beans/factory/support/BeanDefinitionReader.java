package org.springframework.beans.factory.support;

import org.springframework.beans.BeansException;
import org.springframework.beans.core.io.Resource;
import org.springframework.beans.core.io.ResourceLoader;

/**
 * Bean 定义输入流： 获取Bean定义注册信息、加载Bean定义
 *
 * @author: DoubleW2w
 * @date: 2024/10/3
 * @project: sbs-small-spring
 */
public interface BeanDefinitionReader {
  BeanDefinitionRegistry getRegistry();

  ResourceLoader getResourceLoader();

  void loadBeanDefinitions(Resource resource) throws BeansException;

  void loadBeanDefinitions(Resource... resources) throws BeansException;

  void loadBeanDefinitions(String location) throws BeansException;
}
