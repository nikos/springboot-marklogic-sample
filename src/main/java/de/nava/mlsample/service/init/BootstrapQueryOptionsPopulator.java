package de.nava.mlsample.service.init;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.admin.QueryOptionsManager;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.QueryOptionsListHandle;
import com.marklogic.client.io.StringHandle;
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
        // TODO: fix write option problem: defineQueryOptions();
        // testQueryOptions();
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
        /* Currently throws
           com.marklogic.client.FailedRequestException: Local message: /config/query write failed: Bad Request. Server Message: RESTAPI-INVALIDCONTENT: (err:FOER0000) Invalid content: Operation results in invalid Options: XDMP-VALIDATEUNEXPECTED: (err:XQDY0027) validate strict { $opt } -- Invalid node: Found @ns but expected (any(lax,!())) at fn:doc("")/search:options/search:values[3]/search:range/search:path-index/@ns using schema "search.xsd"@ns(any(lax,!()))fn:doc("")/search:options/search:values[3]/search:range/search:path-index/@ns"search.xsd"
                at com.marklogic.client.impl.JerseyServices.putPostValueImpl(JerseyServices.java:2621)
                at com.marklogic.client.impl.JerseyServices.putValue(JerseyServices.java:2472)
                at com.marklogic.client.impl.QueryOptionsManagerImpl.writeOptions(QueryOptionsManagerImpl.java:158)
                at de.nava.mlsample.service.init.BootstrapQueryOptionsPopulator.defineQueryOptions(BootstrapQueryOptionsPopulator.java:66)
        */
        logger.info("Registered query options");
    }

    private void testQueryOptions() {
        ValuesDefinition vdef = queryManager.newValuesDefinition("category", "distinct-values");
        // TODO: Should use ValueHandle()
        StringHandle results = queryManager.values(vdef, new StringHandle());
        logger.info("---> Results: {}", results);

        //for (CountedDistinctValue val : results.getValues()) {
        //    logger.info("   * {} ", val);
        //}
    }

}
