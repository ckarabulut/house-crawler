package com.homeless.rentals;

import com.homeless.rentals.models.Rental;
import java.util.List;
import org.jdbi.v3.sqlobject.config.RegisterRowMapper;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

@RegisterRowMapper(RentalMapper.class)
public interface RentalsDao {

  @SqlUpdate("CREATE DATABASE IF NOT EXISTS homeless CHARACTER SET utf8 COLLATE utf8_general_ci")
  void createDatabase();

  @SqlUpdate(
      ""
          + " CREATE TABLE IF NOT EXISTS homeless.rentals"
          + " ("
          + "    id MEDIUMINT NOT NULL AUTO_INCREMENT,"
          + "    url VARCHAR(1000) NOT NULL,"
          + "    status ENUM('DELETED', 'UNDER_OPTION', 'AVAILABLE') NOT NULL,"
          + "    address VARCHAR(1000) NOT NULL,"
          + "    price DECIMAL(5) NOT NULL,"
          + "    type VARCHAR(100) NOT NULL,"
          + "    area DECIMAL(3) NOT NULL,"
          + "    roomCount DECIMAL(1) NOT NULL,"
          + "    floor DECIMAL(2) NOT NULL,"
          + "    availableDate TIMESTAMP,"
          + "    createdOn TIMESTAMP,"
          + "    updatedOn TIMESTAMP,"
          + "    PRIMARY KEY(id),"
          + "    UNIQUE KEY unique_url (url)"
          + " )")
  void createRentalsTable();

  @SqlQuery("SELECT * FROM homeless.rentals")
  List<Rental> findAll();

  @SqlUpdate(
      ""
          + " INSERT INTO homeless.rentals "
          + " VALUES(:id, :url, :status, :address, :price, :type, :area, :roomCount, :floor, :availableDate, now(), now())")
  void insertRental(@BindBean Rental rental);

  @SqlUpdate(
      ""
          + " UPDATE homeless.rentals"
          + " SET"
          + "  status= :status,"
          + "  address= :address,"
          + "  price= :price,"
          + "  type= :type,"
          + "  area= :area,"
          + "  roomCount= :roomCount,"
          + "  floor= :floor,"
          + "  availableDate= :availableDate,"
          + "  updatedOn= now(),"
          + "  url= :url"
          + " WHERE id= :id")
  void updateRental(@BindBean Rental rental);
}
