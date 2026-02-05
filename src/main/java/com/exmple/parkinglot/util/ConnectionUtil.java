package com.exmple.parkinglot.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public enum ConnectionUtil {
    INSTANCE;

    private final HikariDataSource dataSource;

    ConnectionUtil(){
        HikariConfig config = new HikariConfig();
        config.setDriverClassName("org.mariadb.jdbc.Driver");

        config.setJdbcUrl("jdbc:mariadb://localhost:3306/parking_lot");
        config.setUsername("admin");
        config.setPassword("8282");

        // HikariCP 최적화 설정
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        // 추가 권장 설정
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(5);
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);

        this.dataSource = new HikariDataSource(config);
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}

/*

### 한글 깨짐 해결 포인트:

1. **JDBC URL에 인코딩 추가**: `characterEncoding=UTF-8`
2. **타임존 설정**: `serverTimezone=Asia/Seoul`
3. **HikariCP 풀 설정 최적화** 추가

## 3. IntelliJ IDEA 콘솔 인코딩 설정

**Help → Edit Custom VM Options** 에 추가:
```
-Dfile.encoding=UTF-8
-Dconsole.encoding=UTF-8
```

또는 **Run → Edit Configurations → Tomcat Server → VM options**에 추가:
```
-Dfile.encoding=UTF-8

 */