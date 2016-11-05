package com.beren.qba.dsl.expressionResolvers.impl;

import java.lang.reflect.Field;

import com.beren.qba.annotations.QueryEndsWith;
import com.beren.qba.dsl.expressionResolvers.ExpressionResolver;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.PathBuilder;

public class EndsWithExpressionResolver implements ExpressionResolver
{
  private static final Class<QueryEndsWith> annotation = QueryEndsWith.class;

  @Override
  public Predicate getRestriction(Object dto, PathBuilder<?> entityPath, Field field) throws Exception
  {
    QueryEndsWith currentAnnotation = (QueryEndsWith) field.getAnnotation(annotation);
    if (currentAnnotation.ignoreCase())
      return entityPath.getString(currentAnnotation.value())
          .endsWithIgnoreCase((String) field.get(dto));
    else
      return entityPath.getString(currentAnnotation.value())
          .endsWith((String) field.get(dto));
  }

}
