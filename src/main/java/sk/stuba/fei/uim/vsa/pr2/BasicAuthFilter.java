package sk.stuba.fei.uim.vsa.pr2;
import sk.stuba.fei.uim.vsa.pr2.entities.Reservation;
import sk.stuba.fei.uim.vsa.pr2.entities.User;
import sk.stuba.fei.uim.vsa.pr2.services.UserService;

import java.util.Base64;
import java.util.Objects;

public class BasicAuthFilter {

    private static final UserService userService = new UserService();

    public Boolean getAuthUser(String auth) {
        String encoded = auth.substring("Basic ".length());
        try {
            String decoded = new String(Base64.getDecoder().decode(encoded));
            if (!decoded.contains(":"))
                return false;
            String email = decoded.split(":")[0];
            String id = decoded.split(":")[1];
            User user  = userService.getUser(email);
            if (user == null)
                return false;
            return Objects.equals(String.valueOf(user.getId()), id);
        }
        catch (IllegalArgumentException e){
            return false;
        }
    }

    public User getAuthReservationUser(String auth, Reservation reservation) {
        String encoded = auth.substring("Basic ".length());
        String decoded = new String(Base64.getDecoder().decode(encoded));
        if (!decoded.contains(":"))
            return null;
        String email = decoded.split(":")[0];
        User user  = userService.getUser(email);
        if (reservation == null)
            return null;
        if (Objects.equals(reservation.getCar().getUser().getId(), user.getId())) {
            return user;
        }
        return null;
    }
}
