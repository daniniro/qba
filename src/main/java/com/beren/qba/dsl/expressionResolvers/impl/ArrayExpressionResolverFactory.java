package com.beren.qba.dsl.expressionResolvers.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beren.qba.dsl.utils.PackageScanner;

@SuppressWarnings("unchecked")
public class ArrayExpressionResolverFactory
{
  private static final String BASE_PACKAGE = "com.beren";
  private static final Logger logger = LoggerFactory.getLogger(ArrayExpressionResolverFactory.class);
  private static Map<Class<?>, ArrayExpressionResolver> map = new HashMap<>();

  static
  {
    for (Class<?> clazz : findDescendantsClasses(ArrayExpressionResolver.class))
      registerClass((Class<? extends ArrayExpressionResolver>) clazz);
  }

  public static boolean existsArrayExpressionResolverForClass(Class<?> componentType)
  {
    return !findCompatibleClasses(componentType).isEmpty();
  }

  public static ArrayExpressionResolver getArrayExpressionResolver(Class<?> componentType)
  {
    return map.get(firstCompatibleClass(componentType));
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

  private static List<Class<?>> findDescendantsClasses(Class<?> ancestor)
  {
    return PackageScanner
        .findDescendantsClasses(BASE_PACKAGE, ancestor);
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
