package org.springframework.beans.factory;

/**
 * 回调函数，允许bean知道bean {@link ClassLoader};也就是当前bean工厂用来加载bean类的类加载器。
 *
 * @author: DoubleW2w
 * @date: 2024/10/8
 * @project: sbs-small-spring
 */
public interface BeanClassLoaderAware extends Aware {
  void setBeanClassLoader(ClassLoader classLoader);
}
