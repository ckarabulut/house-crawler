package com.homeless.recipients;

import com.google.gson.Gson;
import org.jdbi.v3.core.generic.GenericType;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class RecipientMapper implements RowMapper<Recipient> {

  private final Gson gson;
  private final GenericType<Map<String, List<String>>> mapGenericType;

  public RecipientMapper() {
    gson = new Gson();
    mapGenericType = new GenericType<Map<String, List<String>>>() {};
  }

  @Override
  public Recipient map(ResultSet r, StatementContext ctx) throws SQLException {
    Recipient rental = new Recipient();
    rental.setId(r.getInt("id"));
    rental.setEmail(r.getString("email"));
    String filters = r.getString("filters");
    if (filters != null) {
      rental.setFilterMap(gson.fromJson(filters, mapGenericType.getType()));
    } else {
      rental.setFilterMap(Collections.emptyMap());
    }
    return rental;
  }
}
