package com.github.mshin.jaxrs.cxf.client.er;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.cxf.jaxrs.client.Client;
import org.apache.cxf.jaxrs.client.JAXRSClientFactory;
import org.apache.cxf.jaxrs.client.WebClient;

import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import com.fasterxml.jackson.jaxrs.xml.JacksonJaxbXMLProvider;
import com.github.mshin.exception.response.handler.ExceptionResponseHandler;
import com.github.mshin.exception.response.handler.ExceptionResponsesHandler;

/**
 * 
 * TODO This might need to be rewritten.
 * 
 * @author MunChul Shin
 *
 */
public class JaxrsCxfClientProxyFactory {

    List<Object> providers;

    HashMap<String, Object> cache = new HashMap<>();

    public JaxrsCxfClientProxyFactory() {
        providers = new ArrayList<>();
        providers.add(new JacksonJaxbJsonProvider());
        providers.add(new JacksonJaxbXMLProvider());
        providers.add(new ExceptionResponseHandler());
        providers.add(new ExceptionResponsesHandler());
    }

    public <T> T getProxy(String url, Class<T> resourceClass) {
        return getProxy(url, resourceClass, null);
    }

    public <T> T getProxy(String url, Class<T> resourceClass, String configFileClasspathLocation) {
        String key = resourceClass.getName() + url;

        @SuppressWarnings("unchecked")
        T proxy = (T) cache.get(key);

        if (proxy == null) {
            proxy = JAXRSClientFactory.create(url, resourceClass, providers, configFileClasspathLocation);
            cache.put(key, proxy);
        }
        return proxy;
    }

    public Client getClient(Object proxy) {
        return WebClient.client(proxy);
    }

    public <T> Client getClient(String url, Class<T> resourceClass) {
        Object proxy = getProxy(url, resourceClass);
        return WebClient.client(proxy);
    }

    public WebClient getWebClient(Object proxy) {
        return WebClient.fromClient(WebClient.client(proxy));
    }

    public WebClient getWebClient(String url, String configFileClasspathLocation) {
        String key = url + "_webClient";
        WebClient webClient = (WebClient) cache.get(key);
        if (null == webClient) {
            webClient = WebClient.create(url, configFileClasspathLocation);
            cache.put(key, webClient);
        }
        return webClient;
    }

    public WebClient getWebClient(String url) {
        return getWebClient(url, null);
    }

}
