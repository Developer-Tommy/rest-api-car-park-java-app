package sk.stuba.fei.uim.vsa.pr2.resources.responses.factories;

import sk.stuba.fei.uim.vsa.pr2.entities.CarParkFloor;
import sk.stuba.fei.uim.vsa.pr2.entities.CarParkFloorId;
import sk.stuba.fei.uim.vsa.pr2.resources.responses.CarParkFloorDto;
import sk.stuba.fei.uim.vsa.pr2.services.CarParkService;

import java.util.Collections;
import java.util.stream.Collectors;

public class CarParkFloorFactory implements ResponseFactory<CarParkFloor, CarParkFloorDto> {

    private static final ParkingSpotFactory factory = new ParkingSpotFactory();
    private static final CarParkService service =  new CarParkService();

    @Override
    public CarParkFloorDto transformToDto(CarParkFloor entity) {
        CarParkFloorDto carParkFloorDto = new CarParkFloorDto();
        carParkFloorDto.setIdentifier(entity.getFloorIdentifier().getFloorIdentifierId());
        carParkFloorDto.setCarPark(entity.getFloorIdentifier().getCarParkId());
        if (entity.getParkingSpots() != null)
            carParkFloorDto.setSpots(entity.getParkingSpots().stream().map(factory::transformToDto).collect(Collectors.toList()));
        else
            carParkFloorDto.setSpots(Collections.emptyList());
        return carParkFloorDto;
    }

    @Override
    public CarParkFloor transformToEntity(CarParkFloorDto dto) {
        CarParkFloorId carParkFloorId = new CarParkFloorId(dto.getCarPark(), dto.getIdentifier());
        CarParkFloor carParkFloor = new CarParkFloor(carParkFloorId, service.getCarPark(dto.getCarPark()));
        if (dto.getSpots() != null)
            carParkFloor.setParkingSpots(dto.getSpots().stream().map(factory::transformToEntity).collect(Collectors.toList()));
        else
            carParkFloor.setParkingSpots(Collections.emptyList());
        return carParkFloor;
    }
}
