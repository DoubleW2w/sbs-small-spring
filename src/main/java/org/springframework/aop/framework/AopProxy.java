package org.springframework.aop.framework;

/**
 * AOP 代理的抽象：代理「已配置AOP代理」的接口，允许为目标对象创建一个代理对象
 *
 * @author: DoubleW2w
 * @date: 2024/11/6
 * @project: sbs-small-spring
 */
public interface AopProxy {
  Object getProxy();
}
