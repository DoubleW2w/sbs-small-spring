package org.springframework.test.beans.factory.aeal;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.context.event.ApplicationContextEvent;

/**
 * AEAL是分支名称 application-event-and-listener 的首字母拼写
 *
 * @author: DoubleW2w
 * @date: 2024/10/23
 * @project: sbs-small-spring
 */
@Setter
@Getter
public class AEALCustomEvent extends ApplicationContextEvent {
  private Long id;
  private String message;

  public AEALCustomEvent(Object source, Long id, String message) {
    super(source);
    this.id = id;
    this.message = message;
  }
}
