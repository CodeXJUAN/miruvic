package cat.uvic.teknos.dam.miruvic.repositories;

import cat.uvic.teknos.dam.miruvic.model.ReservationService;
import java.util.List;

public interface ReservationServiceRepository extends Repository<Integer, ReservationService> {
    List<ReservationService> findByReservationId(Integer reservationId);
    List<ReservationService> findByServiceId(Integer serviceId);
}