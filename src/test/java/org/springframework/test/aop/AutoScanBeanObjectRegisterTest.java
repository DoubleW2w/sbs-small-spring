package org.springframework.test.aop;

import org.junit.jupiter.api.Test;
import org.springframework.beans.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.bean.Car;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * @author: DoubleW2w
 * @date: 2024/11/10
 * @project: sbs-small-spring
 */
public class AutoScanBeanObjectRegisterTest {
  @Test
  public void test_propertyPlaceholderConfigurer() throws Exception {
    ClassPathXmlApplicationContext applicationContext =
        new ClassPathXmlApplicationContext("classpath:spring-auto-scan-object-register-1.xml");

    Car car = applicationContext.getBean("car", Car.class);
    assertThat(car.getBrand()).isEqualTo("bmw");
  }

  @Test
  public void test_packageScan() throws Exception {
    ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring-auto-scan-object-register-2.xml");
    Car car = applicationContext.getBean("car", Car.class);
    assertThat(car).isNotNull();
  }
}
