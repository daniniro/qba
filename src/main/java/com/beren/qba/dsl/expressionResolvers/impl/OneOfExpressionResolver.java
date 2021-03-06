package com.beren.qba.dsl.expressionResolvers.impl;

import java.lang.reflect.Field;
import java.time.temporal.Temporal;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Optional;

import com.beren.qba.annotations.QueryOneOf;
import com.beren.qba.dsl.expressionResolvers.ExpressionResolver;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;

@SuppressWarnings("rawtypes")
public class OneOfExpressionResolver implements ExpressionResolver {
	private static final Class<QueryOneOf> annotation = QueryOneOf.class;

	@Override
	public Predicate getRestriction(Object dto, PathBuilder<?> entityPath, Field field) throws Exception {
		QueryOneOf currentAnnotation = (QueryOneOf) field.getAnnotation(annotation);
		Collection value = convertToCollection(dto, field);
		Class<?> componentType = value.toArray()[0].getClass();
		Optional<BooleanExpression> ret = chooseExpression(entityPath, currentAnnotation, value, componentType);
		if (ret.isPresent())
			return ret.get();
		throw new Exception("Unsupported conversion of " + componentType);
	}

	private Collection convertToCollection(Object dto, Field field) throws IllegalAccessException {
		Collection value;
		if (!Collection.class.isAssignableFrom(field.get(dto).getClass()))
			value = Arrays.asList((Object[]) field.get(dto));
		else
			value = (Collection) field.get(dto);
		return value;
	}

	@SuppressWarnings("unchecked")
	private Optional<BooleanExpression> chooseExpression(PathBuilder<?> entityPath, QueryOneOf currentAnnotation,
			Collection value, Class<?> componentType) {
		// TODO Too many if! Evaluate strategy and visitor pattern
		if (Number.class.isAssignableFrom(componentType))
			return Optional.of(entityPath.getArray(currentAnnotation.value(), Number[].class)
					.in((Collection<? extends Number[]>) value));
		else if (String.class.isAssignableFrom(componentType))
			return Optional.of(entityPath.getArray(currentAnnotation.value(), String[].class)
					.in((Collection<? extends String[]>) value));
		else if (Temporal.class.isAssignableFrom(componentType))
			return Optional.of(entityPath.getArray(currentAnnotation.value(), Temporal[].class)
					.in((Collection<? extends Temporal[]>) value));
		else if (Calendar.class.isAssignableFrom(componentType))
			return Optional.of(entityPath.getArray(currentAnnotation.value(), Calendar[].class)
					.in((Collection<? extends Calendar[]>) value));
		else if (Date.class.isAssignableFrom(componentType))
			return Optional.of(entityPath.getArray(currentAnnotation.value(), Date[].class)
					.in((Collection<? extends Date[]>) value));

		return Optional.empty();
	}

}
