package sk.stuba.fei.uim.vsa.pr2.resources.responses.factories;

import sk.stuba.fei.uim.vsa.pr2.entities.ParkingSpot;
import sk.stuba.fei.uim.vsa.pr2.entities.Reservation;
import sk.stuba.fei.uim.vsa.pr2.resources.responses.ParkingSpotDto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ParkingSpotFactory implements ResponseFactory<ParkingSpot, ParkingSpotDto> {

    private static final CarTypeFactory factory = new CarTypeFactory();
    private static final ReservationFactory reservationFactory = new ReservationFactory();

    @Override
    public ParkingSpotDto transformToDto(ParkingSpot entity) {
        ParkingSpotDto parkingSpotDto = new ParkingSpotDto();
        parkingSpotDto.setId(entity.getId());
        parkingSpotDto.setIdentifier(entity.getSpotIdentifier());
        parkingSpotDto.setCarPark(entity.getCarParkFloor().getFloorIdentifier().getCarParkId());
        parkingSpotDto.setCarParkFloor(entity.getCarParkFloor().getFloorIdentifier().getFloorIdentifierId());
        parkingSpotDto.setType(factory.transformToDto(entity.getCarType()));
        List<Reservation> list = new ArrayList<>(Collections.emptyList());
        if (entity.getReservations() != null){
            list = new ArrayList<>(entity.getReservations());
            for (Reservation r : entity.getReservations()){
                if (r.getEndTime() == null) {
                    parkingSpotDto.setFree(false);
                    break;
                }
                if (parkingSpotDto.getFree() == null)
                    parkingSpotDto.setFree(true);
            }
        }
        if (list.isEmpty())
            parkingSpotDto.setFree(true);

        if (entity.getReservations() != null)
            parkingSpotDto.setReservations(entity.getReservations().stream().map(reservationFactory::transformToDto).collect(Collectors.toList()));
        else
            parkingSpotDto.setReservations(Collections.emptyList());
        return parkingSpotDto;
    }

    public ParkingSpotDto transformToDtoWithoutLooping(ParkingSpot entity) {
        ParkingSpotDto parkingSpotDto = new ParkingSpotDto();
        parkingSpotDto.setId(entity.getId());
        parkingSpotDto.setIdentifier(entity.getSpotIdentifier());
        parkingSpotDto.setCarPark(entity.getCarParkFloor().getFloorIdentifier().getCarParkId());
        parkingSpotDto.setCarParkFloor(entity.getCarParkFloor().getFloorIdentifier().getFloorIdentifierId());
        parkingSpotDto.setType(factory.transformToDto(entity.getCarType()));
        if (entity.getReservations() != null){
            for (Reservation r : entity.getReservations()){
                if (r.getEndTime() == null) {
                    parkingSpotDto.setFree(false);
                    break;
                }
                if (parkingSpotDto.getFree() == null)
                    parkingSpotDto.setFree(true);
            }
        }
        parkingSpotDto.setReservations(Collections.emptyList());
        return parkingSpotDto;
    }

    @Override
    public ParkingSpot transformToEntity(ParkingSpotDto dto) {
        ParkingSpot parkingSpot = new ParkingSpot();
        parkingSpot.setId(dto.getId());
        parkingSpot.setSpotIdentifier(dto.getIdentifier());
        parkingSpot.setReservations(null);
        if (dto.getType() != null)
            parkingSpot.setCarType(factory.transformToEntity(dto.getType()));
        return parkingSpot;
    }
}
