package com.example.Sale.config;



import javax.sql.DataSource;

import com.example.Sale.models.User;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.Collections;


@Configuration
@EnableJpaRepositories(basePackages = "com.example.Sale.repositories")
@EnableTransactionManagement
public class JpaConfig {

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl("jdbc:postgresql://localhost:5433/db1");
        dataSource.setUsername("postgres");
        dataSource.setPassword("123");
        return dataSource;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setGenerateDdl(true);

        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setJpaVendorAdapter(vendorAdapter);
        factory.setPackagesToScan("com.example.Sale.models");
        factory.setDataSource(dataSource());
        factory.getJpaPropertyMap().put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");

        // Добавьте аннотацию @EnableJpaRepositories и укажите пакет с репозиториями
        factory.setJpaPropertyMap(Collections.singletonMap("javax.persistence.validation.mode", "none"));
        factory.setJpaPropertyMap(Collections.singletonMap("hibernate.validator.apply_to_ddl", "false"));
        factory.setJpaPropertyMap(Collections.singletonMap("hibernate.validator.autoregister_listeners", "false"));
        factory.setJpaPropertyMap(Collections.singletonMap("hibernate.validator.fail_fast", "false"));
        factory.setJpaPropertyMap(Collections.singletonMap("hibernate.validator.apply_to_ddl", "false"));

        return factory;
    }

    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {

        JpaTransactionManager txManager = new JpaTransactionManager();
        txManager.setEntityManagerFactory(entityManagerFactory);
        return txManager;
    }

}
