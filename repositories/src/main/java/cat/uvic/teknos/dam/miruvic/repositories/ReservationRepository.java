package cat.uvic.teknos.dam.miruvic.repositories;

import cat.uvic.teknos.dam.miruvic.model.Reservation;

import java.util.List;

public interface ReservationRepository extends Repository<Integer, Reservation> {
    List<Reservation> findByDate(java.time.LocalDate date);

    List<Reservation> findByStudentId(Integer studentId);
}

    