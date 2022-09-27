package sk.stuba.fei.uim.vsa.pr2.entities;

import javax.persistence.*;
import java.io.Serializable;

@Embeddable
public class CarParkFloorId implements Serializable {

    @Column(name = "FLOOR_IDENTIFIER")
    private String floorIdentifierId;

    @Column(name = "CAR_PARK_ID")
    private Long carParkId ;

    public CarParkFloorId() {
    }

    public CarParkFloorId(Long carParkId, String floorIdentifier) {
        this.carParkId = carParkId;
        this.floorIdentifierId = floorIdentifier;
    }

    public String getFloorIdentifierId() {
        return floorIdentifierId;
    }

    public void setFloorIdentifierId(String floorIdentifier) {
        this.floorIdentifierId = floorIdentifier;
    }

    public Long getCarParkId() {
        return carParkId;
    }

    public void setCarParkId(Long carParkId) {
        this.carParkId = carParkId;
    }

    @Override
    public String toString() {
        return "CarParkFloorId{" +
                "floorIdentifier='" + floorIdentifierId + '\'' +
                ", carParkId=" + carParkId +
                '}';
    }

}
