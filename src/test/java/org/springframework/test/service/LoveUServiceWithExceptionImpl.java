package org.springframework.test.service;

/**
 * @author: DoubleW2w
 * @date: 2024/11/9
 * @project: sbs-small-spring
 */
public class LoveUServiceWithExceptionImpl implements LoveUService {

  private String name;

  @Override
  public void explode() {
    System.out.println("I Am Missing");
    throw new RuntimeException();
  }

  @Override
  public String explodeReturn() {
    return null;
  }

  @Override
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
