package org.springframework.beans.factory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: DoubleW2w
 * @date: 2024/9/30
 * @project: sbs-small-spring
 */
public class WorldService {
  private String id;
  private static Map<String, String> hashMap = new HashMap<>();

  public WorldService() {}

  public WorldService(String id) {
    this.id = id;
  }

  public String world() {
    return id;
  }

  public void initDataMethod() {
    System.out.println("执行：init-method");
    this.id = "zhangsan";
    hashMap.put("10001", "小叮当");
    hashMap.put("10002", "大叮当");
    hashMap.put("10003", "笔记本");
  }

  public void destroyDataMethod() {
    System.out.println("执行：destroy-method");
    hashMap.clear();
  }
}
