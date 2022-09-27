package sk.stuba.fei.uim.vsa.pr2.resources.responses.factories;

import sk.stuba.fei.uim.vsa.pr2.entities.CarPark;
import sk.stuba.fei.uim.vsa.pr2.resources.responses.CarParkDto;

import java.util.Collections;
import java.util.stream.Collectors;

public class CarParkFactory implements ResponseFactory<CarPark, CarParkDto> {

    private static final CarParkFloorFactory factory = new CarParkFloorFactory();

    @Override
    public CarParkDto transformToDto(CarPark entity) {
        CarParkDto carParkDto =  new CarParkDto();
        carParkDto.setId(entity.getId());
        carParkDto.setName(entity.getName());
        carParkDto.setAddress(entity.getAddress());
        carParkDto.setPrices(entity.getPricePerHour());
        if (entity.getCarParkFloors() != null)
            carParkDto.setFloors(entity.getCarParkFloors().stream().map(factory::transformToDto).collect(Collectors.toList()));
        else
            carParkDto.setFloors(Collections.emptyList());
        return carParkDto;
    }

    @Override
    public CarPark transformToEntity(CarParkDto dto) {
        CarPark carPark = new CarPark();
        carPark.setName(dto.getName());
        carPark.setAddress(dto.getAddress());
        carPark.setPricePerHour(dto.getPrices());
        carPark.setCarParkFloors(Collections.emptyList());
        return carPark;
    }
}
