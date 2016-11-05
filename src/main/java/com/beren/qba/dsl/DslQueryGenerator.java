package com.beren.qba.dsl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.beren.qba.QueryGenerator;
import com.beren.qba.annotations.QueryContains;
import com.beren.qba.annotations.QueryEndsWith;
import com.beren.qba.annotations.QueryEq;
import com.beren.qba.annotations.QueryIs;
import com.beren.qba.annotations.QueryOneOf;
import com.beren.qba.annotations.QueryStartsWith;
import com.beren.qba.dsl.expressionResolvers.ContainsExpressionResolver;
import com.beren.qba.dsl.expressionResolvers.EndsWithExpressionResolver;
import com.beren.qba.dsl.expressionResolvers.EqExpressionResolver;
import com.beren.qba.dsl.expressionResolvers.IsExpressionResolver;
import com.beren.qba.dsl.expressionResolvers.OneOfExpressionResolver;
import com.beren.qba.dsl.expressionResolvers.StartsWithExpressionResolver;
import com.beren.qba.dsl.validators.DtoValidator;
import com.beren.qba.dsl.validators.impl.SimpleDtoValidator;
import com.beren.qba.exceptions.UnexpectedQueryGenerationError;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.PathBuilder;

public class DslQueryGenerator<T> implements QueryGenerator<T>
{

  private Class<?> actualClass;
  private DtoValidator dtoValidator;
  private Map<Class<? extends Annotation>, ExpressionResolver> expressionMap;

  public DslQueryGenerator(Class<?> actualClass)
  {
    this(actualClass, null);
    defaultExpressionMap();
  }

  @Override
  @SuppressWarnings("unchecked")
  public PathBuilder<T> getEntityPath()
  {
    return new PathBuilder<>((Class<? extends T>) actualClass, "entity");
  }

  public DslQueryGenerator(Class<?> actualClass,
      Map<Class<? extends Annotation>, ExpressionResolver> expressionMap)
  {
    super();
    this.actualClass = actualClass;
    this.expressionMap = expressionMap;
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
      {
        Optional<Class<? extends Annotation>> isAnnotated;
        if ((isAnnotated = getQueryAnnotation(field)).isPresent())
        {
          field.setAccessible(true);
          Class<? extends Annotation> annotation = isAnnotated.get();
          if (field.get(dto) != null)
            expression.and(expressionMap.get(annotation).getRestriction(dto, entityPath, field));
        }
      }
    }
    catch (Exception e)
    {
      throw new UnexpectedQueryGenerationError(e);
    }
    return expression;
  }

  private void defaultExpressionMap()
  {
    expressionMap = new HashMap<>();
    expressionMap.put(QueryEq.class, new EqExpressionResolver());
    expressionMap.put(QueryStartsWith.class, new StartsWithExpressionResolver());
    expressionMap.put(QueryEndsWith.class, new EndsWithExpressionResolver());
    expressionMap.put(QueryContains.class, new ContainsExpressionResolver());
    expressionMap.put(QueryIs.class, new IsExpressionResolver());
    expressionMap.put(QueryOneOf.class, new OneOfExpressionResolver());
  }

  private Optional<Class<? extends Annotation>> getQueryAnnotation(Field field)
  {
    for (Class<? extends Annotation> annotation : supportedAnnotations())
      if (field.isAnnotationPresent(annotation))
        return Optional.of(annotation);
    return Optional.empty();
  }

  private Collection<Class<? extends Annotation>> supportedAnnotations()
  {
    return expressionMap.keySet();
  }

  private void validateDTO(Object dto)
  {
    dtoValidator.validate(dto);
  }

}
