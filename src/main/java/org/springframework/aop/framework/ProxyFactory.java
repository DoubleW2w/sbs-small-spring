package org.springframework.aop.framework;

import org.springframework.aop.AdvisedSupport;

/**
 * 代理工厂
 *
 * @author: DoubleW2w
 * @date: 2024/11/9
 * @project: sbs-small-spring
 */
public class ProxyFactory {
  private AdvisedSupport advisedSupport;

  public ProxyFactory(AdvisedSupport advisedSupport) {
    this.advisedSupport = advisedSupport;
  }

  public Object getProxy() {
    return createAopProxy().getProxy();
  }

  /**
   * 默认情况下是JDK动态代理
   *
   * @return
   */
  private AopProxy createAopProxy() {
    if (advisedSupport.isProxyTargetClass()) {
      return new CglibAopProxy(advisedSupport);
    }

    return new JdkDynamicAopProxy(advisedSupport);
  }
}
