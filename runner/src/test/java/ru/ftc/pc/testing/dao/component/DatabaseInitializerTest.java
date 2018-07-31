package ru.ftc.pc.testing.dao.component;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

/**
 * @author maksimenko
 * @since 31.07.2018
 */
class DatabaseInitializerTest extends DaoTest {
  @Autowired
  private DatabaseInitializer sut;

  @BeforeEach
  void setUp() {
    sut = spy(sut);
  }

  @Test
  void initDb() throws Exception {
    sut.initDb("/create_tables_test.sql");

    ArgumentCaptor<String> stringCaptor = ArgumentCaptor.forClass(String.class);

    verify(sut).execute(stringCaptor.capture());
    String script = "DROP TABLE IF EXISTS DOCUMENT;CREATE TABLE DOCUMENT (DOCUMENT_ID RAW(16),DOCUMENT_NUMBER VARCHAR2(16),DOCUMENT_TYPE VARCHAR2(32),CREATED TIMESTAMP DEFAULT sysdate,UPDATED TIMESTAMP DEFAULT sysdate);\n" +
        "DROP TABLE IF EXISTS DOCUMENT_SUBSCRIPTION;CREATE TABLE DOCUMENT_SUBSCRIPTION (CONSUMER_ID NUMBER(19, 0),CABINET_CODE VARCHAR2(32),DOCUMENT_ID RAW(16),DESCRIPTION VARCHAR2(128),CREATED TIMESTAMP DEFAULT sysdate);\n" +
        "DROP TABLE IF EXISTS PENALTY;CREATE TABLE PENALTY (VSTATUS VARCHAR2(256) AS \n" +
        "  DECODE(\"STATUS\",\n" +
        "  'NEW', \"STATUS\",\n" +
        "  'EXPIRED', \"STATUS\")\n" +
        "  ,UIN VARCHAR2(32),PENALTY_DATE TIMESTAMP,STATUS VARCHAR2(16),TYPE VARCHAR(16),AMOUNT NUMBER(19, 0),DISCOUNT NUMBER(19, 0),DISCOUNT_EXPIRATION_DATE TIMESTAMP,DOCUMENT_ID RAW(16));\n";
    assertEquals(script, stringCaptor.getValue());
  }
}