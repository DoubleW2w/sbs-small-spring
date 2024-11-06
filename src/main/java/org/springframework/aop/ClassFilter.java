package org.springframework.aop;

/**
 * 限制切入点匹配给定目标类集的过滤器。
 *
 * @author: DoubleW2w
 * @date: 2024/11/6
 * @project: sbs-small-spring
 */
public interface ClassFilter {
  /**
   * 切入点是否应用于给定的接口或目标类？
   *
   * @param clazz 候选的目标类
   * @return 通知是否应该应用到目标类上
   */
  boolean matches(Class<?> clazz);
}
