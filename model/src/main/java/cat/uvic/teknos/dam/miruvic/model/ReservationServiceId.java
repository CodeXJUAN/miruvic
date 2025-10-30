package cat.uvic.teknos.dam.miruvic.model;

import java.io.Serializable;

public interface ReservationServiceId extends Serializable {
    Integer getReservationId();
    void setReservationId(Integer reservationId);
    Integer getServiceId();
    void setServiceId(Integer serviceId);
}