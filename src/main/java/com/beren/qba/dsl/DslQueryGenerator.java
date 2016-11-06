package com.beren.qba.dsl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.beren.qba.QueryGenerator;
import com.beren.qba.annotations.QueryContains;
import com.beren.qba.annotations.QueryEndsWith;
import com.beren.qba.annotations.QueryEq;
import com.beren.qba.annotations.QueryGreaterThan;
import com.beren.qba.annotations.QueryIs;
import com.beren.qba.annotations.QueryLessThan;
import com.beren.qba.annotations.QueryOneOf;
import com.beren.qba.annotations.QueryStartsWith;
import com.beren.qba.dsl.expressionResolvers.ExpressionResolver;
import com.beren.qba.dsl.expressionResolvers.impl.ContainsExpressionResolver;
import com.beren.qba.dsl.expressionResolvers.impl.EndsWithExpressionResolver;
import com.beren.qba.dsl.expressionResolvers.impl.EqExpressionResolver;
import com.beren.qba.dsl.expressionResolvers.impl.GreaterThanExpressionResolver;
import com.beren.qba.dsl.expressionResolvers.impl.IsExpressionResolver;
import com.beren.qba.dsl.expressionResolvers.impl.LessThanExpressionResolver;
import com.beren.qba.dsl.expressionResolvers.impl.OneOfExpressionResolver;
import com.beren.qba.dsl.expressionResolvers.impl.StartsWithExpressionResolver;
import com.beren.qba.dsl.validators.DtoValidator;
import com.beren.qba.dsl.validators.impl.SimpleDtoValidator;
import com.beren.qba.exceptions.UnexpectedQueryGenerationError;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.PathBuilder;

public class DslQueryGenerator<T> implements QueryGenerator<T> {

	private Class<?> actualClass;
	private DtoValidator dtoValidator;
	private Map<Class<? extends Annotation>, ExpressionResolver> expressionMap;

	public DslQueryGenerator(Class<?> actualClass) {
		// TODO extract actualclass via reflection
		this(actualClass, null);
		defaultExpressionMap();
	}

	public DslQueryGenerator(Class<?> actualClass, Map<Class<? extends Annotation>, ExpressionResolver> expressionMap) {
		super();
		this.actualClass = actualClass;
		this.expressionMap = expressionMap;
		dtoValidator = new SimpleDtoValidator(actualClass);
	}

	@Override
	@SuppressWarnings("unchecked")
	public PathBuilder<T> getEntityPath(String entityName) {
		return new PathBuilder<>((Class<? extends T>) actualClass, entityName);
	}

	@Override
	public PathBuilder<T> getEntityPath() {
		return getEntityPath("entity");
	}

	@Override
	public Predicate createQuery(Object dto, PathBuilder<T> entityPath) {
		validateDTO(dto);
		BooleanBuilder expression = new BooleanBuilder();
		try {
			for (Field field : dto.getClass().getDeclaredFields()) {
				Optional<Class<? extends Annotation>> isAnnotated;
				if ((isAnnotated = getQueryAnnotation(field)).isPresent()) {
					field.setAccessible(true);
					Class<? extends Annotation> annotation = isAnnotated.get();
					if (field.get(dto) != null)
						expression.and(expressionMap.get(annotation).getRestriction(dto, entityPath, field));
				}
			}
		} catch (Exception e) {
			throw new UnexpectedQueryGenerationError(e);
		}
		return expression;
	}

	private void defaultExpressionMap() {
		// TODO make this map a new type and create a builder
		expressionMap = new HashMap<>();
		expressionMap.put(QueryEq.class, new EqExpressionResolver());
		expressionMap.put(QueryStartsWith.class, new StartsWithExpressionResolver());
		expressionMap.put(QueryEndsWith.class, new EndsWithExpressionResolver());
		expressionMap.put(QueryContains.class, new ContainsExpressionResolver());
		expressionMap.put(QueryIs.class, new IsExpressionResolver());
		expressionMap.put(QueryOneOf.class, new OneOfExpressionResolver());
		expressionMap.put(QueryGreaterThan.class, new GreaterThanExpressionResolver());
		expressionMap.put(QueryLessThan.class, new LessThanExpressionResolver());
	}

	private Optional<Class<? extends Annotation>> getQueryAnnotation(Field field) {
		for (Class<? extends Annotation> annotation : supportedAnnotations())
			if (field.isAnnotationPresent(annotation))
				return Optional.of(annotation);
		return Optional.empty();
	}

	private Collection<Class<? extends Annotation>> supportedAnnotations() {
		return expressionMap.keySet();
	}

	private void validateDTO(Object dto) {
		dtoValidator.validate(dto);
	}

}
