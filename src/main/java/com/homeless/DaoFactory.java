package com.homeless;

import com.homeless.config.Configuration;
import com.homeless.rentals.RentalsDao;
import com.mysql.cj.jdbc.MysqlDataSource;
import org.skife.jdbi.v2.DBI;

public class DaoFactory {

  private final RentalsDao rentalsDao;

  public DaoFactory(Configuration configuration) {
    MysqlDataSource dataSource = new MysqlDataSource();
    dataSource.setUser(configuration.getDbUser());
    dataSource.setPassword(configuration.getDbPassword());
    dataSource.setUrl(configuration.getDbUrl());
    DBI dbi = new DBI(dataSource);
    rentalsDao = dbi.open(RentalsDao.class);
    rentalsDao.createDatabase();
    rentalsDao.createRentalsTable();
  }

  public RentalsDao getRentalsDao() {
    return rentalsDao;
  }
}
