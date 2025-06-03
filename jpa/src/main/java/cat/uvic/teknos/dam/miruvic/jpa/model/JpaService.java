package cat.uvic.teknos.dam.miruvic.jpa.model;

import cat.uvic.teknos.dam.miruvic.model.Service;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "SERVICE")
@NoArgsConstructor
@Getter
@Setter
public class JpaService implements Service {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    
    @Column(name = "name", nullable = false)
    private String serviceName;
    
    @Column(name = "description")
    private String description;
    
    @Column(name = "price", nullable = false)
    private BigDecimal price;
}
