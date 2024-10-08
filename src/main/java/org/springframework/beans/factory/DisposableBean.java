package org.springframework.beans.factory;

/**
 * 希望在销毁时释放资源的bean接口实现。
 *
 * <p>如果BeanFactory分配了一个缓存的单例，它应该调用destroy方法。应用程序上下文应该在关闭时释放其所有的单例。
 *
 * @author: DoubleW2w
 * @date: 2024/10/8
 * @project: sbs-small-spring
 */
public interface DisposableBean {
  /**
   * 销毁方法
   * @throws Exception
   */
  void destroy() throws Exception;
}
