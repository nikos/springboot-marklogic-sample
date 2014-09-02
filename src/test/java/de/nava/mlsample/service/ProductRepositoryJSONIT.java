package de.nava.mlsample.service;

import com.marklogic.client.extra.jackson.JacksonHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.RawCombinedQueryDefinition;
import de.nava.mlsample.MarkLogicSampleApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.FileCopyUtils;

import java.io.FileReader;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = MarkLogicSampleApplication.class)
public class ProductRepositoryJSONIT {

    public static final String COLLECTION_REF = "/products.json";

    @Autowired
    protected QueryManager queryManager;

    @Test
    public void thatFindWithDynamicQueryOptionsReturnsValidResponse() throws IOException {
        String optionsStr = FileCopyUtils.copyToString(
                new FileReader("src/main/resources/queries/options-price-year.json"));
        // construct query
        StringHandle rawHandle = new StringHandle();
        rawHandle.withFormat(Format.JSON).set(optionsStr);
        RawCombinedQueryDefinition queryDef = queryManager.newRawCombinedQueryDefinition(rawHandle);
        queryDef.setCollections(COLLECTION_REF);

        JacksonHandle resultsHandle = new JacksonHandle();
        queryManager.setPageLength(10);
        queryManager.search(queryDef, resultsHandle);
        int start = resultsHandle.get().get("start").intValue();
        assertEquals("Start should be 1", 1, start);
    }
}
