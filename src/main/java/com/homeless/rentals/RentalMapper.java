package com.homeless.rentals;

import com.homeless.rentals.models.Rental;
import com.homeless.rentals.models.Status;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

public class RentalMapper implements RowMapper<Rental> {

  @Override
  public Rental map(ResultSet r, StatementContext ctx) throws SQLException {
    return Rental.builder()
        .id(r.getInt("id"))
        .status(Status.valueOf(r.getString("status")))
        .price(r.getInt("price"))
        .type(r.getString("type"))
        .area(r.getInt("area"))
        .roomCount(r.getInt("roomCount"))
        .floor(r.getInt("floor"))
        .availableDate(getNullableDate(r, "availableDate"))
        .createdOn(getNullableDate(r, "createdOn"))
        .updatedOn(getNullableDate(r, "updatedOn"))
        .url(r.getString("url"))
        .address(r.getString("address"))
        .build();
  }

  private Instant getNullableDate(ResultSet r, String fieldName) throws SQLException {
    Timestamp timestamp = r.getTimestamp(fieldName);
    if (timestamp == null) {
      return null;
    }
    return timestamp.toInstant();
  }
}
