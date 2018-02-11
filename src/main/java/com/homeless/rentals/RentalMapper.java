package com.homeless.rentals;

import com.homeless.rentals.models.Rental;
import com.homeless.rentals.models.Status;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;

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
    rental.setAvailableDate(r.getTimestamp("availableDate").toInstant());
    rental.setInsertionDate(r.getTimestamp("insertionDate").toInstant());
    rental.setLastUpdatedDate(r.getTimestamp("lastUpdatedDate").toInstant());
    rental.setAddress(r.getString("address"));
    rental.setUrl(r.getString("url"));
    return rental;
  }
}
