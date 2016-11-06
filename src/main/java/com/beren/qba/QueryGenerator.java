package com.beren.qba;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.PathBuilder;

public interface QueryGenerator<T> {

	Predicate createQuery(Object dto, PathBuilder<T> pathBuilder);

	PathBuilder<T> getEntityPath();

	PathBuilder<T> getEntityPath(String entityName);

}
