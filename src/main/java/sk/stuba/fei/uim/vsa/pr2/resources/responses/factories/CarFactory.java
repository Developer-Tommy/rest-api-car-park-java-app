package sk.stuba.fei.uim.vsa.pr2.resources.responses.factories;

import sk.stuba.fei.uim.vsa.pr2.entities.Car;
import sk.stuba.fei.uim.vsa.pr2.entities.User;
import sk.stuba.fei.uim.vsa.pr2.resources.responses.CarDto;
import sk.stuba.fei.uim.vsa.pr2.services.UserService;

import java.util.Collections;
import java.util.stream.Collectors;

public class CarFactory implements ResponseFactory<Car, CarDto>{

    private static final CarTypeFactory typeFactory = new CarTypeFactory();
    private static final UserFactory userFactory = new UserFactory();
    private static final ReservationFactory reservationFactory = new ReservationFactory();
    private static final UserService service =  new UserService();

    @Override
    public CarDto transformToDto(Car entity) {
        CarDto carDto = new CarDto();
        carDto.setId(entity.getId());
        carDto.setBrand(entity.getBrand());
        carDto.setModel(entity.getModel());
        carDto.setVrp(entity.getVehicleRegistrationPlate());
        carDto.setColour(entity.getColour());
        carDto.setOwner(userFactory.transformToDtoWithoutLooping(entity.getUser()));
        carDto.setType(typeFactory.transformToDto(entity.getCarType()));
        if (entity.getReservations() != null)
            carDto.setReservations(entity.getReservations().stream().map(reservationFactory::transformToDto).collect(Collectors.toList()));
        else
            carDto.setReservations(Collections.emptyList());
        return carDto;
    }

    public CarDto transformToDtoWithoutLooping(Car entity) {
        CarDto carDto = new CarDto();
        carDto.setId(entity.getId());
        carDto.setBrand(entity.getBrand());
        carDto.setModel(entity.getModel());
        carDto.setVrp(entity.getVehicleRegistrationPlate());
        carDto.setColour(entity.getColour());
        carDto.setOwner(userFactory.transformToDtoWithoutLooping(entity.getUser()));
        carDto.setType(typeFactory.transformToDto(entity.getCarType()));
        carDto.setReservations(Collections.emptyList());
        return carDto;
    }

    @Override
    public Car transformToEntity(CarDto dto) {
        Car car = new Car();
        car.setId(dto.getId());
        car.setBrand(dto.getBrand());
        car.setModel(dto.getModel());
        car.setVehicleRegistrationPlate(dto.getVrp());
        car.setColour(dto.getColour());
        if (dto.getOwner() != null){
            User user = service.getUser(dto.getOwner().getEmail());
            car.setUser(user);
        }
        if (dto.getType() != null)
            car.setCarType(typeFactory.transformToEntity(dto.getType()));
        car.setReservations(null);
        return car;
    }
}
