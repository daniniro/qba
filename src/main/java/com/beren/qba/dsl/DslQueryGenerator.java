package com.beren.qba.dsl;

import java.lang.reflect.Field;

import com.beren.qba.QueryGenerator;
import com.beren.qba.annotations.QueryEndsWith;
import com.beren.qba.annotations.QueryEq;
import com.beren.qba.annotations.QueryStartsWith;
import com.beren.qba.dsl.validators.DtoValidator;
import com.beren.qba.dsl.validators.impl.SimpleDtoValidator;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.PathBuilder;

public class DslQueryGenerator<T> implements QueryGenerator<T>
{

  private Class<?> actualClass;
  private DtoValidator dtoValidator;

  public DslQueryGenerator(Class<?> actualClass)
  {
    super();
    this.actualClass = actualClass;
    dtoValidator = new SimpleDtoValidator(actualClass);
  }

  @Override
  public Predicate createQuery(Object dto, PathBuilder<T> entityPath)
  {
    validateDTO(dto);
    BooleanBuilder expression = new BooleanBuilder();
    try
    {
      for (Field field : dto.getClass().getDeclaredFields())
        if (field.isAnnotationPresent(QueryEq.class))
        {
          field.setAccessible(true);
          QueryEq annotation = field.getAnnotation(QueryEq.class);
          if (field.get(dto) != null)
            if (annotation.ignoreCase())
              expression.and(entityPath.getString(annotation.value())
                  .equalsIgnoreCase((String) field.get(dto)));
            else
              expression.and(entityPath.getString(annotation.value())
                  .eq((String) field.get(dto)));
        }
        else if (field.isAnnotationPresent(QueryStartsWith.class))
        {
          field.setAccessible(true);
          QueryStartsWith annotation = field.getAnnotation(QueryStartsWith.class);
          if (field.get(dto) != null)
            if (annotation.ignoreCase())
              expression.and(entityPath.getString(annotation.value())
                  .startsWithIgnoreCase((String) field.get(dto)));
            else
              expression.and(entityPath.getString(annotation.value())
                  .startsWith((String) field.get(dto)));
        }
        else if (field.isAnnotationPresent(QueryEndsWith.class))
        {
          field.setAccessible(true);
          QueryEndsWith annotation = field.getAnnotation(QueryEndsWith.class);
          if (field.get(dto) != null)
            if (annotation.ignoreCase())
              expression.and(entityPath.getString(annotation.value())
                  .endsWithIgnoreCase((String) field.get(dto)));
            else
              expression.and(entityPath.getString(annotation.value())
                  .endsWith((String) field.get(dto)));
        }
    }
    catch (IllegalArgumentException | IllegalAccessException e)
    {
    }
    return expression;
  }

  private void validateDTO(Object dto)
  {
    dtoValidator.validate(dto);
  }

  @Override
  @SuppressWarnings("unchecked")
  public PathBuilder<T> getEntityPath()
  {
    return new PathBuilder<>((Class<? extends T>) actualClass, "entity");
  }

}
