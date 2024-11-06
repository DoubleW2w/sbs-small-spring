package org.springframework.aop;

/**
 * 被代理的目标对象
 *
 * @author: DoubleW2w
 * @date: 2024/11/6
 * @project: sbs-small-spring
 */
public class TargetSource {
  /** 目标对象 */
  private final Object target;

  public TargetSource(Object target) {
    this.target = target;
  }

  public Class<?>[] getTargetClass() {
    return this.target.getClass().getInterfaces();
  }

  public Object getTarget() {
    return this.target;
  }
}
