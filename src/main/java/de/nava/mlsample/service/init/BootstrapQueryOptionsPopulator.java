package de.nava.mlsample.service.init;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.admin.QueryOptionsManager;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.QueryOptionsListHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.io.ValuesHandle;
import com.marklogic.client.query.CountedDistinctValue;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.RawCombinedQueryDefinition;
import com.marklogic.client.query.ValuesDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

/**
 * Initialize query options (surely roxy would be a more convenient solution).
 *
 * @author Niko Schmuck
 */
@Service
public class BootstrapQueryOptionsPopulator implements InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(BootstrapQueryOptionsPopulator.class);

    @Autowired
    protected DatabaseClient databaseClient;

    @Autowired
    protected QueryManager queryManager;


    @Override
    public void afterPropertiesSet() throws Exception {
        logger.info("~~~ Initialize query options");
        listExistingQueryOptions();
        defineQueryOptions();
        testQueryOptions();
    }

    private void listExistingQueryOptions() {
        QueryOptionsListHandle qolHandle = new QueryOptionsListHandle();
        queryManager.optionsList(qolHandle);
        for (Map.Entry<String, String> option : qolHandle.getValuesMap().entrySet()) {
            logger.info("  * Option {} -> {}", option.getKey(), option.getValue());
        }
    }

    private void defineQueryOptions() throws IOException {
        QueryOptionsManager optionsMgr =
                databaseClient.newServerConfigManager().newQueryOptionsManager();

        String xmlOptions = FileCopyUtils.copyToString(
                new FileReader("src/main/resources/queries/options-distinct-values.xml"));
        StringHandle writeHandle = new StringHandle(xmlOptions);
        RawCombinedQueryDefinition queryDef =
                queryManager.newRawCombinedQueryDefinitionAs(Format.XML, xmlOptions);

        // See Java Developer Guide (ch 3.11.2.2 "Install Query Options")
        optionsMgr.writeOptions("distinct-values", writeHandle);
        logger.info("Registered query options successfully.");
    }

    private void testQueryOptions() {
        ValuesDefinition vdef = queryManager.newValuesDefinition("category", "distinct-values");
        ValuesHandle results = queryManager.values(vdef, new ValuesHandle());
        logger.info("Execute sample query for retrieving distinct values for category");
        logger.info("Results:");

        for (CountedDistinctValue val : results.getValues()) {
            // Raw: <distinct-value frequency="40">book</distinct-value>
            logger.info("   * {} {}", val.get("string", String.class), val.getCount());
        }
    }

}
