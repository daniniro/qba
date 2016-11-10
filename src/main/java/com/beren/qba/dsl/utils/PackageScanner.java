package com.beren.qba.dsl.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ClassInfo;

public class PackageScanner
{
  private static final Logger logger = LoggerFactory.getLogger(PackageScanner.class);

  public static List<Class<?>> findDescendantsClasses(String basePackage,
                                                      Class<?> ancestor)
  {
    List<Class<?>> ret = new ArrayList<>();
    try
    {
      ClassPath classpath = ClassPath.from(Thread.currentThread()
          .getContextClassLoader());
      java.util.function.Predicate<? super ClassInfo> isType = createIsTypePredicate(ancestor);
      Function<ClassInfo, Class<?>> descendantMapper = createDescendantMapper();
      ret = classpath.getTopLevelClassesRecursive(basePackage).stream()
          .filter(isType).map(descendantMapper)
          .collect(Collectors.toList());
    }
    catch (Exception e)
    {
      throw new RuntimeException("Cannot load descendants of " + ancestor, e);
    }
    return ret;

  }

  private static Function<ClassInfo, Class<?>> createDescendantMapper()
  {
    Function<ClassInfo, Class<?>> descendantMapper = new Function<ClassInfo, Class<?>>()
    {

      @Override
      public Class<?> apply(ClassInfo classinfo)
      {
        try
        {
          return (Class<?>) Class.forName(classinfo.getName());
        }
        catch (ClassNotFoundException e)
        {
          logger.error("Cannot find class {}", classinfo.getName(), e);
          throw new RuntimeException("Cannot find class " + classinfo.getName(), e);
        }
      }

    };
    return descendantMapper;
  }

  private static java.util.function.Predicate<? super ClassInfo> createIsTypePredicate(Class<?> ancestor)
  {
    java.util.function.Predicate<? super ClassInfo> isType = new java.util.function.Predicate<ClassInfo>()
    {

      @Override
      public boolean test(ClassInfo classinfo)
      {
        try
        {
          if (!ancestor.getName().equals(classinfo.getName())
              && ancestor.isAssignableFrom(Class.forName(classinfo.getName())))
            return true;
        }
        catch (ClassNotFoundException e)
        {
          logger.error("Cannot find class {}", classinfo.getName(), e);
        }
        return false;
      }

    };
    return isType;
  }

}
