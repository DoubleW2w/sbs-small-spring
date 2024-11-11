package org.springframework.core.convert;

/**
 * 类型转换抽象接口
 *
 * @author: DoubleW2w
 * @date: 2024/11/11
 * @project: sbs-small-spring
 */
public interface ConversionService {
  boolean canConvert(Class<?> sourceType, Class<?> targetType);

  <T> T convert(Object source, Class<T> targetType);
}
