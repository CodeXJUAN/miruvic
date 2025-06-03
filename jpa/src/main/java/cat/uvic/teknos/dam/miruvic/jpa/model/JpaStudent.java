package cat.uvic.teknos.dam.miruvic.model.jpa;

import cat.uvic.teknos.dam.miruvic.model.Address;
import cat.uvic.teknos.dam.miruvic.model.Student;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "STUDENT")
@NoArgsConstructor
@Getter
@Setter
public class JpaStudent implements Student {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    
    @Column(name = "first_name")
    private String firstName;
    
    @Column(name = "last_name")
    private String lastName;
    
    @Column(name = "email")
    private String email;
    
    @Column(name = "phone")
    private String phone;
    
    @Column(name = "birth_date")
    private LocalDate birthDate;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "address_id")
    private JpaAddress address;
    
    @Override
    public Address getAddress() {
        return address;
    }
    
    @Override
    public void setAddress(Address address) {
        if (address instanceof JpaAddress) {
            this.address = (JpaAddress) address;
        } else {
            throw new IllegalArgumentException("Address must be a JpaAddress instance");
        }
    }
}
