package com.book.xw.common.dal.util;

import ch.vorburger.mariadb4j.DB;
import ch.vorburger.mariadb4j.DBConfigurationBuilder;
import com.alibaba.druid.pool.DruidDataSource;
import com.book.xw.common.dal.util.RdbDaoUtil;
import lombok.SneakyThrows;

public class MariaDb4jUtil extends RdbDaoUtil {

    private DB mariaDb;

    public MariaDb4jUtil(String dbName) {
        super(dbName);
    }

    @SneakyThrows
    @Override
    protected DruidDataSource buildRdbDataSource(String... args) {
        buildDb();
        mariaDb.createDB(dbName);
        String url = "jdbc:mysql://localhost:"+mariaDb.getConfiguration().getPort()+"/"+dbName+"?useUnicode=true&characterEncoding=UTF-8";
        String driverClass = "com.mysql.jdbc.driver";
        return buildDataSource(url, "root", "", driverClass);
    }

    @SneakyThrows
    private void buildDb(){
        DBConfigurationBuilder builder = DBConfigurationBuilder.newBuilder();
        // 0 -> auto detect free port
        builder.setPort(0);
        builder.setBaseDir(DB_LOCAL_PATH);
        mariaDb = DB.newEmbeddedDB(builder.build());
        mariaDb.start();
    }

    @SneakyThrows
    @Override
    public void destroyDataSource() {
        super.destroyDataSource();
        if(mariaDb != null){
            mariaDb.stop();
        }
    }
}
