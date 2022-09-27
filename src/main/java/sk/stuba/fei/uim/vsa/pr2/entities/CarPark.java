package sk.stuba.fei.uim.vsa.pr2.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;

@Table(name = "CAR_PARK")
@Entity
@NamedQueries({
        @NamedQuery(name = "CarPark.findByName", query = "select cp from CarPark cp where cp.name = :name"),
        @NamedQuery(name = "CarPark.findAll", query = "select cp from CarPark cp")
})
public class CarPark implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "NAME", unique = true)
    private String name;

    @Column(name = "ADDRESS")
    private String address;

    @Column(name = "PRICE_PER_HOUR")
    private Integer pricePerHour;

    @OneToMany(mappedBy = "carPark", fetch=FetchType.EAGER, orphanRemoval = true)
    private Collection<CarParkFloor> carParkFloors;

    public CarPark() {

    }

    public CarPark(String name, String address, Integer pricePerHour) {
        this.name = name;
        this.address = address;
        this.pricePerHour = pricePerHour;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getPricePerHour() {
        return pricePerHour;
    }

    public void setPricePerHour(Integer pricePerHour) {
        this.pricePerHour = pricePerHour;
    }

    public Collection<CarParkFloor> getCarParkFloors() {
        return carParkFloors;
    }

    public void setCarParkFloors(Collection<CarParkFloor> carParkFloors) {
        this.carParkFloors = carParkFloors;
    }

    @Override
    public String toString() {
        return "CarPark: " +
                "id = " + id +
                ", name = '" + name + '\'' +
                ", address = '" + address + '\'' +
                ", pricePerHour = " + pricePerHour +
                ", CarParkFloors = " + ((carParkFloors != null) ? carParkFloors.size() : 0) + "\n";
    }
}
