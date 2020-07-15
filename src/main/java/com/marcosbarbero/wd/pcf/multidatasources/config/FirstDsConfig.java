/*
 * Copyright 2012-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.marcosbarbero.wd.pcf.multidatasources.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

import java.util.Properties;


/**
 * @author Marcos Barbero
 * Parameter 0 of method firstEntityManagerFactory in com.marcosbarbero.wd.pcf.multidatasources.config.FirstDsConfig required a bean of type 'org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder' that could not be found.
 *    2020-07-14T15:47:27.92-0500 [APP/PROC/WEB/0] OUT Action:
 *    2020-07-14T15:47:27.92-0500 [APP/PROC/WEB/0] OUT Consider defining a bean of type 'org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder' in your configuration.

 */
@Configuration
@EnableJpaRepositories(
        entityManagerFactoryRef = "firstEntityManagerFactory",
        transactionManagerRef = "firstTransactionManager",
        basePackages = "com.marcosbarbero.wd.pcf.multidatasources.first.repository"
)
@EnableTransactionManagement
public class FirstDsConfig {

    // https://stackoverflow.com/questions/48416927/spring-boot-required-a-bean-named-entitymanagerfactory-that-could-not-be-foun
    // https://stackoverflow.com/questions/44516942/consider-defining-a-bean-named-entitymanagerfactory-in-your-configuration-spri

    // https://stackoverflow.com/questions/32285613/java-lang-illegalargumentexception-no-persistenceprovider-specified-in-entityma
    @Primary
    @Bean(name = "firstEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            final @Qualifier("first-db") DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
        emf.setDataSource(dataSource);
        emf.setPackagesToScan("com.marcosbarbero.wd.pcf.multidatasources.first.domain");
        // https://stackoverflow.com/questions/36190331/hibernateexception-access-to-dialectresolutioninfo-cannot-be-null-when-hiberna
        //emf.afterPropertiesSet();
        emf.setJpaProperties(additionalProperties());

        // https://stackoverflow.com/questions/52326477/no-persistenceprovider-specified-in-entitymanagerfactory-and-chosen-persistence
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setDatabasePlatform("org.hibernate.dialect.MySQL8Dialect");
        emf.setJpaVendorAdapter(vendorAdapter);

        emf.setPersistenceUnitName("firstDB");
        //emf.setJpaPropertyMap(singletonMap("hibernate.hbm2ddl.auto", "create-drop"));
        return emf;
    }

    Properties additionalProperties() {
        Properties properties = new Properties();
        properties.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQL8Dialect");
        properties.setProperty("hibernate.hbm2ddl.auto", "create-drop");

        return properties;
    }

    @Primary // https://stackoverflow.com/questions/52401041/spring-boot-multiple-databse-no-qualifying-bean-of-type-entitymanagerfactorybu
    @Bean(name = "firstTransactionManager")
    public PlatformTransactionManager firstTransactionManager(@Qualifier("firstEntityManagerFactory")
                                                              EntityManagerFactory firstEntityManagerFactory) {
        return new JpaTransactionManager(firstEntityManagerFactory);
    }


}
