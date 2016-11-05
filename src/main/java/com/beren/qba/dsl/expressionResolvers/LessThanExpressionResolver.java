package com.beren.qba.dsl.expressionResolvers;

import java.lang.reflect.Field;

import com.beren.qba.annotations.QueryLessThan;
import com.beren.qba.dsl.ExpressionResolver;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.PathBuilder;

public class LessThanExpressionResolver implements ExpressionResolver
{

  private static final Class<QueryLessThan> annotation = QueryLessThan.class;

  @SuppressWarnings("rawtypes")
  @Override
  public Predicate getRestriction(Object dto, PathBuilder<?> entityPath, Field field) throws Exception
  {
    QueryLessThan currentAnnotation = (QueryLessThan) field.getAnnotation(annotation);
    if (currentAnnotation.isInclusive())
      return entityPath.getComparable(currentAnnotation.value(), Comparable.class)
          .loe((Comparable) field.get(dto));
    else
      return entityPath.getComparable(currentAnnotation.value(), Comparable.class)
          .lt((Comparable) field.get(dto));
  }

}
