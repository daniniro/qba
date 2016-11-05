package com.beren.qba.dsl.expressionResolvers;

import java.lang.reflect.Field;

import com.beren.qba.annotations.QueryStartsWith;
import com.beren.qba.dsl.ExpressionResolver;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.PathBuilder;

public class StartsWithExpressionResolver implements ExpressionResolver
{
  private static final Class<QueryStartsWith> annotation = QueryStartsWith.class;

  @Override
  public Predicate getRestriction(Object dto, PathBuilder<?> entityPath, Field field) throws Exception
  {
    QueryStartsWith currentAnnotation = (QueryStartsWith) field.getAnnotation(annotation);
    if (currentAnnotation.ignoreCase())
      return entityPath.getString(currentAnnotation.value())
          .startsWithIgnoreCase((String) field.get(dto));
    else
      return entityPath.getString(currentAnnotation.value())
          .startsWith((String) field.get(dto));
  }

}
