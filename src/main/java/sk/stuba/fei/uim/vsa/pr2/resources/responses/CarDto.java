package sk.stuba.fei.uim.vsa.pr2.resources.responses;

import java.util.List;
import java.util.Objects;

public class CarDto extends Dto {

    private String brand;
    private String model;
    private String vrp;
    private String colour;
    private CarTypeDto type;
    private UserDto owner;
    private List<ReservationDto> reservations;

    public CarDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBrand() {
        return brand;
    }

    public String getModel() {
        return model;
    }

    public String getVrp() {
        return vrp;
    }

    public String getColour() {
        return colour;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setVrp(String vrp) {
        this.vrp = vrp;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }

    public UserDto getOwner() {
        return owner;
    }

    public void setOwner(UserDto owner) {
        this.owner = owner;
    }

    public CarTypeDto getType() {
        return type;
    }

    public void setType(CarTypeDto type) {
        this.type = type;
    }

    public List<ReservationDto> getReservations() {
        return reservations;
    }

    public void setReservations(List<ReservationDto> reservations) {
        this.reservations = reservations;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CarDto carDto = (CarDto) o;
        return Objects.equals(brand, carDto.brand) && Objects.equals(model, carDto.model) && Objects.equals(vrp, carDto.vrp) && Objects.equals(colour, carDto.colour) && Objects.equals(owner, carDto.owner) && Objects.equals(type, carDto.type) && Objects.equals(reservations, carDto.reservations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(brand, model, vrp, colour, owner, type, reservations);
    }

    @Override
    public String toString() {
        return "CarDto{" +
                "id=" + id +
                ", brand='" + brand + '\'' +
                ", model='" + model + '\'' +
                ", vrp='" + vrp + '\'' +
                ", colour='" + colour + '\'' +
                ", type=" + type + '\'' +
                ", owner=" + owner + '\'' +
                ", reservations=" + reservations +
                '}';
    }
}
