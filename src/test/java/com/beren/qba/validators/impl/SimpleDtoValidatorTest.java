package com.beren.qba.validators.impl;

import static org.junit.Assert.fail;

import org.junit.Test;

import com.beren.qba.dsl.validators.DtoValidator;
import com.beren.qba.dsl.validators.impl.SimpleDtoValidator;
import com.beren.qba.dtos.AnnotatedDTOWithoutEntity;
import com.beren.qba.dtos.AnotherValidDTO;
import com.beren.qba.dtos.NotAnnotatedDTO;
import com.beren.qba.entities.Mail;
import com.beren.qba.exceptions.UnsupportedDTO;

public class SimpleDtoValidatorTest
{

  private DtoValidator validator = new SimpleDtoValidator(Mail.class);

  @Test
  public void whenNullDTOThrowException() throws Exception
  {
    try
    {
      whenValidate(null);
    }
    catch (IllegalArgumentException e)
    {
      return;
    }
    fail("Expected IllegalArgumentException exception");
  }

  @Test
  public void whenDTONotAnnotatedThrowException() throws Exception
  {
    Object dto = givenNotAnnotatedDTO();
    try
    {
      whenValidate(dto);
    }
    catch (UnsupportedDTO e)
    {
      return;
    }
    fail("Expected UnsupportedDTO exception");
  }

  @Test
  public void whenDTOisAnnotatedTheRelatedClassMustBeAnEntity() throws Exception
  {
    Object dto = givenAnnotatedDTOWithoutAnEntity();
    try
    {
      whenValidate(dto);
    }
    catch (UnsupportedDTO e)
    {
      return;
    }
    fail("Expected UnsupportedDTO exception");

  }

  @Test
  public void whenEntityIsDifferentFromTypedParameterThrowException() throws Exception
  {
    Object dto = givenAnotherValidDTO();
    try
    {
      whenValidate(dto);
    }
    catch (UnsupportedDTO e)
    {
      return;
    }
    fail("Expected UnsupportedDTO exception");
  }

  private void whenValidate(Object dto)
  {
    validator.validate(dto);
  }

  private NotAnnotatedDTO givenNotAnnotatedDTO()
  {
    return new NotAnnotatedDTO();
  }

  private Object givenAnnotatedDTOWithoutAnEntity()
  {
    return new AnnotatedDTOWithoutEntity();
  }

  private Object givenAnotherValidDTO()
  {
    return new AnotherValidDTO();
  }

}
