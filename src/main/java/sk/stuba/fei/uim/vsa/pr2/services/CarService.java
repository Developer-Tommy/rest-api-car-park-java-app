package sk.stuba.fei.uim.vsa.pr2.services;

import sk.stuba.fei.uim.vsa.pr2.entities.*;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CarService extends AbstractService {

    private static final UserService us = new UserService();
    private static final CarTypeService cts = new CarTypeService();
    private static final ReservationService rs = new ReservationService();

    public Car createCar(Long userId, String brand, String model, String colour, String vehicleRegistrationPlate) {
        EntityManager em = emf.createEntityManager();
        User user = us.getUser(userId);
        CarType carType = cts.getCarType("Gasoline");
        try {
            if (user != null) {
                if (carType == null){
                    cts.createCarType("Gasoline");
                    carType = cts.getCarType("Gasoline");
                }
                Car car = new Car(user, brand, model, colour, vehicleRegistrationPlate, carType);
                em.getTransaction().begin();
                em.persist(car);
                em.getTransaction().commit();
                em.getEntityManagerFactory().getCache().evictAll();
                em.close();
                return car;
            }

        } catch (Exception e) {
            em.close();
            return null;
        }
        em.close();
        return null;
    }

    public Car createCar(Long userId, String brand, String model, String colour, String vehicleRegistrationPlate, Long carTypeId) {
        EntityManager em = emf.createEntityManager();
        try{
            User user = us.getUser(userId);
            CarType carType = cts.getCarType(carTypeId);

            if (user != null) {
                if (carType == null) {
                    em.close();
                    return null;
                }

                Car car = new Car(user, brand, model, colour, vehicleRegistrationPlate, carType);
                em.getTransaction().begin();
                em.persist(car);
                em.getTransaction().commit();
                em.getEntityManagerFactory().getCache().evictAll();
                em.close();
                return car;
            }
        }
        catch (Exception e){
            em.close();
            return null;
        }
        em.close();
        return null;
    }

    public Car getCar(Long carId) {
        EntityManager em = emf.createEntityManager();
        try {
            Car carResult = em.find(Car.class, carId);
            if (carResult != null) {
                em.getEntityManagerFactory().getCache().evictAll();
                em.close();
                return carResult;
            }
        } catch (Exception e) {
            em.close();
            return null;
        }
        em.close();
        return null;
    }

    public Car getCar(String vehicleRegistrationPlate) {
        EntityManager em = emf.createEntityManager();
        TypedQuery<Car> query = em.createNamedQuery("Car.findByVehicleRegistrationPlate",Car.class);
        query.setParameter("vehicleRegistrationPlate",vehicleRegistrationPlate);
        try {
            Car carResult = query.getSingleResult();
            if (carResult != null) {
                em.getEntityManagerFactory().getCache().evictAll();
                em.close();
                return carResult;
            }
        } catch (Exception e) {
            em.close();
            return null;
        }
        em.close();
        return null;
    }

    public List<Car> getCars(Long userId) {
        EntityManager em = emf.createEntityManager();
        TypedQuery <Car> query = em.createNamedQuery("Car.findByUserId", Car.class);
        query.setParameter("id", userId);
        try {
            List <Car> carResult = query.getResultList();
            if (!(carResult.isEmpty())) {
                em.getEntityManagerFactory().getCache().evictAll();
                em.close();
                return new ArrayList<>(carResult);
            }
        } catch (Exception e) {
            em.close();
            return Collections.emptyList();
        }
        em.close();
        return Collections.emptyList();
    }

    public List<Car> getCars() {
        EntityManager em = emf.createEntityManager();
        TypedQuery<Car> query = em.createNamedQuery("Car.findAll", Car.class);
        try {
            List <Car> carResult = query.getResultList();
            if (!(carResult.isEmpty())) {
                em.getEntityManagerFactory().getCache().evictAll();
                em.close();
                return new ArrayList<>(carResult);
            }
        } catch (Exception e) {
            em.close();
            return Collections.emptyList();
        }
        em.close();
        return Collections.emptyList();
    }

    public Car updateCar(Car car) {
        EntityManager em = emf.createEntityManager();
        Car newCar = car;
        Car carResult = getCar(newCar.getId());
        User user = us.getUser(newCar.getUser().getId());
        try {
            if (carResult != null){
                newCar.setReservations(carResult.getReservations());
                if (newCar.getUser() == null)
                    newCar.setUser(carResult.getUser());
                else if (user == null) {
                    em.close();
                    return null;
                }
                if (newCar.getBrand() == null)
                    newCar.setBrand(carResult.getBrand());
                if (newCar.getModel() == null)
                    newCar.setModel(carResult.getModel());
                if (newCar.getColour() == null)
                    newCar.setColour(carResult.getColour());
                if (newCar.getVehicleRegistrationPlate() == null)
                    newCar.setVehicleRegistrationPlate(carResult.getVehicleRegistrationPlate());
                if (newCar.getCarType() == null)
                    newCar.setCarType(carResult.getCarType());

                em.getTransaction().begin();
                em.merge(newCar);
                em.getTransaction().commit();
                em.close();
                return newCar;
            }
        } catch (Exception e) {
            em.close();
            return null;
        }
        em.close();
        return null;
    }

    public Car deleteCar(Long carId) {
        EntityManager em = emf.createEntityManager();
        Car carResult = getCar(carId);
        try{
            if (carResult != null) {
                for (Reservation reservation : carResult.getReservations()) {
                    reservation = rs.endReservation(reservation.getId());
                    reservation.setCar(null);
                    rs.updateReservation(reservation);

                }
                if (!em.contains(carResult)){
                    carResult = em.merge(carResult);
                }

                em.getTransaction().begin();
                em.remove(carResult);
                em.getTransaction().commit();
                em.getEntityManagerFactory().getCache().evictAll();
                em.close();
                return carResult;
            }

        } catch (Exception e){
            em.close();
            return null;
        }
        em.close();
        return null;
    }
}
