package org.springframework.core.convert.converter;

import lombok.Getter;

import java.util.Set;

/**
 * @author: DoubleW2w
 * @date: 2024/11/11
 * @project: sbs-small-spring
 */
public interface GenericConverter {
  Set<ConvertiblePair> getConvertibleTypes();

  Object convert(Object source, Class sourceType, Class targetType);

  final class ConvertiblePair {
    /** 原始类型 */
    @Getter private final Class<?> sourceType;

    /** 目标类型 */
    @Getter private final Class<?> targetType;

    public ConvertiblePair(Class<?> sourceType, Class<?> targetType) {
      this.sourceType = sourceType;
      this.targetType = targetType;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      }
      if (obj == null || obj.getClass() != ConvertiblePair.class) {
        return false;
      }
      ConvertiblePair other = (ConvertiblePair) obj;
      return this.sourceType.equals(other.sourceType) && this.targetType.equals(other.targetType);
    }

    @Override
    public int hashCode() {
      return this.sourceType.hashCode() * 31 + this.targetType.hashCode();
    }
  }
}
