package cat.uvic.teknos.dam.miruvic.jpa.model;

import cat.uvic.teknos.dam.miruvic.model.Student;
import cat.uvic.teknos.dam.miruvic.model.Address;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "STUDENT")
@Data
public class JpaStudent implements Student {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @OneToOne
    @JoinColumn(name = "address_id", nullable = false, unique = true)
    private JpaAddress address;


    @Override
    public Address getAddress() {
        return address;
    }

    @Override
    public void setAddress(Address address) {
        if (address instanceof JpaAddress) {
            this.address = (JpaAddress) address;
        }
    }
}
