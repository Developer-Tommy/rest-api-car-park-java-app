package sk.stuba.fei.uim.vsa.pr2.services;

import sk.stuba.fei.uim.vsa.pr2.entities.Car;
import sk.stuba.fei.uim.vsa.pr2.entities.CarType;
import sk.stuba.fei.uim.vsa.pr2.entities.ParkingSpot;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.*;

public class CarTypeService extends AbstractService {

    private static final CarService cs = new CarService();
    private static final ParkingSpotService pss = new ParkingSpotService();

    public CarType createCarType(String name) {
        EntityManager em = emf.createEntityManager();
        CarType carType = new CarType(name);
        try{
            em.getTransaction().begin();
            em.persist(carType);
            em.getTransaction().commit();
            em.getEntityManagerFactory().getCache().evictAll();
        }
        catch (Exception e ){
            em.close();
            return null;
        }
        em.close();
        return carType;
    }

    public List<CarType> getCarTypes() {
        EntityManager em = emf.createEntityManager();
        TypedQuery<CarType> query = em.createNamedQuery("CarType.findAll", CarType.class);
        try{
            List<CarType> carTypes = query.getResultList();
            if(!(carTypes.isEmpty())) {
                em.getEntityManagerFactory().getCache().evictAll();
                em.close();
                return new ArrayList<>(carTypes);
            }
        }
        catch (Exception e){
            em.close();
            return Collections.emptyList();
        }
        em.close();
        return Collections.emptyList();
    }

    public CarType getCarType(Long carTypeId) {
        EntityManager em = emf.createEntityManager();
        CarType carType = em.find(CarType.class, carTypeId);
        try{
            if (carType != null){
                em.getEntityManagerFactory().getCache().evictAll();
                em.close();
                return carType;
            }
        }
        catch (Exception e){
            em.close();
            return null;
        }
        em.close();
        return null;
    }

    public CarType getCarType(String name) {
        EntityManager em = emf.createEntityManager();
        TypedQuery<CarType> query = em.createNamedQuery("CarType.findByName", CarType.class);
        query.setParameter("name", name);
        try {
            CarType carType = query.getSingleResult();
            if (carType != null) {
                em.getEntityManagerFactory().getCache().evictAll();
                em.close();
                return carType;
            }
        }
        catch (Exception e){
            em.close();
            return null;
        }
        em.close();
        return null;
    }

    public CarType deleteCarType(Long carTypeId) {
        EntityManager em = emf.createEntityManager();
        CarType carType = getCarType(carTypeId);
        try{
            if (Objects.equals(carType.getName(), "Gasoline")) {
                em.close();
                return carType;
            }

            for (Car car: carType.getCars()){
                car.setCarType(getCarType("Gasoline"));
                cs.updateCar(car);
            }
            for (ParkingSpot parkingSpot: carType.getParkingSpots()){
                parkingSpot.setCarType(getCarType("Gasoline"));
                pss.updateParkingSpot(parkingSpot);
            }

            em.getTransaction().begin();
            CarType carTypeTmp = em.merge(carType);
            em.remove(carTypeTmp);
            em.getTransaction().commit();
            em.close();
            return carType;
        }
        catch (Exception e){
            em.close();
            return null;
        }
    }
}
