package org.cxs.demo.config;

import com.baomidou.dynamic.datasource.provider.AbstractJdbcDataSourceProvider;
import com.baomidou.dynamic.datasource.provider.DynamicDataSourceProvider;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DataSourceProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

/**
 * note:
 *
 * @author cxs
 * @date 2021/12/2 14:15
 **/
@Primary
@Configuration
public class DsConfig {

    @Autowired
    private BaseDsProperties defaultDsConfig;

    @Bean
    public DynamicDataSourceProvider jdbcDynamicDataSourceProvider() {
        return new AbstractJdbcDataSourceProvider(defaultDsConfig.getDriverClassName(),
                defaultDsConfig.getUrl(),
                defaultDsConfig.getUsername(),
                defaultDsConfig.getPassword()) {
            @Override
            protected Map<String, DataSourceProperty> executeStmt(Statement statement) {
                Map<String, DataSourceProperty> dataSourcePropertiesMap = null;
                ResultSet rs = null;
                try {
                    dataSourcePropertiesMap = new HashMap<>();
                    rs = statement.executeQuery("SELECT * FROM DYNAMIC_DATASOURCE_INSTANCE");
                    while (rs.next()) {
                        String name = rs.getString("name");
                        DataSourceProperty property = new DataSourceProperty();
                        property.setDriverClassName(rs.getString("driver"));
                        property.setUrl(rs.getString("url"));
                        property.setUsername(rs.getString("username"));
                        property.setPassword(rs.getString("password"));
                        dataSourcePropertiesMap.put(name, property);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (rs != null) {
                            rs.close();
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    try {
                        statement.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                return dataSourcePropertiesMap;
            }
        };
    }

}
