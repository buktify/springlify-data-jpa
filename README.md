# Springlify Data JPA

Данная библиотека позволяет использовать Spring для написания Spigot плагинов.

В отличие от Springlify, тут есть встроенная поддержка баз данных.

После первого запуска плагина создастся файл `database.yml`, где вы сможете полностью настроить Hibernate.

Чтобы иметь возможность использовать эту библиотеку, нужно просто подключить её как implementation.
Так-же, не забудьте подключить сам стартер Spring'a (куда-же без него)

```groovy
plugins {
    id 'java'
    id 'org.springframework.boot' version '3.0.4'
    id 'io.spring.dependency-management' version '1.1.0'
}

repositories {
    maven {
        name = 'buktify-repo'
        url = 'https://repo.crazylegend.space/public'
    }
}

dependencies {
    compileOnly 'org.buktify:springlify-data-jpa:1.0.7'
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
}
```

Использование идентично Springlify. Ознакомьтесь с документацией [тут](https://github.com/buktify/springlify)