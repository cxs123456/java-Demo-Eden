package org.cxs.demo.controller;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLType;
import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.dynamic.datasource.creator.DefaultDataSourceCreator;
import com.baomidou.dynamic.datasource.ds.ItemDataSource;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DataSourceProperty;
import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.cxs.demo.dao.DynamicDatasourceInstanceMapper;
import org.cxs.demo.entity.DynamicDatasourceInstance;
import org.cxs.demo.entity.SqlReqDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * note: 数据源 查询、增加、移除
 *
 * @author cxs
 * @date 2021/12/1 20:53
 **/
@RestController
@RequestMapping("/datasources")
public class DSController {

    @Resource
    private DataSource dataSource;
    @Resource
    private DefaultDataSourceCreator dataSourceCreator;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private DynamicDatasourceInstanceMapper instanceMapper;

    @GetMapping("list")
    public Set<String> now() {
        DynamicRoutingDataSource ds = (DynamicRoutingDataSource) dataSource;
        DynamicDatasourceInstance dynamicDatasourceInstance = instanceMapper.selectById(1);
        return ds.getDataSources().keySet();
    }

    @PostMapping("add")
    public Set<String> add(@RequestBody DataSourceProperty dataSourceProperty) {
        DynamicRoutingDataSource ds = (DynamicRoutingDataSource) dataSource;
        DataSource dataSource = dataSourceCreator.createDataSource(dataSourceProperty);
        ds.addDataSource(dataSourceProperty.getPoolName(), dataSource);
        // TODO 数据库表中添加
        DynamicDatasourceInstance data = new DynamicDatasourceInstance();
        data.setName(dataSourceProperty.getPoolName());
        data.setUrl(dataSourceProperty.getUrl());
        data.setUsername(dataSourceProperty.getUsername());
        data.setPassword(dataSourceProperty.getPassword());
        data.setDriver(dataSourceProperty.getDriverClassName());
        instanceMapper.insert(data);

        return ds.getDataSources().keySet();
    }

    @DeleteMapping("remove")
    public void remove(String name) {
        DynamicRoutingDataSource ds = (DynamicRoutingDataSource) dataSource;
        ds.removeDataSource(name);

        // TODO 数据库表中删除
        instanceMapper.delete(new LambdaQueryWrapper<DynamicDatasourceInstance>().eq(DynamicDatasourceInstance::getName, name));
    }

    /**
     * 根据
     *
     * @param datasource
     * @param sqlReq
     * @return
     */
    @PostMapping("exec")
    @DS("#header.datasource")
    public List<Map<String, Object>> exec(@RequestHeader String datasource,
            @RequestBody SqlReqDto sqlReq) {
        String sql = sqlReq.getSql();
        DynamicRoutingDataSource ds = (DynamicRoutingDataSource) dataSource;
        String key = DynamicDataSourceContextHolder.peek();
        System.out.println("key= " + key);
        ItemDataSource itemDataSource = (ItemDataSource) ds.determineDataSource();
        DruidDataSource realDataSource = (DruidDataSource) itemDataSource.getRealDataSource();

        String dbType = realDataSource.getDbType();
        String name = realDataSource.getName();
        System.out.println("当前数据源类型：" + dbType + ", 当前数据源名称：" + name);

        // TODO 执行sql....
        // 查询当前使用的数据库
        List<Map<String, Object>> maps = jdbcTemplate.queryForList("select database()");
        System.out.println("当前使用的数据库： " + maps);
        // 1.判断sql语法是否正确
        SQLExpr sqlExpr = SQLUtils.toSQLExpr(sql);
        // 2.获取 sql 语句 是哪种类型 select,insert,delete,update
        SQLType sqlType = SQLParserUtils.getSQLTypeV2(sql, null);
        // 3.根据类型执行sql
        if (SQLType.SELECT.equals(sqlType)) {
            //if (StringUtils.startsWithIgnoreCase(sql, "select")) {
            List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
            return list;
        } else {
            jdbcTemplate.execute(sql);
            return null;
        }
    }

    /**
     * 手动切换数据源 A数据源-->B数据源-->C数据源-->A数据源
     */
    @PostMapping("multiExec")
    public void multiExec() {
        // 数据源0  默认数据源 db
        List<Map<String, Object>> maps0 = jdbcTemplate.queryForList("select database()");
        System.out.println("000当前使用的数据库： " + maps0);

        try {
            // 数据源1 test
            DynamicDataSourceContextHolder.push("test");
            List<Map<String, Object>> maps1 = jdbcTemplate.queryForList("select database()");
            System.out.println("111当前使用的数据库： " + maps1);
            DynamicDataSourceContextHolder.poll();


            // 数据源2 pinyougoudb
            DynamicDataSourceContextHolder.push("pinyougoudb");
            List<Map<String, Object>> maps2 = jdbcTemplate.queryForList("select database()");
            System.out.println("222当前使用的数据库： " + maps2);
            DynamicDataSourceContextHolder.poll();


            // 数据源3 默认数据源 db
            List<Map<String, Object>> maps3 = jdbcTemplate.queryForList("select database()");
            System.out.println("333当前使用的数据库： " + maps3);

        } finally {
            DynamicDataSourceContextHolder.clear();
        }
    }

}



