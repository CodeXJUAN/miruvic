package cat.uvic.teknos.dam.miruvic.jpa.model;

import cat.uvic.teknos.dam.miruvic.model.Address;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "ADDRESS")
@NoArgsConstructor
@Getter
@Setter
public class JpaAddress implements Address {

    @Id
    @Column(name = "id")
    private Integer id;

    @Column(name = "street", nullable = false)
    private String street;

    @Column(name = "city", nullable = false)
    private String city;

    @Column(name = "state", nullable = false)
    private String state;

    @Column(name = "zip_code", nullable = false)
    private String zipCode;

    @Column(name = "country", nullable = false)
    private String country;

    @OneToOne(mappedBy = "address")
    private JpaStudent student;
}
