package org.springframework.beans.factory;

/**
 * @author: DoubleW2w
 * @date: 2024/9/29
 * @project: sbs-small-spring
 */
public class HelloService {
  public String sayHello() {
    System.out.println("hello");
    return "hello";
  }
}
