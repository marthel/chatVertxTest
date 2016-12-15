package DB.DAL;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

class DbContext {
    final static EntityManagerFactory emf = Persistence.createEntityManagerFactory("test");
}
