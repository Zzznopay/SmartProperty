package com.smart.property.common.mybatis.config;

import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;

/**
 * 数据源配置
 *
 * <p>注意：此处自定义 SqlSessionFactory 会使 MybatisPlusAutoConfiguration
 * 因 @ConditionalOnMissingBean 退避，因此必须在 GlobalConfig 上显式挂载
 * {@link MetaObjectHandler}（AutoFillHandler），否则
 * createTime / updateTime / createBy 等审计字段自动填充不会生效。</p>
 *
 * @author zzz
 * @since 2026-07-25
 */
@Configuration
public class DataSourceConfig {

    @Autowired(required = false)
    private MybatisPlusInterceptor mybatisPlusInterceptor;

    @Bean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource,
                                               ObjectProvider<MetaObjectHandler> metaObjectHandlerProvider) throws Exception {
        MybatisSqlSessionFactoryBean factory = new MybatisSqlSessionFactoryBean();
        factory.setDataSource(dataSource);
        factory.setMapperLocations(new PathMatchingResourcePatternResolver()
                .getResources("classpath*:mapper/**/*.xml"));
        if (mybatisPlusInterceptor != null) {
            factory.setPlugins(mybatisPlusInterceptor);
        }
        MetaObjectHandler metaObjectHandler = metaObjectHandlerProvider.getIfAvailable();
        if (metaObjectHandler != null) {
            GlobalConfig globalConfig = new GlobalConfig();
            globalConfig.setMetaObjectHandler(metaObjectHandler);
            factory.setGlobalConfig(globalConfig);
        }
        return factory.getObject();
    }

    @Bean
    public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}
