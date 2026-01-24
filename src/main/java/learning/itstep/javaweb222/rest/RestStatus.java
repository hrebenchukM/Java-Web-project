package learning.itstep.javaweb222.rest;


public class RestStatus {

  
   private int code;
   private boolean isOk;
   private String phrase;

 // ================= SUCCESS =================
    public static final RestStatus status200 = new RestStatus(200, true, "OK");
    public static final RestStatus status201 = new RestStatus(201, true, "Created");
    public static final RestStatus status204 = new RestStatus(204, true, "No Content");

    // ================= CLIENT ERRORS =================
    public static final RestStatus status400 = new RestStatus(400, false, "Bad Request");
    public static final RestStatus status401 = new RestStatus(401, false, "Unauthorized");
    public static final RestStatus status403 = new RestStatus(403, false, "Forbidden");
    public static final RestStatus status404 = new RestStatus(404, false, "Not Found");
    public static final RestStatus status405 = new RestStatus(405, false, "Method Not Allowed");
    public static final RestStatus status409 = new RestStatus(409, false, "Conflict");

    // ================= SERVER ERRORS =================
    public static final RestStatus status500 = new RestStatus(500, false, "Internal Server Error");

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
