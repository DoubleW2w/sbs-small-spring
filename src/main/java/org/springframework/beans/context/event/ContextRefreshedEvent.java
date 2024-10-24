package org.springframework.beans.context.event;

/**
 * 上下文刷新事件类
 *
 * @author: DoubleW2w
 * @date: 2024/10/23
 * @project: sbs-small-spring
 */
public class ContextRefreshedEvent extends ApplicationContextEvent {
  /**
   * Constructs a prototypical Event.
   *
   * @param source the object on which the Event initially occurred
   * @throws IllegalArgumentException if source is null
   */
  public ContextRefreshedEvent(Object source) {
    super(source);
  }
}
