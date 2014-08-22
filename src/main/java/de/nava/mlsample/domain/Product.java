package de.nava.mlsample.domain;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "product")
public class Product {

    private Long sku;
    private String name;
    private String description;
    private Double price;
    private List<String> tags;

    public Long getSku() {
        return sku;
    }

    public void setSku(Long sku) {
        this.sku = sku;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    @Override
    public String toString() {
        return String.format("%s [%d]", name, sku);
    }
}
