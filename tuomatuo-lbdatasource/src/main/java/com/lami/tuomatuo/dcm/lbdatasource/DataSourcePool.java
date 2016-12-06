package com.lami.tuomatuo.dcm.lbdatasource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.datasource.AbstractDataSource;
import org.springframework.util.CollectionUtils;


import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * AbstractRoutingDataSource
 * Created by xjk on 2016/9/9.
 */
public class DataSourcePool extends AbstractDataSource implements InitializingBean {

    private static final Logger logger = Logger.getLogger(DataSourcePool.class);

    private DataSource writeDataSource;
    private Map<String, DataSource> readDataSourceMap;

    private int readDataSourceCount;
    private String[] readDataSourceNames;
    private DataSource[] readDataSources;

    private AtomicInteger counter = new AtomicInteger(1);

    public void setReadDataSourceMap(Map<String, DataSource> readDataSourceMap) {
        this.readDataSourceMap = readDataSourceMap;
    }

    public void setWriteDataSource(DataSource writeDataSource){
        this.writeDataSource = writeDataSource;
    }

    public Connection getConnection() throws SQLException {
        return determineDataSource().getConnection();
    }

    public Connection getConnection(String username, String password) throws SQLException {
        return determineDataSource().getConnection(username, password);
    }


    public void afterPropertiesSet() throws Exception {
        if(writeDataSource == null){
            throw new IllegalArgumentException("property 'writeDataSource' ir required");
        }
        if(CollectionUtils.isEmpty(readDataSourceMap)){
            throw new IllegalArgumentException("property 'readDataSourceMap' is required");
        }

        readDataSourceCount = readDataSourceMap.size();
        readDataSources = new DataSource[readDataSourceCount];
        readDataSourceNames = new String[readDataSourceCount];

        int i = 0;
        for(Map.Entry<String, DataSource> e : readDataSourceMap.entrySet()){
            readDataSources[i] = e.getValue();
            readDataSourceNames[i] = e.getKey();
            i++;
        }
    }

    private DataSource determineDataSource(){
        if(ReadWriteDataSourceDecision.isChoiceNone() || ReadWriteDataSourceDecision.isChoiceWrite()){
            return writeDataSource;
        }
        return determineReadDataSource();
    }

    private DataSource determineReadDataSource(){
        int index = counter.incrementAndGet() % readDataSourceCount;
        if(index < 0){
            index = -index;
        }

        String dataSourceName = readDataSourceNames[index];
        logger.debug("current determine read datasource " + dataSourceName);
        return readDataSources[index];
    }
}
