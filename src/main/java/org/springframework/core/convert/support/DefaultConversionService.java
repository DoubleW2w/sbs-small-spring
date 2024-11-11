package org.springframework.core.convert.support;

import org.springframework.core.convert.converter.ConverterRegistry;

/**
 * 默认的转换接口
 *
 * @author: DoubleW2w
 * @date: 2024/11/11
 * @project: sbs-small-spring
 */
public class DefaultConversionService extends GenericConversionService {
  public DefaultConversionService() {
    addDefaultConverters(this);
  }

  public static void addDefaultConverters(ConverterRegistry converterRegistry) {
    converterRegistry.addConverterFactory(new StringToNumberConverterFactory());
    // TODO 添加其他ConverterFactory
  }
}
