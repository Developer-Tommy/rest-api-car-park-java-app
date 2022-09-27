package sk.stuba.fei.uim.vsa.pr2.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;

@Table(name = "CAR_TYPE")
@NamedQueries({
        @NamedQuery(name = "CarType.findAll", query = "select c from CarType c"),
        @NamedQuery(name = "CarType.findById", query = "select c from CarType c where c.id = :id"),
        @NamedQuery(name = "CarType.findByName", query = "select c from CarType c where c.name = :name")
})
@Entity
public class CarType implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "NAME", unique = true)
    private String name;

    @OneToMany(mappedBy = "carType", fetch=FetchType.EAGER, orphanRemoval = true)
    private Collection<Car> cars;

    @OneToMany(mappedBy = "carType", fetch=FetchType.EAGER, orphanRemoval = true)
    private Collection<ParkingSpot> parkingSpots;

    public CarType() {
    }

    public CarType(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Collection<Car> getCars() {
        return cars;
    }

    public void setCars(Collection<Car> cars) {
        this.cars = cars;
    }

    public Collection<ParkingSpot> getParkingSpots() {
        return parkingSpots;
    }

    public void setParkingSpots(Collection<ParkingSpot> parkingSpots) {
        this.parkingSpots = parkingSpots;
    }

    @Override
    public String toString() {
        return "CarType: " +
                "id = " + id +
                ", name = '" + name + '\'' + "\n";
    }
}
