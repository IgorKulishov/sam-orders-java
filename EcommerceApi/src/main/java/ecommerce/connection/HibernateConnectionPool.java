package ecommerce.connection;

import ecommerce.dao.User;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.service.ServiceRegistry;
import org.postgresql.gss.GSSOutputStream;

import java.util.HashMap;
import java.util.Map;


public class HibernateConnectionPool {
    public SessionFactory createSessionFactory() {
//        Tutorials:
//        1. https://www.baeldung.com/java-aws-lambda-hibernate
        Map<String, String> settings = new HashMap<>();
        settings.put("connection.driver_class", "org.postgresql.Driver");
        settings.put("dialect", "org.hibernate.dialect.PostgreSQLDialect");
        settings.put("hibernate.connection.url", getJDBCUrl());
        settings.put("hibernate.connection.username", System.getenv("RDS_USERNAME"));
        settings.put("hibernate.connection.password", System.getenv("RDS_PASSWORD"));
        settings.put("hibernate.current_session_context_class", "thread");
        settings.put("hibernate.show_sql", "true");
        settings.put("hibernate.format_sql", "true");

        ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().applySettings(settings).build();
        MetadataSources metadataSources = new MetadataSources(serviceRegistry);
//        TODO: create class and add .addAnnotatedClass(Item.class)
        Metadata metadata = metadataSources
                .addAnnotatedClass(User.class)
                .buildMetadata();
        SessionFactory sessionFactory = metadata.getSessionFactoryBuilder().build();
        System.out.println(sessionFactory);
        return sessionFactory;
    }
    private String getJDBCUrl() {
        String JDBC_PREFIX = "jdbc:postgresql://";
        String dbEndpoint = System.getenv("RDS_ENDPOINTS");
        String port = System.getenv("RDS_PORT");
        String dbName = System.getenv("RDS_DB_NAME");
        return JDBC_PREFIX + dbEndpoint + ":" + port + "/" + dbName;
    }
}
