package cat.uvic.teknos.dam.miruvic.repositories;

public interface PaymentRepository<Payment> extends Repository<Integer, Payment> {
    java.util.List<Payment> findByMethod(String method);

    java.util.List<Payment> findByAmountRange(double min, double max);
}