package sk.stuba.fei.uim.vsa.pr2.resources.responses.factories;

import sk.stuba.fei.uim.vsa.pr2.entities.CarType;
import sk.stuba.fei.uim.vsa.pr2.resources.responses.CarTypeDto;

public class CarTypeFactory implements ResponseFactory<CarType, CarTypeDto> {

    @Override
    public CarTypeDto transformToDto(CarType entity) {
        CarTypeDto carTypeDto = new CarTypeDto();
        carTypeDto.setId(entity.getId());
        carTypeDto.setName(entity.getName());
        return carTypeDto;
    }

    @Override
    public CarType transformToEntity(CarTypeDto dto) {
        CarType carType = new CarType();
        carType.setId(dto.getId());
        carType.setName(dto.getName());
        return carType;
    }
}
