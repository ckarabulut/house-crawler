package com.homeless.rentals;

import com.homeless.models.Rental;
import com.homeless.models.Status;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class RentalMapper implements ResultSetMapper<Rental> {
  @Override
  public Rental map(int i, ResultSet r, StatementContext statementContext) throws SQLException {
    Rental rental = new Rental();
    rental.setId(r.getInt("id"));
    rental.setStatus(Status.valueOf(r.getString("status")));
    rental.setPrice(r.getInt("price"));
    rental.setType(r.getString("type"));
    rental.setArea(r.getInt("area"));
    rental.setRoomCount(r.getInt("room_count"));
    rental.setAvailableDate(r.getDate("availableDate").toInstant());
    rental.setInsertionDate(r.getDate("insertionDate").toInstant());
    rental.setAddress(r.getString("address"));
    rental.setUrl(r.getString("url"));
    return rental;
  }
}
