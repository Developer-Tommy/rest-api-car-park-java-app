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
import sk.stuba.fei.uim.vsa.pr2.resources.responses.UserDto;
import sk.stuba.fei.uim.vsa.pr2.resources.responses.factories.CarFactory;
import sk.stuba.fei.uim.vsa.pr2.resources.responses.factories.UserFactory;
import sk.stuba.fei.uim.vsa.pr2.services.CarService;
import sk.stuba.fei.uim.vsa.pr2.services.CarTypeService;
import sk.stuba.fei.uim.vsa.pr2.services.UserService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Path("/users")
public class UserResource {

    private static final Logger LOGGER = Logger.getLogger(UserResource.class.getName());
    private final ObjectMapper json = new ObjectMapper();
    private final BasicAuthFilter authFilter = new BasicAuthFilter();
    private final UserService service = new UserService();
    private final UserFactory factory = new UserFactory();
    private final CarService carService = new CarService();
    private final CarTypeService serviceType = new CarTypeService();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUsers(@HeaderParam(HttpHeaders.AUTHORIZATION) String auth, @QueryParam("email") String email){
        LOGGER.info("AUTH: " + authFilter.getAuthUser(auth));
        if (!authFilter.getAuthUser(auth))
            return Response.status(Response.Status.UNAUTHORIZED).build();
        List<UserDto> userDtos = new ArrayList<>(Collections.emptyList());
        if (email == null){
            List<User> users = service.getUsers();
            if (!users.isEmpty())
                userDtos = users.stream().map(factory::transformToDto).collect(Collectors.toList());
            try {
                return Response.status(Response.Status.OK).entity(json.writeValueAsString(userDtos)).build();
            } catch (JsonProcessingException e) {
                LOGGER.log(Level.SEVERE, null, e);
                return Response.status(Response.Status.BAD_REQUEST).build();
            }
        }

        else {
            User user = service.getUser(email);
            if (user != null) {
                UserDto userDto = factory.transformToDto(user);
                userDtos.add(userDto);

                try {
                    return Response.status(Response.Status.OK).entity(json.writeValueAsString(userDtos)).build();
                } catch (JsonProcessingException e) {
                    LOGGER.log(Level.SEVERE, null, e);
                    return Response.status(Response.Status.BAD_REQUEST).build();
                }
            }
            return Response.status(Response.Status.NOT_FOUND).entity(userDtos).build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public Response getUserById(@HeaderParam(HttpHeaders.AUTHORIZATION) String auth, @PathParam("id") Long id) {
        LOGGER.info("AUTH: " + authFilter.getAuthUser(auth));
        if (!authFilter.getAuthUser(auth))
            return Response.status(Response.Status.UNAUTHORIZED).build();
        if (id != null){
            User user = service.getUser(id);
            if (user != null){
                UserDto userDto = factory.transformToDto(user);
                try {
                    return Response.status(Response.Status.OK).entity(json.writeValueAsString(userDto)).build();
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
    public Response createUser(@HeaderParam(HttpHeaders.AUTHORIZATION) String auth, String request) {
        LOGGER.info("AUTH: " + authFilter.getAuthUser(auth));
        if (!authFilter.getAuthUser(auth))
            return Response.status(Response.Status.UNAUTHORIZED).build();
        try {
            UserDto userDto = json.readValue(request, UserDto.class);
            User user = factory.transformToEntity(userDto);
            User newUser = service.createUser(user.getFirstname(), user.getLastname(), user.getEmail());

            if (newUser == null)
                return Response.status(Response.Status.BAD_REQUEST).build();

            List<Car> carList = new ArrayList<>(Collections.emptyList());
            if (userDto.getCars() != null) {
                List<CarDto> carDtos = new ArrayList<>(userDto.getCars());
                if (!carDtos.isEmpty()){
                    for (CarDto carDto : carDtos) {
                        if (carDto.getType() != null){
                            CarType type = serviceType.getCarType(carDto.getType().getName());
                            if (type == null){
                                type = serviceType.createCarType(carDto.getType().getName());
                            }
                            Car c = carService.createCar(newUser.getId() ,carDto.getBrand(), carDto.getModel(), carDto.getColour(), carDto.getVrp(), type.getId());
                            if (c == null) {
                                service.deleteUser(newUser.getId());
                                return Response.status(Response.Status.BAD_REQUEST).build();
                            }
                            CarResource.carList(carList, type, c);
                        }
                        else {
                            Car c = carService.createCar(newUser.getId() ,carDto.getBrand(), carDto.getModel(), carDto.getColour(), carDto.getVrp());
                            if (c == null) {
                                service.deleteUser(newUser.getId());
                                return Response.status(Response.Status.BAD_REQUEST).build();
                            }
                            carList.add(c);
                        }
                    }
                }
            }
            newUser.setCars(carList);

            UserDto uDto = factory.transformToDto(newUser);
            return Response.status(Response.Status.CREATED).entity(json.writeValueAsString(uDto)).build();
        }
        catch (JsonProcessingException e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

    }

    @DELETE
    @Path("/{id}")
    public Response deleteUser(@HeaderParam(HttpHeaders.AUTHORIZATION) String auth, @PathParam("id") Long id) {
        LOGGER.info("AUTH: " + authFilter.getAuthUser(auth));
        if (!authFilter.getAuthUser(auth))
            return Response.status(Response.Status.UNAUTHORIZED).build();
        if (id != null) {
            User user = service.deleteUser(id);
            if (user != null)
                return Response.status(Response.Status.NO_CONTENT).build();
            else
                return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.status(Response.Status.BAD_REQUEST).build();
    }
}
