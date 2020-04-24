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
import org.springframework.context.annotation.Primary;
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
@MapperScan(basePackages = ProjectConstant.ATS_ES_MAPPER_PACKAGE,sqlSessionFactoryRef="sqlSessionFactory")
public class AtsEsMybatisPlusConfig {

    @Value("${ats_es.orcl.url}")
    private String url;

    @Value("${ats_es.orcl.user}")
    private String username;

    @Value("${ats_es.orcl.password}")
    private String password;

    @Value("${ats_es.orcl.driver}")
    private String driverClass;

    @Primary
    @Bean(name = "dataSource")
    public DataSource dataSource() {
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


    @Bean("sqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory(ResourceLoader resourceLoader) throws Exception {
        MybatisSqlSessionFactoryBean factoryBean = new MybatisSqlSessionFactoryBean();
        factoryBean.setDataSource(dataSource());
        //扫描entity包
        factoryBean.setTypeAliasesPackage(ProjectConstant.ATS_ES_MODEL_PACKAGE);
        //扫描sql配置文件
        Resource[] resources = new PathMatchingResourcePatternResolver().getResources(ProjectConstant.ATS_ES_MAPPER_LOCATION);
        factoryBean.setMapperLocations(resources);
        // 配置mybatis全局配置文件
        Resource resource = resourceLoader.getResource(ProjectConstant.CONFIG_LOCATION);
        factoryBean.setConfigLocation(resource);
        return factoryBean.getObject();
    }
}
