package com.beren.qba.dsl.validators.impl;

import javax.persistence.Entity;

import com.beren.qba.annotations.QueryRefersTo;
import com.beren.qba.dsl.validators.DtoValidator;
import com.beren.qba.exceptions.UnsupportedDTO;

public class SimpleDtoValidator implements DtoValidator
{

  private Class<?> actualClass;

  public SimpleDtoValidator(Class<?> actualClass)
  {
    this.actualClass = actualClass;
  }

  @Override
  public void validate(Object dto)
  {
    if (dto == null)
      throw new IllegalArgumentException("The DTO is null");
    if (!dto.getClass().isAnnotationPresent(QueryRefersTo.class))
      throw new UnsupportedDTO("The DTO is not annotated with QueryRefersTo", dto.getClass());
    if (!dto.getClass().getAnnotation(QueryRefersTo.class).value().isAnnotationPresent(Entity.class))
      throw new UnsupportedDTO("The DTO refers to non-entity class", dto.getClass());
    if (!dto.getClass().getAnnotation(QueryRefersTo.class).value().equals(actualClass))
      throw new UnsupportedDTO("The DTO refers to a different entity class", dto.getClass());
  }

}
