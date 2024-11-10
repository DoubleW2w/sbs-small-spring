package org.springframework.test.aop;

import org.junit.jupiter.api.Test;
import org.springframework.beans.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.bean.Car;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author: DoubleW2w
 * @date: 2024/11/10
 * @project: sbs-small-spring
 */
public class AnnotationInjectPropertiesTest {
  @Test
  public void test_valueAnnotation() throws Exception {
    ClassPathXmlApplicationContext applicationContext =
        new ClassPathXmlApplicationContext("classpath:spring-annotation-inject-properties-1.xml");

    Car car = applicationContext.getBean("car", Car.class);
    assertThat(car).isNotNull();
    assertThat(car.getBrand()).isEqualTo("bmw");
  }
}
