package org.springframework.context.annotation;

import cn.hutool.core.util.ClassUtil;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.stereotype.Component;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 扫描类路径以查找符合条件的候选组件（Candidate Component），即符合特定条件的类。只负责扫描，不负责注册 bean 定义并且允许自定义过滤条件
 *
 * @author: DoubleW2w
 * @date: 2024/11/10
 * @project: sbs-small-spring
 */
public class ClassPathScanningCandidateComponentProvider {
  public Set<BeanDefinition> findCandidateComponents(String basePackage) {
    Set<BeanDefinition> candidates = new LinkedHashSet<>();
    // 扫描有org.springframework.stereotype.Component注解的类
    Set<Class<?>> classes = ClassUtil.scanPackageByAnnotation(basePackage, Component.class);
    for (Class<?> clazz : classes) {
      BeanDefinition beanDefinition = new BeanDefinition(clazz);
      candidates.add(beanDefinition);
    }
    return candidates;
  }
}
