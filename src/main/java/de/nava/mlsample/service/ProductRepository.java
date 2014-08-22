package de.nava.mlsample.service;

import de.nava.mlsample.domain.Product;

import java.util.List;

/**
 * Showcase for a simple repository allowing to access and modify
 * {@link Product} objects in a domain specific way.
 *
 * @author Niko Schmuck
 */
public interface ProductRepository {

    void add(Product product);

    void remove(Long sku);

    Product findBySku(Long sku);

    List<Product> findAll();

    List<Product> findByName(String name);

    Long count();

}
