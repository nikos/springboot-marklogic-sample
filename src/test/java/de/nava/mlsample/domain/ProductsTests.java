package de.nava.mlsample.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;

/**
 * @author Niko Schmuck
 */
public class ProductsTests {

    @Test
    public void thatProductsCanBeReadFromXML() throws JAXBException, IOException {
        JAXBContext context = JAXBContext.newInstance(Products.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        InputStream inputStream = Products.class.getResourceAsStream("/sampledata/products.xml");
        try {
            Products products = (Products) unmarshaller.unmarshal(inputStream);
            assertEquals(40, products.getProducts().size());
        } finally  {
            inputStream.close();
        }
    }

    @Test
    public void thatProductsCanBeReadFromJSON() throws JAXBException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        InputStream inputStream = Products.class.getResourceAsStream("/sampledata/products.json");
        Product[] products = mapper.readValue(inputStream, Product[].class);
        assertEquals(40, products.length);
    }

}



