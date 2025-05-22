package cat.uvic.teknos.dam.miruvic.repositories;

public interface ReservationRepository<Reservation> extends Repository<Integer, Reservation> {
    Reservation findByDate(java.time.LocalDate date);

    Reservation findByStudentId(int studentId);
}

    