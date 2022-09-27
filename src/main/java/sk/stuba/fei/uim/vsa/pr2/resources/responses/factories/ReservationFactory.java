package sk.stuba.fei.uim.vsa.pr2.resources.responses.factories;

import sk.stuba.fei.uim.vsa.pr2.entities.Car;
import sk.stuba.fei.uim.vsa.pr2.entities.ParkingSpot;
import sk.stuba.fei.uim.vsa.pr2.entities.Reservation;
import sk.stuba.fei.uim.vsa.pr2.resources.responses.ReservationDto;
import sk.stuba.fei.uim.vsa.pr2.services.CarService;
import sk.stuba.fei.uim.vsa.pr2.services.ParkingSpotService;

import java.util.ArrayList;
import java.util.List;

public class ReservationFactory implements ResponseFactory<Reservation, ReservationDto> {

    private static final ParkingSpotFactory parkingFactory = new ParkingSpotFactory();
    private static final CarFactory carFactory = new CarFactory();
    private static final CarService carService = new CarService();
    private static final ParkingSpotService spotService = new ParkingSpotService();

    @Override
    public ReservationDto transformToDto(Reservation entity) {
        ReservationDto reservationDto = new ReservationDto();
        reservationDto.setId(entity.getId());
        reservationDto.setStart(entity.getStartTime());
        reservationDto.setEnd(entity.getEndTime());
        reservationDto.setPrices(entity.getPrice());
        if (entity.getParkingSpot() != null) {
            List<Reservation> reservations = new ArrayList<>(entity.getParkingSpot().getReservations());
            reservations.add(entity);
            entity.getParkingSpot().setReservations(reservations);
            reservationDto.setSpot(parkingFactory.transformToDtoWithoutLooping(entity.getParkingSpot()));
        }
        else
            reservationDto.setSpot(null);
        if (entity.getCar() != null) {
            List<Reservation> reservations = new ArrayList<>(entity.getCar().getReservations());
            reservations.add(entity);
            entity.getCar().setReservations(reservations);
            reservationDto.setCar(carFactory.transformToDtoWithoutLooping(entity.getCar()));
        }
        else
            reservationDto.setCar(null);
        return reservationDto;
    }

    @Override
    public Reservation transformToEntity(ReservationDto dto) {
        Reservation reservation = new Reservation();
        reservation.setId(dto.getId());
        reservation.setStartTime(dto.getStart());
        reservation.setEndTime(dto.getEnd());
        reservation.setPrice(dto.getPrices());
        if (dto.getCar() != null){
            Car car = carService.getCar(dto.getCar().getId());
            reservation.setCar(car);
        }
        if (dto.getSpot() != null){
            ParkingSpot parkingSpot = spotService.getParkingSpot(dto.getSpot().getId());
            reservation.setParkingSpot(parkingSpot);
        }
        return reservation;
    }
}
