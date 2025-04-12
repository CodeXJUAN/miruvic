package cat.uvic.teknos.dam.ruvic;

import java.math.BigDecimal;

public class Service {
    private int id;
    private String serviceName;
    private String description;
    private BigDecimal price; // Cambiado de double a BigDecimal para decimal(5,2)

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}