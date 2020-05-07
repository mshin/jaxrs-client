package com.github.mshin.jaxrs.resteasy.client.er;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.mshin.jaxrs.resteasy.client.JsonObjectMapperProvider;

/**
 * This client is capable of handling ExceptionResponses.
 * 
 * @author MunChul Shin
 * @param <T>
 */
public class JaxrsResteasyClient<T> extends com.github.mshin.jaxrs.resteasy.client.JaxrsResteasyClient<T> {

    private static final String CLASS_NAME = JaxrsResteasyClient.class.getName();
    private static final Logger LOGGER = LoggerFactory.getLogger(CLASS_NAME);

    protected void addDefaultComponents() {
        LOGGER.info("Adding " + CLASS_NAME + "default components.");
        JsonObjectMapperProvider mapperProvider = new JsonObjectMapperProvider();
        ExceptionResponsesFilter exceptionResponsesFilter = new ExceptionResponsesFilter(
                mapperProvider.getContext(null));
        components.add(mapperProvider);
        components.add(exceptionResponsesFilter);
    }

    public JaxrsResteasyClient(String baseUri, Class<T> clazz) {
        super(baseUri, clazz);
    }

}
