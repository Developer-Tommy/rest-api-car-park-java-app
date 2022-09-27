package sk.stuba.fei.uim.vsa.pr2.resources;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import sk.stuba.fei.uim.vsa.pr2.BasicAuthFilter;
import sk.stuba.fei.uim.vsa.pr2.entities.CarType;
import sk.stuba.fei.uim.vsa.pr2.resources.responses.CarTypeDto;
import sk.stuba.fei.uim.vsa.pr2.resources.responses.factories.CarTypeFactory;
import sk.stuba.fei.uim.vsa.pr2.services.CarTypeService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Path("/cartypes")
public class CarTypeResource {

    private static final Logger LOGGER = Logger.getLogger(CarTypeResource.class.getName());
    private final ObjectMapper json = new ObjectMapper();
    private final BasicAuthFilter authFilter = new BasicAuthFilter();
    private final CarTypeService service = new CarTypeService();
    private final CarTypeFactory factory = new CarTypeFactory();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCarTypes(@HeaderParam(HttpHeaders.AUTHORIZATION) String auth, @QueryParam("name") String name) {
        LOGGER.info("AUTH: " + authFilter.getAuthUser(auth));
        if (!authFilter.getAuthUser(auth))
            return Response.status(Response.Status.UNAUTHORIZED).build();
        List<CarTypeDto> carTypeDtos = new ArrayList<>(Collections.emptyList());
        if (name == null){
            List<CarType> carTypes = service.getCarTypes();
            if (!carTypes.isEmpty())
                carTypeDtos = carTypes.stream().map(factory::transformToDto).collect(Collectors.toList());
            try {
                return Response.status(Response.Status.OK).entity(json.writeValueAsString(carTypeDtos)).build();
            } catch (JsonProcessingException e) {
                LOGGER.log(Level.SEVERE, null, e);
                return Response.status(Response.Status.BAD_REQUEST).build();
            }
        }

        else {
            CarType carType = service.getCarType(name);
            if (carType != null) {
                CarTypeDto carTypeDto = factory.transformToDto(carType);
                carTypeDtos.add(carTypeDto);

                try {
                    return Response.status(Response.Status.OK).entity(json.writeValueAsString(carTypeDtos)).build();
                } catch (JsonProcessingException e) {
                    LOGGER.log(Level.SEVERE, null, e);
                    return Response.status(Response.Status.BAD_REQUEST).build();
                }
            }
            return Response.status(Response.Status.NOT_FOUND).entity(carTypeDtos).build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public Response getCarTypeById(@HeaderParam(HttpHeaders.AUTHORIZATION) String auth, @PathParam("id") Long id) {
        LOGGER.info("AUTH: " + authFilter.getAuthUser(auth));
        if (!authFilter.getAuthUser(auth))
            return Response.status(Response.Status.UNAUTHORIZED).build();
        if (id != null){
            CarType carType = service.getCarType(id);
            if (carType != null){
                CarTypeDto carTypeDto = factory.transformToDto(carType);
                try {
                    return Response.status(Response.Status.OK).entity(json.writeValueAsString(carTypeDto)).build();
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
    public Response createCarType(@HeaderParam(HttpHeaders.AUTHORIZATION) String auth, String request) {
        LOGGER.info("AUTH: " + authFilter.getAuthUser(auth));
        if (!authFilter.getAuthUser(auth))
            return Response.status(Response.Status.UNAUTHORIZED).build();
        try {
            CarTypeDto carTypeDto = json.readValue(request, CarTypeDto.class);
            CarType carType = factory.transformToEntity(carTypeDto);
            CarType ct = service.createCarType(carType.getName());
            if (ct == null)
                return Response.status(Response.Status.BAD_REQUEST).build();

            CarTypeDto ctDto = factory.transformToDto(ct);
            return Response.status(Response.Status.CREATED).entity(json.writeValueAsString(ctDto)).build();
        }
        catch (JsonProcessingException e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

    }

    @DELETE
    @Path("/{id}")
    public Response deleteCarType(@HeaderParam(HttpHeaders.AUTHORIZATION) String auth, @PathParam("id") Long id) {
        LOGGER.info("AUTH: " + authFilter.getAuthUser(auth));
        if (!authFilter.getAuthUser(auth))
            return Response.status(Response.Status.UNAUTHORIZED).build();
        if (id != null) {
            CarType carType = service.deleteCarType(id);
            if (carType != null)
                return Response.status(Response.Status.NO_CONTENT).build();
            else
                return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.status(Response.Status.BAD_REQUEST).build();
    }
}
