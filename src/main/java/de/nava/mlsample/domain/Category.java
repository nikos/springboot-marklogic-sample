package de.nava.mlsample.domain;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

@XmlRootElement(name = "category")
public class Category {

    private String text;

    // Default constructor to keep JAXB happy
    public Category() {
    }

    public Category(String text) {
        this.text = text;
    }

    @XmlValue
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
