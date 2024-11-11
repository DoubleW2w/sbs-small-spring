package org.springframework.util;

import java.security.AccessController;
import java.security.PrivilegedAction;

/**
 * @author: DoubleW2w
 * @date: 2024/10/3
 * @project: sbs-small-spring
 */
public class ClassUtils {

  /**
   * 获取{@link ClassLoader}<br>
   * 获取顺序如下：<br>
   *
   * <pre>
   * 1、获取当前线程的ContextClassLoader
   * 2、获取当前类对应的ClassLoader
   * 3、获取系统ClassLoader（{@link ClassLoader#getSystemClassLoader()}）
   * </pre>
   *
   * @return 类加载器
   */
  public static ClassLoader getDefaultClassLoader() {
    ClassLoader classLoader = getContextClassLoader();
    if (classLoader == null) {
      classLoader = ClassUtils.class.getClassLoader();
      if (null == classLoader) {
        classLoader = getSystemClassLoader();
      }
    }
    return classLoader;
  }

  public static ClassLoader getContextClassLoader() {
    if (System.getSecurityManager() == null) {
      return Thread.currentThread().getContextClassLoader();
    } else {
      // 绕开权限检查
      return AccessController.doPrivileged(
          (PrivilegedAction<ClassLoader>) () -> Thread.currentThread().getContextClassLoader());
    }
  }

  public static ClassLoader getSystemClassLoader() {
    if (System.getSecurityManager() == null) {
      return ClassLoader.getSystemClassLoader();
    } else {
      // 绕开权限检查
      return AccessController.doPrivileged(
          (PrivilegedAction<ClassLoader>) ClassLoader::getSystemClassLoader);
    }
  }

  /**
   * 检查指定的类是否为cglib生成的类。
   * @param clazz the class to check
   */
  public static boolean isCglibProxyClass(Class<?> clazz) {
    return (clazz != null && isCglibProxyClassName(clazz.getName()));
  }

  /**
   * 请检查指定的类名是否为cglib生成的类。
   * @param className the class name to check
   */
  public static boolean isCglibProxyClassName(String className) {
    return (className != null && className.contains("$$"));
  }
}
