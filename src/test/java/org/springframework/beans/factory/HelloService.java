package org.springframework.beans.factory;

import lombok.Getter;
import lombok.Setter;

/**
 * @author: DoubleW2w
 * @date: 2024/9/29
 * @project: sbs-small-spring
 */
public class HelloService implements InitializingBean, DisposableBean {
  @Getter @Setter private String name;
  @Getter @Setter private String id;
  @Getter @Setter private WorldService worldService;

  public HelloService(String name) {
    this.name = name;
  }

  public HelloService() {}

  public String sayHello() {
    System.out.println("name=" + name);
    return "hello";
  }

  public void sayWorld() {
    worldService.world();
  }

  @Override
  public void destroy() throws Exception {
    System.out.println("执行：HelloService destroy() ");
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    System.out.println("执行：HelloService.afterPropertiesSet");
  }
}
