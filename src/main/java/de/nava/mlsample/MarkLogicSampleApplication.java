package de.nava.mlsample;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.document.JSONDocumentManager;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.JAXBHandle;
import com.marklogic.client.query.QueryManager;
import de.nava.mlsample.domain.Product;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.xml.bind.JAXBException;

/**
 * @author Niko Schmuck
 */
@Configuration
@EnableAutoConfiguration
@ComponentScan
public class MarkLogicSampleApplication {

    @Value("${marklogic.host}")
    private String host;

    @Value("${marklogic.port}")
    private int port;

    @Value("${marklogic.username}")
    private String username;

    @Value("${marklogic.password}")
    private String password;

    @Bean
    public DatabaseClient getDatabaseClient() {
        // TODO: is this really required?
        try {
            // configure once before creating a client
            DatabaseClientFactory.getHandleRegistry().register(
                    JAXBHandle.newFactory(Product.class)
            );
        } catch (JAXBException e) {
            e.printStackTrace();
        }

        return DatabaseClientFactory.newClient(host, port, username, password,
                DatabaseClientFactory.Authentication.DIGEST);
    }

    @Bean
    public QueryManager getQueryManager() {
        return getDatabaseClient().newQueryManager();
    }

    @Bean
    public XMLDocumentManager getXMLDocumentManager() {
        return getDatabaseClient().newXMLDocumentManager();
    }

    @Bean
    public JSONDocumentManager getJSONDocumentManager() {
        return getDatabaseClient().newJSONDocumentManager();
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(MarkLogicSampleApplication.class, args);
    }

}
