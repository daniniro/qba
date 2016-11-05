package com.beren.qba.dsl.expressionResolvers;

import java.lang.reflect.Field;

import com.beren.qba.annotations.QueryIs;
import com.beren.qba.dsl.ExpressionResolver;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.PathBuilder;

public class IsExpressionResolver implements ExpressionResolver
{

  private static final Class<QueryIs> annotation = QueryIs.class;

  @Override
  public Predicate getRestriction(Object dto, PathBuilder<?> entityPath, Field field) throws Exception
  {
    QueryIs currentAnnotation = (QueryIs) field.getAnnotation(annotation);

    return entityPath.get(currentAnnotation.value())
        .eq(field.get(dto));
  }

}
