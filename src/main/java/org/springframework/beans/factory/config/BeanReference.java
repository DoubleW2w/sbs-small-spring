package org.springframework.beans.factory.config;

import lombok.Getter;
import lombok.Setter;

/**
 * 用来标识和引用其他Bean
 *
 * @author: DoubleW2w
 * @date: 2024/9/30
 * @project: sbs-small-spring
 */
public class BeanReference {
  @Getter private final String beanName;

  public BeanReference(String beanName) {
    this.beanName = beanName;
  }
}
