package com.beren.qba.dsl.expressionResolvers.impl;

import java.util.Collection;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;

public interface ArrayExpressionResolver {

	BooleanExpression create(PathBuilder<?> entityPath, String property, Collection value);

	Class<?> getSupportedType();

}
