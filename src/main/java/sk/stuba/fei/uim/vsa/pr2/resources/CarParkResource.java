package sk.stuba.fei.uim.vsa.pr2.resources;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import sk.stuba.fei.uim.vsa.pr2.BasicAuthFilter;
import sk.stuba.fei.uim.vsa.pr2.entities.*;
import sk.stuba.fei.uim.vsa.pr2.resources.responses.*;
import sk.stuba.fei.uim.vsa.pr2.resources.responses.factories.*;
import sk.stuba.fei.uim.vsa.pr2.services.*;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Path("/carparks")
public class CarParkResource {

    private static final Logger LOGGER = Logger.getLogger(CarParkResource.class.getName());
    private final ObjectMapper json = new ObjectMapper();
    private final BasicAuthFilter authFilter = new BasicAuthFilter();
    private final CarParkService service = new CarParkService();
    private final CarParkFactory factory = new CarParkFactory();
    private final CarParkFloorService carParkFloorService = new CarParkFloorService();
    private final CarParkFloorFactory carParkFloorFactory = new CarParkFloorFactory();
    private final ParkingSpotService parkingSpotService = new ParkingSpotService();
    private final ParkingSpotFactory parkingSpotFactory = new ParkingSpotFactory();
    private final CarTypeService serviceType = new CarTypeService();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCarParks(@HeaderParam(HttpHeaders.AUTHORIZATION) String auth, @QueryParam("name") String name){
        LOGGER.info("AUTH: " + authFilter.getAuthUser(auth));
        if (!authFilter.getAuthUser(auth))
            return Response.status(Response.Status.UNAUTHORIZED).build();
        List<CarParkDto> carParkDtos = new ArrayList<>(Collections.emptyList());
        if (name == null){
            List<CarPark> carParks = service.getCarParks();
            if (!carParks.isEmpty())
                carParkDtos = carParks.stream().map(factory::transformToDto).collect(Collectors.toList());

            try {
                return Response.status(Response.Status.OK).entity(json.writeValueAsString(carParkDtos)).build();
            } catch (JsonProcessingException e) {
                LOGGER.log(Level.SEVERE, null, e);
                return Response.status(Response.Status.BAD_REQUEST).build();
            }
        }

        else {
            CarPark carPark = service.getCarPark(name);
            if (carPark != null) {
                CarParkDto carParkDto = factory.transformToDto(carPark);
                carParkDtos.add(carParkDto);

                try {
                    return Response.status(Response.Status.OK).entity(json.writeValueAsString(carParkDtos)).build();
                } catch (JsonProcessingException e) {
                    LOGGER.log(Level.SEVERE, null, e);
                    return Response.status(Response.Status.BAD_REQUEST).build();
                }
            }
            return Response.status(Response.Status.NOT_FOUND).entity(carParkDtos).build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public Response getCarParkById(@HeaderParam(HttpHeaders.AUTHORIZATION) String auth, @PathParam("id") Long id) {
        LOGGER.info("AUTH: " + authFilter.getAuthUser(auth));
        if (!authFilter.getAuthUser(auth))
            return Response.status(Response.Status.UNAUTHORIZED).build();
        if (id != null){
            CarPark carPark = service.getCarPark(id);
            if (carPark != null){
                CarParkDto carParkDto = factory.transformToDto(carPark);
                try {
                    return Response.status(Response.Status.OK).entity(json.writeValueAsString(carParkDto)).build();
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
    public Response createCarPark(@HeaderParam(HttpHeaders.AUTHORIZATION) String auth, String request) {
        LOGGER.info("AUTH: " + authFilter.getAuthUser(auth));
        if (!authFilter.getAuthUser(auth))
            return Response.status(Response.Status.UNAUTHORIZED).build();
        try {
            CarParkDto carParkDto = json.readValue(request, CarParkDto.class);
            CarPark carPark = factory.transformToEntity(carParkDto);
            CarPark cp = service.createCarPark(carPark.getName(), carPark.getAddress(), carPark.getPricePerHour());

            if (cp == null)
                return Response.status(Response.Status.BAD_REQUEST).build();

            List<CarParkFloor> carParkFloorList = new ArrayList<>(Collections.emptyList());
            if (carParkDto.getFloors() != null){
                List<CarParkFloorDto> carParkFloorsDtos = new ArrayList<>(carParkDto.getFloors());
                if (!carParkFloorsDtos.isEmpty()){
                    for (CarParkFloorDto cpfDto : carParkFloorsDtos) {
                        CarParkFloor carParkFloor = carParkFloorService.createCarParkFloor(cp.getId(), cpfDto.getIdentifier());
                        if (carParkFloor == null){
                            service.deleteCarPark(cp.getId());
                            return Response.status(Response.Status.BAD_REQUEST).build();
                        }
                        if (cpfDto.getSpots() != null){
                            List<ParkingSpotDto> parkingSpotDtos = new ArrayList<>(cpfDto.getSpots());
                            if (!parkingSpotDtos.isEmpty()) {
                                List<ParkingSpot> parkingSpotList = new ArrayList<>();
                                for (ParkingSpotDto spot : parkingSpotDtos) {
                                    if (spot.getType() != null) {
                                        CarType type = serviceType.getCarType(spot.getType().getName());
                                        if (type == null){
                                            type = serviceType.createCarType(spot.getType().getName());
                                        }
                                        ParkingSpot parkingSpot = parkingSpotService.createParkingSpot(cp.getId(), carParkFloor.getFloorIdentifier().getFloorIdentifierId(), spot.getIdentifier(), type.getId());
                                        if (parkingSpot == null){
                                            service.deleteCarPark(cp.getId());
                                            return Response.status(Response.Status.BAD_REQUEST).build();
                                        }
                                        typeList(parkingSpotList, type, parkingSpot);
                                    }
                                    else {
                                        ParkingSpot parkingSpot = parkingSpotService.createParkingSpot(cp.getId(), carParkFloor.getFloorIdentifier().getFloorIdentifierId(), spot.getIdentifier());
                                        if (parkingSpot == null){
                                            service.deleteCarPark(cp.getId());
                                            return Response.status(Response.Status.BAD_REQUEST).build();
                                        }
                                        parkingSpotList.add(parkingSpot);
                                    }
                                }
                                carParkFloor.setParkingSpots(parkingSpotList);
                            }
                        }
                        carParkFloorList.add(carParkFloor);
                    }
                }
            }

            cp.setCarParkFloors(carParkFloorList);
            CarParkDto cpDto = factory.transformToDto(cp);
            return Response.status(Response.Status.CREATED).entity(json.writeValueAsString(cpDto)).build();
        }
        catch (JsonProcessingException e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

    }

    @DELETE
    @Path("/{id}")
    public Response deleteCarPark(@HeaderParam(HttpHeaders.AUTHORIZATION) String auth, @PathParam("id") Long id) {
        LOGGER.info("AUTH: " + authFilter.getAuthUser(auth));
        if (!authFilter.getAuthUser(auth))
            return Response.status(Response.Status.UNAUTHORIZED).build();
        if (id != null) {
            CarPark carPark = service.deleteCarPark(id);
            if (carPark != null)
                return Response.status(Response.Status.NO_CONTENT).build();
            else
                return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.status(Response.Status.BAD_REQUEST).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}/floors")
    public Response getFloorsByCarParkId(@HeaderParam(HttpHeaders.AUTHORIZATION) String auth, @PathParam("id") Long id) {
        LOGGER.info("AUTH: " + authFilter.getAuthUser(auth));
        if (!authFilter.getAuthUser(auth))
            return Response.status(Response.Status.UNAUTHORIZED).build();
        if (id != null){
            CarPark carPark = service.getCarPark(id);
            List<CarParkFloor> carParkFloors = carParkFloorService.getCarParkFloors(id);
            try {
                if (carPark == null)
                    return Response.status(Response.Status.BAD_REQUEST).build();
                if (carParkFloors.isEmpty())
                    return Response.status(Response.Status.NOT_FOUND).entity(carParkFloors).build();
                List<CarParkFloorDto> carParkFloorDtos = carParkFloors.stream().map(carParkFloorFactory::transformToDto).collect(Collectors.toList());
                return Response.status(Response.Status.OK).entity(json.writeValueAsString(carParkFloorDtos)).build();
            } catch (JsonProcessingException e) {
                LOGGER.log(Level.SEVERE, null, e);
                return Response.status(Response.Status.BAD_REQUEST).build();
            }
        }
        return Response.status(Response.Status.BAD_REQUEST).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}/floors/{identifier}")
    public Response getFloorByIdentifier(@HeaderParam(HttpHeaders.AUTHORIZATION) String auth, @PathParam("id") Long id, @PathParam("identifier") String identifier) {
        LOGGER.info("AUTH: " + authFilter.getAuthUser(auth));
        if (!authFilter.getAuthUser(auth))
            return Response.status(Response.Status.UNAUTHORIZED).build();
        if (id != null && identifier != null){
            CarParkFloor carParkFloor = carParkFloorService.getCarParkFloor(id, identifier);
            if (carParkFloor != null) {
                CarParkFloorDto carParkFloorDto = carParkFloorFactory.transformToDto(carParkFloor);
                try {
                    return Response.status(Response.Status.OK).entity(json.writeValueAsString(carParkFloorDto)).build();
                } catch (JsonProcessingException e) {
                    LOGGER.log(Level.SEVERE, null, e);
                    return Response.status(Response.Status.BAD_REQUEST).build();
                }
            }
            else return Response.status(Response.Status.NOT_FOUND).build();

        }
        return Response.status(Response.Status.BAD_REQUEST).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{id}/floors")
    public Response createCarParkFloor(@HeaderParam(HttpHeaders.AUTHORIZATION) String auth, @PathParam("id") Long id, String request) {
        LOGGER.info("AUTH: " + authFilter.getAuthUser(auth));
        if (!authFilter.getAuthUser(auth))
            return Response.status(Response.Status.UNAUTHORIZED).build();
        try {
            CarPark carPark = service.getCarPark(id);
            if (carPark == null)
                return Response.status(Response.Status.BAD_REQUEST).build();

            CarParkFloorDto carParkFloorDto = json.readValue(request, CarParkFloorDto.class);
            CarParkFloor cpfs = carParkFloorService.createCarParkFloor(id, carParkFloorDto.getIdentifier());

            if (cpfs == null){
                return Response.status(Response.Status.BAD_REQUEST).build();
            }

            List<ParkingSpot> parkingSpotList = new ArrayList<>(Collections.emptyList());
            if (carParkFloorDto.getSpots() != null){
                List<ParkingSpotDto> parkingSpotDtos = new ArrayList<>(carParkFloorDto.getSpots());
                if (!parkingSpotDtos.isEmpty()){
                    for (ParkingSpotDto spot : parkingSpotDtos) {
                        if (spot.getType() != null){
                            CarType type = serviceType.getCarType(spot.getType().getName());
                            if (type == null){
                                type = serviceType.createCarType(spot.getType().getName());
                            }
                            ParkingSpot parkingSpot = parkingSpotService.createParkingSpot(id, cpfs.getFloorIdentifier().getFloorIdentifierId(), spot.getIdentifier(), type.getId());
                            if (parkingSpot == null){
                                carParkFloorService.deleteCarParkFloor(id, cpfs.getFloorIdentifier().getFloorIdentifierId());
                                return Response.status(Response.Status.BAD_REQUEST).build();
                            }
                            typeList(parkingSpotList, type, parkingSpot);
                        }
                        else {
                            ParkingSpot parkingSpot = parkingSpotService.createParkingSpot(id, cpfs.getFloorIdentifier().getFloorIdentifierId(), spot.getIdentifier());
                            if (parkingSpot == null){
                                carParkFloorService.deleteCarParkFloor(id, cpfs.getFloorIdentifier().getFloorIdentifierId());
                                return Response.status(Response.Status.BAD_REQUEST).build();
                            }
                            parkingSpotList.add(parkingSpot);
                        }
                    }
                }
            }

            cpfs.setParkingSpots(parkingSpotList);
            List<CarParkFloor> carParkFloors = new ArrayList<>(carPark.getCarParkFloors());
            carParkFloors.add(cpfs);
            carPark.setCarParkFloors(carParkFloors);

            CarParkFloorDto cpfDto = carParkFloorFactory.transformToDto(cpfs);
            return Response.status(Response.Status.CREATED).entity(json.writeValueAsString(cpfDto)).build();
        }
        catch (JsonProcessingException e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

    }

    @DELETE
    @Path("/{id}/floors/{identifier}")
    public Response deleteCarParkFloor(@HeaderParam(HttpHeaders.AUTHORIZATION) String auth, @PathParam("id") Long id, @PathParam("identifier") String identifier) {
        LOGGER.info("AUTH: " + authFilter.getAuthUser(auth));
        if (!authFilter.getAuthUser(auth))
            return Response.status(Response.Status.UNAUTHORIZED).build();
        if (id != null && identifier != null) {
            CarParkFloor carParkFloor = carParkFloorService.deleteCarParkFloor(id, identifier);
            if (carParkFloor != null)
                return Response.status(Response.Status.NO_CONTENT).build();
            else
                return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.status(Response.Status.BAD_REQUEST).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}/spots")
    public Response getSpotsByCarParkId(@HeaderParam(HttpHeaders.AUTHORIZATION) String auth, @PathParam("id") Long id, @QueryParam("free") Boolean free) {
        LOGGER.info("AUTH: " + authFilter.getAuthUser(auth));
        if (!authFilter.getAuthUser(auth))
            return Response.status(Response.Status.UNAUTHORIZED).build();
        if (id != null){
            CarPark carPark = service.getCarPark(id);
            if (carPark == null)
                return Response.status(Response.Status.BAD_REQUEST).build();
            if (free != null) {
                if (free){
                    List<ParkingSpot> parkingSpots = new ArrayList<>(Collections.emptyList());
                    Map<String, List<ParkingSpot>> availableSpotsMap = parkingSpotService.getAvailableParkingSpots(carPark.getName());

                    if (!availableSpotsMap.isEmpty()) {
                        return getResponse(parkingSpots, availableSpotsMap);
                    }
                    else return Response.status(Response.Status.OK).entity(parkingSpots).build();
                }
                else {
                    List<ParkingSpot> parkingSpots = new ArrayList<>(Collections.emptyList());
                    Map<String, List<ParkingSpot>> occupiedSpotsMap = parkingSpotService.getOccupiedParkingSpots(carPark.getName());

                    if (!occupiedSpotsMap.isEmpty()) {
                        return getResponse(parkingSpots, occupiedSpotsMap);
                    }
                    else return Response.status(Response.Status.OK).entity(parkingSpots).build();
                }
            }

            List<ParkingSpot> parkingSpots = new ArrayList<>(Collections.emptyList());
            Map<String, List<ParkingSpot>> parkingSpotsMap = parkingSpotService.getParkingSpots(id);

            if (!parkingSpotsMap.isEmpty()) {
                return getResponse(parkingSpots, parkingSpotsMap);
            }
            else return Response.status(Response.Status.NOT_FOUND).entity(parkingSpots).build();

        }
        return Response.status(Response.Status.BAD_REQUEST).build();
    }

    private Response getResponse(List<ParkingSpot> parkingSpots, Map<String, List<ParkingSpot>> parkingSpotsMap) {
        parkingSpotsMap.forEach((key, value) -> parkingSpots.addAll(value));
        return getResponse(parkingSpots);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}/floors/{identifier}/spots")
    public Response getSpotsByIdentifier(@HeaderParam(HttpHeaders.AUTHORIZATION) String auth, @PathParam("id") Long id, @PathParam("identifier") String identifier) {
        LOGGER.info("AUTH: " + authFilter.getAuthUser(auth));
        if (!authFilter.getAuthUser(auth))
            return Response.status(Response.Status.UNAUTHORIZED).build();
        if (id != null && identifier != null){
            CarPark carPark = service.getCarPark(id);
            CarParkFloor carParkFloor = carParkFloorService.getCarParkFloor(id, identifier);
            if (carPark == null)
                return Response.status(Response.Status.BAD_REQUEST).build();
            if (carParkFloor == null)
                return Response.status(Response.Status.BAD_REQUEST).build();
            List<ParkingSpot> parkingSpots = parkingSpotService.getParkingSpots(id, identifier);
            if (!parkingSpots.isEmpty()) {
                return getResponse(parkingSpots);
            }
            else return Response.status(Response.Status.OK).entity(parkingSpots).build();

        }
        return Response.status(Response.Status.BAD_REQUEST).build();
    }

    private Response getResponse(List<ParkingSpot> parkingSpots) {
        List<ParkingSpotDto> parkingSpotDtos = parkingSpots.stream().map(parkingSpotFactory::transformToDto).collect(Collectors.toList());
        try {
            return Response.status(Response.Status.OK).entity(json.writeValueAsString(parkingSpotDtos)).build();
        } catch (JsonProcessingException e) {
            LOGGER.log(Level.SEVERE, null, e);
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{id}/floors/{identifier}/spots")
    public Response createParkingSpot(@HeaderParam(HttpHeaders.AUTHORIZATION) String auth, @PathParam("id") Long id, @PathParam("identifier") String identifier, String request) {
        LOGGER.info("AUTH: " + authFilter.getAuthUser(auth));
        if (!authFilter.getAuthUser(auth))
            return Response.status(Response.Status.UNAUTHORIZED).build();
        try {
            CarPark carPark = service.getCarPark(id);
            if (carPark == null)
                return Response.status(Response.Status.BAD_REQUEST).build();
            CarParkFloor carParkFloor = carParkFloorService.getCarParkFloor(id, identifier);
            if (carParkFloor == null)
                return Response.status(Response.Status.BAD_REQUEST).build();

            ParkingSpotDto parkingSpotDto = json.readValue(request, ParkingSpotDto.class);
            List<ParkingSpot> parkingSpots = new ArrayList<>(Collections.emptyList());

            if (carParkFloor.getParkingSpots() != null)
                parkingSpots = new ArrayList<>(carParkFloor.getParkingSpots());

            if (parkingSpotDto.getType() != null){
                CarType type = serviceType.getCarType(parkingSpotDto.getType().getName());
                if (type == null){
                    type = serviceType.createCarType(parkingSpotDto.getType().getName());
                }
                ParkingSpot parkingSpot = parkingSpotService.createParkingSpot(id, identifier, parkingSpotDto.getIdentifier(), type.getId());
                if (parkingSpot == null){
                    return Response.status(Response.Status.BAD_REQUEST).build();
                }
                typeList(parkingSpots, type, parkingSpot);

                carParkFloor.setParkingSpots(parkingSpots);
                ParkingSpotDto psDto = parkingSpotFactory.transformToDto(parkingSpot);
                return Response.status(Response.Status.CREATED).entity(json.writeValueAsString(psDto)).build();
            }
            else {
                ParkingSpot parkingSpot = parkingSpotService.createParkingSpot(id, identifier, parkingSpotDto.getIdentifier());
                if (parkingSpot == null){
                    return Response.status(Response.Status.BAD_REQUEST).build();
                }
                parkingSpots.add(parkingSpot);
                carParkFloor.setParkingSpots(parkingSpots);
                ParkingSpotDto psDto = parkingSpotFactory.transformToDto(parkingSpot);
                return Response.status(Response.Status.CREATED).entity(json.writeValueAsString(psDto)).build();
            }
        }
        catch (JsonProcessingException e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

    }

    private void typeList(List<ParkingSpot> parkingSpots, CarType type, ParkingSpot parkingSpot) {
        List<ParkingSpot> spotList;
        if (type.getParkingSpots() != null){
            spotList = new ArrayList<>(type.getParkingSpots());
        }
        else {
            spotList = new ArrayList<>(Collections.emptyList());
        }
        spotList.add(parkingSpot);
        type.setParkingSpots(spotList);
        parkingSpots.add(parkingSpot);
    }

}
