package com.beren.qba.dsl.expressionResolvers;

import java.lang.reflect.Field;

import com.beren.qba.annotations.QueryContains;
import com.beren.qba.dsl.ExpressionResolver;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.PathBuilder;

public class ContainsExpressionResolver implements ExpressionResolver
{
  private static final Class<QueryContains> annotation = QueryContains.class;

  @Override
  public Predicate getRestriction(Object dto, PathBuilder<?> entityPath, Field field) throws Exception
  {
    QueryContains currentAnnotation = (QueryContains) field.getAnnotation(annotation);
    if (currentAnnotation.ignoreCase())
      return entityPath.getString(currentAnnotation.value())
          .containsIgnoreCase((String) field.get(dto));
    else
      return entityPath.getString(currentAnnotation.value())
          .contains((String) field.get(dto));
  }

}
