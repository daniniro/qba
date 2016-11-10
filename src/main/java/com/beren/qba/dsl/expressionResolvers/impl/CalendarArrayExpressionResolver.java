package com.beren.qba.dsl.expressionResolvers.impl;

import java.util.Calendar;
import java.util.Collection;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;

public class CalendarArrayExpressionResolver implements ArrayExpressionResolver {

	@SuppressWarnings("unchecked")
	@Override
	public BooleanExpression create(PathBuilder<?> entityPath, String property, Collection value) {
		return entityPath.getArray(property, Calendar[].class).in((Collection<? extends Calendar[]>) value);
	}

	@Override
	public Class<?> getSupportedType() {
		return Calendar.class;
	}

}
