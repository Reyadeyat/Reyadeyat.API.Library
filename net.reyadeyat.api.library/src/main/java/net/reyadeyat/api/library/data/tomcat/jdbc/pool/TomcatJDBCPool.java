package net.reyadeyat.api.library.data.tomcat.jdbc.pool;

import java.sql.Connection;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;

public class TomcatJDBCPool {
    final private PoolProperties poolProperties;
    final private DataSource dataSource;
    
    public TomcatJDBCPool(String DBDriver, String DBURL, String username, String password, String validationQuery) {
        this.poolProperties = new PoolProperties();
        this.poolProperties.setUrl(DBURL);
        this.poolProperties.setDriverClassName(DBDriver);
        this.poolProperties.setUsername(username);
        this.poolProperties.setPassword(password);
        this.poolProperties.setJmxEnabled(true);
        this.poolProperties.setValidationQuery(validationQuery);
        this.poolProperties.setTestWhileIdle(true);
        this.poolProperties.setTestOnBorrow(true);
        this.poolProperties.setTestOnReturn(true);
        this.poolProperties.setValidationInterval(30000);
        this.poolProperties.setTimeBetweenEvictionRunsMillis(30000);
        this.poolProperties.setMaxActive(100);
        this.poolProperties.setInitialSize(10);
        this.poolProperties.setMaxWait(10000);
        this.poolProperties.setRemoveAbandonedTimeout(60);
        this.poolProperties.setMinEvictableIdleTimeMillis(30000);
        this.poolProperties.setMinIdle(10);
        this.poolProperties.setLogAbandoned(true);
        this.poolProperties.setRemoveAbandoned(true);
        this.poolProperties.setJdbcInterceptors(
                "org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;"
                + "org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer");
        this.dataSource = new DataSource();
        dataSource.setPoolProperties(poolProperties);
    }

    public Connection getPooledConnection() throws Exception {
        return dataSource.getConnection();
    }
}
