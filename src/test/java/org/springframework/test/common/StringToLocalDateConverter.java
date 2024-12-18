package org.springframework.test.common;

import org.springframework.core.convert.converter.Converter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * @author: DoubleW2w
 * @date: 2024/11/11
 * @project: sbs-small-spring
 */
public class StringToLocalDateConverter implements Converter<String, LocalDate> {
  private final DateTimeFormatter DATE_TIME_FORMATTER;

  public StringToLocalDateConverter(String pattern) {
    DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(pattern);
  }

  @Override
  public LocalDate convert(String source) {
    return LocalDate.parse(source, DATE_TIME_FORMATTER);
  }
}
