package sk.stuba.fei.uim.vsa.pr2.services;

import sk.stuba.fei.uim.vsa.pr2.entities.CarParkFloor;
import sk.stuba.fei.uim.vsa.pr2.entities.CarParkFloorId;
import sk.stuba.fei.uim.vsa.pr2.entities.ParkingSpot;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CarParkFloorService extends AbstractService {

    private static final CarParkService cps = new CarParkService();
    private static final ParkingSpotService pss = new ParkingSpotService();

    public CarParkFloor createCarParkFloor(Long carParkId, String floorIdentifier) {
        EntityManager em = emf.createEntityManager();
        CarParkFloorId carParkFloorId = new CarParkFloorId(carParkId,floorIdentifier);
        CarParkFloor carParkFloor = new CarParkFloor(carParkFloorId, cps.getCarPark(carParkId));
        try {
            carParkFloor.setCarPark(cps.getCarPark(carParkId));
            if (carParkFloor.getCarPark() != null){
                em.getTransaction().begin();
                em.persist(carParkFloor);
                em.getTransaction().commit();
                em.getEntityManagerFactory().getCache().evictAll();
                em.close();
                return carParkFloor;
            }
        } catch (Exception e) {
            em.close();
            return null;
        }
        em.close();
        return null;
    }

    public CarParkFloor getCarParkFloor(Long carParkId, String floorIdentifier) {
        EntityManager em = emf.createEntityManager();
        CarParkFloorId carParkFloorId = new CarParkFloorId(carParkId,floorIdentifier);
        try {
            CarParkFloor carParkFloorResult = em.find(CarParkFloor.class, carParkFloorId);
            if (carParkFloorResult != null) {
                //em.refresh(carParkFloorResult);
                em.getEntityManagerFactory().getCache().evictAll();
                em.close();
                return carParkFloorResult;
            }

        } catch (Exception e) {
            em.close();
            return null;
        }
        em.close();
        return null;
    }

    public List<CarParkFloor> getCarParkFloors(Long carParkId) {
        EntityManager em = emf.createEntityManager();
        TypedQuery<CarParkFloor> query = em.createNamedQuery("CarParkFloor.findByFloorIdentifier_CarParkId",CarParkFloor.class);
        query.setParameter("carParkId", carParkId);
        try {
            List <CarParkFloor> carParkFloorResult = query.getResultList();
            if (!(carParkFloorResult.isEmpty())) {
                em.getEntityManagerFactory().getCache().evictAll();
                em.close();
                return new ArrayList<>(carParkFloorResult);
            }
        } catch (Exception e) {
            em.close();
            return Collections.emptyList();
        }
        em.close();
        return Collections.emptyList();
    }

    public CarParkFloor deleteCarParkFloor(Long carParkId, String floorIdentifier) {
        EntityManager em = emf.createEntityManager();
        CarParkFloor carParkFloorResult = getCarParkFloor(carParkId, floorIdentifier);
        try {
            if (carParkFloorResult != null) {
                for (ParkingSpot parkingSpot : carParkFloorResult.getParkingSpots()){
                    pss.deleteParkingSpot(parkingSpot.getId());
                }
                em.getTransaction().begin();
                CarParkFloor carParkFloorTmp = em.merge(carParkFloorResult);
                em.remove(carParkFloorTmp);
                em.getTransaction().commit();
                return carParkFloorResult;
            }

        } catch (Exception e) {
            return null;
        }
        return null;
    }
}
