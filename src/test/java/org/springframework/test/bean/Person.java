package org.springframework.test.bean;

import lombok.Data;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author: DoubleW2w
 * @date: 2024/11/10
 * @project: sbs-small-spring
 */
@Component
@Data
@ToString
public class Person {
  private String name;

  @Autowired private Car car;
}
