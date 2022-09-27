package sk.stuba.fei.uim.vsa.pr2.services;

import sk.stuba.fei.uim.vsa.pr2.entities.*;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.*;

public class ParkingSpotService extends AbstractService{

    private static final CarParkFloorService cpfs = new CarParkFloorService();
    private static final CarTypeService cts = new CarTypeService();
    private static final ReservationService rs = new ReservationService();

    public ParkingSpot createParkingSpot(Long carParkId, String floorIdentifier, String spotIdentifier) {
        EntityManager em = emf.createEntityManager();
        CarParkFloor carParkFloorResult = cpfs.getCarParkFloor(carParkId, floorIdentifier);
        CarType carType = cts.getCarType("Gasoline");
        try {
            if (carParkFloorResult != null) {
                if (carType == null){
                    cts.createCarType("Gasoline");
                    carType = cts.getCarType("Gasoline");
                }
                ParkingSpot parkingSpot = new ParkingSpot(spotIdentifier, carParkFloorResult, carType);
                em.getTransaction().begin();
                em.persist(parkingSpot);
                em.getTransaction().commit();
                em.getEntityManagerFactory().getCache().evictAll();
                em.close();
                return parkingSpot;
            }
        }
        catch (Exception e){
            em.close();
            return null;
        }
        em.close();
        return null;
    }

    public ParkingSpot createParkingSpot(Long carParkId, String floorIdentifier, String spotIdentifier, Long carTypeId) {
        EntityManager em = emf.createEntityManager();
        try {
            CarParkFloor carParkFloor = cpfs.getCarParkFloor(carParkId, floorIdentifier);
            CarType carType = cts.getCarType(carTypeId);

            if (carParkFloor != null){
                if(carType == null){
                    em.close();
                    return null;
                }

                ParkingSpot parkingSpot = new ParkingSpot(spotIdentifier, carParkFloor, carType);
                em.getTransaction().begin();
                em.persist(parkingSpot);
                em.getTransaction().commit();
                em.getEntityManagerFactory().getCache().evictAll();
                em.close();
                return parkingSpot;
            }
        }
        catch (Exception e){
            em.close();
            return null;
        }
        em.close();
        return null;
    }

    public ParkingSpot getParkingSpot(Long parkingSpotId) {
        EntityManager em = emf.createEntityManager();
        try {
            ParkingSpot parkingSpotResult = em.find(ParkingSpot.class, parkingSpotId);
            if (parkingSpotResult != null) {
                em.getEntityManagerFactory().getCache().evictAll();
                em.close();
                return parkingSpotResult;
            }
        } catch (Exception e) {
            em.close();
            return null;
        }
        em.close();
        return null;
    }

    public List<ParkingSpot> getParkingSpots(Long carParkId, String floorIdentifier) {
        EntityManager em = emf.createEntityManager();
        CarParkFloorId carParkFloorId = new CarParkFloorId(carParkId,floorIdentifier);
        TypedQuery<ParkingSpot> query = em.createNamedQuery("ParkingSpot.findParkingSpotByFloorIdentifier", ParkingSpot.class);
        query.setParameter("floorIdentifier", carParkFloorId);
        try {
            List <ParkingSpot> parkingSpotsResult = query.getResultList();
            if (!(parkingSpotsResult.isEmpty())) {
                em.getEntityManagerFactory().getCache().evictAll();
                em.close();
                return new ArrayList<>(parkingSpotsResult);
            }
        } catch (Exception e) {
            em.close();
            return Collections.emptyList();
        }
        em.close();
        return Collections.emptyList();
    }

    public Map<String, List<ParkingSpot>> getParkingSpots(Long carParkId) {
        EntityManager em = emf.createEntityManager();
        Map<String, List<ParkingSpot>> listToMap = new HashMap<>();
        TypedQuery<ParkingSpot> query = em.createNamedQuery("ParkingSpot.findParkingSpotByCarParkId", ParkingSpot.class);
        query.setParameter("carParkId", carParkId);
        try {
            List <ParkingSpot> parkingSpotsResult = query.getResultList();
            if (!(parkingSpotsResult.isEmpty())) {
                em.getEntityManagerFactory().getCache().evictAll();
                em.close();
                return getStringListMap(listToMap, parkingSpotsResult);
            }
        } catch (Exception e) {
            em.close();
            return Collections.emptyMap();
        }
        em.close();
        return Collections.emptyMap();
    }

    public Map<String, List<ParkingSpot>> getAvailableParkingSpots(String carParkName) {
        EntityManager em = emf.createEntityManager();
        Map<String, List<ParkingSpot>> listToMap = new HashMap<>();
        TypedQuery<ParkingSpot> queryName = em.createNamedQuery("ParkingSpot.findParkingSpotByCarParkName", ParkingSpot.class);
        queryName.setParameter("name", carParkName);
        TypedQuery<ParkingSpot> queryOccupied = em.createNamedQuery("ParkingSpot.findAllOccupiedParkingSpots", ParkingSpot.class);
        queryOccupied.setParameter("name", carParkName);
        try {
            List<ParkingSpot> parkingSpotsResult = queryName.getResultList();
            List<ParkingSpot> occupiedSpots = queryOccupied.getResultList();
            if (!(parkingSpotsResult.isEmpty())) {
                parkingSpotsResult.removeAll(occupiedSpots);
                em.getEntityManagerFactory().getCache().evictAll();
                em.close();
                return getStringListMap(listToMap, parkingSpotsResult);
            }
        } catch (Exception e) {
            em.close();
            return Collections.emptyMap();
        }
        em.close();
        return Collections.emptyMap();
    }

    public Map<String, List<ParkingSpot>> getOccupiedParkingSpots(String carParkName) {
        EntityManager em = emf.createEntityManager();
        Map<String, List<ParkingSpot>> listToMap = new HashMap<>();
        TypedQuery<ParkingSpot> query = em.createNamedQuery("ParkingSpot.findAllOccupiedParkingSpots", ParkingSpot.class);
        query.setParameter("name", carParkName);
        try {
            List<ParkingSpot> occupiedSpots = query.getResultList();
            if (!(occupiedSpots.isEmpty())) {
                em.getEntityManagerFactory().getCache().evictAll();
                em.close();
                return getStringListMap(listToMap, occupiedSpots);
            }
        } catch (Exception e) {
            em.close();
            return Collections.emptyMap();
        }
        em.close();
        return Collections.emptyMap();
    }

    private Map<String, List<ParkingSpot>> getStringListMap(Map<String, List<ParkingSpot>> listToMapResult, List<ParkingSpot> parkingSpotsResult) {
        for (ParkingSpot parkingSpot: parkingSpotsResult){
            if(!listToMapResult.containsKey(parkingSpot.getCarParkFloor().getFloorIdentifier().getFloorIdentifierId())){
                listToMapResult.put(parkingSpot.getCarParkFloor().getFloorIdentifier().getFloorIdentifierId(), new ArrayList<>());
            }
            listToMapResult.get(parkingSpot.getCarParkFloor().getFloorIdentifier().getFloorIdentifierId()).add(parkingSpot);
        }
        return listToMapResult;
    }

    public ParkingSpot updateParkingSpot(ParkingSpot parkingSpot) {
        EntityManager em = emf.createEntityManager();
        ParkingSpot newParkingSpot = parkingSpot;
        ParkingSpot parkingSpotResult = getParkingSpot(newParkingSpot.getId());
        CarParkFloor carParkFloor = cpfs.getCarParkFloor(newParkingSpot.getCarParkFloor().getFloorIdentifier().getCarParkId(), newParkingSpot.getCarParkFloor().getFloorIdentifier().getFloorIdentifierId());
        CarType carType = cts.getCarType(newParkingSpot.getCarType().getId());
        try {
            if (parkingSpotResult != null) {
                newParkingSpot.setReservations(parkingSpotResult.getReservations());
                if (newParkingSpot.getSpotIdentifier() == null)
                    newParkingSpot.setSpotIdentifier(parkingSpotResult.getSpotIdentifier());
                if (newParkingSpot.getCarParkFloor() == null)
                    newParkingSpot.setCarParkFloor(parkingSpotResult.getCarParkFloor());
                else if (carParkFloor == null){
                    em.close();
                    return null;
                }
                if (newParkingSpot.getCarType() == null)
                    newParkingSpot.setCarType(parkingSpotResult.getCarType());
                else if (carType == null){
                    em.close();
                    return null;
                }

                em.getTransaction().begin();
                em.merge(newParkingSpot);
                em.getTransaction().commit();
                em.close();
                return newParkingSpot;
            }

        } catch (Exception e) {
            em.close();
            return null;
        }
        em.close();
        return null;
    }

    public ParkingSpot deleteParkingSpot(Long parkingSpotId) {
        EntityManager em = emf.createEntityManager();
        ParkingSpot parkingSpot = getParkingSpot(parkingSpotId);
        try{
            if (parkingSpot != null) {
                for (Reservation reservation : parkingSpot.getReservations()) {
                    reservation = rs.endReservation(reservation.getId());
                    reservation.setParkingSpot(null);
                    rs.updateReservation(reservation);
                }
                if (!em.contains(parkingSpot)){
                    parkingSpot = em.merge(parkingSpot);
                }
                em.getTransaction().begin();
                em.remove(parkingSpot);
                em.getTransaction().commit();
                em.getEntityManagerFactory().getCache().evictAll();
                em.close();
                return parkingSpot;
            }

        } catch (Exception e){
            em.close();
            return null;
        }
        em.close();
        return null;
    }
}
