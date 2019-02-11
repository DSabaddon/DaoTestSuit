package ru.mds.testing.dao.component;


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
        "  ,UIN VARCHAR2(32),PENALTY_DATE TIMESTAMP,STATUS VARCHAR2(16),TYPE VARCHAR(16),AMOUNT NUMBER(19, 0),DISCOUNT NUMBER(19, 0),DISCOUNT_EXPIRATION_DATE TIMESTAMP,DOCUMENT_ID RAW(16));\n" +
        "DROP TABLE IF EXISTS RECHARGE_LINK;CREATE TABLE RECHARGE_LINK (VLINK_UUID VARCHAR2(256) AS LINK_UUID,LINK_UUID RAW(16),CARD_TOKEN VARCHAR2(255 CHAR),STATUS VARCHAR2(255 CHAR),CONFIRMED TIMESTAMP,MOBILE_OLD_ID NUMBER(19, 0)DEFAULT 1104);\n" +
        "DROP TABLE IF EXISTS RECHARGE_CONFIRM_ATTEMPT;CREATE TABLE RECHARGE_CONFIRM_ATTEMPT (VREVERSAL_STATUS VARCHAR2(256) AS DECODE(REVERSAL_STATUS, 'REVERSAL_FAIL', REVERSAL_STATUS, NULL),VLINK_UUID VARCHAR2(256) AS LINK_UUID,LINK_UUID RAW(16),ATTEMPT NUMBER(10, 0) DEFAULT 0,CONFIRM_AMOUNT NUMBER(19, 2),HISTORY_UUID RAW(16) DEFAULT sys_guid(),CREATED TIMESTAMP DEFAULT SYSDATE,REVERSAL_ATTEMPT_NEXT NUMBER(19, 0),REVERSAL_STATUS VARCHAR2(255 CHAR),REVERSAL_ATTEMPT NUMBER(10, 0) DEFAULT 0);\n";
    assertEquals(script.replaceAll("\\r", ""), stringCaptor.getValue().replaceAll("\\r", ""));
  }
}