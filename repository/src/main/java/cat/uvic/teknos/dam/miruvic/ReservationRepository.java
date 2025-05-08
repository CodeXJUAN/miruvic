package cat.uvic.teknos.dam.miruvic;

public interface ReservationRepository<Reservation> extends Repository<Integer, Reservation> {
    java.util.List<Reservation> findByDate(java.time.LocalDate date);

    java.util.List<Reservation> findByStudentId(int studentId);
}

    