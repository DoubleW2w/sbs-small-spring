package org.springframework.beans.factory;

/**
 * @author: DoubleW2w
 * @date: 2024/9/30
 * @project: sbs-small-spring
 */
public class WorldService {
  private String id = "world";

  public WorldService() {}

  public WorldService(String id) {
    this.id = id;
  }

  public String world() {
    System.out.println(id);
    return id;
  }
}
