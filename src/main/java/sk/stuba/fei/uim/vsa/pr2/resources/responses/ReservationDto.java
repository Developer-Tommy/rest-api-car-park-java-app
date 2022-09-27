package sk.stuba.fei.uim.vsa.pr2.resources.responses;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;
import java.util.Objects;

public class ReservationDto extends Dto {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss", timezone = "CET")
    private Date start;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss", timezone = "CET")
    private Date end;
    private Integer prices;
    private CarDto car;
    private ParkingSpotDto spot;

    public ReservationDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public ParkingSpotDto getSpot() {
        return spot;
    }

    public void setSpot(ParkingSpotDto spot) {
        this.spot = spot;
    }

    public CarDto getCar() {
        return car;
    }

    public void setCar(CarDto car) {
        this.car = car;
    }

    public Integer getPrices() {
        return prices;
    }

    public void setPrices(Integer prices) {
        this.prices = prices;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReservationDto that = (ReservationDto) o;
        return Objects.equals(start, that.start) && Objects.equals(end, that.end) && Objects.equals(spot, that.spot) && Objects.equals(car, that.car) && Objects.equals(prices, that.prices);
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, end, spot, car, prices);
    }

    @Override
    public String toString() {
        return "ReservationDto{" +
                "id=" + id +
                ", start=" + start +
                ", end=" + end +
                ", prices=" + prices +
                ", car=" + car +
                ", spot=" + spot +
                '}';
    }
}
