package com.book.xw.common.dal.util;

import com.alibaba.druid.pool.DruidDataSource;
import lombok.Data;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class RdbDaoUtil {
    protected static String DB_LOCAL_PATH = "./mydb";
    private JdbcTemplate  jdbcTemplate;
    private DruidDataSource dataSource;
    protected String dbName;

    private volatile Integer dataSourceStatus = 1;


    public RdbDaoUtil(String dbName) {
        this.dbName = dbName;
        this.dataSource = buildRdbDataSource(dbName);
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    protected abstract DruidDataSource buildRdbDataSource(String ... args);

    protected DruidDataSource buildDataSource(String dbUrl, String user, String pwd, String driverClass){
        synchronized (dbName){
            if(dataSourceStatus == 1){
                DruidDataSource dataSource = new DruidDataSource();
                dataSource.setUrl(dbUrl);
                dataSource.setUsername(user);
                dataSource.setPassword(pwd);

                dataSource.setInitialSize(1);
                dataSource.setMinIdle(1);
                dataSource.setMaxWait(30000);
                dataSource.setMinEvictableIdleTimeMillis(30000);
                dataSource.setTestWhileIdle(true);
                dataSource.setValidationQuery("select 1");
                dataSource.setTestOnBorrow(true);
                dataSource.setTestOnReturn(false);
                dataSource.setLogAbandoned(true);
                try {
                    dataSource.init();
                }catch (Exception e){
                    throw new RuntimeException(e);
                }
            }else{
                return this.dataSource;
            }
        }
        return dataSource;
    }

    public void destroyDataSource(){
        if(this.dataSource != null && !dataSource.isClosed()){
            this.dataSource.close();
            dataSourceStatus = 1;
        }
    }

    public void createTable(String table, List<ColumnType> columns ){
        String sql = "";
        jdbcTemplate.execute(sql);
    }

    public void deleteTable(String table){
        String sql = "delete table if exists ` "+table+"` ;";
        jdbcTemplate.update(sql);
    }

    public void executeSql(String sql){
        jdbcTemplate.execute(sql);
    }

    public List<Map<String, Object>> queryForMap(String sql){
        return jdbcTemplate.queryForList(sql);
    }

    public MyData queryDbData(String sql, boolean needColName){
        MyData data = new MyData();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = this.dataSource.getConnection();
            conn.setAutoCommit(false);
            stmt = conn.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            rs = stmt.executeQuery();
            if(needColName){
                getColNameAndType(rs, data);
            }
            int row = 0;
            while (rs.next()){
                getColData(rs, data);
                row++;
            }
            data.setRows(row);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if(rs != null){
                    rs.close();
                }
                if(stmt != null){
                    stmt.close();
                }
                if(conn != null){
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return data;
    }

    private void getColData(ResultSet rs, MyData data){
        int size = data.getColNames().size();
        if(rs != null){
            List<Object> list = new ArrayList<>(size);
            try {
                for(int i = 0; i< size; i++){
                    list.add(rs.getObject(i + 1));
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            data.getColData().add(list);
        }
    }

    private void getColNameAndType(ResultSet rs, MyData data){
        List<String> names = new ArrayList<>();
        List<Integer> types = new ArrayList<>();
        if(rs != null){
            try {
                ResultSetMetaData rms = rs.getMetaData();
                for(int i = 1; i<=rms.getColumnCount(); i++){
                    types.add(rms.getColumnType(i));
                    names.add(rms.getColumnLabel(i));
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        data.setColNames(names);
        data.setColTypes(types);
    }


    @Data
    public class ColumnType{
        // 字段名字
        private String name;
        // 字段类型
        private String type;
        // 约束条件，主键、普通索引
        private String constraint;
    }

    @Data
    public class MyData{
        private List<String> colNames;
        private List<Integer> colTypes;
        private List<List<Object>> colData = new ArrayList<>();
        private int rows ;
    }
}
