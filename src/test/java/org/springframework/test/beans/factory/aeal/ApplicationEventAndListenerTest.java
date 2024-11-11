package org.springframework.test.beans.factory.aeal;

import org.junit.jupiter.api.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author: DoubleW2w
 * @date: 2024/10/23
 * @project: sbs-small-spring
 */
public class ApplicationEventAndListenerTest {
  @Test
  public void test_event() {
    ClassPathXmlApplicationContext applicationContext =
        new ClassPathXmlApplicationContext("classpath:spring-application-event-and-listener.xml");
    applicationContext.publishEvent(
        new AEALCustomEvent(applicationContext, 1019129009086763L, "成功了！"));

    applicationContext.registerShutdownHook();
  }
}
