package org.springframework.core.convert.converter;

/**
 * 类型转换抽象接口
 *
 * @author: DoubleW2w
 * @date: 2024/11/11
 * @project: sbs-small-spring
 */
public interface Converter<S, T> {
  /** 类型转换 */
  T convert(S source);
}
