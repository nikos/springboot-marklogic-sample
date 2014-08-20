package de.nava.mlsample.web;

import de.nava.mlsample.domain.Product;
import de.nava.mlsample.domain.Products;
import de.nava.mlsample.service.ProductRepositoryXML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
public class SampleRESTwithXMLController {

    private static final Logger logger = LoggerFactory.getLogger(SampleRESTwithXMLController.class);

    @Autowired
    protected ProductRepositoryXML productRepositoryXML;

    @RequestMapping(
            value = "/products",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_XML_VALUE
    )
    @ResponseStatus(HttpStatus.CREATED)
    public void createProduct(@RequestBody Product product) {
        productRepositoryXML.add(product);
    }

    @RequestMapping(
            value = "/products/{sku}.xml",
            method = RequestMethod.DELETE
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduct(@PathVariable("sku") Long sku) {
        productRepositoryXML.remove(sku);
    }

    @RequestMapping(
            value = "/products/{sku}.xml",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_XML_VALUE
    )
    public Product readProduct(@PathVariable("sku") Long sku) {
        return productRepositoryXML.findBySku(sku);
    }

    @RequestMapping(
            value = "/products.xml",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_XML_VALUE
    )
    public Products searchProducts(@RequestParam(required=true, value="name") String name) {
        logger.info("Lookup products by name: {}", name);
        Products result = new Products();
        result.setProducts(productRepositoryXML.findByName(name));
        return result;
    }
}
