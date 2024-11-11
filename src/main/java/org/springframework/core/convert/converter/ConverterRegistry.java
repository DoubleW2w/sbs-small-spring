package org.springframework.core.convert.converter;

/**
 * 类型转换器注册接口
 *
 * @author: DoubleW2w
 * @date: 2024/11/11
 * @project: sbs-small-spring
 */
public interface ConverterRegistry {
  void addConverter(Converter<?, ?> converter);

  void addConverterFactory(ConverterFactory<?, ?> converterFactory);

  void addConverter(GenericConverter converter);
}
