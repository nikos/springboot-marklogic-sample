package de.nava.mlsample.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

/**
 * This would be the perfect place to initialize same sample data.
 *
 * @author Niko Schmuck
 */
@Service
public class BootstrapDataPopulator implements InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(BootstrapDataPopulator.class);

    @Override
    public void afterPropertiesSet() throws Exception {
        logger.info("~~~ Load bootstrap data");
    }

}
