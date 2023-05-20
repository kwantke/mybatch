package kr.or.mybatch.common.datasourceConfig;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableJpaRepositories(basePackages = {"kr.or.mybatch.repository"}
,entityManagerFactoryRef =  "firstJpaEntityManagerFactory"
,transactionManagerRef = "firstTransactionManager")
@MapperScan(value="kr.or.mybatch.mapper.first", sqlSessionFactoryRef="firstSqlSessionFactory", sqlSessionTemplateRef = "firstSqlSessionTemplate")
@EnableTransactionManagement
public class FirstDataSourceConfig {
    private boolean isJndiDataSource;
    private String jndiName;


    public FirstDataSourceConfig(@Value("${db.datasource.using-jndi}") boolean isJndiDataSource
            ,@Value("${db.datasource.jndi-name}")String jndiName){
        this.isJndiDataSource = isJndiDataSource;
        this.jndiName = jndiName;
    }


    @Primary
    @Bean(name="firstDataSource")
    @ConfigurationProperties(prefix="first.datasource.hikari")
    public DataSource firstDataSource(){
        if(isJndiDataSource){
            JndiDataSourceLookup lookup = new JndiDataSourceLookup();
            return lookup.getDataSource(jndiName);
        } else {
            //application.yml에서 정의된 DB 연결 정보 빌드
            return DataSourceBuilder.create()
                    .build();
        }
    }

    @Primary
    @Bean(name="firstSqlSessionFactory")
    public SqlSessionFactory firstSqlSessionFactory(@Qualifier("firstDataSource")DataSource firstDataSource
    , ApplicationContext applicationContext) throws Exception {
        //세션 생성 시, 빌드된 DataSource를 세팅하고 SQL문을 관리할 mapper.xml 경로를 알려준다.
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(firstDataSource);
        bean.setMapperLocations(applicationContext.getResources("classpath:mybatis/mapper/first/**.xml"));

        return bean.getObject();
    }

    @Primary
    @Bean(name="firstSqlSessionTemplate")
    public SqlSessionTemplate firstSqlSessionTemplate(@Qualifier("firstSqlSessionFactory")SqlSessionFactory firstSqlSessionFactory) throws Exception{
        return new SqlSessionTemplate(firstSqlSessionFactory);
    }

    @Primary
    @Bean(name="firstJpaEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean firstJpaEntityManager(
            EntityManagerFactoryBuilder builder,
            @Qualifier("firstDataSource") DataSource firstDataSource){
        //entity 경로
        return builder.dataSource(firstDataSource).packages("kr.or.mybatch.model.entity.first").build();
    }

    @Primary
    @Bean(name = "firstTransactionManager")
    public JpaTransactionManager transactionManager(
            @Qualifier("firstJpaEntityManagerFactory") LocalContainerEntityManagerFactoryBean mfBean
    ) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory( mfBean.getObject() );
        return transactionManager;
    }

}

