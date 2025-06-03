package cat.uvic.teknos.dam.miruvic.model.jpa;

import cat.uvic.teknos.dam.miruvic.model.Room;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "ROOM")
@NoArgsConstructor
@Getter
@Setter
public class JpaRoom implements Room {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    
    @Column(name = "room_number")
    private String roomNumber;
    
    @Column(name = "floor")
    private Integer floor;
    
    @Column(name = "capacity")
    private Integer capacity;
    
    @Column(name = "type")
    private String type;
    
    @Column(name = "price")
    private BigDecimal price;
}
