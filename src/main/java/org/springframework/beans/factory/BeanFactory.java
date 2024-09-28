package org.springframework.beans.factory;

import java.util.HashMap;
import java.util.Map;

/**
 * bean工厂：负责注册bean和获取bean
 *
 * @author derekyi
 * @date 2020/11/22
 * @project: sbs-small-spring
 */
public class BeanFactory {

  private final Map<String, Object> beanMap = new HashMap<>();

  public void registerBean(String name, Object bean) {
    beanMap.put(name, bean);
  }

  public Object getBean(String name) {
    return beanMap.get(name);
  }
}
