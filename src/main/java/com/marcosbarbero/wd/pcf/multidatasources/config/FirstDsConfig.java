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

import com.marcosbarbero.wd.pcf.multidatasources.first.repository.FirstRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import static java.util.Collections.singletonMap;

/**
 * @author Marcos Barbero
 * Parameter 0 of method firstEntityManagerFactory in com.marcosbarbero.wd.pcf.multidatasources.config.FirstDsConfig required a bean of type 'org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder' that could not be found.
 *    2020-07-14T15:47:27.92-0500 [APP/PROC/WEB/0] OUT Action:
 *    2020-07-14T15:47:27.92-0500 [APP/PROC/WEB/0] OUT Consider defining a bean of type 'org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder' in your configuration.

 */
@Configuration
//@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
@EnableJpaRepositories(
        entityManagerFactoryRef = "firstEntityManagerFactory",
        transactionManagerRef = "firstTransactionManager",
        basePackages = "com.marcosbarbero.wd.pcf.multidatasources.first.repository"
)
@EnableTransactionManagement
public class FirstDsConfig {
/*
    @Autowired
    private org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder entityManagerFactoryBean;
*/
    @Primary
    @Bean(name = "firstEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean firstEntityManagerFactory(final EntityManagerFactoryBuilder builder,
                                                                            final @Qualifier("first-db") DataSource dataSource) {
/*

https://stackoverflow.com/questions/44516942/consider-defining-a-bean-named-entitymanagerfactory-in-your-configuration-spri

        LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactoryBean.setDataSource(dataSource);
        entityManagerFactoryBean.setPackagesToScan("com.marcosbarbero.wd.pcf.multidatasources.first.domain");
        //additional config of factory
        entityManagerFactoryBean.setPersistenceUnitName("firstDb");
        entityManagerFactoryBean.setJpaPropertyMap(singletonMap("hibernate.hbm2ddl.auto", "create-drop"));
        return entityManagerFactoryBean;
*/
        return builder
                .dataSource(dataSource)
                .packages("com.marcosbarbero.wd.pcf.multidatasources.first.domain")
                .persistenceUnit("firstDb")
                .properties(singletonMap("hibernate.hbm2ddl.auto", "create-drop"))
                .build();
    }

    @Primary // https://stackoverflow.com/questions/52401041/spring-boot-multiple-databse-no-qualifying-bean-of-type-entitymanagerfactorybu
    @Bean(name = "firstTransactionManager")
    public PlatformTransactionManager firstTransactionManager(@Qualifier("firstEntityManagerFactory")
                                                              EntityManagerFactory firstEntityManagerFactory) {
        return new JpaTransactionManager(firstEntityManagerFactory);
    }
}
