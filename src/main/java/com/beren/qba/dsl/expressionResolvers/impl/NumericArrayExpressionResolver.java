package com.beren.qba.dsl.expressionResolvers.impl;

import java.util.Collection;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;

public class NumericArrayExpressionResolver implements ArrayExpressionResolver {

	@SuppressWarnings("unchecked")
	@Override
	public BooleanExpression create(PathBuilder<?> entityPath, String property, Collection value) {
		return entityPath.getArray(property, Number[].class).in((Collection<? extends Number[]>) value);
	}

	@Override
	public Class<?> getSupportedType() {
		return Number.class;
	}

}
