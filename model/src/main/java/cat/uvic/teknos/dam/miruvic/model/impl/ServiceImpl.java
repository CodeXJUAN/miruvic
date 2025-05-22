package cat.uvic.teknos.dam.miruvic.impl;

import java.math.BigDecimal;

public class ServiceImpl implements cat.uvic.teknos.dam.miruvic.Service {
    private int id;
    private String serviceName;
    private String description;
    private BigDecimal price;

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String getServiceName() {
        return serviceName;
    }

    @Override
    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public BigDecimal getPrice() {
        return price;
    }

    @Override
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}