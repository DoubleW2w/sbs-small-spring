package org.springframework.context;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.Aware;

/**
 * 实现此接口，既能感知到所属的 ApplicationContext
 *
 * @author: DoubleW2w
 * @date: 2024/10/8
 * @project: sbs-small-spring
 */
public interface ApplicationContextAware extends Aware {

  void setApplicationContext(ApplicationContext applicationContext) throws BeansException;
}
