package de.nava.mlsample.service;

import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.JAXBHandle;
import com.marklogic.client.io.SearchHandle;
import com.marklogic.client.query.*;
import de.nava.mlsample.domain.Product;
import de.nava.mlsample.domain.ProductSearchResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.List;

/**
 * Sample implementation of the {@link ProductRepository}
 * making use of MarkLogic's {@link XMLDocumentManager}.
 *
 * @author Niko Schmuck
 */
@Component
public class ProductRepositoryXML implements ProductRepository {

    private static final Logger logger = LoggerFactory.getLogger(ProductRepositoryXML.class);

    public static final String COLLECTION_REF = "/products.xml";
    public static final int PAGE_SIZE = 10;

    @Autowired
    protected QueryManager queryManager;

    @Autowired
    protected XMLDocumentManager xmlDocumentManager;

    @Override
    public void add(Product product) {
        // Add this document to a dedicated collection for later retrieval
        DocumentMetadataHandle metadata = new DocumentMetadataHandle();
        metadata.getCollections().add(COLLECTION_REF);

        JAXBHandle contentHandle = getProductHandle();
        contentHandle.set(product);
        xmlDocumentManager.write(getDocId(product.getSku()), metadata, contentHandle);
    }

    @Override
    public void remove(Long sku) {
        xmlDocumentManager.delete(getDocId(sku));
    }

    @Override
    public Product findBySku(Long sku) {
        JAXBHandle contentHandle = getProductHandle();
        JAXBHandle result = xmlDocumentManager.read(getDocId(sku), contentHandle);
        return (Product) result.get(Product.class);
    }

    @Override
    public Long count() {
        StructuredQueryBuilder sb = queryManager.newStructuredQueryBuilder();
        StructuredQueryDefinition criteria = sb.collection(COLLECTION_REF);

        SearchHandle resultsHandle = new SearchHandle();
        queryManager.search(criteria, resultsHandle);
        return resultsHandle.getTotalResults();
    }

    @Override
    public ProductSearchResult findAll() {
        StructuredQueryBuilder sb = queryManager.newStructuredQueryBuilder();
        StructuredQueryDefinition criteria = sb.collection(COLLECTION_REF);

        SearchHandle resultsHandle = new SearchHandle();
        queryManager.search(criteria, resultsHandle);
        return toSearchResult(resultsHandle);
    }

    @Override
    public ProductSearchResult findByName(String name) {
        KeyValueQueryDefinition query = queryManager.newKeyValueDefinition();
        queryManager.setPageLength(PAGE_SIZE);
        query.put(queryManager.newElementLocator(new QName("name")), name);
        // TODO: How to restrict either to XML or JSON document types?
        SearchHandle resultsHandle = new SearchHandle();
        queryManager.search(query, resultsHandle);
        return toSearchResult(resultsHandle);
    }

    @Override
    public ProductSearchResult findByYear(int year) {
        throw new UnsupportedOperationException("findByYear: not yet implemented");
    }

    // ~~

    private JAXBHandle getProductHandle() {
        try {
            JAXBContext context = JAXBContext.newInstance(Product.class);
            return new JAXBHandle(context);
        } catch (JAXBException e) {
            throw new RuntimeException("Unable to create product JAXB context", e);
        }
    }

    private String getDocId(Long sku) {
        return String.format("/products/%d.xml", sku);
    }

    private ProductSearchResult toSearchResult(SearchHandle resultsHandle) {
        List<Product> products = new ArrayList<>();
        for (MatchDocumentSummary summary : resultsHandle.getMatchResults()) {
            JAXBHandle contentHandle = getProductHandle();
            logger.info("  * found {}", summary.getUri());
            xmlDocumentManager.read(summary.getUri(), contentHandle);
            products.add((Product) contentHandle.get(Product.class));
        }
        return new ProductSearchResult(products, resultsHandle.getFacetResult("price"),
                resultsHandle.getFacetResult("year"));
    }
}
