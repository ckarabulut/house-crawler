package com.homeless;

import com.homeless.config.Configuration;
import com.mysql.cj.jdbc.MysqlDataSource;
import org.skife.jdbi.v2.DBI;

import javax.sql.DataSource;

public class DataSourceConnection {

  private final RentalsDao rentalsDao;

  public DataSourceConnection(Configuration configuration) {
    DataSource ds = new MysqlDataSource();
    DBI dbi = new DBI(ds);
    rentalsDao = dbi.open(RentalsDao.class);
  }

  public RentalsDao getRentalsDao() {
    return rentalsDao;
  }
}
