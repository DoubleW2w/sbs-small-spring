package org.springframework.test.service;

/**
 * @author: DoubleW2w
 * @date: 2024/11/6
 * @project: sbs-small-spring
 */
public class LoveUServiceImpl implements LoveUService {

  private String name;
  @Override
  public void explode() {
    System.out.println("I Am Missing");
  }

  @Override
  public String explodeReturn() {
    return "I Love U";
  }

  @Override
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
