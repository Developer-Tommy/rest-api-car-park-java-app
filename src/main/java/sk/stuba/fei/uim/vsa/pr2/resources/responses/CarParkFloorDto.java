package sk.stuba.fei.uim.vsa.pr2.resources.responses;

import java.util.List;
import java.util.Objects;

public class CarParkFloorDto extends Dto {

    private String identifier;
    private Long carPark;
    private List<ParkingSpotDto> spots;

    public CarParkFloorDto() {
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public Long getCarPark() {
        return carPark;
    }

    public void setCarPark(Long carPark) {
        this.carPark = carPark;
    }

    public List<ParkingSpotDto> getSpots() {
        return spots;
    }

    public void setSpots(List<ParkingSpotDto> spots) {
        this.spots = spots;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CarParkFloorDto that = (CarParkFloorDto) o;
        return Objects.equals(identifier, that.identifier) && Objects.equals(carPark, that.carPark) && Objects.equals(spots, that.spots);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier, carPark, spots);
    }

    @Override
    public String toString() {
        return "CarParkFloorDto{" +
                "identifier='" + identifier + '\'' +
                ", carPark=" + carPark +
                ", spots=" + spots +
                '}';
    }
}
