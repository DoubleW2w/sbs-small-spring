package org.springframework.test.beans.factory.aeal;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

/**
 * @author: DoubleW2w
 * @date: 2024/10/23
 * @project: sbs-small-spring
 */
public class ContextRefreshedEventListener implements ApplicationListener<ContextRefreshedEvent> {
  @Override
  public void onApplicationEvent(ContextRefreshedEvent event) {
    System.out.println("刷新事件：" + this.getClass().getName());
  }
}
