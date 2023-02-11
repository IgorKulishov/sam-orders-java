package ecommerce.connection;

import org.hibernate.SessionFactory;

public interface HibernateConnectionPool {
    public SessionFactory createSessionFactory();
}
