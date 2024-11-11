package org.springframework.core.convert.support;

import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;

/**
 * @author: DoubleW2w
 * @date: 2024/11/11
 * @project: sbs-small-spring
 */
public class StringToNumberConverterFactory implements ConverterFactory<String, Number> {
  @Override
  public <T extends Number> Converter<String, T> getConverter(Class<T> targetType) {
    return new StringToNumber<>(targetType);
  }

  private static final class StringToNumber<T extends Number> implements Converter<String, T> {

    private final Class<T> targetType;

    public StringToNumber(Class<T> targetType) {
      this.targetType = targetType;
    }

    @Override
    public T convert(String source) {
      if (source.length() == 0) {
        return null;
      }

      if (targetType.equals(Integer.class)) {
        return (T) Integer.valueOf(source);
      } else if (targetType.equals(Long.class)) {
        return (T) Long.valueOf(source);
      } else if (targetType.equals(Double.class)) {
        return (T) Double.valueOf(source);
      } else if (targetType.equals(Float.class)) {
        return (T) Float.valueOf(source);
      }
      // TODO 其他数字类型

      else {
        throw new IllegalArgumentException(
            "Cannot convert String ["
                + source
                + "] to target class ["
                + targetType.getName()
                + "]");
      }
    }
  }
}
