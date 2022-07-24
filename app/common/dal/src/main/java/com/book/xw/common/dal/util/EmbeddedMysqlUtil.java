package com.book.xw.common.dal.util;

import com.alibaba.druid.pool.DruidDataSource;
import com.book.xw.common.dal.util.RdbDaoUtil;
import com.wix.mysql.EmbeddedMysql;
import com.wix.mysql.config.Charset;
import com.wix.mysql.config.MysqldConfig;
import com.wix.mysql.distribution.Version;
import lombok.SneakyThrows;

import java.net.ServerSocket;
import java.util.TimeZone;

public class EmbeddedMysqlUtil extends RdbDaoUtil {

    private EmbeddedMysql embeddedMysql;

    public EmbeddedMysqlUtil(String dbName) {
        super(dbName);
    }

    @Override
    protected DruidDataSource buildRdbDataSource(String... args) {
        MysqldConfig config = mysqldConfig();
        embeddedMysql = EmbeddedMysql
                .anEmbeddedMysql(mysqldConfig())
                .addSchema(dbName)
                .start();
        String url = "jdbc:mysql://localhost:"+config.getPort()+"/"+dbName+"?useUnicode=true&characterEncoding=UTF-8";
        String driverClass = "com.mysql.jdbc.driver";
        return buildDataSource(url, mysqldConfig().getUsername(), mysqldConfig().getPassword(), driverClass);
    }


    @Override
    public void destroyDataSource() {
        super.destroyDataSource();
        if(embeddedMysql != null){
            embeddedMysql.stop();
        }
    }

    private MysqldConfig mysqldConfig(){
        return MysqldConfig.aMysqldConfig(Version.v5_7_latest)
                .withCharset(Charset.UTF8)
                .withPort(randomPort())
                .withTimeZone(TimeZone.getDefault())
                .withTempDir(DB_LOCAL_PATH)
                .build();
    }

    @SneakyThrows
    private int randomPort(){
        try(ServerSocket serverSocket = new ServerSocket(0)){
            return serverSocket.getLocalPort();
        }
    }

}
