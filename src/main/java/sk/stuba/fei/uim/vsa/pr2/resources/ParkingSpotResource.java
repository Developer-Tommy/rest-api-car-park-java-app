package sk.stuba.fei.uim.vsa.pr2.resources;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import sk.stuba.fei.uim.vsa.pr2.BasicAuthFilter;
import sk.stuba.fei.uim.vsa.pr2.entities.ParkingSpot;
import sk.stuba.fei.uim.vsa.pr2.resources.responses.ParkingSpotDto;
import sk.stuba.fei.uim.vsa.pr2.resources.responses.factories.ParkingSpotFactory;
import sk.stuba.fei.uim.vsa.pr2.services.ParkingSpotService;

import java.util.logging.Level;
import java.util.logging.Logger;

@Path("/parkingspots")
public class ParkingSpotResource {

    private static final Logger LOGGER = Logger.getLogger(ParkingSpotResource.class.getName());
    private final ObjectMapper json = new ObjectMapper();
    private final BasicAuthFilter authFilter = new BasicAuthFilter();
    private final ParkingSpotService service = new ParkingSpotService();
    private final ParkingSpotFactory factory = new ParkingSpotFactory();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public Response getSpotById(@HeaderParam(HttpHeaders.AUTHORIZATION) String auth, @PathParam("id") Long id) {
        LOGGER.info("AUTH: " + authFilter.getAuthUser(auth));
        if (!authFilter.getAuthUser(auth))
            return Response.status(Response.Status.UNAUTHORIZED).build();
        if (id != null){
            ParkingSpot parkingSpot = service.getParkingSpot(id);
            if (parkingSpot != null){
                ParkingSpotDto parkingSpotDto = factory.transformToDto(parkingSpot);
                try {
                    return Response.status(Response.Status.OK).entity(json.writeValueAsString(parkingSpotDto)).build();
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

    @DELETE
    @Path("/{id}")
    public Response deleteParkingSpot(@HeaderParam(HttpHeaders.AUTHORIZATION) String auth, @PathParam("id") Long id) {
        LOGGER.info("AUTH: " + authFilter.getAuthUser(auth));
        if (!authFilter.getAuthUser(auth))
            return Response.status(Response.Status.UNAUTHORIZED).build();
        if (id != null) {
            ParkingSpot parkingSpot = service.deleteParkingSpot(id);
            if (parkingSpot != null)
                return Response.status(Response.Status.NO_CONTENT).build();
            else
                return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.status(Response.Status.BAD_REQUEST).build();
    }

}
