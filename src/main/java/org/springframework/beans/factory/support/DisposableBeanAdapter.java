package org.springframework.beans.factory.support;

import cn.hutool.core.util.StrUtil;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.config.BeanDefinition;

import java.lang.reflect.Method;

/**
 * 实现了{@link DisposableBean}和{@link Runnable}接口的适配器，这些接口在给定的bean实例上执行各种销毁步骤:
 *
 * @author: DoubleW2w
 * @date: 2024/10/8
 * @project: sbs-small-spring
 */
public class DisposableBeanAdapter implements DisposableBean {
  /** bean对象 */
  private final Object bean;

  /** bean名称 */
  private final String beanName;

  /** 销毁方法 */
  private String destroyMethodName;

  public DisposableBeanAdapter(Object bean, String beanName, BeanDefinition beanDefinition) {
    this.bean = bean;
    this.beanName = beanName;
    this.destroyMethodName = beanDefinition.getDestroyMethodName();
  }

  @Override
  public void destroy() throws Exception {
    // 1. 实现接口 DisposableBean
    if (bean instanceof DisposableBean) {
      ((DisposableBean) bean).destroy();
    }

    // 2. 注解配置 destroy-method {判断是为了避免二次执行销毁}
    if (StrUtil.isNotEmpty(destroyMethodName)
        && !(bean instanceof DisposableBean && "destroy".equals(this.destroyMethodName))) {
      Method destroyMethod = bean.getClass().getMethod(destroyMethodName);
      if (null == destroyMethod) {
        throw new BeansException(
            "Couldn't find a destroy method named '"
                + destroyMethodName
                + "' on bean with name '"
                + beanName
                + "'");
      }
      destroyMethod.invoke(bean);
    }
  }
}
