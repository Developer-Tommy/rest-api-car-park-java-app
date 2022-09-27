package sk.stuba.fei.uim.vsa.pr2.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Table(name = "RESERVATION")
@NamedQueries({
        @NamedQuery(name = "Reservation.findAll", query = "select r from Reservation r"),
        @NamedQuery(name = "Reservation.findByDate", query = "select r from Reservation r where r.parkingSpot.id = :id and r.startTime >= :startTime and r.startTime < :endTime"),
        @NamedQuery(name = "Reservation.findUserReservations", query = "select r from Reservation r where r.car.user.id = :id and r.endTime is null")
})
@Entity
public class Reservation implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "START_TIME")
    private Date startTime;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "END_TIME")
    private Date endTime;

    @Column(name = "PRICE")
    private Integer price;

    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "CAR_ID", referencedColumnName = "ID")
    private Car car;

    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "PARKING_SPOT_ID", referencedColumnName = "ID")
    private ParkingSpot parkingSpot;

    public Reservation() {
    }

    public Reservation(Date startTime, Car car, ParkingSpot parkingSpot) {
        this.startTime = startTime;
        this.car = car;
        this.parkingSpot = parkingSpot;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Date getStartTime() {
        return startTime;
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    public ParkingSpot getParkingSpot() {
        return parkingSpot;
    }

    public void setParkingSpot(ParkingSpot parkingSpot) {
        this.parkingSpot = parkingSpot;
    }

    @Override
    public String toString() {
        return "Reservation: " +
                "id = " + id +
                ", startTime = '" + startTime + '\'' +
                ", endTime = '" + endTime + '\'' +
                ", price = " + price +
                ", carId = " + ((car != null) ? car.getId() : null) +
                ", parkingSpotId = " + ((parkingSpot != null) ? parkingSpot.getId() : null) + "\n";
    }
}
