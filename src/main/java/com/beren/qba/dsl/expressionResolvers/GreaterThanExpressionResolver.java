package com.beren.qba.dsl.expressionResolvers;

import java.lang.reflect.Field;

import com.beren.qba.annotations.QueryGreaterThan;
import com.beren.qba.dsl.ExpressionResolver;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.PathBuilder;

public class GreaterThanExpressionResolver implements ExpressionResolver
{

  private static final Class<QueryGreaterThan> annotation = QueryGreaterThan.class;

  @SuppressWarnings("rawtypes")
  @Override
  public Predicate getRestriction(Object dto, PathBuilder<?> entityPath, Field field) throws Exception
  {
    QueryGreaterThan currentAnnotation = (QueryGreaterThan) field.getAnnotation(annotation);
    if (currentAnnotation.isInclusive())
      return entityPath.getComparable(currentAnnotation.value(), Comparable.class)
          .goe((Comparable) field.get(dto));
    else
      return entityPath.getComparable(currentAnnotation.value(), Comparable.class)
          .gt((Comparable) field.get(dto));
  }

}
