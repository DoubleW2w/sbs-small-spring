package org.springframework.beans.context;

import java.util.EventObject;

/**
 * 定义出具备事件功能的抽象类 ApplicationEvent，后续所有事件的类都需要继承这个类。
 *
 * @author: DoubleW2w
 * @date: 2024/10/23
 * @project: sbs-small-spring
 */
public abstract class ApplicationEvent extends EventObject {

  /**
   * Constructs a prototypical Event.
   *
   * @param source the object on which the Event initially occurred
   * @throws IllegalArgumentException if source is null
   */
  public ApplicationEvent(Object source) {
    super(source);
  }
}
