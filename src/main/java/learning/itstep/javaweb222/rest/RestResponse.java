package learning.itstep.javaweb222.rest;


public class RestResponse {
    private RestStatus status = RestStatus.status200;
    private RestMeta meta;
    private Object data;

    public RestMeta getMeta() {
        return meta;
    }

    public void setMeta(RestMeta meta) {
        this.meta = meta;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public RestStatus getStatus() {
        return status;
    }

    public void setStatus(RestStatus status) {
        this.status = status;
    }
    
}
