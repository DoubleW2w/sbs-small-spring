package org.springframework.core.convert.converter;

/**
 * 类型转换工厂
 *
 * <p>类型转换器工厂的泛型类型是S,T，其中由S转换T。
 *
 * <p>类型转换器工厂的泛型类型是S，R，其中T继承R，也就是就说可以获取到转换成R类或者T类的转换器，其目的是可以获取到更具体的转换器
 *
 * @author: DoubleW2w
 * @date: 2024/11/11
 * @project: sbs-small-spring
 */
public interface ConverterFactory<S, R> {
  <T extends R> Converter<S, T> getConverter(Class<T> targetType);
}
