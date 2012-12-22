package jtp2.ss.server.data;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class Database {

    private static final SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        try {
            // Create the SessionFactory from hibernate.cfg.xml
            return new Configuration().configure().buildSessionFactory();
        } catch (Throwable e) {
            // Make sure you log the exception, as it might be swallowed
            System.err.println("Initial SessionFactory creation failed." + e);
            throw new ExceptionInInitializerError(e);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static void main(String[] args) {
        Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();

        User user = new User();
        user.setLogin("blah");
        session.save(user);

        session.getTransaction().commit();
        session = getSessionFactory().getCurrentSession();
        session.beginTransaction();
        User u = (User) session.get(User.class, 1L);
        System.out.println("Login: " + u.getLogin());
    }
}
