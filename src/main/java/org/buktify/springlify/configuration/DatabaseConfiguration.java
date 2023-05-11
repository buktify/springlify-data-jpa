package org.buktify.springlify.configuration;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import lombok.SneakyThrows;
import org.bukkit.plugin.java.JavaPlugin;
import org.buktify.configurate.ConfigurationService;
import org.buktify.springlify.configuration.settings.DatabaseSettings;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class DatabaseConfiguration {

    @Bean
    @SneakyThrows
    public DatabaseSettings databaseSettings(@NotNull ConfigurationService configurationService) {
        return configurationService.getConfigurationPool().get(DatabaseSettings.class);
    }

    @Bean
    public ConfigurationService configurationService(@NotNull JavaPlugin javaPlugin) {
        ConfigurationService configurationService = new ConfigurationService()
                .rootDirectory(javaPlugin.getDataFolder())
                .registerConfigurations(DatabaseSettings.class);
        configurationService.apply();
        return configurationService;
    }

    @Bean
    public HashMap<Object, Object> hibernateProperties(@NotNull DatabaseSettings databaseSettings) {
        return new HashMap<>() {
            {
                put("hibernate.connection.driver_class", databaseSettings.getDriver());
                put("hibernate.connection.url", databaseSettings.getUrl());
                put("hibernate.connection.username", databaseSettings.getUsername());
                put("hibernate.connection.password", databaseSettings.getPassword());

                for (String option : databaseSettings.getAdditionalProperties()) {
                    String[] optionsArray = option.split(":");
                    put(optionsArray[0], optionsArray[1]);
                }
            }
        };
    }

    @Bean
    public DataSource dataSource(@NotNull DatabaseSettings databaseSettings){
        if(databaseSettings.isHikariEnabled()){
            HikariConfig hikariConfig = new HikariConfig();
            hikariConfig.setUsername(databaseSettings.getUsername());
            hikariConfig.setJdbcUrl(databaseSettings.getUrl());
            hikariConfig.setDriverClassName(databaseSettings.getDriver());
            hikariConfig.setPassword(databaseSettings.getPassword());
            return new HikariDataSource(hikariConfig);
        }
        return new SimpleDriverDataSource();
    }

    @Bean
    public EntityManagerFactory entityManagerFactory(@NotNull Map<Object, Object> hibernateProperties) {
        return new HibernatePersistenceProvider().createEntityManagerFactory("Dummy", hibernateProperties);
    }

    @Bean
    public JpaTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

    @Bean
    public EntityManager entityManager(EntityManagerFactory entityManagerFactory) {
        return entityManagerFactory.createEntityManager();
    }
}
