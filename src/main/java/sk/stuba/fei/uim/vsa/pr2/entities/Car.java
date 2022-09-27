package sk.stuba.fei.uim.vsa.pr2.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;

@Table(name = "CAR")
@NamedQueries({
        @NamedQuery(name = "Car.findAll", query = "select c from Car c"),
        @NamedQuery(name = "Car.findByVehicleRegistrationPlate", query = "select c from Car c where c.vehicleRegistrationPlate = :vehicleRegistrationPlate"),
        @NamedQuery(name = "Car.findByUserId", query = "select c from Car c where c.user.id = :id")
})
@Entity
public class Car implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "BRAND")
    private String brand;

    @Column(name = "MODEL")
    private String model;

    @Column(name = "VEHICLE_REGISTRATION_PLATE", unique = true)
    private String vehicleRegistrationPlate;

    @Column(name = "COLOUR")
    private String colour;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private User user;

    @OneToMany(mappedBy = "car", fetch = FetchType.EAGER)
    private Collection<Reservation> reservation;

    @ManyToOne
    @JoinColumn(name = "CAR_TYPE_ID")
    private CarType carType;

    public Car() {
    }

    public Car(User user, String brand, String model, String colour, String vehicleRegistrationPlate, CarType carType) {
        this.user = user;
        this.brand = brand;
        this.model = model;
        this.colour = colour;
        this.vehicleRegistrationPlate = vehicleRegistrationPlate;
        this.carType = carType;
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

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getVehicleRegistrationPlate() {
        return vehicleRegistrationPlate;
    }

    public void setVehicleRegistrationPlate(String license_number) {
        this.vehicleRegistrationPlate = license_number;
    }

    public String getColour() {
        return colour;
    }

    public void setColour(String color) {
        this.colour = color;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Collection<Reservation> getReservations() {
        return reservation;
    }

    public void setReservations(Collection<Reservation> reservation) {
        this.reservation = reservation;
    }

    public CarType getCarType() {
        return carType;
    }

    public void setCarType(CarType carType) {
        this.carType = carType;
    }

    @Override
    public String toString() {
        return "Car: " +
                "id = " + id +
                ", brand = '" + brand + '\'' +
                ", model = '" + model + '\'' +
                ", registrationPlate = '" + vehicleRegistrationPlate + '\'' +
                ", colour = '" + colour + '\'' +
                ", carType = '" + carType.getName() + '\'' +
                ", user = '" + user.getFirstname() + " " + user.getLastname() + '\'' +
                ", (Non)/Active reservations = " + ((reservation != null) ? reservation.size() : 0) + "\n";
    }
}
