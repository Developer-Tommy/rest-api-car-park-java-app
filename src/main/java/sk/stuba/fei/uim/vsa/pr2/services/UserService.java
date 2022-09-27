package sk.stuba.fei.uim.vsa.pr2.services;

import sk.stuba.fei.uim.vsa.pr2.entities.Car;
import sk.stuba.fei.uim.vsa.pr2.entities.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UserService extends AbstractService {

    private static final CarService cs = new CarService();

    public User createUser(String firstname, String lastname, String email) {
        EntityManager em = emf.createEntityManager();
        User user = new User(firstname, lastname, email);
        user.setCars(Collections.emptyList());
        try{
            em.getTransaction().begin();
            em.persist(user);
            em.getTransaction().commit();
            em.getEntityManagerFactory().getCache().evictAll();
        }
        catch (Exception e){
            em.close();
            return null;
        }
        em.close();
        return user;
    }

    public User getUser(Long userId) {
        EntityManager em = emf.createEntityManager();
        try{
            User userResult =  em.find(User.class,userId);
            if (userResult != null){
                em.getEntityManagerFactory().getCache().evictAll();
                em.close();
                return userResult;
            }
        }
        catch (Exception e){
            em.close();
            return null;

        }
        em.close();
        return null;
    }

    public User getUser(String email) {
        EntityManager em = emf.createEntityManager();
        TypedQuery<User> query = em.createNamedQuery("User.findByEmail", User.class);
        query.setParameter("email", email);
        try {
            User userResult = query.getSingleResult();
            if (userResult != null) {
                em.getEntityManagerFactory().getCache().evictAll();
                em.close();
                return userResult;
            }
        } catch (Exception e) {
            em.close();
            return null;
        }
        em.close();
        return null;
    }

    public List<User> getUsers() {
        EntityManager em = emf.createEntityManager();
        TypedQuery<User> query = em.createNamedQuery("User.findAll", User.class);
        try {
            List<User> userResult = query.getResultList();
            if (!(userResult.isEmpty())){
                em.getEntityManagerFactory().getCache().evictAll();
                em.close();
                return new ArrayList<>(userResult);
            }
        }
        catch (Exception e){
            em.close();
            return Collections.emptyList();
        }
        em.close();
        return Collections.emptyList();
    }

    public User updateUser(User user) {
        EntityManager em = emf.createEntityManager();
        User newUser = user;
        User userResult = getUser(newUser.getId());
        try {
            if (userResult != null) {
                newUser.setCars(userResult.getCars());
                if (newUser.getFirstname() == null)
                    newUser.setFirstname(userResult.getFirstname());
                if (newUser.getLastname() == null)
                    newUser.setLastname(userResult.getLastname());
                if (newUser.getEmail() == null)
                    newUser.setEmail(userResult.getEmail());
                em.getTransaction().begin();
                em.merge(newUser);
                em.getTransaction().commit();
                return newUser;
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    public User deleteUser(Long userId) {
        EntityManager em = emf.createEntityManager();
        User userResult = getUser(userId);
        try {
            if (userResult != null){
                for (Car car : userResult.getCars()) {
                    cs.deleteCar(car.getId());
                }
                em.getTransaction().begin();
                User userTmp = em.merge(userResult);
                em.remove(userTmp);
                em.getTransaction().commit();
                return userResult;
            }
        }
        catch (Exception e){
            return null;
        }
        return null;
    }

}
