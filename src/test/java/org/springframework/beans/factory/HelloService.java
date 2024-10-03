package org.springframework.beans.factory;

import lombok.Setter;

/**
 * @author: DoubleW2w
 * @date: 2024/9/29
 * @project: sbs-small-spring
 */
public class HelloService {

  private String name;
  @Setter private WorldService worldService;

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
}
