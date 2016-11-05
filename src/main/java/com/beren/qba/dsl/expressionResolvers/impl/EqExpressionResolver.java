package com.beren.qba.dsl.expressionResolvers.impl;

import java.lang.reflect.Field;

import com.beren.qba.annotations.QueryEq;
import com.beren.qba.dsl.expressionResolvers.ExpressionResolver;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.PathBuilder;

public class EqExpressionResolver implements ExpressionResolver
{

  private static final Class<QueryEq> annotation = QueryEq.class;

  @Override
  public Predicate getRestriction(Object dto, PathBuilder<?> entityPath, Field field) throws Exception
  {
    QueryEq currentAnnotation = (QueryEq) field.getAnnotation(annotation);
    if (currentAnnotation.ignoreCase())
      return entityPath.getString(currentAnnotation.value())
          .equalsIgnoreCase((String) field.get(dto));
    else
      return entityPath.getString(currentAnnotation.value())
          .eq((String) field.get(dto));
  }

}
