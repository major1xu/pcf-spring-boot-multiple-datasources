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

import com.marcosbarbero.wd.pcf.multidatasources.Application;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.cloud.Cloud;
import org.springframework.cloud.CloudFactory;
import org.springframework.cloud.config.java.AbstractCloudConfig;
import org.springframework.cloud.config.java.ServiceScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

/**
 * @author Marcos Barbero
 */
@Configuration
//@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
public class CloudConfig extends AbstractCloudConfig
{
    // https://docs.cloudfoundry.org/buildpacks/java/configuring-service-connections/spring-service-bindings.html

    private static final Logger logger = LoggerFactory.getLogger(Application.class);
    @Primary
    @Bean(name = "first-db")
    public DataSource firstDataSource() {
        // https://docs.cloudfoundry.org/buildpacks/java/configuring-service-connections/spring-service-bindings.html
        CloudFactory cloudFactory = new CloudFactory();
        Cloud cloud = cloudFactory.getCloud();
        DataSource firstDataSource = ((Cloud) cloud).getServiceConnector("first-db-gcp", DataSource.class, null);
        logger.info(firstDataSource.toString());
        return firstDataSource;
    }

    @Bean(name = "second-db")
    public DataSource secondDataSource() {
        CloudFactory cloudFactory = new CloudFactory();
        Cloud cloud = cloudFactory.getCloud();
        DataSource secondDataSource = ((Cloud) cloud).getServiceConnector("second-db-gcp", DataSource.class, null);
        logger.info(secondDataSource.toString());
        return secondDataSource;
    }
}

