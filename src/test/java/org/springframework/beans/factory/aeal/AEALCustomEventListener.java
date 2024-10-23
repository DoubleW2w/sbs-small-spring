package org.springframework.beans.factory.aeal;

import org.springframework.beans.context.ApplicationListener;

import java.util.Date;

/**
 * @author: DoubleW2w
 * @date: 2024/10/23
 * @project: sbs-small-spring
 */
public class AEALCustomEventListener implements ApplicationListener<AEALCustomEvent> {
  @Override
  public void onApplicationEvent(AEALCustomEvent event) {
    System.out.println("收到：" + event.getSource() + "消息; 时间：" + new Date());
    System.out.println("消息：" + event.getId() + ":" + event.getMessage());
  }
}
