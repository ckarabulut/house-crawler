package com.homeless;

import com.homeless.models.Rental;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;

import java.util.List;

public interface RentalsDao {

  @SqlUpdate("CREATE DATABASE IF NOT EXISTS homeless CHARACTER SET utf8 COLLATE utf8_general_ci")
  void createDatabase();

  @SqlUpdate(
      "CREATE TABLE IF NOT EXISTS mydb.rentals"
          + " ("
          + "    ID VARCHAR(200) PRIMARY KEY NOT NULL,"
          + "    STATUS ENUM('DELETED', 'UPDATED', 'ACTIVE') NOT NULL,"
          + "    PRICE DECIMAL(5) NOT NULL,"
          + "    TYPE VARCHAR(20) NOT NULL,"
          + "    AREA DECIMAL(3) NOT NULL,"
          + "    ROOM_COUNT DECIMAL(1) NOT NULL,"
          + "    AVAILABLE_DATE DATE,"
          + "    INSERTION_DATE DATE,"
          + "    ADDRESS VARCHAR(100) NOT NULL,"
          + "    LINK VARCHAR(500) NOT NULL"
          + " )")
  void createRentalsTable();

  List<Rental> getRentals();

  void insertRental();
}
