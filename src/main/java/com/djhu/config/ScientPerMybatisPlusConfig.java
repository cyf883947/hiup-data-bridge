package com.djhu.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.baomidou.mybatisplus.spring.MybatisSqlSessionFactoryBean;
import com.djhu.common.constant.ProjectConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;

/**
 * @Author zw
 * @DATE 2019/4/23 18:16
 * @VERSION 1.0.0
 **/
@Slf4j
@Configuration
@MapperScan(basePackages = ProjectConstant.SCIENT_PER_MAPPER_PACKAGE,sqlSessionFactoryRef="scientPerSqlSessionFactory")
public class ScientPerMybatisPlusConfig {

    @Value("${scient_per.orcl.url}")
    private String url;

    @Value("${scient_per.orcl.user}")
    private String username;

    @Value("${scient_per.orcl.password}")
    private String password;

    @Value("${scient_per.orcl.driver}")
    private String driverClass;


    @Bean(name = "scientPerDataSource")
    public DataSource scientPerDataSource() {
        try{
            DruidDataSource dataSource = new DruidDataSource();
            dataSource.setUrl(url);
            dataSource.setDriverClassName(driverClass);
            dataSource.setUsername(username);
            dataSource.setPassword(password);
            return dataSource;
        }catch (Exception e){
            log.error("数据库连接失败!!!",e);
        }
        return null;
    }


    @Bean("scientPerSqlSessionFactory")
    public SqlSessionFactory scientPerSqlSessionFactory(ResourceLoader resourceLoader) throws Exception {
        MybatisSqlSessionFactoryBean factoryBean = new MybatisSqlSessionFactoryBean();
        factoryBean.setDataSource(scientPerDataSource());
        //扫描entity包
        factoryBean.setTypeAliasesPackage(ProjectConstant.SCIENT_PER_MODEL_PACKAGE);
        //扫描sql配置文件
        Resource[] resources = new PathMatchingResourcePatternResolver().getResources(ProjectConstant.SCIENT_PER_MAPPER_LOCATION);
        factoryBean.setMapperLocations(resources);
        // 配置mybatis全局配置文件
        Resource resource = resourceLoader.getResource(ProjectConstant.CONFIG_LOCATION);
        factoryBean.setConfigLocation(resource);
        return factoryBean.getObject();
    }
}
