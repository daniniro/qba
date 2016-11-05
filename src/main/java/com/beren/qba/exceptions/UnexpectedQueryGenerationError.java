package com.beren.qba.exceptions;

public class UnexpectedQueryGenerationError extends RuntimeException
{
  private static final long serialVersionUID = -6031355725509532664L;

  public UnexpectedQueryGenerationError(Exception e)
  {
    super(e);
  }

}
