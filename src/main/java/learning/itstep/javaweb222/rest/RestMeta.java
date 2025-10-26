package learning.itstep.javaweb222.rest;

import java.util.Map;


public class RestMeta {
    private String serviceName;
    private RestPagination pagination;
    private long cacheSeconds;
    private String dataType;
    private String[] manipulations;
    private Map<String, String> links;
    private String[] pathParams;

    public String[] getPathParams() {
        return pathParams;
    }

    public void setPathParams(String[] pathParams) {
        this.pathParams = pathParams;
    }
    
    public String getServiceName() {
        return serviceName;
    }

    public RestMeta setServiceName(String serviceName) {
        this.serviceName = serviceName;
        return this;
    }

    public RestPagination getPagination() {
        return pagination;
    }

    public RestMeta setPagination(RestPagination pagination) {
        this.pagination = pagination;
        return this;
    }

    public long getCacheSeconds() {
        return cacheSeconds;
    }

    public RestMeta setCacheSeconds(long cacheSeconds) {
        this.cacheSeconds = cacheSeconds;
        return this;
    }

    public String getDataType() {
        return dataType;
    }

    public RestMeta setDataType(String dataType) {
        this.dataType = dataType;
        return this;
    }

    public String[] getManipulations() {
        return manipulations;
    }

    public RestMeta setManipulations(String[] manipulations) {
        this.manipulations = manipulations;
        return this;
    }

    public Map getLinks() {
        return links;
    }

    public RestMeta setLinks(Map links) {
        this.links = links;
        return this;
    }
    
    
}
