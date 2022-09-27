package sk.stuba.fei.uim.vsa.pr2.services;

import sk.stuba.fei.uim.vsa.pr2.entities.CarPark;
import sk.stuba.fei.uim.vsa.pr2.entities.CarParkFloor;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CarParkService extends AbstractService {

    private static final CarParkFloorService cpfs = new CarParkFloorService();

    public CarPark createCarPark(String name, String address, Integer pricePerHour) {
        EntityManager em = emf.createEntityManager();
        CarPark carPark = new CarPark(name, address, pricePerHour);
        try {
            em.getTransaction().begin();
            em.persist(carPark);
            em.getTransaction().commit();
            em.getEntityManagerFactory().getCache().evictAll();
        } catch (Exception e) {
            em.close();
            return null;
        }
        em.close();
        return carPark;
    }

    public CarPark getCarPark(Long carParkId) {
        EntityManager em = emf.createEntityManager();
        try {
            CarPark carParkResult = em.find(CarPark.class, carParkId);
            if (carParkResult != null) {
                em.getEntityManagerFactory().getCache().evictAll();
                em.close();
                return carParkResult;
            }
        } catch (Exception e) {
            e.printStackTrace();
            em.getTransaction().rollback();
            em.close();
            return null;
        }
        em.close();
        return null;
    }

    public CarPark getCarPark(String carParkName) {
        EntityManager em = emf.createEntityManager();
        TypedQuery<CarPark> query = em.createNamedQuery("CarPark.findByName",CarPark.class);
        query.setParameter("name", carParkName);
        try {
            CarPark carParkResult = query.getSingleResult();
            if (carParkResult != null) {
                em.getEntityManagerFactory().getCache().evictAll();
                em.close();
                return carParkResult;
            }
        } catch (Exception e) {
            em.close();
            return null;
        }
        em.close();
        return null;
    }

    public List<CarPark> getCarParks() {
        EntityManager em = emf.createEntityManager();
        TypedQuery<CarPark> query = em.createNamedQuery("CarPark.findAll", CarPark.class);
        try {
            List <CarPark> carParkResult = query.getResultList();
            if (!(carParkResult.isEmpty())) {
                em.getEntityManagerFactory().getCache().evictAll();
                em.close();
                return new ArrayList<>(carParkResult);
            }
        } catch (Exception e) {
            em.close();
            return Collections.emptyList();
        }
        em.close();
        return Collections.emptyList();
    }

    public CarPark updateCarPark(CarPark carPark) {
        EntityManager em = emf.createEntityManager();
        CarPark carParkUpdated = carPark;
        CarPark carParkResult = getCarPark(carParkUpdated.getId());
        try {
            if (carParkResult != null) {
                carParkUpdated.setCarParkFloors(carParkResult.getCarParkFloors());
                if (carParkUpdated.getName() == null)
                    carParkUpdated.setName(carParkResult.getName());
                if (carParkUpdated.getAddress() == null)
                    carParkUpdated.setAddress(carParkResult.getAddress());
                if (carParkUpdated.getPricePerHour() == null)
                    carParkUpdated.setPricePerHour(carParkResult.getPricePerHour());

                em.getTransaction().begin();
                em.merge(carParkUpdated);
                em.getTransaction().commit();
                return carParkUpdated;
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    public CarPark deleteCarPark(Long carParkId) {
        EntityManager em = emf.createEntityManager();
        CarPark carPark = getCarPark(carParkId);
        try {
            if (carPark != null) {
                for (CarParkFloor carParkFloor : carPark.getCarParkFloors()) {
                    cpfs.deleteCarParkFloor(carParkFloor.getFloorIdentifier().getCarParkId(), carParkFloor.getFloorIdentifier().getFloorIdentifierId());
                }
                em.getTransaction().begin();
                CarPark carParkTmp = em.merge(carPark);
                em.remove(carParkTmp);
                em.getTransaction().commit();
                return carPark;
            }
        }
        catch (Exception e){
            return null;
        }
        return null;
    }
}
