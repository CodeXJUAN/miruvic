package cat.uvic.teknos.dam.miruvic.jpa.model;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.io.Serializable;

@Embeddable
@NoArgsConstructor
@Getter
@Setter
public class ReservationServiceId implements Serializable {
    private Integer reservationId;
    private Integer serviceId;

    public ReservationServiceId(Integer reservationId, Integer serviceId) {
        this.reservationId = reservationId;
        this.serviceId = serviceId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReservationServiceId that = (ReservationServiceId) o;
        return java.util.Objects.equals(reservationId, that.reservationId) &&
                java.util.Objects.equals(serviceId, that.serviceId);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(reservationId, serviceId);
    }
}