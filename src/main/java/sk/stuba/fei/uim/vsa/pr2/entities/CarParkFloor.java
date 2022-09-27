package sk.stuba.fei.uim.vsa.pr2.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;

@Table(name = "CAR_PARK_FLOOR")
@NamedQueries({
        @NamedQuery(name = "CarParkFloor.findByFloorIdentifier_CarParkId", query = "select c from CarParkFloor c where c.floorIdentifier.carParkId = :carParkId")
})
@Entity
public class CarParkFloor implements Serializable {

    @EmbeddedId
    private CarParkFloorId floorIdentifier;

    @ManyToOne
    @MapsId("carParkId")
    @JoinColumn(name = "CAR_PARK_ID")
    private CarPark carPark;

    @OneToMany(mappedBy = "carParkFloor", fetch=FetchType.EAGER, orphanRemoval = true)
    private Collection<ParkingSpot> parkingSpots;

    public CarParkFloor() {
    }

    public CarParkFloor(CarParkFloorId floorIdentifier, CarPark carPark) {
        this.floorIdentifier = floorIdentifier;
        this.carPark = carPark;
    }

    public CarParkFloorId getFloorIdentifier() {
        return floorIdentifier;
    }

    public void setFloorIdentifier(CarParkFloorId floorIdentifier) {
        this.floorIdentifier = floorIdentifier;
    }

    public CarPark getCarPark() {
        return carPark;
    }

    public void setCarPark(CarPark carPark) {
        this.carPark = carPark;
    }

    public Collection<ParkingSpot> getParkingSpots() {
        return parkingSpots;
    }

    public void setParkingSpots(Collection<ParkingSpot> parkingSpots) {
        this.parkingSpots = parkingSpots;
    }

    @Override
    public String toString() {
        return "CarParkFloor: " +
                "floorIdentifier = '" + floorIdentifier.getFloorIdentifierId() + '\'' +
                ", carPark = '" + carPark.getName() + '\'' +
                ", parkingSpots = " + ((parkingSpots != null) ? parkingSpots.size() : 0) + "\n";
    }
}
