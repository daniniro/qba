package com.beren.qba.dsl.expressionResolvers.impl;

import java.time.temporal.Temporal;
import java.util.Collection;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;

public class TemporalArrayExpressionResolver implements ArrayExpressionResolver {

	@SuppressWarnings("unchecked")
	@Override
	public BooleanExpression create(PathBuilder<?> entityPath, String property, Collection value) {
		return entityPath.getArray(property, Temporal[].class).in((Collection<? extends Temporal[]>) value);
	}

	@Override
	public Class<?> getSupportedType() {
		return Temporal.class;
	}

}
