package com.beren.qba.entities;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "OTHER")
public class AnotherEntity
{
  @Id
  private String id;

  protected AnotherEntity() {
	super();
}

public AnotherEntity(String id)
  {
	this();
    this.id = id;
  }
}
