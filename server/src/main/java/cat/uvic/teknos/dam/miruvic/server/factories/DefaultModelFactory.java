package cat.uvic.teknos.dam.miruvic.server.factories;

import cat.uvic.teknos.dam.miruvic.model.Address;
import cat.uvic.teknos.dam.miruvic.model.Student;
import cat.uvic.teknos.dam.miruvic.model.impl.AddressImpl;
import cat.uvic.teknos.dam.miruvic.model.impl.StudentImpl;

public class DefaultModelFactory implements ModelFactory {

    @Override
    public Address createAddress() {
        return new AddressImpl();
    }

    @Override
    public Student createStudent() {
        return new StudentImpl();
    }
}