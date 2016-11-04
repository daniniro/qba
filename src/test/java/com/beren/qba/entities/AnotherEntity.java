package com.beren.qba.entities;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "OTHERß")
public class AnotherEntity
{
  @Id
  private String id;

  public AnotherEntity(String id)
  {
    this.id = id;
  }
}
