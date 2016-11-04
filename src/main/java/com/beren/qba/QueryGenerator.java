package com.beren.qba;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.PathBuilder;

public interface QueryGenerator<T>
{

  public Predicate createQuery(Object dto, PathBuilder<T> pathBuilder);

  public PathBuilder<T> getEntityPath();

}
