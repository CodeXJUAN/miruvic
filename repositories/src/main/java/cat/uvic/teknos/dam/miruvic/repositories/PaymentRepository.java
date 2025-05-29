package cat.uvic.teknos.dam.miruvic.repositories;

import cat.uvic.teknos.dam.miruvic.model.Payment;

public interface PaymentRepository extends Repository<Integer, Payment> {
    Payment findByMethod(String method);

    Payment findByAmountRange(double min, double max);
}