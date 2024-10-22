package org.springframework.beans.factory;

/**
 * 由希望在bean工厂中知道自己的bean名称的bean实现的接口。
 *
 * <p>注意，通常不建议对象依赖于它的bean名，因为这代表着对外部配置的潜在脆弱依赖，也可能是对Spring API的不必要依赖。
 *
 * @author: DoubleW2w
 * @date: 2024/10/8
 * @project: sbs-small-spring
 */
public interface BeanNameAware extends Aware {
  void setBeanName(String name);
}
