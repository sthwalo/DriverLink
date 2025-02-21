# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/driverlink_db
spring.datasource.username=${DB_USERNAME:your_username}
spring.datasource.password=${DB_PASSWORD:your_password}
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA/Hibernate Properties
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

# JWT Configuration
jwt.secret=${JWT_SECRET:your-256-bit-secret}
jwt.expiration=${JWT_EXPIRATION:86400000}

# Server Configuration
server.port=8080
server.address=0.0.0.0
server.servlet.context-path=/api

# Spring Security (temporary basic config)
spring.security.user.name=${ADMIN_USERNAME:admin}
spring.security.user.password=${ADMIN_PASSWORD:changeme}

# Logging
logging.level.org.springframework=INFO
logging.level.org.springframework.security=DEBUG
logging.level.com.driverlink=DEBUG
logging.pattern.console=%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n

# CORS Configuration
cors.allowed-origins=http://localhost:5173,http://localhost:4173
cors.allowed-methods=GET,POST,PUT,DELETE,OPTIONS
cors.allowed-headers=Authorization,Content-Type
cors.exposed-headers=Authorization
cors.allow-credentials=true