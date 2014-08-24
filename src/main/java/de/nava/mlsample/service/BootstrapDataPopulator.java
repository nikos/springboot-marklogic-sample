package de.nava.mlsample.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.nava.mlsample.domain.Product;
import de.nava.mlsample.domain.Products;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;

/**
 * This would be the perfect place to initialize same sample data.
 *
 * @author Niko Schmuck
 */
@Service
public class BootstrapDataPopulator implements InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(BootstrapDataPopulator.class);

    @Autowired
    protected ProductRepositoryJSON productRepositoryJSON;

    @Override
    public void afterPropertiesSet() throws Exception {
        logger.info("~~~ Load bootstrap data");
        if (productRepositoryJSON.count() == 0) {
            importJSONProducts();
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
