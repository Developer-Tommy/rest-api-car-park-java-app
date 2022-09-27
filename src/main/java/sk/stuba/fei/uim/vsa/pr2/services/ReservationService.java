package sk.stuba.fei.uim.vsa.pr2.services;

import sk.stuba.fei.uim.vsa.pr2.entities.Car;
import sk.stuba.fei.uim.vsa.pr2.entities.ParkingSpot;
import sk.stuba.fei.uim.vsa.pr2.entities.Reservation;

import javax.persistence.EntityManager;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class ReservationService extends AbstractService{

    private static final ParkingSpotService pss = new ParkingSpotService();
    private static final CarService cs = new CarService();

    public Reservation createReservation(Long parkingSpotId, Long cardId) {
        EntityManager em = emf.createEntityManager();
        ParkingSpot parkingSpot = pss.getParkingSpot(parkingSpotId);
        Car car = cs.getCar(cardId);
        try{
            if (parkingSpot != null && car != null){

                if (!Objects.equals(parkingSpot.getCarType().getId(), car.getCarType().getId())) {
                    System.out.println("Not suitable Parking Spot for your Car.");
                    em.close();
                    return null;
                }
                if (parkingSpot.getReservations() != null || car.getReservations() != null) {
                    for (Reservation activeReservation: parkingSpot.getReservations()){
                        if (activeReservation.getPrice() == null) {
                            System.out.println("There is already an active Reservation.");
                            em.close();
                            return null;
                        }
                    }
                    for (Reservation activeReservation: car.getReservations()){
                        if (activeReservation.getPrice() == null) {
                            System.out.println("There is already an active Reservation.");
                            em.close();
                            return null;
                        }
                    }

                }
                Reservation reservation = new Reservation(new Date(), car, parkingSpot);
                em.getTransaction().begin();
                em.persist(reservation);
                em.getTransaction().commit();
                em.getEntityManagerFactory().getCache().evictAll();
                em.close();
                return reservation;
            }

        }
        catch (Exception e){
            em.close();
            return null;
        }
        em.close();
        return null;
    }

    public Reservation getReservation(Long reservationId) {
        EntityManager em = emf.createEntityManager();
        try{
            Reservation reservationResult =  em.find(Reservation.class, reservationId);
            if (reservationResult != null){
                em.getEntityManagerFactory().getCache().evictAll();
                em.close();
                return reservationResult;
            }
        }
        catch (Exception e){
            em.close();
            return null;

        }
        em.close();
        return null;
    }

    public List<Reservation> getReservations() {
        EntityManager em = emf.createEntityManager();
        TypedQuery<Reservation> query = em.createNamedQuery("Reservation.findAll", Reservation.class);
        try {
            List <Reservation> reservationResult = query.getResultList();
            if (!(reservationResult.isEmpty())) {
                em.getEntityManagerFactory().getCache().evictAll();
                em.close();
                return new ArrayList<>(reservationResult);
            }
        } catch (Exception e) {
            em.close();
            return Collections.emptyList();
        }
        em.close();
        return Collections.emptyList();
    }

    public Reservation endReservation(Long reservationId) {
        EntityManager em = emf.createEntityManager();
        Reservation reservation = em.find(Reservation.class, reservationId);
        try{
            if (reservation != null) {
                if (reservation.getEndTime() == null){
                    reservation.setEndTime(new Date());
                    long time = Math.abs(new Date().getTime() - reservation.getStartTime().getTime());
                    long hours = TimeUnit.HOURS.convert(time, TimeUnit.MILLISECONDS) + 1;
                    int price = (int) (reservation.getParkingSpot().getCarParkFloor().getCarPark().getPricePerHour() * hours);
                    reservation.setPrice(price);
                    em.getTransaction().begin();
                    em.merge(reservation);
                    em.getTransaction().commit();
                    em.getEntityManagerFactory().getCache().evictAll();
                    em.close();
                    return reservation;
                }
                else
                    System.out.println("Reservation has already ended.");

                em.close();
                return reservation;
            }
        }
        catch (Exception e){
            em.close();
            return null;
        }
        em.close();
        return null;
    }

    public List<Reservation> getReservations(Long parkingSpotId, Date date) {
        EntityManager em = emf.createEntityManager();
        TypedQuery<Reservation> query = em.createNamedQuery("Reservation.findByDate", Reservation.class);
        Calendar c = new GregorianCalendar();
        c.set(Calendar.DAY_OF_YEAR, c.get(Calendar.DAY_OF_YEAR) + 1);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        Date date2 = c.getTime();
        query.setParameter("id", parkingSpotId);
        query.setParameter("startTime", date, TemporalType.DATE );
        query.setParameter("endTime", date2, TemporalType.DATE);

        try{
            List<Reservation> reservations = query.getResultList();
            if (!(reservations.isEmpty())) {
                em.getEntityManagerFactory().getCache().evictAll();
                em.close();
                return new ArrayList<>(reservations);
            }
        }
        catch (Exception e){
            em.close();
            return Collections.emptyList();
        }
        em.close();
        return Collections.emptyList();
    }

    public List<Reservation> getMyReservations(Long userId) {
        EntityManager em = emf.createEntityManager();
        TypedQuery<Reservation> query = em.createNamedQuery("Reservation.findUserReservations", Reservation.class);
        query.setParameter("id", userId);

        try{
            List<Reservation> reservations = query.getResultList();
            if (!(reservations.isEmpty())) {
                em.getEntityManagerFactory().getCache().evictAll();
                em.close();
                return new ArrayList<>(reservations);
            }
        }
        catch (Exception e){
            em.close();
            return Collections.emptyList();
        }
        em.close();
        return Collections.emptyList();

    }

    public Reservation updateReservation(Reservation reservation) {
        EntityManager em = emf.createEntityManager();
        Reservation newReservation = reservation;
        Reservation reservationResult = em.find(Reservation.class, newReservation.getId());
        try {
            if (reservationResult != null) {
                ParkingSpot parkingSpot = pss.getParkingSpot(newReservation.getParkingSpot().getId());
                Car car = cs.getCar(newReservation.getCar().getId());

                if (newReservation.getStartTime() == null)
                    newReservation.setStartTime(reservationResult.getStartTime());
                if (newReservation.getCar() == null)
                    newReservation.setCar(reservationResult.getCar());
                else if (car == null)
                    return null;

                if (newReservation.getParkingSpot() == null)
                    newReservation.setParkingSpot(reservationResult.getParkingSpot());
                else if (parkingSpot == null)
                    return null;

                if (newReservation.getEndTime() == null)
                    newReservation.setEndTime(reservationResult.getEndTime());
                if (newReservation.getPrice() == null)
                    newReservation.setPrice(reservationResult.getPrice());

                if (parkingSpot != null && car != null){
                    if (!Objects.equals(parkingSpot.getCarType().getId(), car.getCarType().getId())) {
                        System.out.println("Not suitable Parking Spot for your Car.");
                        return null;
                    }
                    em.refresh(parkingSpot);
                    em.refresh(car);

                    if (parkingSpot.getReservations() != null || car.getReservations() != null) {
                        for (Reservation activeReservation: parkingSpot.getReservations()){
                            if (activeReservation.getPrice() == null) {
                                System.out.println("There is already an active Reservation.");
                                return null;
                            }
                        }
                        for (Reservation activeReservation: car.getReservations()){
                            if (activeReservation.getPrice() == null) {
                                System.out.println("There is already an active Reservation.");
                                return null;
                            }
                        }
                    }
                    em.getTransaction().begin();
                    newReservation = em.merge(newReservation);
                    em.getTransaction().commit();
                    return newReservation;
                }
            }
        }
        catch (Exception e){
            em.getTransaction().begin();
            newReservation = em.merge(newReservation);
            em.getTransaction().commit();
            return newReservation;
        }
        return null;
    }
}
