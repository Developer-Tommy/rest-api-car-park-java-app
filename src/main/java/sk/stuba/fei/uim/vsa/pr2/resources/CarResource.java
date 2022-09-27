package sk.stuba.fei.uim.vsa.pr2.resources;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import sk.stuba.fei.uim.vsa.pr2.BasicAuthFilter;
import sk.stuba.fei.uim.vsa.pr2.entities.Car;
import sk.stuba.fei.uim.vsa.pr2.entities.CarType;
import sk.stuba.fei.uim.vsa.pr2.entities.User;
import sk.stuba.fei.uim.vsa.pr2.resources.responses.CarDto;
import sk.stuba.fei.uim.vsa.pr2.resources.responses.factories.CarFactory;
import sk.stuba.fei.uim.vsa.pr2.services.CarService;
import sk.stuba.fei.uim.vsa.pr2.services.CarTypeService;
import sk.stuba.fei.uim.vsa.pr2.services.UserService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Path("/cars")
public class CarResource {

    private static final Logger LOGGER = Logger.getLogger(CarResource.class.getName());
    private final ObjectMapper json = new ObjectMapper();
    private final BasicAuthFilter authFilter = new BasicAuthFilter();
    private final CarService service = new CarService();
    private final UserService serviceUser = new UserService();
    private final CarFactory factory = new CarFactory();
    private final CarTypeService serviceType = new CarTypeService();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCars(@HeaderParam(HttpHeaders.AUTHORIZATION) String auth, @QueryParam("user") Long id, @QueryParam("vrp") String vrp){
        LOGGER.info("AUTH: " + authFilter.getAuthUser(auth));
        if (!authFilter.getAuthUser(auth))
            return Response.status(Response.Status.UNAUTHORIZED).build();
        List<CarDto> carDtos = new ArrayList<>(Collections.emptyList());
        if (id != null) {
            List<Car> cars = service.getCars(id);
            if (!cars.isEmpty()){
                if (vrp != null) {
                    for (Car c : cars) {
                        if (Objects.equals(c.getVehicleRegistrationPlate(), vrp)) {
                            CarDto carDto = factory.transformToDto(c);
                            carDtos.add(carDto);
                        }
                    }
                    try {
                        if (carDtos.isEmpty() )
                            return Response.status(Response.Status.NOT_FOUND).entity(json.writeValueAsString(carDtos)).build();
                        return Response.status(Response.Status.OK).entity(json.writeValueAsString(carDtos)).build();
                    } catch (JsonProcessingException e) {
                        LOGGER.log(Level.SEVERE, null, e);
                        return Response.status(Response.Status.BAD_REQUEST).build();
                    }
                }
                return getResponse(cars);

            }
            else return Response.status(Response.Status.NOT_FOUND).entity(cars).build();

        }
        else if (vrp != null){
            Car car = service.getCar(vrp);
            if (car != null){
                CarDto carDto = factory.transformToDto(car);
                carDtos.add(carDto);
                try {
                    return Response.status(Response.Status.OK).entity(json.writeValueAsString(carDtos)).build();
                } catch (JsonProcessingException e) {
                    LOGGER.log(Level.SEVERE, null, e);
                    return Response.status(Response.Status.BAD_REQUEST).build();
                }
            }
            else return Response.status(Response.Status.OK).entity(carDtos).build();
        }

        else {
            List<Car> cars = service.getCars();
            if (!cars.isEmpty()){
                return getResponse(cars);
            }
            else return Response.status(Response.Status.OK).entity(cars).build();
        }
    }

    private Response getResponse(List<Car> cars) {
        List<CarDto> carDtos = cars.stream().map(factory::transformToDto).collect(Collectors.toList());
        try {
            return Response.status(Response.Status.OK).entity(json.writeValueAsString(carDtos)).build();
        } catch (JsonProcessingException e) {
            LOGGER.log(Level.SEVERE, null, e);
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public Response getCarById(@HeaderParam(HttpHeaders.AUTHORIZATION) String auth, @PathParam("id") Long id) {
        LOGGER.info("AUTH: " + authFilter.getAuthUser(auth));
        if (!authFilter.getAuthUser(auth))
            return Response.status(Response.Status.UNAUTHORIZED).build();
        if (id != null){
            Car car = service.getCar(id);
            if (car != null){
                CarDto carDto = factory.transformToDto(car);
                try {
                    return Response.status(Response.Status.OK).entity(json.writeValueAsString(carDto)).build();
                } catch (JsonProcessingException e) {
                    LOGGER.log(Level.SEVERE, null, e);
                    return Response.status(Response.Status.BAD_REQUEST).build();
                }
            }
            else {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
        }
        return Response.status(Response.Status.BAD_REQUEST).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createCar(@HeaderParam(HttpHeaders.AUTHORIZATION) String auth, String request) {
        LOGGER.info("AUTH: " + authFilter.getAuthUser(auth));
        if (!authFilter.getAuthUser(auth))
            return Response.status(Response.Status.UNAUTHORIZED).build();
        try {
            CarDto carDto = json.readValue(request, CarDto.class);
            Car car = factory.transformToEntity(carDto);

            if (carDto.getOwner() == null)
                return Response.status(Response.Status.BAD_REQUEST).build();

            if (carDto.getOwner() != null)
                if (carDto.getOwner().getFirstName() == null || carDto.getOwner().getLastName() == null || carDto.getOwner().getEmail() == null)
                    return Response.status(Response.Status.BAD_REQUEST).build();

            User user;
            if (car.getUser() == null)
                user = serviceUser.createUser(carDto.getOwner().getFirstName(), carDto.getOwner().getLastName(), carDto.getOwner().getEmail());
            else
                user = car.getUser();

            List<Car> cars = new ArrayList<>(Collections.emptyList());
            if (user.getCars() != null)
                cars = new ArrayList<>(user.getCars());

            if (car.getCarType() != null){
                CarType type = serviceType.getCarType(car.getCarType().getName());
                if (type == null){
                    type = serviceType.createCarType(car.getCarType().getName());
                }
                Car c = service.createCar(user.getId() ,car.getBrand(), car.getModel(), car.getColour(), car.getVehicleRegistrationPlate(), type.getId());
                if (c == null)
                    return Response.status(Response.Status.BAD_REQUEST).build();
                carList(cars, type, c);
                user.setCars(cars);
                CarDto cDto = factory.transformToDto(c);
                return Response.status(Response.Status.CREATED).entity(json.writeValueAsString(cDto)).build();
            }
            else {
                Car c = service.createCar(user.getId() ,car.getBrand(), car.getModel(), car.getColour(), car.getVehicleRegistrationPlate());
                if (c == null)
                    return Response.status(Response.Status.BAD_REQUEST).build();
                cars.add(c);
                user.setCars(cars);
                CarDto cDto = factory.transformToDto(c);
                return Response.status(Response.Status.CREATED).entity(json.writeValueAsString(cDto)).build();
            }

        }
        catch (JsonProcessingException e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

    }

    static void carList(List<Car> cars, CarType type, Car c) {
        List<Car> carList;
        if (type.getCars() != null){
            carList = new ArrayList<>(type.getCars());
        }
        else {
            carList = new ArrayList<>(Collections.emptyList());
        }
        carList.add(c);
        type.setCars(carList);
        cars.add(c);
    }

    @DELETE
    @Path("/{id}")
    public Response deleteCar(@HeaderParam(HttpHeaders.AUTHORIZATION) String auth, @PathParam("id") Long id) {
        LOGGER.info("AUTH: " + authFilter.getAuthUser(auth));
        if (!authFilter.getAuthUser(auth))
            return Response.status(Response.Status.UNAUTHORIZED).build();
        if (id != null) {
            Car car = service.deleteCar(id);
            if (car != null)
                return Response.status(Response.Status.NO_CONTENT).build();
            else
                return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.status(Response.Status.BAD_REQUEST).build();
    }
}
