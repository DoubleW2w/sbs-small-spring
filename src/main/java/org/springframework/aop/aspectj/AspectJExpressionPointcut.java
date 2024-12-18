package org.springframework.aop.aspectj;

import org.aspectj.weaver.tools.PointcutExpression;
import org.aspectj.weaver.tools.PointcutParser;
import org.aspectj.weaver.tools.PointcutPrimitive;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.MethodMatcher;
import org.springframework.aop.Pointcut;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

/**
 * Spring {@link Pointcut}的一个实现，它使用AspectJ来计算切入点表达式。
 *
 * <p>AspectJ切点表达式
 *
 * @author: DoubleW2w
 * @date: 2024/11/6
 * @project: sbs-small-spring
 */
public class AspectJExpressionPointcut implements Pointcut, ClassFilter, MethodMatcher {
  private static final Set<PointcutPrimitive> SUPPORTED_PRIMITIVES = new HashSet<>();

  static {
    SUPPORTED_PRIMITIVES.add(PointcutPrimitive.EXECUTION);
  }

  private final PointcutExpression pointcutExpression;

  public AspectJExpressionPointcut(String expression) {
    // 获取切点解析器
    PointcutParser pointcutParser =
        PointcutParser
            .getPointcutParserSupportingSpecifiedPrimitivesAndUsingSpecifiedClassLoaderForResolution(
                SUPPORTED_PRIMITIVES, this.getClass().getClassLoader());
    // 使用解析器解析给定的切点表达式
    pointcutExpression = pointcutParser.parsePointcutExpression(expression);
  }

  @Override
  public boolean matches(Class<?> clazz) {
    return pointcutExpression.couldMatchJoinPointsInType(clazz);
  }

  @Override
  public boolean matches(Method method, Class<?> targetClass) {
    return pointcutExpression.matchesMethodExecution(method).alwaysMatches();
  }

  @Override
  public ClassFilter getClassFilter() {
    return this;
  }

  @Override
  public MethodMatcher getMethodMatcher() {
    return this;
  }
}
