package com.beren.qba.dsl.expressionResolvers.impl;

import java.util.Collection;
import java.util.Date;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;

public class DateArrayExpressionResolver implements ArrayExpressionResolver {

	@SuppressWarnings("unchecked")
	@Override
	public BooleanExpression create(PathBuilder<?> entityPath, String property, Collection value) {
		return entityPath.getArray(property, Date[].class).in((Collection<? extends Date[]>) value);
	}

	@Override
	public Class<?> getSupportedType() {
		return Date.class;
	}

}
