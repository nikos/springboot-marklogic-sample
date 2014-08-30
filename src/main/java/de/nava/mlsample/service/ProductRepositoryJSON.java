package de.nava.mlsample.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.marklogic.client.document.JSONDocumentManager;
import com.marklogic.client.extra.jackson.JacksonHandle;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.SearchHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.query.*;
import de.nava.mlsample.domain.Product;
import de.nava.mlsample.domain.ProductSearchResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

    public static final String COLLECTION_REF = "/products.json";

    @Autowired
    protected QueryManager queryManager;

    @Autowired
    protected JSONDocumentManager jsonDocumentManager;

    @Override
    public void add(Product product) {
        // Add this document to a dedicated collection for later retrieval
        DocumentMetadataHandle metadata = new DocumentMetadataHandle();
        metadata.getCollections().add(COLLECTION_REF);

        JacksonHandle writeHandle = new JacksonHandle();
        JsonNode writeDocument = writeHandle.getMapper().convertValue(product, JsonNode.class);
        writeHandle.set(writeDocument);

        // TODO: writing JacksonHandle with metadata throws: java.io.IOException: Attempted write to closed stream.
        StringHandle stringHandle = new StringHandle(writeDocument.toString());
        jsonDocumentManager.write(getDocId(product.getSku()), metadata, stringHandle);
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

    /**
     * Demonstrates End-to-End JSON direct access.
     */
    public JsonNode rawfindBySku(Long sku) {
        JacksonHandle jacksonHandle = new JacksonHandle();
        jsonDocumentManager.read(getDocId(sku), jacksonHandle);
        return jacksonHandle.get();
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
        //StructuredQueryBuilder sb = queryManager.newStructuredQueryBuilder();
        //StructuredQueryDefinition simpleQueryDef = sb.collection(COLLECTION_REF);

        // TODO: how to get facets  // Java Developer Guide p. 54
        // TODO: set query options: 4.1 Using Query Options p. 64

        String rawSearch = new StringBuilder()
                .append("<search:search xmlns:search='http://marklogic.com/appservices/search'>")
                .append("<search:options>")
                .append("<search:constraint name='year'>")
                .append("<search:range type='xs:gYear' facet='true'>")
                .append("<search:facet-option>frequency-order</search:facet-option>")
                .append("<search:facet-option>descending</search:facet-option>")
                .append("<search:facet-option>limit=50</search:facet-option>")
                .append("<search:element ns='http://marklogic.com/xdmp/json/basic' name='year'/>")
                .append("</search:range>")
                .append("</search:constraint>")
                .append("<search:constraint name='price'>")
                .append("<search:range type='xs:double' facet='true'>")
                .append("<search:bucket lt='20.00' name='lt20.00'>&lt; 20.00</search:bucket>")
                .append("<search:bucket ge='20.00' lt='40.00' name='20-40'>20-40</search:bucket>")
                .append("<search:bucket ge='40.00' name='40.00'>&gt;= 40</search:bucket>")
                .append("<search:facet-option>item-order</search:facet-option>")
                .append("<search:facet-option>ascending</search:facet-option>")
                .append("<search:facet-option>limit=50</search:facet-option>")
                .append("<search:element ns='http://marklogic.com/xdmp/json/basic' name='price'/>")
                .append("</search:range>")
                .append("</search:constraint>")
                .append("</search:options>")
                .append("</search:search>")
                .toString();

        RawCombinedQueryDefinition queryDef =
                queryManager.newRawCombinedQueryDefinitionAs(Format.XML, rawSearch);

        queryDef.setCollections(COLLECTION_REF);

        SearchHandle resultsHandle = new SearchHandle();
        queryManager.setPageLength(10);
        queryManager.search(queryDef, resultsHandle, 0);
        return toSearchResult(resultsHandle);
    }

    @Override
    public ProductSearchResult findByName(String name) {
        //KeyValueQueryDefinition query = queryManager.newKeyValueDefinition();
        //query.put(queryManager.newKeyLocator("name"), name);  // exact match

        // Alternatively use:
        StringQueryDefinition query = queryManager.newStringDefinition();
        query.setCriteria(name); // example: "index OR Cassel NEAR Hare"
        query.setCollections(COLLECTION_REF);

        queryManager.setPageLength(10);
        SearchHandle resultsHandle = new SearchHandle();
        queryManager.search(query, resultsHandle);
        return toSearchResult(resultsHandle);
    }

    // ~~

    private String getDocId(Long sku) {
        return String.format("/products/%d.json", sku);
    }

    private ProductSearchResult toSearchResult(SearchHandle resultsHandle) {
        List<Product> products = new ArrayList<>();
        for (MatchDocumentSummary summary : resultsHandle.getMatchResults()) {
            logger.info("  * found {}", summary.getUri());
            // Assumption: JSON documents refered by summary URI
            JacksonHandle jacksonHandle = new JacksonHandle();
            jsonDocumentManager.read(summary.getUri(), jacksonHandle);
            products.add(fetchProduct(jacksonHandle));
        }
        return new ProductSearchResult(products, resultsHandle.getFacetResult("price"),
                resultsHandle.getFacetResult("year"));
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
