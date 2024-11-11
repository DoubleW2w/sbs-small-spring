package org.springframework.test.common;

import org.springframework.core.convert.converter.Converter;

/**
 * @author: DoubleW2w
 * @date: 2024/11/11
 * @project: sbs-small-spring
 */
public class StringToIntegerConverter implements Converter<String,Integer> {
  @Override
  public Integer convert(String source) {
    return Integer.valueOf(source);
  }
}
