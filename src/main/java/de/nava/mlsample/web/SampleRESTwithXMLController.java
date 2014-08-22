package de.nava.mlsample.web;

import de.nava.mlsample.domain.Product;
import de.nava.mlsample.domain.Products;
import de.nava.mlsample.service.ProductRepositoryXML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

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
    public ResponseEntity<String> createProduct(@RequestBody Product product, UriComponentsBuilder builder) {
        productRepositoryXML.add(product);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(
                builder.path("/products/{id}.xml")
                        .buildAndExpand(product.getSku()).toUri());

        return new ResponseEntity<>("", headers, HttpStatus.CREATED);
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
    public Products searchProducts(@RequestParam(required=false, value="name") String name) {
        List<Product> products;
        if (StringUtils.isEmpty(name)) {
            logger.info("Lookup all {} products...", productRepositoryXML.count());
            products = productRepositoryXML.findAll();
        } else {
            logger.info("Lookup products by name: {}", name);
            products = productRepositoryXML.findByName(name);
        }
        Products result = new Products();
        result.setProducts(products);
        return result;
    }
}
