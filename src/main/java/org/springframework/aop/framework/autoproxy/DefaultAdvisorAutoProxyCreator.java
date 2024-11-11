package org.springframework.aop.framework.autoproxy;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.aop.*;
import org.springframework.aop.aspectj.AspectJExpressionPointcutAdvisor;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

/**
 * 职责：在应用上下文中扫描 Advisor，并根据配置条件自动为目标 Bean 创建代理，以应用 AOP 切面。
 *
 * <p>1. 扫描和查找 Advisor
 *
 * <p>2. 创建代理对象
 *
 * <p>3. 返回代理对象
 *
 * @author: DoubleW2w
 * @date: 2024/11/9
 * @project: sbs-small-spring
 */
public class DefaultAdvisorAutoProxyCreator
    implements InstantiationAwareBeanPostProcessor, BeanFactoryAware {

  private DefaultListableBeanFactory beanFactory;

  private Set<Object> earlyProxyReferences = new HashSet<>();

  @Override
  public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
    this.beanFactory = (DefaultListableBeanFactory) beanFactory;
  }

  @Override
  public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName)
      throws BeansException {
    return null;
  }

  @Override
  public boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {
    return true;
  }

  @Override
  public Object postProcessBeforeInitialization(Object bean, String beanName)
      throws BeansException {
    return bean;
  }

  @Override
  public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
    // 如果前期代理对象不存在
    if (!earlyProxyReferences.contains(beanName)) {
      return wrapIfNecessary(bean, beanName);
    }
    return bean;
  }

  @Override
  public Object getEarlyBeanReference(Object bean, String beanName) throws BeansException {
    earlyProxyReferences.add(beanName);
    return wrapIfNecessary(bean, beanName);
  }

  /**
   * 根据需要包装Bean，如果Bean需要代理，则创建并返回代理对象
   * @param bean bean对象
   * @param beanName bean名称
   * @return bean或者bean的代理对象
   */
  protected Object wrapIfNecessary(Object bean, String beanName) {
    //避免死循环
    if (isInfrastructureClass(bean.getClass())) {
      return bean;
    }

    Collection<AspectJExpressionPointcutAdvisor> advisors = beanFactory.getBeansOfType(AspectJExpressionPointcutAdvisor.class).values();
    try {
      for (AspectJExpressionPointcutAdvisor advisor : advisors) {
        ClassFilter classFilter = advisor.getPointcut().getClassFilter();
        if (classFilter.matches(bean.getClass())) {
          AdvisedSupport advisedSupport = new AdvisedSupport();
          TargetSource targetSource = new TargetSource(bean);

          advisedSupport.setTargetSource(targetSource);
          advisedSupport.setMethodInterceptor((MethodInterceptor) advisor.getAdvice());
          advisedSupport.setMethodMatcher(advisor.getPointcut().getMethodMatcher());

          //返回代理对象
          return new ProxyFactory(advisedSupport).getProxy();
        }
      }
    } catch (Exception ex) {
      throw new BeansException("Error create proxy bean for: " + beanName, ex);
    }
    return bean;
  }

  @Override
  public PropertyValues postProcessPropertyValues(PropertyValues pvs, Object bean, String beanName)
      throws BeansException {
    return pvs;
  }

  /**
   * 「类对象表示的」类或者接口与「beanClass所表示的」类或者接口是否相同，或者是「beanClass所表示的」类或者接口的父类
   *
   * <p>即 是否是 Advice、Pointcut、Advisor 类
   *
   * @param beanClass bean对象所表示的类
   * @return
   */
  private boolean isInfrastructureClass(Class<?> beanClass) {
    return Advice.class.isAssignableFrom(beanClass)
        || Pointcut.class.isAssignableFrom(beanClass)
        || Advisor.class.isAssignableFrom(beanClass);
  }
}
