package de.nava.mlsample.domain;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.xml.sax.InputSource;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Arrays;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

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
        } finally {
            inputStream.close();
        }
    }

    @Test
    public void thatProductCanBeWrittenToXML() throws JAXBException, XPathExpressionException {
        JAXBContext context = JAXBContext.newInstance(Product.class);
        Marshaller marshaller = context.createMarshaller();
        // create test object for serialization
        StringWriter sw = new StringWriter();
        marshaller.marshal(generateTestProduct(4711L, "FooBar"), sw);
        String xml = sw.toString();
        assertThat(xml, containsString("<product sku=\"4711\">"));
        assertThat(xml, containsString("<name>FooBar</name"));
        assertThat(xml, containsString("<categories>"));
        XPath xPath = XPathFactory.newInstance().newXPath();
        assertEquals("2", xPath.evaluate("count(//category)", new InputSource(new StringReader(xml))));
    }

    @Test
    public void thatProductsCanBeReadFromJSON() throws JAXBException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        InputStream inputStream = Products.class.getResourceAsStream("/sampledata/products.json");
        Product[] products = mapper.readValue(inputStream, Product[].class);
        assertEquals(40, products.length);
    }

    @Test
    public void thatProductCanBeWrittenToJSON() {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode json = mapper.convertValue(generateTestProduct(4712L, "JsonManiac"), JsonNode.class);
        assertThat(json.toString(), containsString("\"sku\":4712"));
        assertThat(json.toString(), containsString("\"name\":\"JsonManiac\""));
        assertThat(json.toString(), containsString("\"categories\":[\"Book\",\"Test\"]}"));
    }

    // ~~

    private Product generateTestProduct(Long sku, String name) {
        return new Product(sku, name, 9.99,
                Arrays.asList(new Category("Book"), new Category("Test")));
    }

}
