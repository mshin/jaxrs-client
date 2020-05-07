package com.github.mshin.jaxrs.resteasy.client;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.ContextResolver;

import org.jboss.resteasy.plugins.providers.jackson.ResteasyJackson2Provider;

/**
 * 
 * @author MunChul Shin
 *
 */
public class JsonObjectMapperProvider extends ResteasyJackson2Provider implements ContextResolver<ObjectMapper> {

    private ObjectMapper mapper;

    public JsonObjectMapperProvider() {
        mapper = locateMapper(null, MediaType.APPLICATION_JSON_TYPE);
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Override
    public ObjectMapper getContext(Class<?> type) {
        return mapper;
    }

}
