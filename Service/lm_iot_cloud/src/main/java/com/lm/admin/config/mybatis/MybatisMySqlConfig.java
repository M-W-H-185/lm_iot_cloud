package com.lm.admin.config.mybatis;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import com.lm.admin.config.mybatis.interceptorconfig.MyBatisTableFieldHandler;
import com.lm.admin.config.mybatis.interceptorconfig.MyBatisTableIdHandler;
import org.apache.ibatis.logging.slf4j.Slf4jImpl;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
@MapperScan(basePackages = MybatisMySqlConfig.PACKAGE, sqlSessionFactoryRef = "mysqlSqlSessionFactory")
public class MybatisMySqlConfig {

    @Autowired   // 拦截数据库的字段修改
    private MyBatisTableFieldHandler myBatisCommonFieldHandler;
    @Autowired //拦截数据库的主键 修改
    private MyBatisTableIdHandler myBatisTableIdHandler;

    // 精确到 cluster 目录，以便跟其他数据源隔离
    static final String PACKAGE = "com.lm.admin.mapper.mysql";
    static final String MAPPER_LOCATION = "classpath*:mapper/mysql/*.xml";
    @Value("${spring.datasource.mysql.url}")
    private String url;

    @Value("${spring.datasource.mysql.username}")
    private String user;

    @Value("${spring.datasource.mysql.password}")
    private String password;

    @Value("${spring.datasource.mysql.driver-class-name}")
    private String driverClass;

    @Bean(name = "mysqlDataSource")
    public DataSource mysqlDataSource() {
        DruidDataSource dataSource = DruidDataSourceBuilder.create().build();
        dataSource.setDriverClassName(driverClass);
        dataSource.setUrl(url);
        dataSource.setUsername(user);
        dataSource.setPassword(password);
        dataSource.setDbType("com.alibaba.druid.pool.DruidDataSource");
        return dataSource;
    }
    // 事务管理器 只需要配置mysql就可以了 td不用
    @Bean
    @Primary
    public DataSourceTransactionManager mysqlTransactionManager() {
        return new DataSourceTransactionManager(mysqlDataSource());
    }


    @Bean(name = "mysqlSqlSessionFactory")
    public SqlSessionFactory mysqlSqlSessionFactory(@Qualifier("mysqlDataSource") DataSource clusterDataSource)
            throws Exception {
        final SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(clusterDataSource);
        // mapper xml扫描
        sessionFactory.setMapperLocations(resolveMapperLocations());

        // 自定义数据源需要重新设置一次插件  拦截器
        sessionFactory.setPlugins(myBatisCommonFieldHandler,myBatisTableIdHandler);


        //此处创建一个Configuration 注意包不要引错了
        org.apache.ibatis.session.Configuration configuration=new org.apache.ibatis.session.Configuration();
        //配置日志实现 需要再开
        configuration.setLogImpl(Slf4jImpl.class );
        //此处可以添加其他mybatis配置 例如转驼峰命名
        configuration.setMapUnderscoreToCamelCase(true);

        // sessionFactory工厂装载上面配置的Configuration
        sessionFactory.setConfiguration(configuration);

        return sessionFactory.getObject();

    }

    /**
     * 扫描多个mapper.xml路径
     * @return
     */
    public Resource[] resolveMapperLocations() {
        ResourcePatternResolver resourceResolver = new PathMatchingResourcePatternResolver();
        List<String> mapperLocations = new ArrayList<>();
        mapperLocations.add("classpath*:mapper/mysql/*.xml");
        mapperLocations.add("classpath*:mapper/mysql/role_admin/*.xml");
        mapperLocations.add("classpath*:mapper/mysql/role_user/*.xml");
        List<Resource> resources = new ArrayList();
        if (mapperLocations != null) {
            for (String mapperLocation : mapperLocations) {
                try {
                    Resource[] mappers = resourceResolver.getResources(mapperLocation);
                    resources.addAll(Arrays.asList(mappers));
                } catch (IOException e) {
                    // ignore
                }
            }
        }
        return resources.toArray(new Resource[resources.size()]);
    }

}
