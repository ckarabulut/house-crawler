package com.homeless.rentals;

import com.homeless.models.Rental;
import com.homeless.models.Status;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import java.util.List;

@RegisterMapper(RentalMapper.class)
public interface RentalsDao {

  @SqlUpdate("CREATE DATABASE IF NOT EXISTS homeless CHARACTER SET utf8 COLLATE utf8_general_ci")
  void createDatabase();

  @SqlUpdate(
      "CREATE TABLE IF NOT EXISTS homeless.rentals"
          + " ("
          + "    id MEDIUMINT NOT NULL AUTO_INCREMENT,"
          + "    address VARCHAR(1000) NOT NULL,"
          + "    status ENUM('DELETED', 'UPDATED', 'ACTIVE') NOT NULL,"
          + "    price DECIMAL(5) NOT NULL,"
          + "    type VARCHAR(20) NOT NULL,"
          + "    area DECIMAL(3) NOT NULL,"
          + "    roomCount DECIMAL(1) NOT NULL,"
          + "    availableDate TIMESTAMP,"
          + "    insertionDate TIMESTAMP,"
          + "    lastUpdatedDate TIMESTAMP,"
          + "    url VARCHAR(1000) NOT NULL,"
          + "    PRIMARY KEY(id)"
          + " )")
  void createRentalsTable();

  @SqlQuery("SELECT * FROM homeless.rentals WHERE status = :status")
  List<Rental> findByStatus(@Bind("status") Status status);

  @SqlUpdate(
      "INSERT INTO homeless.rentals "
          + " VALUES(:id, :address, :status, :price, :type, :area, :roomCount, :availableDate, :insertionDate, :lastUpdateDate, :url)")
  void insertRental(@BindBean Rental rental);

  @SqlUpdate(
      "UPDATE homeless.rentals SET"
          + " status= :status,"
          + " price= :price,"
          + " status= :status,"
          + " type= :type,"
          + " area= :area,"
          + " roomCount= :roomCount,"
          + " availableDate= :availableDate,"
          + " insertionDate= :insertionDate,"
          + " lastUpdateDate= :lastUpdateDate,"
          + " url= :url"
          + " WHERE id= :id")
  void updateRental(@BindBean Rental rental);
}
