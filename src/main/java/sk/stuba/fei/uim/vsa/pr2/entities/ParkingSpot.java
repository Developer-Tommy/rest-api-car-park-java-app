package sk.stuba.fei.uim.vsa.pr2.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;

@Table(name = "PARKING_SPOT", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"CAR_PARK_ID", "SPOT_IDENTIFIER"})
})
@NamedQueries({
        @NamedQuery(name = "ParkingSpot.findParkingSpotByFloorIdentifier", query = "select p from ParkingSpot p where p.carParkFloor.floorIdentifier = :floorIdentifier"),
        @NamedQuery(name = "ParkingSpot.findParkingSpotByCarParkId", query = "select p from ParkingSpot p where p.carParkFloor.floorIdentifier.carParkId = :carParkId"),
        @NamedQuery(name = "ParkingSpot.findAllOccupiedParkingSpots", query = "select p from ParkingSpot p inner join p.reservations reservations where reservations.price is null and p.carParkFloor.carPark.name = :name"),
        @NamedQuery(name = "ParkingSpot.findParkingSpotByCarParkName", query = "select p from ParkingSpot p where p.carParkFloor.carPark.name = :name")
})

@Entity
public class ParkingSpot implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "SPOT_IDENTIFIER")
    private String spotIdentifier;

    @ManyToOne()
    @JoinColumns({
            @JoinColumn(name = "FLOOR_IDENTIFIER", referencedColumnName = "FLOOR_IDENTIFIER"),
            @JoinColumn(name = "CAR_PARK_ID", referencedColumnName = "CAR_PARK_ID")
    })
    private CarParkFloor carParkFloor;

    @OneToMany(mappedBy = "parkingSpot", fetch = FetchType.EAGER)
    private Collection<Reservation> reservations;

    @ManyToOne
    @JoinColumn(name = "CAR_TYPE_ID")
    private CarType carType;

    public ParkingSpot() {
    }

    public ParkingSpot(String spotIdentifier, CarParkFloor carParkFloor, CarType carType) {
        this.spotIdentifier = spotIdentifier;
        this.carParkFloor = carParkFloor;
        this.carType = carType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CarType getCarType() {
        return carType;
    }

    public void setCarType(CarType carType) {
        this.carType = carType;
    }

    public String getSpotIdentifier() {
        return spotIdentifier;
    }

    public void setSpotIdentifier(String spotIdentifier) {
        this.spotIdentifier = spotIdentifier;
    }

    public CarParkFloor getCarParkFloor() {
        return carParkFloor;
    }

    public void setCarParkFloor(CarParkFloor carParkFloor) {
        this.carParkFloor = carParkFloor;
    }

    public Collection<Reservation> getReservations() {
        return reservations;
    }

    public void setReservations(Collection<Reservation> reservations) {
        this.reservations = reservations;
    }

    @Override
    public String toString() {
        return "ParkingSpot: " +
                "id = " + id +
                ", spotIdentifier = '" + spotIdentifier + '\'' +
                ", carParkFloor = '" + carParkFloor.getFloorIdentifier().getFloorIdentifierId() + '\'' +
                ", carType = '" + carType.getName() + '\'' +
                ", (Non)/Active reservations = '" + ((reservations != null) ? reservations.size() : 0) + '\'' + "\n";
    }
}
