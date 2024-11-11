package org.springframework.beans.factory;

import org.springframework.beans.BeansException;

/**
 * 对象工厂
 *
 * @author: DoubleW2w
 * @date: 2024/11/11
 * @project: sbs-small-spring
 */
public interface ObjectFactory<T> {

  T getObject() throws BeansException;
}
