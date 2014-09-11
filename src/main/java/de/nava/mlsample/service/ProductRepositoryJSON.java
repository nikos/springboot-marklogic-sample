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
import org.springframework.util.FileCopyUtils;

import javax.annotation.PostConstruct;
import java.io.FileReader;
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
    public static final int PAGE_SIZE = 10;

    @Autowired
    protected QueryManager queryManager;

    @Autowired
    protected JSONDocumentManager jsonDocumentManager;

    private String optionsPriceYear;

    @PostConstruct
    protected void init() throws IOException {
        optionsPriceYear = FileCopyUtils.copyToString(
                new FileReader("src/main/resources/queries/options-price-year.xml"));
    }

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

        // Java Developer Guide p. 54 ("Using query options for dynamic search")
        RawCombinedQueryDefinition queryDef =
                queryManager.newRawCombinedQueryDefinitionAs(Format.XML, optionsPriceYear);

        queryDef.setCollections(COLLECTION_REF);

        SearchHandle resultsHandle = new SearchHandle();
        queryManager.setPageLength(PAGE_SIZE);
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

        queryManager.setPageLength(PAGE_SIZE);
        SearchHandle resultsHandle = new SearchHandle();
        queryManager.search(query, resultsHandle);
        return toSearchResult(resultsHandle);
    }

    @Override
    public ProductSearchResult findByYear(int year) {
        throw new UnsupportedOperationException("findByYear: not yet implemented");
    }

    // ~~

    private String getDocId(Long sku) {
        return String.format("/products/%d.json", sku);
    }

    private ProductSearchResult toSearchResult(SearchHandle resultsHandle) {
        List<Product> products = new ArrayList<>();
        for (MatchDocumentSummary summary : resultsHandle.getMatchResults()) {
            logger.info("  * found {}", summary.getUri());
            // Assumption: summary URI refers to JSON document
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
