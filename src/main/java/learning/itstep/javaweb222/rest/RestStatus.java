package learning.itstep.javaweb222.rest;


public class RestStatus {
   private int code;
   private boolean isOk;
   private String phrase;

   public static final RestStatus status200 = new RestStatus(200,true,"OK");
   public static final RestStatus status400 = new RestStatus(400,false,"Bad request");
   public static final RestStatus status401 = new RestStatus(401,false,"UnAuthorized");
   public static final RestStatus status403 = new RestStatus(403,false,"Forbidden");
   public static final RestStatus status404 = new RestStatus(404,false,"Not Found");
   
    public RestStatus(int code, boolean isOk, String phrase) {
        this.code = code;
        this.isOk = isOk;
        this.phrase = phrase;
    }

   
   
   
    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public boolean isIsOk() {
        return isOk;
    }

    public void setIsOk(boolean isOk) {
        this.isOk = isOk;
    }

    public String getPhrase() {
        return phrase;
    }

    public void setPhrase(String phrase) {
        this.phrase = phrase;
    }
   
}
