package cat.uvic.teknos.dam.miruvic.server.factories;

import cat.uvic.teknos.dam.miruvic.model.Address;
import cat.uvic.teknos.dam.miruvic.model.Student;

public interface ModelFactory {

    Address createAddress();

    Student createStudent();
}

