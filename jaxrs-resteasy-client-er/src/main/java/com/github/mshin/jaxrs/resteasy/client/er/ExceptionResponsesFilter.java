package com.github.mshin.jaxrs.resteasy.client.er;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientResponseContext;
import javax.ws.rs.client.ClientResponseFilter;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mshin.exception.response.model.ExceptionResponses;

/**
 * @author MunChul Shin
 */
@Priority(value = Priorities.USER)
public class ExceptionResponsesFilter implements ClientResponseFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionResponsesFilter.class);

    ObjectMapper mapper;

    public ExceptionResponsesFilter() {
    }

    public ExceptionResponsesFilter(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public void filter(ClientRequestContext rqc, ClientResponseContext rspc) throws IOException {
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("ClientResponseContext=" + rspc);

        if (null == rspc) {
            LOGGER.warn("ClientResponseContext was null. Exiting filter.");
            return;
        }

        if (Response.Status.OK.getStatusCode() != rspc.getStatus()) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("ErrorStatus=" + rspc.getStatus());
                LOGGER.debug("HasErrorEntity=" + rspc.hasEntity());
            }

            if (rspc.hasEntity()) {
                ExceptionResponses exceptionResponses = null;
                boolean unmarshaledToErrorResponses = true;

                // Copying the input stream to check if it can be marshaled to
                // ErrorResponses.
                // Reading the input stream 1ce closes it so checking on copy.
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                IOUtils.copy(rspc.getEntityStream(), os);
                InputStream copiedEntityStream = new ByteArrayInputStream(os.toByteArray());

                Boolean canDeserializeJsonToErrorResponses = null;
                try {
                    exceptionResponses = mapper.readValue(copiedEntityStream, ExceptionResponses.class);
                    canDeserializeJsonToErrorResponses = true;

                } catch (IOException e) {
                    LOGGER.warn("Serialization failed. Exception: " + e);
                    canDeserializeJsonToErrorResponses = false;
                }

                if (LOGGER.isDebugEnabled())
                    LOGGER.debug("canDeserializeJsonToErrorResponses=" + canDeserializeJsonToErrorResponses);

                if (true == canDeserializeJsonToErrorResponses) {
                    unmarshaledToErrorResponses = true;
                    rspc.getEntityStream().close();
                } else {
                    LOGGER.warn("Can't deserialize to ErrorResponses. Not attempting serialization.");
                    unmarshaledToErrorResponses = false;
                }

                if (unmarshaledToErrorResponses && null != exceptionResponses) {
                    if (LOGGER.isDebugEnabled())
                        LOGGER.debug("Throwing ErrorResponses.");
                    throw exceptionResponses;
                }
                // else let client handle code normally; will check for not 200
                // status, and then throw WebApplicationException based on http
                // status code
            }

        }

        // if it's OK, do nothing; let marshaler marshal opbject normally.
    }

    public void setMapper(ObjectMapper mapper) {
        this.mapper = mapper;
    }
}