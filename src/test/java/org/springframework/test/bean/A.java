package org.springframework.test.bean;


/**
 * @author: DoubleW2w
 * @date: 2024/11/11
 * @project: sbs-small-spring
 */
public class A {
  private B b;

  public A() {
  }

  public void func(){}

  public B getB() {
    return b;
  }

  public void setB(B b) {
    this.b = b;
  }
}
