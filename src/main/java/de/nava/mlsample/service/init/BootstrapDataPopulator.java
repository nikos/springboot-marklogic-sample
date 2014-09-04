package de.nava.mlsample.service.init;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.nava.mlsample.domain.Product;
import de.nava.mlsample.domain.Products;
import de.nava.mlsample.service.ProductRepositoryJSON;
import de.nava.mlsample.service.ProductRepositoryXML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.IOException;
import java.io.InputStream;

/**
 * Initialize some sample data (if collection are empty yet).
 *
 * @author Niko Schmuck
 */
@Service
public class BootstrapDataPopulator implements InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(BootstrapDataPopulator.class);

    @Autowired
    protected ProductRepositoryJSON productRepositoryJSON;

    @Autowired
    protected ProductRepositoryXML productRepositoryXML;

    @Override
    public void afterPropertiesSet() throws Exception {
        logger.info("~~~ Load bootstrap data");
        if (productRepositoryJSON.count() == 0) {
            importJSONProducts();
        }
        if (productRepositoryXML.count() == 0) {
            importXMLProducts();
        }
    }

    private void importXMLProducts() throws JAXBException, IOException {
        JAXBContext context = JAXBContext.newInstance(Products.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        try (InputStream inputStream = Products.class.getResourceAsStream("/sampledata/products.xml")) {
            Products products = (Products) unmarshaller.unmarshal(inputStream);
            for (Product product : products.getProducts()) {
                productRepositoryXML.add(product);
            }
            logger.info("Imported {} products to JSON store", products.getProducts().size());
        }
    }

    private void importJSONProducts() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        InputStream inputStream = Products.class.getResourceAsStream("/sampledata/products.json");
        Product[] products = mapper.readValue(inputStream, Product[].class);
        for (Product product : products) {
            productRepositoryJSON.add(product);
        }
        logger.info("Imported {} products to JSON store", products.length);
    }

}
