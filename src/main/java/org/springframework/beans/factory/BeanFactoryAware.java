package org.springframework.beans.factory;

import org.springframework.beans.BeansException;

/**
 * 实现此接口，既能感知到所属的 BeanFactory
 *
 * @author: DoubleW2w
 * @date: 2024/10/8
 * @project: sbs-small-spring
 */
public interface BeanFactoryAware extends Aware {
  void setBeanFactory(BeanFactory beanFactory) throws BeansException;
}
