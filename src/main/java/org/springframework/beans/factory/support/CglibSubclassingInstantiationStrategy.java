package org.springframework.beans.factory.support;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.NoOp;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;

import java.lang.reflect.Constructor;

/**
 * cglib实例化
 *
 * @author: DoubleW2w
 * @date: 2024/9/29
 * @project: sbs-small-spring
 */
public class CglibSubclassingInstantiationStrategy implements InstantiationStrategy {
  @Override
  public Object instantiate(
      BeanDefinition beanDefinition, String beanName, Constructor ctor, Object[] args)
      throws BeansException {
    Enhancer enhancer = new Enhancer();
    enhancer.setSuperclass(beanDefinition.getBeanClass());
    enhancer.setCallback(
        new NoOp() {
          @Override
          public int hashCode() {
            return super.hashCode();
          }
        });
    // 无参
    if (null == ctor) return enhancer.create();
    // 有参
    return enhancer.create(ctor.getParameterTypes(), args);
  }
}
