package com.homeless;

import com.homeless.config.Configuration;
import com.homeless.rentals.RentalsDao;
import com.mysql.cj.jdbc.MysqlDataSource;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;

public class DaoFactory {

  private final RentalsDao rentalsDao;

  public DaoFactory(Configuration configuration) {
    MysqlDataSource dataSource = new MysqlDataSource();
    dataSource.setUser(configuration.getDbUser());
    dataSource.setPassword(configuration.getDbPassword());
    dataSource.setUrl(configuration.getDbUrl());
    Jdbi dbi = Jdbi.create(dataSource);
    dbi.installPlugin(new SqlObjectPlugin());
    rentalsDao = dbi.onDemand(RentalsDao.class);
    rentalsDao.createDatabase();
    rentalsDao.createRentalsTable();
  }

  public RentalsDao getRentalsDao() {
    return rentalsDao;
  }
}
