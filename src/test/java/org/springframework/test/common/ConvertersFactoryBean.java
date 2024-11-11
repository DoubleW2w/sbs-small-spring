package org.springframework.test.common;

import org.springframework.beans.factory.FactoryBean;

import java.util.HashSet;
import java.util.Set;

/**
 * @author: DoubleW2w
 * @date: 2024/11/11
 * @project: sbs-small-spring
 */
public class ConvertersFactoryBean implements FactoryBean<Set<?>> {
  @Override
  public Set<?> getObject() throws Exception {
    HashSet<Object> converters = new HashSet<>();
    StringToLocalDateConverter stringToLocalDateConverter =
        new StringToLocalDateConverter("yyyy-MM-dd");
    converters.add(stringToLocalDateConverter);
    return converters;
  }

  @Override
  public Class<?> getObjectType() {
    return Set.class;
  }

  @Override
  public boolean isSingleton() {
    return true;
  }
}
