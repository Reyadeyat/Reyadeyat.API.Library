package net.reyadeyat.api.library.data.source;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;

public class DataSourceService {
    
    static private DataSourceService data_source_service;
    static private List<DataSource> datasources_list;
    static private Map<String, DataSource> datasources_map;
    
    
    public DataSourceService(List<DataSource> datasources_list, Map<String, DataSource> datasources_map, DataSource default_data_source) {
        DataSourceService.data_source_service = this;
        DataSourceService.data_source_service.datasources_list = datasources_list;
        DataSourceService.data_source_service.datasources_map = datasources_map;
        DataSourceService.data_source_service.datasources_map.put("default", default_data_source);
    }
    
    static public Connection getConnection() throws SQLException {
        DataSource data_source = DataSourceService.datasources_map.get("default");
        if (data_source == null) {
            throw new SQLException("Default DataSource is null");
        }
        return data_source.getConnection();
    }
    
    static public Connection getConnection(String data_source_name) throws SQLException {
        DataSource data_source = DataSourceService.datasources_map.get(data_source_name);
        if (data_source == null) {
            throw new SQLException("DataSource '"+data_source_name+"' is null");
        }
        return data_source.getConnection();
    }
}
