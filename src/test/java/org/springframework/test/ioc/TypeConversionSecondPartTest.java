package org.springframework.test.ioc;

import org.junit.jupiter.api.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.bean.Car;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author: DoubleW2w
 * @date: 2024/11/11
 * @project: sbs-small-spring
 */
public class TypeConversionSecondPartTest {
  @Test
  public void test_conversionService() throws Exception{
    ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring-type-converter-second.xml");

    Car car = applicationContext.getBean("car", Car.class);
    assertThat(car.getPrice()).isEqualTo(1000000);
    assertThat(car.getProduceDate()).isEqualTo(LocalDate.of(2021, 1, 1));
  }
}
