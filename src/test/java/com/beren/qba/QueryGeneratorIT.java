package com.beren.qba;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
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

public class QueryGeneratorIT {
	private static final String PU = "qba_test";

	@ClassRule
	public static H2Database database = new H2Database("test");

	private static EntityManagerFactory entityManagerFactory;
	private EntityManager entityManager;
	private QueryGenerator<Mail> queryGenerator = new DslQueryGenerator<>(Mail.class);
	private PathBuilder<Mail> entityPath;
	private JPAQuery<Mail> baseQuery;

	private List<Mail> defaultMails;

	@BeforeClass
	public static void init() {
		entityManagerFactory = Persistence.createEntityManagerFactory(PU);
	}

	@Before
	public void setUp() {
		entityManager = entityManagerFactory.createEntityManager();
		clearDB();
		entityPath = queryGenerator.getEntityPath();
		baseQuery = new JPAQuery<Mail>(entityManager).from(entityPath);
		defaultMails = givenDefaultMails(3);
	}

	@After
	public void tearDown() {
		if (entityManager != null && entityManager.isOpen())
			entityManager.close();
	}

	@Test
	public void whenWithoutRestrictionsSelectAll() throws Exception {
		Object dto = givenValidDTO();
		Predicate restrictions = whenAskForQuery(dto);
		thenAllEntitiesAreFound(restrictions, defaultMails.toArray(new Mail[defaultMails.size()]));
	}

	@Test
	public void whenEqRestrictionSelectEq() throws Exception {
		Mail first = defaultMails.get(0);
		MailSearchDTO dto = givenSearchByFrom(first.getFrom());
		Predicate restrictions = whenAskForQuery(dto);
		thenAllEntitiesAreFound(restrictions, first);
	}

	@Test
	public void whenEqIgnoreCaseRestrictionSelectIgnoreCase() throws Exception {
		Mail first = defaultMails.get(0);
		String subject = changeCase(first.getSubject());
		MailSearchDTO dto = givenSearchBySubject(subject);
		Predicate restrictions = whenAskForQuery(dto);
		thenAllEntitiesAreFound(restrictions, first);
	}

	@Test
	public void whenStartsWithRestrictionSelectStartsWith() throws Exception {
		Mail first = defaultMails.get(0);
		MailSearchDTO dto = givenSearchBySubjectStartsWith(StringUtils.substringBefore(first.getSubject(), "-"));
		Predicate restrictions = whenAskForQuery(dto);
		thenAllEntitiesAreFound(restrictions, first);
	}

	@Test
	public void whenEndsWithRestrictionSelectEndsWith() throws Exception {

		Mail first = defaultMails.get(0);
		MailSearchDTO dto = givenSearchBySubjectEndsWith(StringUtils.substringAfter(first.getSubject(), "-"));
		Predicate restrictions = whenAskForQuery(dto);
		thenAllEntitiesAreFound(restrictions, first);
	}

	@Test
	public void whenContainsRestrictionSelectContains() throws Exception {
		Mail first = defaultMails.get(0);
		MailSearchDTO dto = givenSearchBySubjectContains(StringUtils.substringBetween(first.getSubject(), "-"));
		Predicate restrictions = whenAskForQuery(dto);
		thenAllEntitiesAreFound(restrictions, first);
	}

	@Test
	public void whenIsRestrictionSelectExact() throws Exception {

		Mail first = defaultMails.get(0);
		MailSearchDTO dto = givenSearchById(first.getId());
		Predicate restrictions = whenAskForQuery(dto);
		thenAllEntitiesAreFound(restrictions, first);
	}

	@Test
	public void whenOneOfRestrictionSelectIn() throws Exception {
		Mail first = defaultMails.get(0);
		Mail second = defaultMails.get(1);
		MailSearchDTO dto = givenSearchByIds(first.getId(), second.getId());
		Predicate restrictions = whenAskForQuery(dto);
		thenAllEntitiesAreFound(restrictions, first, second);
	}

	@Test
	public void whenGreaterThanRestrictionSelectGreaterThan() throws Exception {
		Mail tomorrowMail = givenTomorrowMail();
		MailSearchDTO dto = givenSearchByDateFrom(Calendar.getInstance().getTime());
		Predicate restrictions = whenAskForQuery(dto);
		thenAllEntitiesAreFound(restrictions, tomorrowMail);
	}

	@Test
	public void whenLessThanRestrictionSelectGreaterThan() throws Exception {
		Mail tomorrowMail = givenYesterdayMail();
		MailSearchDTO dto = givenSearchByDateToInclusive(
				Date.from(LocalDateTime.now().minusDays(1).atZone(ZoneId.systemDefault()).toInstant()));
		Predicate restrictions = whenAskForQuery(dto);
		thenAllEntitiesAreFound(restrictions, tomorrowMail);
	}

	@Test
	public void whenMoreRestrictionsArePresentSelectInConjuction() throws Exception {
		Mail firstMail = defaultMails.get(0);
		Mail copiedMail = givenCopiedMail(firstMail);
		givenCopiedMailInAnotherDate(firstMail);
		givenSimilarMail(firstMail);
		MailSearchDTO dto = givenSearchByMoreRestrictions(firstMail.getSubject(), firstMail.getDate());
		Predicate restrictions = whenAskForQuery(dto);
		thenAllEntitiesAreFound(restrictions, firstMail, copiedMail);
	}

	// TODO rise coverage of OneOfRestriction
	// TODO rise coverage of GreaterThanRestriction
	// TODO rise coverage of LessThanRestriction
	// TODO add support for @QueryFetch
	// TODO add support for @QueryJoin
	// TODO add support for @QueryExists
	// TODO add support for @QueryAllOf
	// TODO add support for @QueryIsNull and @QueryIsNotNull
	// TODO add support for @QueryNot
	// TODO this class should be splitted
	// TODO add checks annotated types validation
	private MailSearchDTO givenSearchByMoreRestrictions(String subject, Date date) {
		MailSearchDTO dto = givenValidDTO();
		dto.setSubjectStart(StringUtils.substringBefore(subject, "-") + "-");
		dto.setSubjectEnd(StringUtils.substringAfterLast(subject, "-"));
		dto.setToDateInclusive(date);
		Date dateBefore = Date.from(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().minusDays(1)
				.atZone(ZoneId.systemDefault()).toInstant());
		dto.setFromDate(dateBefore);
		return dto;
	}

	private MailSearchDTO givenSearchByDateToInclusive(Date date) {
		MailSearchDTO dto = givenValidDTO();
		dto.setToDateInclusive(date);
		return dto;
	}

	private MailSearchDTO givenSearchByDateFrom(Date date) {
		MailSearchDTO dto = givenValidDTO();
		dto.setFromDate(date);
		return dto;
	}

	private MailSearchDTO givenSearchByIds(Long... ids) {
		MailSearchDTO dto = givenValidDTO();
		dto.setIds(ids);
		return dto;
	}

	private MailSearchDTO givenSearchById(Long id) {
		MailSearchDTO dto = givenValidDTO();
		dto.setId(id);
		return dto;
	}

	private MailSearchDTO givenSearchBySubjectContains(String subject) {
		MailSearchDTO dto = givenValidDTO();
		dto.setSubjectContains(subject);
		return dto;
	}

	private MailSearchDTO givenSearchBySubjectEndsWith(String subject) {
		MailSearchDTO dto = givenValidDTO();
		dto.setSubjectEnd(subject);
		return dto;
	}

	private MailSearchDTO givenSearchBySubjectStartsWith(String subject) {
		MailSearchDTO dto = givenValidDTO();
		dto.setSubjectStart(subject);
		return dto;
	}

	private MailSearchDTO givenSearchBySubject(String subject) {
		MailSearchDTO dto = givenValidDTO();
		dto.setSubject(subject);
		return dto;
	}

	private MailSearchDTO givenSearchByFrom(String from) {
		MailSearchDTO dto = givenValidDTO();
		dto.setFrom(from);
		return dto;
	}

	private MailSearchDTO givenValidDTO() {
		return new MailSearchDTO();
	}

	private Mail givenSimilarMail(Mail mail) {
		Mail similarMailMail = cloneMail(mail);
		similarMailMail.setSubject(StringUtils.replace(similarMailMail.getSubject(), "-", "?"));
		similarMailMail = givenMails(similarMailMail)[0];
		return similarMailMail;
	}

	private Mail givenCopiedMailInAnotherDate(Mail mail) {
		Mail copiedMailInAnotherDate = cloneMail(mail);
		copiedMailInAnotherDate
				.setDate(Date.from(LocalDateTime.now().plusDays(1).atZone(ZoneId.systemDefault()).toInstant()));
		copiedMailInAnotherDate = givenMails(copiedMailInAnotherDate)[0];
		return copiedMailInAnotherDate;
	}

	private Mail givenCopiedMail(Mail mail) {
		Mail copiedMail = cloneMail(mail);
		copiedMail = givenMails(copiedMail)[0];
		return copiedMail;
	}

	private Mail givenTomorrowMail() {
		Mail tomorrowMail = createRandomMail();
		tomorrowMail.setDate(Date.from(LocalDateTime.now().plusDays(1).atZone(ZoneId.systemDefault()).toInstant()));
		tomorrowMail = givenMails(tomorrowMail)[0];
		return tomorrowMail;
	}

	private Mail givenYesterdayMail() {
		Mail tomorrowMail = createRandomMail();
		tomorrowMail.setDate(Date.from(LocalDateTime.now().minusDays(1).atZone(ZoneId.systemDefault()).toInstant()));
		tomorrowMail = givenMails(tomorrowMail)[0];
		return tomorrowMail;
	}

	private Mail[] givenMails(Mail... mails) {
		try {
			entityManager.getTransaction().begin();
			for (Mail mail : mails) {
				entityManager.persist(mail);
			}
			entityManager.getTransaction().commit();
		} catch (Exception e) {
			entityManager.getTransaction().rollback();
		}
		return mails;
	}

	private List<Mail> givenDefaultMails(int n) {
		List<Mail> inserted = new ArrayList<>();
		for (int i = 0; i < n; i++)
			inserted.add(createRandomMail());
		inserted = Arrays.asList(givenMails(inserted.toArray(new Mail[inserted.size()])));
		return inserted;
	}

	private Predicate whenAskForQuery(Object dto) {
		return queryGenerator.createQuery(dto, entityPath);
	}

	private void thenAllEntitiesAreFound(Predicate restrictions, Mail... entities) {
		List<Mail> results = baseQuery.where(restrictions).fetch();
		assertThat(results.size(), is(entities.length));
		assertThat(results, hasItems(entities));
	}

	private Mail createRandomMail() {
		Mail randomEntity = new Mail(UUID.randomUUID().toString());
		randomEntity.setSubject(UUID.randomUUID().toString());
		randomEntity.setDate(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
		return randomEntity;
	}

	private Mail cloneMail(Mail mail) {
		Mail copiedMail = new Mail(mail.getFrom());
		copiedMail.setDate(mail.getDate());
		copiedMail.setSubject(mail.getSubject());
		return copiedMail;
	}

	private String changeCase(String text) {
		char[] chars = text.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			char c = chars[i];
			if (Character.isUpperCase(c)) {
				chars[i] = Character.toLowerCase(c);
			} else if (Character.isLowerCase(c)) {
				chars[i] = Character.toUpperCase(c);
			}
		}
		return new String(chars);
	}

	private void clearDB() {
		entityManager.getTransaction().begin();
		entityManager.createNativeQuery("delete from mail").executeUpdate();
		entityManager.getTransaction().commit();
	}

}
