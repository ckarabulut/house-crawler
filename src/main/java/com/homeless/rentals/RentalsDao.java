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
          + "    ID VARCHAR(200) PRIMARY KEY NOT NULL AUTO_INCREMENT,"
          + "    ADDRESS VARCHAR(1000) NOT NULL,"
          + "    STATUS ENUM('DELETED', 'UPDATED', 'ACTIVE') NOT NULL,"
          + "    PRICE DECIMAL(5) NOT NULL,"
          + "    TYPE VARCHAR(20) NOT NULL,"
          + "    AREA DECIMAL(3) NOT NULL,"
          + "    ROOM_COUNT DECIMAL(1) NOT NULL,"
          + "    AVAILABLE_DATE DATE,"
          + "    INSERTION_DATE DATE,"
          + "    LAST_UPDATE DATE,"
          + "    URL VARCHAR(1000) NOT NULL"
          + " )")
  void createRentalsTable();

  @SqlQuery("SELECT * FROM homeless.rentals WHERE STATUS = :status")
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
