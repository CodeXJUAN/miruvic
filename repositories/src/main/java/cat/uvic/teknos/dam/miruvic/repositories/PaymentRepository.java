package cat.uvic.teknos.dam.miruvic.repositories;

public interface PaymentRepository<Payment> extends Repository<Integer, Payment> {
    Payment findByMethod(String method);

    Payment findByAmountRange(double min, double max);
}