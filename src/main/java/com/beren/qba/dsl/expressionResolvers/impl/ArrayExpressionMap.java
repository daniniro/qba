package com.beren.qba.dsl.expressionResolvers.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ArrayExpressionMap {
	private Map<Class<?>, ArrayExpressionResolver> map;

	public ArrayExpressionMap() throws Exception {
		map = new HashMap<>();
		registerClass(NumericArrayExpressionResolver.class);
		registerClass(DateArrayExpressionResolver.class);
		registerClass(CalendarArrayExpressionResolver.class);
		registerClass(TemporalArrayExpressionResolver.class);
	}

	private void registerClass(Class<? extends ArrayExpressionResolver> clazz)
			throws InstantiationException, IllegalAccessException {
		ArrayExpressionResolver temp = clazz.newInstance();
		map.put(temp.getSupportedType(), temp);
	}

	public boolean containsCompatibleKey(Class<?> componentType) {
		List<Class<?>> compatibleClasses = map.keySet().stream().filter(clazz -> clazz.isAssignableFrom(componentType))
				.collect(Collectors.toList());
		return !compatibleClasses.isEmpty();
	}

	public ArrayExpressionResolver get(Class<?> componentType) {
		return null
	}

}
