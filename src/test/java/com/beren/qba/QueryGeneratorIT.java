package com.beren.qba;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import com.beren.qba.dsl.DslQueryGenerator;
import com.beren.qba.dtos.MailSearchDTO;
import com.beren.qba.entities.Mail;
import com.beren.qba.utils.H2Database;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;

public class QueryGeneratorIT
{
  private static final String PU = "qba_test";

  @ClassRule
  public static H2Database database = new H2Database("test");

  private static EntityManagerFactory entityManagerFactory;
  private EntityManager entityManager;
  private QueryGenerator<Mail> queryGenerator = new DslQueryGenerator<>(
      Mail.class);
  private PathBuilder<Mail> entityPath;
  private JPAQuery<Mail> baseQuery;

  @BeforeClass
  public static void init()
  {
    entityManagerFactory = Persistence.createEntityManagerFactory(PU);
  }

  @Before
  public void setUp()
  {
    entityManager = entityManagerFactory.createEntityManager();
    clearDB();
    entityPath = queryGenerator.getEntityPath();
    baseQuery = new JPAQuery<Mail>(entityManager).from(entityPath);
  }

  @After
  public void tearDown()
  {
    if (entityManager != null && entityManager.isOpen())
      entityManager.close();
  }

  @Test
  public void whenWithoutRestrictionsSelectAll() throws Exception
  {
    Object dto = givenValidDTO();
    Collection<Mail> entities = givenEntities(3);
    Predicate restrictions = whenAskForQuery(dto);
    thenAllEntitiesAreFound(restrictions, entities.toArray(new Mail[entities.size()]));
  }

  @Test
  public void whenEqRestrictionSelectEq() throws Exception
  {
    List<Mail> entities = givenEntities(3);
    Mail first = entities.get(0);
    MailSearchDTO dto = givenSearchByFrom(first.getFrom());
    Predicate restrictions = whenAskForQuery(dto);
    thenAllEntitiesAreFound(restrictions, first);
  }

  @Test
  public void whenEqIgnoreCaseRestrictionSelectIgnoreCase() throws Exception
  {
    List<Mail> entities = givenEntities(3);
    Mail first = entities.get(0);
    String subject = changeCase(first.getSubject());
    MailSearchDTO dto = givenSearchBySubject(subject);
    Predicate restrictions = whenAskForQuery(dto);
    thenAllEntitiesAreFound(restrictions, first);
  }

  @Test
  public void whenStartsWithRestrictionSelectStartsWiht() throws Exception
  {
    List<Mail> entities = givenEntities(3);
    Mail first = entities.get(0);
    MailSearchDTO dto = givenSearchBySubjectStartsWith(StringUtils.substringBefore(first.getSubject(), "-"));
    Predicate restrictions = whenAskForQuery(dto);
    thenAllEntitiesAreFound(restrictions, first);
  }

  @Test
  public void whenEndsWithRestrictionSelectEndsWiht() throws Exception
  {
    List<Mail> entities = givenEntities(3);
    Mail first = entities.get(0);
    MailSearchDTO dto = givenSearchBySubjectEndsWith(StringUtils.substringAfter(first.getSubject(), "-"));
    Predicate restrictions = whenAskForQuery(dto);
    thenAllEntitiesAreFound(restrictions, first);
  }

  private MailSearchDTO givenSearchBySubjectEndsWith(String subject)
  {
    MailSearchDTO dto = givenValidDTO();
    dto.setSubjectEnd(subject);
    return dto;
  }

  private MailSearchDTO givenSearchBySubjectStartsWith(String subject)
  {
    MailSearchDTO dto = givenValidDTO();
    dto.setSubjectStart(subject);
    return dto;
  }

  private MailSearchDTO givenSearchBySubject(String subject)
  {
    MailSearchDTO dto = givenValidDTO();
    dto.setSubject(subject);
    return dto;
  }

  private MailSearchDTO givenSearchByFrom(String from)
  {
    MailSearchDTO dto = givenValidDTO();
    dto.setFrom(from);
    return dto;
  }

  private MailSearchDTO givenValidDTO()
  {
    return new MailSearchDTO();
  }

  private List<Mail> givenEntities(int n)
  {
    List<Mail> inserted = new ArrayList<>();
    try
    {
      entityManager.getTransaction().begin();
      for (int i = 0; i < n; i++)
      {
        Mail randomMail = createRandomMail();
        entityManager.persist(randomMail);
        inserted.add(randomMail);
      }
      entityManager.getTransaction().commit();
    }
    catch (Exception e)
    {
      entityManager.getTransaction().rollback();
    }
    return inserted;
  }

  private Mail createRandomMail()
  {
    Mail randomEntity = new Mail(UUID.randomUUID().toString());
    randomEntity.setSubject(UUID.randomUUID().toString());
    return randomEntity;
  }

  private Predicate whenAskForQuery(Object dto)
  {
    return queryGenerator.createQuery(dto, entityPath);
  }

  private void thenAllEntitiesAreFound(Predicate restrictions,
                                       Mail... entities)
  {
    JPAQuery<Mail> query = baseQuery.where(restrictions);
    assertThat(query.fetch().size(), is(entities.length));
    assertThat(query.fetch(), hasItems(entities));
  }

  private void clearDB()
  {
    entityManager.getTransaction().begin();
    entityManager.createNativeQuery("delete from mail").executeUpdate();
    entityManager.getTransaction().commit();
  }

  private String changeCase(String text)
  {
    char[] chars = text.toCharArray();
    for (int i = 0; i < chars.length; i++)
    {
      char c = chars[i];
      if (Character.isUpperCase(c))
      {
        chars[i] = Character.toLowerCase(c);
      }
      else if (Character.isLowerCase(c))
      {
        chars[i] = Character.toUpperCase(c);
      }
    }
    return new String(chars);
  }

}
