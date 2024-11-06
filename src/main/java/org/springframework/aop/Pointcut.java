package org.springframework.aop;

/**
 * Spring切入点抽象
 *
 * <p>切入点由一个{@link ClassFilter}和一个{@link MethodMatcher}组成。它们和切入点本身都可以组合起来进行构建
 *
 * @author: DoubleW2w
 * @date: 2024/11/6
 * @project: sbs-small-spring
 */
public interface Pointcut {
  /**
   * 返回这个切入点的类过滤器。
   *
   * @return the ClassFilter (never <code>null</code>)
   */
  ClassFilter getClassFilter();

  /**
   * 返回这个切入点的方法匹配器（MethodMatcher）。
   *
   * @return the MethodMatcher (never <code>null</code>)
   */
  MethodMatcher getMethodMatcher();
}
