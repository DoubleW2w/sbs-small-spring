package org.springframework.context.annotation;

import cn.hutool.core.util.StrUtil;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * 负责扫描类路径并自动注册找到的Bean定义到Spring容器中。
 *
 * <p>典型的应用场景是通过配置类或 XML 中配置 @ComponentScan 时，Spring 自动使用 ClassPathBeanDefinitionScanner 来进行类路径扫描和
 * bean 注册。
 *
 * @author: DoubleW2w
 * @date: 2024/11/10
 * @project: sbs-small-spring
 */
public class ClassPathBeanDefinitionScanner extends ClassPathScanningCandidateComponentProvider {

  public static final String AUTOWIRED_ANNOTATION_PROCESSOR_BEAN_NAME =
      "org.springframework.context.annotation.internalAutowiredAnnotationProcessor";

  private BeanDefinitionRegistry registry;

  public ClassPathBeanDefinitionScanner(BeanDefinitionRegistry registry) {
    this.registry = registry;
  }

  /**
   * 扫描类路径的某个包
   *
   * @param basePackages 包路径
   */
  public void doScan(String... basePackages) {
    for (String basePackage : basePackages) {
      // 寻找Component注解的类的bean定义
      Set<BeanDefinition> candidates = findCandidateComponents(basePackage);
      for (BeanDefinition candidate : candidates) {
        // 解析bean的作用域
        String beanScope = resolveBeanScope(candidate);
        if (StrUtil.isNotEmpty(beanScope)) {
          candidate.setScope(beanScope);
        }
        // 生成bean的名称
        String beanName = determineBeanName(candidate);
        // 注册BeanDefinition
        registry.registerBeanDefinition(beanName, candidate);
      }
    }

    // 注册处理@Autowired和@Value注解的BeanPostProcessor
    registry.registerBeanDefinition(
        AUTOWIRED_ANNOTATION_PROCESSOR_BEAN_NAME,
        new BeanDefinition(AutowiredAnnotationBeanPostProcessor.class));
  }

  /**
   * 获取bean的作用域
   *
   * @param beanDefinition bean定义
   * @return 结果
   */
  private String resolveBeanScope(BeanDefinition beanDefinition) {
    Class<?> beanClass = beanDefinition.getBeanClass();
    Scope scope = beanClass.getAnnotation(Scope.class);
    if (scope != null) {
      return scope.value();
    }
    return StrUtil.EMPTY;
  }

  /**
   * 生成bean的名称，在不配置name属性的情况就默认返回类名称（首字母小写）
   *
   * @param beanDefinition bean定义
   * @return bean名称
   */
  private String determineBeanName(BeanDefinition beanDefinition) {
    Class<?> beanClass = beanDefinition.getBeanClass();
    Component component = beanClass.getAnnotation(Component.class);
    String value = component.value();
    if (StrUtil.isEmpty(value)) {
      value = StrUtil.lowerFirst(beanClass.getSimpleName());
    }
    return value;
  }
}
