package sk.stuba.fei.uim.vsa.pr2.resources.responses;

import java.util.List;
import java.util.Objects;

public class ParkingSpotDto extends Dto {

    private String identifier;
    private String carParkFloor;
    private Long carPark;
    private CarTypeDto type;
    private Boolean free;
    private List<ReservationDto> reservations;

    public ParkingSpotDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getCarParkFloor() {
        return carParkFloor;
    }

    public void setCarParkFloor(String carParkFloor) {
        this.carParkFloor = carParkFloor;
    }

    public Long getCarPark() {
        return carPark;
    }

    public void setCarPark(Long carPark) {
        this.carPark = carPark;
    }

    public CarTypeDto getType() {
        return type;
    }

    public void setType(CarTypeDto type) {
        this.type = type;
    }

    public Boolean getFree() {
        return free;
    }

    public void setFree(Boolean free) {
        this.free = free;
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
        ParkingSpotDto that = (ParkingSpotDto) o;
        return Objects.equals(identifier, that.identifier) && Objects.equals(carParkFloor, that.carParkFloor) && Objects.equals(carPark, that.carPark) && Objects.equals(free, that.free) && Objects.equals(type, that.type) && Objects.equals(reservations, that.reservations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier, carParkFloor, carPark, free, type, reservations);
    }

    @Override
    public String toString() {
        return "ParkingSpotDto{" +
                "id=" + id +
                ", identifier='" + identifier + '\'' +
                ", carParkFloor='" + carParkFloor + '\'' +
                ", carPark=" + carPark +
                ", free=" + free +
                ", type=" + type +
                ", reservations=" + reservations +
                '}';
    }
}
