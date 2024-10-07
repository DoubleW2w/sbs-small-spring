package org.springframework.beans;

import java.util.ArrayList;
import java.util.List;

/**
 * 属性值集合
 *
 * @author: DoubleW2w
 * @date: 2024/9/30
 * @project: sbs-small-spring
 */
public class PropertyValues {
  private final List<PropertyValue> propertyValueList = new ArrayList<>();

  public void addPropertyValue(PropertyValue pv) {
    for (int i = 0; i < this.propertyValueList.size(); i++) {
      PropertyValue currentPv = this.propertyValueList.get(i);
      if (currentPv.getName().equals(pv.getName())) {
        //覆盖原有的属性值
        this.propertyValueList.set(i, pv);
        return;
      }
    }
    this.propertyValueList.add(pv);
  }

  public PropertyValue[] getPropertyValues() {
    return this.propertyValueList.toArray(new PropertyValue[0]);
  }

  public PropertyValue getPropertyValue(String propertyName) {
    for (PropertyValue pv : this.propertyValueList) {
      if (pv.getName().equals(propertyName)) {
        return pv;
      }
    }
    return null;
  }
}
