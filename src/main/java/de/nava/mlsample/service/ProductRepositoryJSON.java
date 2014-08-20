package de.nava.mlsample.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.marklogic.client.document.JSONDocumentManager;
import com.marklogic.client.extra.jackson.JacksonHandle;
import com.marklogic.client.io.SearchHandle;
import com.marklogic.client.query.KeyValueQueryDefinition;
import com.marklogic.client.query.MatchDocumentSummary;
import com.marklogic.client.query.QueryManager;
import de.nava.mlsample.domain.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.xml.namespace.QName;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Sample implementation of the {@link ProductRepository}
 * making use of MarkLogic's {@link JSONDocumentManager}.
 *
 * @author Niko Schmuck
 */
@Component
public class ProductRepositoryJSON implements ProductRepository {

    private static final Logger logger = LoggerFactory.getLogger(ProductRepositoryJSON.class);

    @Autowired
    protected QueryManager queryManager;

    @Autowired
    protected JSONDocumentManager jsonDocumentManager;

    @Override
    public void add(Product product) {
        JacksonHandle writeHandle = new JacksonHandle();

        JsonNode writeDocument = writeHandle.getMapper().convertValue(product, JsonNode.class);
        writeHandle.set(writeDocument);

        jsonDocumentManager.write(getDocId(product.getSku()), writeHandle);
    }

    @Override
    public void remove(Long sku) {
        jsonDocumentManager.delete(getDocId(sku));
    }

    @Override
    public Product findBySku(Long sku) {
        JacksonHandle jacksonHandle = new JacksonHandle();
        logger.info("Search for product with SKU {} ...", sku);
        jsonDocumentManager.read(getDocId(sku), jacksonHandle);
        return fetchProduct(jacksonHandle);
    }

    // Demonstrates
    public JsonNode rawfindBySku(Long sku) {
        JacksonHandle jacksonHandle = new JacksonHandle();
        jsonDocumentManager.read(getDocId(sku), jacksonHandle);
        return jacksonHandle.get();
    }

    @Override
    public List<Product> findByName(String name) {
        KeyValueQueryDefinition query = queryManager.newKeyValueDefinition();
        queryManager.setPageLength(10);
        query.put(queryManager.newElementLocator(new QName("name")), name);
        // TODO: How to restrict either to XML or JSON document types?
        SearchHandle resultsHandle = new SearchHandle();
        queryManager.search(query, resultsHandle);
        return getResultListFor(resultsHandle);
    }

    // ~~

    private String getDocId(Long sku) {
        return String.format("/products/%d.json", sku);
    }

    private List<Product> getResultListFor(SearchHandle resultsHandle) {
        List<Product> result = new ArrayList<>();
        for (MatchDocumentSummary summary : resultsHandle.getMatchResults()) {
            JacksonHandle jacksonHandle = new JacksonHandle();
            logger.info("  * found {}", summary.getUri());
            if (summary.getUri().endsWith(".json")) {
                jsonDocumentManager.read(summary.getUri(), jacksonHandle);
                result.add(fetchProduct(jacksonHandle));
            }
        }
        return result;
    }

    private Product fetchProduct(JacksonHandle jacksonHandle) {
        try {
            JsonNode jsonNode = jacksonHandle.get();
            return jacksonHandle.getMapper().readValue(jsonNode.toString(), Product.class);
        } catch (IOException e) {
            throw new RuntimeException("Unable to cast to product", e);
        }
    }
}
