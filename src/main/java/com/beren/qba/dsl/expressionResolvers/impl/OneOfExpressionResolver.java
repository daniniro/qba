package com.beren.qba.dsl.expressionResolvers.impl;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

import com.beren.qba.annotations.QueryOneOf;
import com.beren.qba.dsl.expressionResolvers.ExpressionResolver;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;

@SuppressWarnings("rawtypes")
public class OneOfExpressionResolver implements ExpressionResolver {
	private static final Class<QueryOneOf> annotation = QueryOneOf.class;
	private ArrayExpressionMap arrayExpressionsMap;

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

	private Optional<BooleanExpression> chooseExpression(PathBuilder<?> entityPath, QueryOneOf currentAnnotation,
			Collection value, Class<?> componentType) {
		if (arrayExpressionsMap.containsCompatibleKey(componentType))
			return Optional
					.of(arrayExpressionsMap.get(componentType).create(entityPath, currentAnnotation.value(), value));
		return Optional.empty();
	}

}
