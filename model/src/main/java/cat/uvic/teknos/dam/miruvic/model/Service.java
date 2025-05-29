package cat.uvic.teknos.dam.miruvic.model;

import java.math.BigDecimal;

public interface Service {
    Integer getId();
    String getServiceName();
    String getDescription();
    BigDecimal getPrice();

    void setId(Integer id);
    void setServiceName(String serviceName);
    void setDescription(String description);
    void setPrice(BigDecimal price);
}