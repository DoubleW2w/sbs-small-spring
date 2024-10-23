package org.springframework.beans.factory.aeal;

import org.springframework.beans.context.ApplicationListener;
import org.springframework.beans.context.event.ContextClosedEvent;

/**
 * @author: DoubleW2w
 * @date: 2024/10/23
 * @project: sbs-small-spring
 */
public class AEALContextClosedEventListener implements ApplicationListener<ContextClosedEvent> {
  @Override
  public void onApplicationEvent(ContextClosedEvent event) {
    System.out.println("关闭事件：" + this.getClass().getName());
  }
}
