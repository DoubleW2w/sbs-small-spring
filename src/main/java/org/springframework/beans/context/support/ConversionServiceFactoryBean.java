package org.springframework.beans.context.support;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.core.convert.converter.ConverterRegistry;
import org.springframework.core.convert.converter.GenericConverter;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.convert.support.GenericConversionService;

import java.util.Set;

/**
 * 类型转换工厂Bean
 *
 * @author: DoubleW2w
 * @date: 2024/11/11
 * @project: sbs-small-spring
 */
public class ConversionServiceFactoryBean
    implements FactoryBean<ConversionService>, InitializingBean {
  private Set<?> converters;

  private GenericConversionService conversionService;

  @Override
  public ConversionService getObject() throws Exception {
    return conversionService;
  }

  @Override
  public Class<?> getObjectType() {
    return GenericConversionService.class;
  }

  @Override
  public boolean isSingleton() {
    return true;
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    conversionService = new DefaultConversionService();
    registerConverters(converters, conversionService);
  }

  /**
   * 注册转换器列表
   *
   * @param converters 类型转换器列表
   * @param registry 类型转换注册机
   */
  private void registerConverters(Set<?> converters, ConverterRegistry registry) {
    if (converters != null) {
      for (Object converter : converters) {
        if (converter instanceof GenericConverter) {
          registry.addConverter((GenericConverter) converter);
        } else if (converter instanceof Converter<?, ?>) {
          registry.addConverter((Converter<?, ?>) converter);
        } else if (converter instanceof ConverterFactory<?, ?>) {
          registry.addConverterFactory((ConverterFactory<?, ?>) converter);
        } else {
          throw new IllegalArgumentException(
              "Each converter object must implement one of the "
                  + "Converter, ConverterFactory, or GenericConverter interfaces");
        }
      }
    }
  }

  public void setConverters(Set<?> converters) {
    this.converters = converters;
  }
}
