package cat.uvic.teknos.dam.miruvic.model.jpa;

import cat.uvic.teknos.dam.miruvic.model.Reservation;
import cat.uvic.teknos.dam.miruvic.model.Room;
import cat.uvic.teknos.dam.miruvic.model.Service;
import cat.uvic.teknos.dam.miruvic.model.Student;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "RESERVATION")
@NoArgsConstructor
@Getter
@Setter
public class JpaReservation implements Reservation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "student_id")
    private JpaStudent student;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "room_id")
    private JpaRoom room;
    
    @Column(name = "start_date")
    private LocalDate startDate;
    
    @Column(name = "end_date")
    private LocalDate endDate;
    
    @Column(name = "status")
    private String status;
    
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "RESERVATION_SERVICE",
        joinColumns = @JoinColumn(name = "reservation_id"),
        inverseJoinColumns = @JoinColumn(name = "service_id")
    )
    private List<JpaService> services = new ArrayList<>();
    
    @Override
    public Student getStudent() {
        return student;
    }
    
    @Override
    public void setStudent(Student student) {
        if (student instanceof JpaStudent) {
            this.student = (JpaStudent) student;
        } else {
            throw new IllegalArgumentException("Student must be a JpaStudent instance");
        }
    }
    
    @Override
    public Room getRoom() {
        return room;
    }
    
    @Override
    public void setRoom(Room room) {
        if (room instanceof JpaRoom) {
            this.room = (JpaRoom) room;
        } else {
            throw new IllegalArgumentException("Room must be a JpaRoom instance");
        }
    }
    
    @Override
    public List<Service> getServices() {
        return new ArrayList<>(services);
    }
    
    @Override
    public void setServices(List<Service> services) {
        this.services.clear();
        if (services != null) {
            for (Service service : services) {
                if (service instanceof JpaService) {
                    this.services.add((JpaService) service);
                } else {
                    throw new IllegalArgumentException("Service must be a JpaService instance");
                }
            }
        }
    }
}
