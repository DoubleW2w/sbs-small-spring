package org.springframework.core.convert.support;

import cn.hutool.core.convert.BasicType;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.core.convert.converter.ConverterRegistry;
import org.springframework.core.convert.converter.GenericConverter;
import org.springframework.core.convert.converter.GenericConverter.ConvertiblePair;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

/**
 * @author: DoubleW2w
 * @date: 2024/11/11
 * @project: sbs-small-spring
 */
public class GenericConversionService implements ConversionService, ConverterRegistry {

  private Map<ConvertiblePair, GenericConverter> converters = new HashMap<>();

  @Override
  public boolean canConvert(Class<?> sourceType, Class<?> targetType) {
    GenericConverter converter = getConverter(sourceType, targetType);
    return converter != null;
  }

  @Override
  public <T> T convert(Object source, Class<T> targetType) {
    Class<?> sourceType = source.getClass();
    targetType = (Class<T>) BasicType.wrap(targetType);
    GenericConverter converter = getConverter(sourceType, targetType);
    return (T) converter.convert(source, sourceType, targetType);
  }

  @Override
  public void addConverter(Converter<?, ?> converter) {
    ConvertiblePair typeInfo = getRequiredTypeInfo(converter);
    ConverterAdapter converterAdapter = new ConverterAdapter(typeInfo, converter);
    for (ConvertiblePair convertibleType : converterAdapter.getConvertibleTypes()) {
      converters.put(convertibleType, converterAdapter);
    }
  }

  @Override
  public void addConverterFactory(ConverterFactory<?, ?> converterFactory) {
    ConvertiblePair typeInfo = getRequiredTypeInfo(converterFactory);
    ConverterFactoryAdapter converterFactoryAdapter =
        new ConverterFactoryAdapter(typeInfo, converterFactory);
    for (ConvertiblePair convertibleType : converterFactoryAdapter.getConvertibleTypes()) {
      converters.put(convertibleType, converterFactoryAdapter);
    }
  }

  @Override
  public void addConverter(GenericConverter converter) {
    for (ConvertiblePair convertibleType : converter.getConvertibleTypes()) {
      converters.put(convertibleType, converter);
    }
  }

  /**
   * 获取必需的类型信息
   *
   * @param object 对象
   * @return 结果
   */
  private ConvertiblePair getRequiredTypeInfo(Object object) {
    Type[] types = object.getClass().getGenericInterfaces();
    ParameterizedType parameterized = (ParameterizedType) types[0];
    Type[] actualTypeArguments = parameterized.getActualTypeArguments();
    Class sourceType = (Class) actualTypeArguments[0];
    Class targetType = (Class) actualTypeArguments[1];
    return new ConvertiblePair(sourceType, targetType);
  }

  /**
   * 获取转换器
   *
   * @param sourceType 原类型
   * @param targetType 目标类型
   * @return 结果
   */
  protected GenericConverter getConverter(Class<?> sourceType, Class<?> targetType) {
    List<Class<?>> sourceCandidates = getClassHierarchy(sourceType);
    List<Class<?>> targetCandidates = getClassHierarchy(targetType);
    for (Class<?> sourceCandidate : sourceCandidates) {
      for (Class<?> targetCandidate : targetCandidates) {
        ConvertiblePair convertiblePair = new ConvertiblePair(sourceCandidate, targetCandidate);
        GenericConverter converter = converters.get(convertiblePair);
        if (converter != null) {
          return converter;
        }
      }
    }
    return null;
  }

  /**
   * 获取指定参数类的父类列表
   *
   * @param clazz 类型
   * @return 父类列表
   */
  private List<Class<?>> getClassHierarchy(Class<?> clazz) {
    List<Class<?>> hierarchy = new ArrayList<>();
    while (clazz != null) {
      hierarchy.add(clazz);
      clazz = clazz.getSuperclass();
    }
    return hierarchy;
  }

  /** 转换器适配器 */
  private final class ConverterAdapter implements GenericConverter {

    /** 可转换的类型对信息 */
    private final ConvertiblePair typeInfo;

    /** 类型转换 */
    private final Converter<Object, Object> converter;

    public ConverterAdapter(ConvertiblePair typeInfo, Converter<?, ?> converter) {
      this.typeInfo = typeInfo;
      this.converter = (Converter<Object, Object>) converter;
    }

    @Override
    public Set<ConvertiblePair> getConvertibleTypes() {
      return Collections.singleton(typeInfo);
    }

    @Override
    public Object convert(Object source, Class sourceType, Class targetType) {
      return converter.convert(source);
    }
  }

  /** 转换器工厂适配器 */
  private final class ConverterFactoryAdapter implements GenericConverter {

    private final ConvertiblePair typeInfo;

    private final ConverterFactory<Object, Object> converterFactory;

    public ConverterFactoryAdapter(
        ConvertiblePair typeInfo, ConverterFactory<?, ?> converterFactory) {
      this.typeInfo = typeInfo;
      this.converterFactory = (ConverterFactory<Object, Object>) converterFactory;
    }

    @Override
    public Set<ConvertiblePair> getConvertibleTypes() {
      return Collections.singleton(typeInfo);
    }

    @Override
    public Object convert(Object source, Class sourceType, Class targetType) {
      return converterFactory.getConverter(targetType).convert(source);
    }
  }
}
