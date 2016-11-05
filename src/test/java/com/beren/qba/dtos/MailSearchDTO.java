package com.beren.qba.dtos;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

import com.beren.qba.annotations.QueryContains;
import com.beren.qba.annotations.QueryEndsWith;
import com.beren.qba.annotations.QueryEq;
import com.beren.qba.annotations.QueryGreaterThan;
import com.beren.qba.annotations.QueryIs;
import com.beren.qba.annotations.QueryLessThan;
import com.beren.qba.annotations.QueryOneOf;
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
  @QueryContains("subject")
  private String subjectContains;
  @QueryIs("id")
  private Long id;
  @QueryOneOf("id")
  private Collection<Long> ids;
  @QueryGreaterThan("date")
  private Date fromDate;
  @QueryLessThan(value = "date", isInclusive = true)
  private Date toDateInclusive;

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

  public void setSubjectContains(String subjectContains)
  {
    this.subjectContains = subjectContains;

  }

  public void setId(Long id)
  {
    this.id = id;
  }

  public void setIds(Long... ids)
  {
    this.ids = Arrays.asList(ids);

  }

  public void setFromDate(Date date)
  {
    this.fromDate = date;

  }

  public void setToDateInclusive(Date date)
  {
    this.toDateInclusive = date;
  }

}
