package com.beren.qba.dsl.expressionResolvers.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ArrayExpressionFactory
{
  private static final Logger logger = LoggerFactory.getLogger(ArrayExpressionFactory.class);
  private static Map<Class<?>, ArrayExpressionResolver> map;

  static
  {
    map = new HashMap<>();
    registerClass(NumericArrayExpressionResolver.class);
    registerClass(DateArrayExpressionResolver.class);
    registerClass(CalendarArrayExpressionResolver.class);
    registerClass(TemporalArrayExpressionResolver.class);
    registerClass(StringArrayExpressionResolver.class);
  }

  private static void registerClass(Class<? extends ArrayExpressionResolver> clazz)
  {
    try
    {
      ArrayExpressionResolver temp = clazz.newInstance();
      map.put(temp.getSupportedType(), temp);
    }
    catch (Exception e)
    {
      logger.error("Cannota register class {}", clazz, e);
    }
  }

  public static boolean existsArrayExpressionResolverForClass(Class<?> componentType)
  {
    return !findCompatibleClasses(componentType).isEmpty();
  }

  public static ArrayExpressionResolver getArrayExpressionResolver(Class<?> componentType)
  {
    return map.get(firstCompatibleClass(componentType));
  }

  private static Class<?> firstCompatibleClass(Class<?> componentType)
  {
    Class<?> key = findCompatibleClasses(componentType).iterator().next();
    return key;
  }

  private static List<Class<?>> findCompatibleClasses(Class<?> componentType)
  {
    List<Class<?>> compatibleClasses = map.keySet().stream()
        .filter(clazz -> clazz.isAssignableFrom(componentType))
        .collect(Collectors.toList());
    return compatibleClasses;
  }

}
