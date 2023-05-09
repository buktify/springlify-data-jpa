package org.buktify.springlify.configuration;

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
import org.springframework.orm.jpa.JpaTransactionManager;

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
                put("hibernate.connection.driver", databaseSettings.getDriver());
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
