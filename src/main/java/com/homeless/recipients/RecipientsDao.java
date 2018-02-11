package com.homeless.recipients;

import org.jdbi.v3.sqlobject.config.RegisterRowMapper;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.List;

@RegisterRowMapper(RecipientMapper.class)
public interface RecipientsDao {

  @SqlUpdate(
      "CREATE TABLE IF NOT EXISTS homeless.recipients"
          + " ("
          + "    id MEDIUMINT NOT NULL AUTO_INCREMENT,"
          + "    email VARCHAR(100) NOT NULL,"
          + "    filters TEXT,"
          + "    PRIMARY KEY(id)"
          + " )")
  void createRecipientsTable();

  @SqlQuery("SELECT * FROM homeless.recipients")
  List<Recipient> findAll();
}
