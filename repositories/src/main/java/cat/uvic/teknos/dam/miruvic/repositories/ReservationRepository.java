package cat.uvic.teknos.dam.miruvic.repositories;

import cat.uvic.teknos.dam.miruvic.model.Reservation;

public interface ReservationRepository extends Repository<Integer, Reservation> {
    Reservation findByDate(java.time.LocalDate date);

    Reservation findByStudentId(int studentId);
}

    