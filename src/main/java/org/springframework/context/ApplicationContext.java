package org.springframework.context;

import org.springframework.core.io.ResourceLoader;
import org.springframework.beans.factory.HierarchicalBeanFactory;
import org.springframework.beans.factory.ListableBeanFactory;

/**
 * 应用上下文接口
 *
 * @author: DoubleW2w
 * @date: 2024/10/8
 * @project: sbs-small-spring
 */
public interface ApplicationContext extends ListableBeanFactory, HierarchicalBeanFactory, ResourceLoader, ApplicationEventPublisher  {
}
