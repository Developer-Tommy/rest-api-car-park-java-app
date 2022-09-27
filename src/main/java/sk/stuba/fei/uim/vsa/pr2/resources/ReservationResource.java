package sk.stuba.fei.uim.vsa.pr2.resources;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import sk.stuba.fei.uim.vsa.pr2.BasicAuthFilter;
import sk.stuba.fei.uim.vsa.pr2.entities.Reservation;
import sk.stuba.fei.uim.vsa.pr2.entities.User;
import sk.stuba.fei.uim.vsa.pr2.resources.responses.ReservationDto;
import sk.stuba.fei.uim.vsa.pr2.resources.responses.factories.ReservationFactory;
import sk.stuba.fei.uim.vsa.pr2.services.ReservationService;
import sk.stuba.fei.uim.vsa.pr2.services.UserService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Path("/reservations")
public class ReservationResource {

    private static final Logger LOGGER = Logger.getLogger(ReservationResource.class.getName());
    private final ObjectMapper json = new ObjectMapper();
    private final BasicAuthFilter authFilter = new BasicAuthFilter();
    private final ReservationService service = new ReservationService();
    private final UserService serviceUser = new UserService();
    private final ReservationFactory factory = new ReservationFactory();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getReservations(@HeaderParam(HttpHeaders.AUTHORIZATION) String auth, @QueryParam("user") Long userId, @QueryParam("spot") Long spot, @QueryParam("date") String date) throws ParseException {
        LOGGER.info("AUTH: " + authFilter.getAuthUser(auth));
        if (!authFilter.getAuthUser(auth))
            return Response.status(Response.Status.UNAUTHORIZED).build();
        if ((spot == null && date != null) || (spot != null && date == null))
            return Response.status(Response.Status.BAD_REQUEST).build();
        List<User> list = new ArrayList<>(Collections.emptyList());
        List<ReservationDto> reservationDtos = new ArrayList<>(Collections.emptyList());
        if (userId != null){
            User user = serviceUser.getUser(userId);
            if (user == null)
                return Response.status(Response.Status.OK).entity(list).build();
            List<Reservation> myReservations = service.getMyReservations(userId);
            if (!myReservations.isEmpty()) {
                if (spot != null && date != null) {
                    List<Reservation> reservationList = new ArrayList<>(Collections.emptyList());
                    Date newDate = new SimpleDateFormat("yyyy-MM-dd").parse(date);
                    List<Reservation> reservations = service.getReservations(spot, newDate);
                    if (!reservations.isEmpty()) {
                        for (Reservation r : reservations) {
                            if (Objects.equals(r.getCar().getUser().getId(), userId)) {
                                reservationList.add(r);
                            }
                        }
                    }
                    try {
                        if (!reservationList.isEmpty())
                            reservationDtos = reservationList.stream().map(factory::transformToDto).collect(Collectors.toList());
                        return Response.status(Response.Status.OK).entity(json.writeValueAsString(reservationDtos)).build();
                    } catch (JsonProcessingException e) {
                        LOGGER.log(Level.SEVERE, null, e);
                        return Response.status(Response.Status.BAD_REQUEST).build();
                    }
                }
                else {
                    return getResponse(myReservations);
                }
            }
            else
                return Response.status(Response.Status.OK).entity(myReservations).build();

        }
        if (spot != null && date != null){
            Date newDate = new SimpleDateFormat("yyyy-MM-dd").parse(date);
            List<Reservation> reservations = service.getReservations(spot, newDate);
            if (!reservations.isEmpty()){
                return getResponse(reservations);
            }
            return Response.status(Response.Status.OK).entity(reservations).build();
        }
        List<Reservation> reservations = service.getReservations();
        if (!reservations.isEmpty()){
            try {
                reservationDtos = reservations.stream().map(factory::transformToDto).collect(Collectors.toList());
                return Response.status(Response.Status.OK).entity(json.writeValueAsString(reservationDtos)).build();
            } catch (JsonProcessingException e) {
                LOGGER.log(Level.SEVERE, null, e);
                return Response.status(Response.Status.BAD_REQUEST).build();
            }
        }
        return Response.status(Response.Status.OK).entity(reservations).build();
    }

    private Response getResponse(List<Reservation> reservations) {
        List<ReservationDto> reservationDtos;
        reservationDtos = reservations.stream().map(factory::transformToDto).collect(Collectors.toList());
        try {
            return Response.status(Response.Status.OK).entity(json.writeValueAsString(reservationDtos)).build();
        } catch (JsonProcessingException e) {
            LOGGER.log(Level.SEVERE, null, e);
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public Response getReservationById(@HeaderParam(HttpHeaders.AUTHORIZATION) String auth, @PathParam("id") Long id) {
        LOGGER.info("AUTH: " + authFilter.getAuthUser(auth));
        if (!authFilter.getAuthUser(auth))
            return Response.status(Response.Status.UNAUTHORIZED).build();
        if (id != null){
            Reservation reservation = service.getReservation(id);
            if (reservation != null){
                ReservationDto reservationDto = factory.transformToDto(reservation);
                try {
                    return Response.status(Response.Status.OK).entity(json.writeValueAsString(reservationDto)).build();
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
    public Response createReservation(@HeaderParam(HttpHeaders.AUTHORIZATION) String auth, String request) {
        LOGGER.info("AUTH: " + authFilter.getAuthUser(auth));
        if (!authFilter.getAuthUser(auth))
            return Response.status(Response.Status.UNAUTHORIZED).build();
        try {
            ReservationDto reservationDto = json.readValue(request, ReservationDto.class);
            Reservation reservation = factory.transformToEntity(reservationDto);
            if (reservation.getCar() == null)
                return Response.status(Response.Status.NOT_FOUND).build();
            if (reservation.getParkingSpot() == null)
                return Response.status(Response.Status.NOT_FOUND).build();

            Reservation r = service.createReservation(reservation.getParkingSpot().getId(), reservation.getCar().getId());
            if (r == null){
                return Response.status(Response.Status.BAD_REQUEST).build();
            }
            ReservationDto rDto = factory.transformToDto(r);
            return Response.status(Response.Status.CREATED).entity(rDto).build();

        } catch (JsonProcessingException e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}/end")
    public Response endReservation(@HeaderParam(HttpHeaders.AUTHORIZATION) String auth, @PathParam("id") Long id) {
        LOGGER.info("AUTH: " + authFilter.getAuthUser(auth));
        if (!authFilter.getAuthUser(auth))
            return Response.status(Response.Status.UNAUTHORIZED).build();
        Reservation reservation = service.getReservation(id);
        User user = authFilter.getAuthReservationUser(auth, reservation);
        if (user == null)
            return Response.status(Response.Status.UNAUTHORIZED).build();
        if (reservation != null) {
            Reservation r = service.endReservation(id);
            ReservationDto rDto = factory.transformToDto(r);
            return Response.status(Response.Status.CREATED).entity(rDto).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

}
