package org.springframework.aop.framework.autoproxy;

import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.aop.*;
import org.springframework.aop.aspectj.AspectJExpressionPointcutAdvisor;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

import java.util.Collection;

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

  @Override
  public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
    this.beanFactory = (DefaultListableBeanFactory) beanFactory;
  }

  @Override
  public Object postProcessBeforeInitialization(Object bean, String beanName)
      throws BeansException {
    return bean;
  }

  @Override
  public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
    return bean;
  }

  @Override
  public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName)
      throws BeansException {
    if (isInfrastructureClass(beanClass)) return null;

    Collection<AspectJExpressionPointcutAdvisor> advisors =
        beanFactory.getBeansOfType(AspectJExpressionPointcutAdvisor.class).values();

    for (AspectJExpressionPointcutAdvisor advisor : advisors) {
      ClassFilter classFilter = advisor.getPointcut().getClassFilter();
      if (!classFilter.matches(beanClass)) continue;

      AdvisedSupport advisedSupport = new AdvisedSupport();

      TargetSource targetSource = null;
      try {
        targetSource = new TargetSource(beanClass.getDeclaredConstructor().newInstance());
      } catch (Exception e) {
        e.printStackTrace();
      }
      advisedSupport.setTargetSource(targetSource);
      advisedSupport.setMethodInterceptor((MethodInterceptor) advisor.getAdvice());
      advisedSupport.setMethodMatcher(advisor.getPointcut().getMethodMatcher());
      advisedSupport.setProxyTargetClass(false);

      return new ProxyFactory(advisedSupport).getProxy();
    }

    return null;
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
