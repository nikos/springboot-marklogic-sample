package de.nava.mlsample.web;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.core.MultivaluedMap;

@RestController
public class QueryProxyController {

    private static final Logger logger = LoggerFactory.getLogger(SampleRESTwithJSONController.class);

    @Autowired
    protected Client jerseyClient;

    @Autowired
    protected String markLogicBaseURL;

    @RequestMapping(
            value = "/search",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> query() {

        WebResource webResource = jerseyClient.resource(String.format("%s/v1/search", markLogicBaseURL));
        String payload = "";
        MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl();
        queryParams.add("format", "json");
        queryParams.add("view", "all");
        queryParams.add("options", "all");
        queryParams.add("start", "1");
        queryParams.add("pageLength", "10");
        ClientResponse response = webResource
                .queryParams(queryParams)
                .type("application/json")
                .post(ClientResponse.class, payload);
        return new ResponseEntity<String>(response.getEntity(String.class), HttpStatus.valueOf(response.getStatus()));
    }

}


