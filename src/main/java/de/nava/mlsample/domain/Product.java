package de.nava.mlsample.domain;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "product")
public class Product {

    private Long sku;
    private String name;
    private String description;
    private Double price;
    private int year;
    private String link;

    private List<Category> categories = new ArrayList<>();

    // Default constructor to keep JAXB happy
    public Product() {
    }

    public Product(Long sku, String name, Double price, List<Category> categories) {
        this(sku, name, "N/A", price, 0, null, categories);
    }

    public Product(Long sku, String name, String description, Double price, int year, String link,
                   List<Category> categories) {
        this.sku = sku;
        this.name = name;
        this.description = description;
        this.price = price;
        this.year = year;
        this.link = link;
        this.categories = categories;
    }

    // ~~

    @XmlAttribute(name="sku")
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

    @XmlElementWrapper(name="categories")
    @XmlElement(name="category")
    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    @Override
    public String toString() {
        return String.format("%s [%d]", name, sku);
    }
}
