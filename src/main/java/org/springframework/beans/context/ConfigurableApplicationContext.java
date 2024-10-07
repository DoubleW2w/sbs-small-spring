package org.springframework.beans.context;

import org.springframework.beans.BeansException;

/**
 * SPI 接口配置应用上下文
 *
 * @author: DoubleW2w
 * @date: 2024/10/8
 * @project: sbs-small-spring
 */
public interface ConfigurableApplicationContext extends ApplicationContext {
  /**
   * 刷新容器
   *
   * @throws BeansException
   */
  void refresh() throws BeansException;
}
