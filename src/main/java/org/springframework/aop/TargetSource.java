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

  /**
   * 获取目标对象实现的接口类数组
   *
   * @return 目标对象实现的接口的Class对象数组
   */
  public Class<?>[] getTargetClass() {
    return this.target.getClass().getInterfaces();
  }

  /**
   * 获取目标对象
   *
   * @return 目标对象
   */
  public Object getTarget() {
    return this.target;
  }
}
