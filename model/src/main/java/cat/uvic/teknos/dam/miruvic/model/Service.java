package cat.uvic.teknos.dam.miruvic.model;

import java.math.BigDecimal;

public interface Service {
    public int getId();

    public void setId(int id);

    public String getServiceName();

    public void setServiceName(String serviceName);

    public String getDescription();

    public void setDescription(String description);

    public BigDecimal getPrice();

    public void setPrice(BigDecimal price);
}