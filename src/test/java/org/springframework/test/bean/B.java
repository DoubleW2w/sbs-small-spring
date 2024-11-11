package org.springframework.test.bean;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * @author: DoubleW2w
 * @date: 2024/11/11
 * @project: sbs-small-spring
 */
public class B {
  private A a;

  public B(A a) {
    this.a = a;
  }

  public B() {
  }

  public A getA() {
    return a;
  }

  public void setA(A a) {
    this.a = a;
  }
}
