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
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableJpaRepositories(basePackages = {"kr.or.mybatch.repository"}// repository 경로
        ,entityManagerFactoryRef =  "secondJpaEntityManagerFactory"
        ,transactionManagerRef = "secondTransactionManager")
@MapperScan(value="kr.or.mybatch.mapper.second", sqlSessionFactoryRef="secondSqlSessionFactory")
@EnableTransactionManagement
public class SecondDataSourceConfig {
    private boolean isJndiDataSource;
    private String jndiName;


    public SecondDataSourceConfig(@Value("${db.datasource.using-jndi}") boolean isJndiDataSource
            , @Value("${db.datasource.jndi-name}")String jndiName){
        this.isJndiDataSource = isJndiDataSource;
        this.jndiName = jndiName;
    }

    @Bean(name="secondDataSource")
    @ConfigurationProperties(prefix="second.datasource.hikari")
    public DataSource secondDataSource(){
        if(isJndiDataSource){
            JndiDataSourceLookup lookup = new JndiDataSourceLookup();
            return lookup.getDataSource(jndiName);
        } else {
            //application.yml에서 정의된 DB 연결 정보 빌드
            return DataSourceBuilder.create()
                    .build();
        }
    }

    @Bean(name="secondSqlSessionFactory")
    public SqlSessionFactory secondSqlSessionFactory(@Qualifier("secondDataSource")DataSource secondDataSource
            , ApplicationContext applicationContext) throws Exception {
        //세션 생성 시, 빌드된 DataSource를 세팅하고 SQL문을 관리할 mapper.xml 경로를 알려준다.
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(secondDataSource);
        bean.setMapperLocations(applicationContext.getResources("classpath:mybatis/mapper/second/**.xml"));

        return bean.getObject();
    }

    @Bean(name="secondSqlSessionTemplate")
    public SqlSessionTemplate secondSqlSessionTemplate(SqlSessionFactory secondSqlSessionFactory) throws Exception{
        return new SqlSessionTemplate(secondSqlSessionFactory);
    }

    @Bean(name="secondJpaEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean secondJpaEntityManager(
            EntityManagerFactoryBuilder builder,
            @Qualifier("secondDataSource") DataSource secondDataSource){

        return builder.dataSource(secondDataSource).packages("kr.or.mybatch.model.entity.second").build();
    }

    @Bean(name = "secondTransactionManager")
    public JpaTransactionManager transactionManager(
            @Qualifier("secondJpaEntityManagerFactory") LocalContainerEntityManagerFactoryBean mfBean
    ) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory( mfBean.getObject() );
        return transactionManager;
    }
}
