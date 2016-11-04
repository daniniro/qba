package com.beren.qba.dtos;

import com.beren.qba.annotations.QueryEndsWith;
import com.beren.qba.annotations.QueryEq;
import com.beren.qba.annotations.QueryRefersTo;
import com.beren.qba.annotations.QueryStartsWith;
import com.beren.qba.entities.Mail;

@QueryRefersTo(Mail.class)
public class MailSearchDTO
{
  @QueryEq("from")
  private String from;
  @QueryEq(value = "subject", ignoreCase = true)
  private String subject;
  @QueryStartsWith("subject")
  private String subjectStart;
  @QueryEndsWith("subject")
  private String subjectEnd;

  public void setFrom(String from)
  {
    this.from = from;
  }

  public void setSubject(String subject)
  {
    this.subject = subject;
  }

  public void setSubjectStart(String subjectStart)
  {
    this.subjectStart = subjectStart;

  }

  public void setSubjectEnd(String subjectEnd)
  {
    this.subjectEnd = subjectEnd;

  }

}
