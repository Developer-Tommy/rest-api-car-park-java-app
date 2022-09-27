package sk.stuba.fei.uim.vsa.pr2.resources.responses;

import java.util.List;
import java.util.Objects;

public class CarParkDto extends Dto {

    private String name;
    private String address;
    private Integer prices;
    private List<CarParkFloorDto> floors;

    public CarParkDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPrices() {
        return prices;
    }

    public void setPrices(Integer prices) {
        this.prices = prices;
    }

    public List<CarParkFloorDto> getFloors() {
        return floors;
    }

    public void setFloors(List<CarParkFloorDto> floors) {
        this.floors = floors;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CarParkDto that = (CarParkDto) o;
        return Objects.equals(address, that.address) && Objects.equals(name, that.name) && Objects.equals(prices, that.prices) && Objects.equals(floors, that.floors);
    }

    @Override
    public int hashCode() {
        return Objects.hash(address, name, prices, floors);
    }

    @Override
    public String toString() {
        return "CarParkDto{" +
                "id=" + id + '\'' +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", prices=" + prices +
                ", floors=" + floors +
                '}';
    }
}
