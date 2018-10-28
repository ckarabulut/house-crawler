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
    Rental rental = new Rental();
    rental.setId(r.getInt("id"));
    rental.setStatus(Status.valueOf(r.getString("status")));
    rental.setPrice(r.getInt("price"));
    rental.setType(r.getString("type"));
    rental.setArea(r.getInt("area"));
    rental.setRoomCount(r.getInt("roomCount"));
    rental.setAvailableDate(getNullableDate(r, "availableDate"));
    rental.setInsertionDate(getNullableDate(r, "insertionDate"));
    rental.setLastUpdatedDate(getNullableDate(r, "lastUpdatedDate"));
    rental.setAddress(r.getString("address"));
    rental.setUrl(r.getString("url"));
    return rental;
  }

  private Instant getNullableDate(ResultSet r, String fieldName) throws SQLException {
    Timestamp timestamp = r.getTimestamp(fieldName);
    if (timestamp == null) {
      return null;
    }
    return timestamp.toInstant();
  }
}
