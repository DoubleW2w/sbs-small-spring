package org.springframework.beans.factory.config;

import org.springframework.beans.factory.HierarchicalBeanFactory;

/**
 * 与Bean的作用域（Scope）、生命周期回调、依赖注入等相关的功能
 *
 * @author: DoubleW2w
 * @date: 2024/10/4
 * @project: sbs-small-spring
 */
public interface ConfigurableBeanFactory extends HierarchicalBeanFactory, SingletonBeanRegistry {

  String SCOPE_SINGLETON = "singleton";

  String SCOPE_PROTOTYPE = "prototype";
}
