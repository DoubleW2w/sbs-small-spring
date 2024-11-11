package org.springframework.test.bean;

import lombok.Data;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * @author: DoubleW2w
 * @date: 2024/11/10
 * @project: sbs-small-spring
 */
@Component
@Data
@ToString
public class Car {
  @Value(value = "${brand}")
  private String brand;

  @Value(value = "${name}")
  private String name;

  private int price;

  private LocalDate produceDate;
}
