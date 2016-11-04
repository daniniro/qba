package com.beren.qba.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "MAIL")
public class Mail
{
  private Long id;
  private String from;
  private String subject;

  public Mail(String from)
  {
    this.from = from;
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ID_MAIL")
  public Long getId()
  {
    return id;
  }

  public void setId(Long id)
  {
    this.id = id;
  }

  @Column(name = "SENDER")
  public String getFrom()
  {
    return from;
  }

  public void setFrom(String from)
  {
    this.from = from;
  }

  @Column(name = "SUBJECT")
  public String getSubject()
  {
    return this.subject;
  }

  public void setSubject(String subject)
  {
    this.subject = subject;
  }

}
