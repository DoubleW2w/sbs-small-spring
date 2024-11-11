package org.springframework.core.io;

/**
 * @author: DoubleW2w
 * @date: 2024/10/3
 * @project: sbs-small-spring
 */
public interface ResourceLoader {
  /** Pseudo URL prefix for loading from the class path: "classpath:" */
  String CLASSPATH_URL_PREFIX = "classpath:";

  Resource getResource(String location);
}
