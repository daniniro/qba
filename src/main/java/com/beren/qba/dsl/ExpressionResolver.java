package com.beren.qba.dsl;

import java.lang.reflect.Field;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.PathBuilder;

public interface ExpressionResolver
{

  Predicate getRestriction(Object dto, PathBuilder<?> entityPath, Field field) throws Exception;

}
