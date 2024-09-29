package org.springframework.beans.factory;

/**
 * Bean 异常
 *
 * @author: DoubleW2w
 * @date: 2024/9/29
 * @project: sbs-small-spring
 */
public class BeansException extends RuntimeException {
  public BeansException(String msg) {
    super(msg);
  }

  public BeansException(String msg, Throwable cause) {
    super(msg, cause);
  }
}
