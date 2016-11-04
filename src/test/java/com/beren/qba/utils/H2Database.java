package com.beren.qba.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.junit.rules.ExternalResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.FileSystemResourceAccessor;

public class H2Database extends ExternalResource
{

  public static final String DEFAULT_CHANGELOG = "src/test/resources/liquibase/master.xml";
  public static final String CONNECTION_STRING = "jdbc:h2:mem:test";
  private static final String USER_NAME = "sa";
  private static final String PASSWORD = "";
  private static final Logger LOG = LoggerFactory.getLogger(H2Database.class);

  private Connection holdingConnection;
  private Liquibase liquibase;
  private String contexts;
  private String changeLog;

  private String username = USER_NAME;
  private String password = PASSWORD;

  public H2Database(String contexts)
  {
    this(DEFAULT_CHANGELOG, contexts);
  }

  public H2Database(String changeLog, String contexts)
  {
    this.changeLog = changeLog;
    this.contexts = contexts;
  }

  public H2Database(String changeLog, String contexts, String username, String password)
  {
    this(changeLog, contexts);
    this.username = username;
    this.password = password;
  }

  @Override
  protected void before() throws Throwable
  {
    setUp();
  }

  @Override
  protected void after()
  {
    tearDown();
  }

  public void setUp()
  {
    try
    {
      Class.forName("org.h2.Driver");
      holdingConnection = getConnectionImpl();

      Database database = DatabaseFactory.getInstance()
          .findCorrectDatabaseImplementation(new JdbcConnection(holdingConnection));

      liquibase = new Liquibase(changeLog,
          new FileSystemResourceAccessor(), database);
      liquibase.dropAll();
      liquibase.update(contexts);
      // holdingConnection.close();
    }
    catch (Exception ex)
    {
      LOG.error("Error during database initialization", ex);
      throw new RuntimeException("Error during database initialization", ex);
    }
  }

  public void tearDown()
  {
    try
    {
      holdingConnection.close();
    }
    catch (Exception ex)
    {
      LOG.error("Error closing database connection", ex);
      throw new RuntimeException("Error closing database connection", ex);
    }
  }

  public Connection getConnectionImpl() throws SQLException
  {
    return DriverManager.getConnection(CONNECTION_STRING, username, password);
  }
}
