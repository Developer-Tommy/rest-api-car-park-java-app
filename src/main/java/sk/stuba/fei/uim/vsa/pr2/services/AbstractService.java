package sk.stuba.fei.uim.vsa.pr2.services;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public abstract class AbstractService {

    protected EntityManagerFactory emf;

    protected AbstractService() {
        this.emf = Persistence.createEntityManagerFactory("default");
    }

    protected void close() {
        emf.close();
    }
}
