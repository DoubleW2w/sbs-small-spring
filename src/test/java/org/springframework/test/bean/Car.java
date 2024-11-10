package org.springframework.test.bean;

import lombok.Data;
import lombok.ToString;

import java.util.StringJoiner;

/**
 * @author: DoubleW2w
 * @date: 2024/11/10
 * @project: sbs-small-spring
 */
@Data
@ToString
public class Car {
  private String brand;
  private String name;
}
