package com.beren.qba.exceptions;

public class UnsupportedDTO extends RuntimeException
{

  private static final long serialVersionUID = -180534081345554269L;
  private Class<? extends Object> clazz;

  public UnsupportedDTO(String message, Class<? extends Object> clazz)
  {
    super(message);
    this.clazz = clazz;
  }

  public Class<? extends Object> getClazz()
  {
    return clazz;
  }

}
