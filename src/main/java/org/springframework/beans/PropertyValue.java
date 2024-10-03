package org.springframework.beans;

import lombok.Getter;

/**
 * 属性信息
 *
 * @author: DoubleW2w
 * @date: 2024/9/30
 * @project: sbs-small-spring
 */
public class PropertyValue {
  /** 属性名称 */
  @Getter private final String name;

  /** 属性值 */
  @Getter private final Object value;

  public PropertyValue(String name, Object value) {
    this.name = name;
    this.value = value;
  }
}
