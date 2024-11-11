package org.springframework.test.aop;

import org.junit.jupiter.api.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.bean.Car;
import org.springframework.test.bean.Person;

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

  @Test
  public void test_autowiredAnnotation() throws Exception {
    ClassPathXmlApplicationContext applicationContext =
        new ClassPathXmlApplicationContext("classpath:spring-annotation-inject-properties-2.xml");

    Person person = applicationContext.getBean(Person.class);
    assertThat(person.getCar()).isNotNull();
    assertThat(person.getCar().getBrand()).isEqualTo("bmw");
  }
}
