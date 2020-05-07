package com.github.mshin.jaxrs.resteasy.client;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder.HostnameVerificationPolicy;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author MunChul Shin
 * @param <T>
 */
public class JaxrsResteasyClient<T> {

    private static final String CLASS_NAME = JaxrsResteasyClient.class.getName();
    private static final Logger LOGGER = LoggerFactory.getLogger(CLASS_NAME);

    private String baseUri;
    private Class<T> clazz;

    private Integer connectionPoolSize = 250;
    private Integer maxPooledPerRoute = 250;
    private Integer connectionTTL = 2000;
    private Integer connectTimeout = 500;
    private Integer readTimeout = 60000;
    private Integer connectionCheckoutTimeout = 2000;
    private HostnameVerificationPolicy hostnameVerificationPolicy = HostnameVerificationPolicy.ANY;

    private ResteasyClientBuilder clientBuilder = new ResteasyClientBuilder();
    private ResteasyClient resteasyClient;
    private ResteasyWebTarget resteasyWebTarget;
    private T proxy;
    protected List<Object> components = new ArrayList<>();
    private Boolean isUseDefaultComponents = true;

    public JaxrsResteasyClient(String baseUri, Class<T> clazz) {
        this.baseUri = baseUri;
        this.clazz = clazz;
    }

    public T getProxy() {
        if (null == resteasyClient) {
            LOGGER.info("Client is null; creating a new client.");

            if (isUseDefaultComponents) {
                addDefaultComponents();
            }

            clientBuilder = clientBuilder.connectionPoolSize(connectionPoolSize).maxPooledPerRoute(maxPooledPerRoute)
                    .connectionTTL(connectionTTL, TimeUnit.MILLISECONDS)
                    .connectTimeout(connectTimeout, TimeUnit.MILLISECONDS)
                    .connectionCheckoutTimeout(connectionCheckoutTimeout, TimeUnit.MILLISECONDS)
                    .readTimeout(readTimeout, TimeUnit.MILLISECONDS).hostnameVerification(hostnameVerificationPolicy);

            for (Object component : components) {
                LOGGER.debug("Registering " + component);
                clientBuilder = clientBuilder.register(component);
            }

            resteasyClient = clientBuilder.build();
        }
        if (null == resteasyWebTarget) {
            LOGGER.info("Target is null; creating a new target.");
            resteasyWebTarget = resteasyClient.target(baseUri);
        }
        if (null == proxy) {
            LOGGER.info("Proxy is null; creating a new proxy.");

            this.proxy = resteasyWebTarget.proxy(clazz);
        }

        return proxy;
    }

    /**
     * Allows reconfiguration of the proxy. Sets the resteasyClient,
     * resteasyWebTarget, components and proxy to null; sets the clientBuilder to a
     * new ResteasyClientBuilder.
     */
    public void resetAll() {
        this.clientBuilder = new ResteasyClientBuilder();
        this.resteasyClient = null;
        this.resteasyWebTarget = null;
        this.components = new ArrayList<>();
        this.proxy = null;
    }

    protected void addDefaultComponents() {
        LOGGER.info("Adding " + CLASS_NAME + "default components.");
        components.add(new JsonObjectMapperProvider());
    }

    public String getBaseUri() {
        return baseUri;
    }

    public void setBaseUri(String baseUri) {
        this.baseUri = baseUri;
    }

    public Class<T> getClazz() {
        return clazz;
    }

    public Integer getConnectionPoolSize() {
        return connectionPoolSize;
    }

    public void setConnectionPoolSize(Integer connectionPoolSize) {
        this.connectionPoolSize = connectionPoolSize;
    }

    public Integer getMaxPooledPerRoute() {
        return maxPooledPerRoute;
    }

    public void setMaxPooledPerRoute(Integer maxPooledPerRoute) {
        this.maxPooledPerRoute = maxPooledPerRoute;
    }

    public Integer getConnectionTTL() {
        return connectionTTL;
    }

    public void setConnectionTTL(Integer connectionTTL) {
        this.connectionTTL = connectionTTL;
    }

    public Integer getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(Integer connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public Integer getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(Integer readTimeout) {
        this.readTimeout = readTimeout;
    }

    public Integer getConnectionCheckoutTimeout() {
        return connectionCheckoutTimeout;
    }

    public void setConnectionCheckoutTimeout(Integer connectionCheckoutTimeout) {
        this.connectionCheckoutTimeout = connectionCheckoutTimeout;
    }

    public HostnameVerificationPolicy getHostnameVerificationPolicy() {
        return hostnameVerificationPolicy;
    }

    public void setHostnameVerificationPolicy(HostnameVerificationPolicy hostnameVerificationPolicy) {
        this.hostnameVerificationPolicy = hostnameVerificationPolicy;
    }

    public ResteasyClient getResteasyClient() {
        return resteasyClient;
    }

    public void setResteasyClient(ResteasyClient resteasyClient) {
        this.resteasyClient = resteasyClient;
    }

    public ResteasyWebTarget getResteasyWebTarget() {
        return resteasyWebTarget;
    }

    public void setResteasyWebTarget(ResteasyWebTarget resteasyWebTarget) {
        this.resteasyWebTarget = resteasyWebTarget;
    }

    public List<Object> getComponents() {
        return components;
    }

    public void setComponents(List<Object> components) {
        this.components = components;
    }

    public void addComponent(Object component) {
        this.components.add(component);
    }

    public Boolean getIsUseDefaultComponents() {
        return isUseDefaultComponents;
    }

    public void setIsUseDefaultComponents(Boolean isUseDefaultComponents) {
        this.isUseDefaultComponents = isUseDefaultComponents;
    }

}
