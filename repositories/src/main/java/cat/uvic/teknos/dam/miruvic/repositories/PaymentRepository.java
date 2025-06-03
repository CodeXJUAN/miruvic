package cat.uvic.teknos.dam.miruvic.repositories;

import cat.uvic.teknos.dam.miruvic.model.Payment;

import java.util.List;

public interface PaymentRepository extends Repository<Integer, Payment> {
    List<Payment> findByMethod(String method);

    List<Payment> findByAmountRange(double min, double max);
}