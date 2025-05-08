package miruvic;

import java.math.BigDecimal;

public interface Service {
    int getId();

    void setId(int id);

    String getServiceName();

    void setServiceName(String serviceName);

    String getDescription();

    void setDescription(String description);

    BigDecimal getPrice();

    void setPrice(BigDecimal price);
}