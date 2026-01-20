package learning.itstep.javaweb222.servlets;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import jakarta.servlet.ServletException;
// import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Base64;
import java.util.Map;
import learning.itstep.javaweb222.data.DataAccessor;
import learning.itstep.javaweb222.data.dto.AccessToken;
import learning.itstep.javaweb222.data.dto.AuthCredential;
import learning.itstep.javaweb222.data.jwt.JwtToken;
import learning.itstep.javaweb222.models.user.UserProfileModel;
import learning.itstep.javaweb222.rest.RestMeta;
import learning.itstep.javaweb222.rest.RestResponse;
import learning.itstep.javaweb222.rest.RestStatus;
import learning.itstep.javaweb222.services.Signature.SignatureService;

@Singleton
public class UserServlet extends HttpServlet {
    private final DataAccessor  dataAccessor;
    private final SignatureService signatureService;
    private final Gson gson = new GsonBuilder().serializeNulls().create();
    private RestResponse restResponse;
    private JwtToken jwtToken;
        
    @Inject
    public UserServlet(DataAccessor  dataAccessor, SignatureService signatureService) {
        this.dataAccessor = dataAccessor;
        this.signatureService = signatureService;
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.restResponse = new RestResponse();
        restResponse.setMeta(
                new RestMeta()
                .setServiceName("Shop API 'User'")
                .setCacheSeconds(1000)
                .setManipulations(new String[] {"GET", "POST"})
                .setLinks( Map.ofEntries(
                    Map.entry("authenticate", "GET /user"),
                    Map.entry("profile", "GET /user/profile"),
                    Map.entry("analytics", "GET /user/analytics")
                ) )
        );
        jwtToken = (JwtToken) req.getAttribute("JWT");
        
        super.service(req, resp);        
        
        resp.setContentType("application/json; charset=utf-8");
        resp.getWriter().print(
                gson.toJson(restResponse)
        );
    }
    

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getPathInfo() ;
        if(path == null || path.length() == 0) {
            this.authenticate(req, resp);
        }
        else if("/profile".equals(path)) {
            this.profile(req, resp);
        }
        else if("/analytics".equals(path)) {
            this.analytics(req, resp);
        }
        
        else if ("/experience".equals(path)) {
            if (jwtToken == null) {
                restResponse.setStatus(RestStatus.status401);
                return;
            }

            restResponse.setData(
                dataAccessor.getUserExperienceBlocks(
                    jwtToken.getPayload().getSub()
                )
            );
            restResponse.getMeta().setDataType("array");
        }


        else {
            this.restResponse.setStatus(RestStatus.status404);
            this.restResponse.setData("Path not found: " + path);
        }

    }
    
    private void profile(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        
        if(jwtToken == null) {
            this.restResponse.setStatus(RestStatus.status401);
            return;
        }
        String userId = jwtToken.getPayload().getSub();
        try {
            AuthCredential userAccess = dataAccessor.getUserAccess(
                    jwtToken.getPayload().getSub(),
                    jwtToken.getPayload().getAud()
            );
               
            if( userAccess == null || userAccess.getUser()== null ) {
            this.restResponse.getMeta().setDataType("string");
            this.restResponse.setStatus(RestStatus.status404);
            this.restResponse.setData("User not found");
            return;
            }

            this.restResponse.setData(
                    new UserProfileModel()
                    .setLogin(userAccess.getLogin())
                    .setRole(userAccess.getRoleId())
                    .setUser(userAccess.getUser())
                     .setProfileViews(
                        dataAccessor.getProfileViewsCount(userId)
                    )
                    .setPostViews(
                        dataAccessor.getPostViewsCount(userId)
                    )
                    

                   
            );
            this.restResponse.getMeta().setDataType("object");
        }
        catch(Exception ex) {
            this.restResponse.getMeta().setDataType("string");
            this.restResponse.setStatus(RestStatus.status400);
            this.restResponse.setData(ex.getMessage());
        }   
    }
    private void analytics(HttpServletRequest req, HttpServletResponse resp) {

        if(jwtToken == null) {
            this.restResponse.setStatus(RestStatus.status401);
            return;
        }

        String userId = jwtToken.getPayload().getSub();

        try {
            this.restResponse.setData(
                Map.of(
                    "profileViews",
                    dataAccessor.getProfileViewsCount(userId),
                    "postViews",
                    dataAccessor.getPostViewsCount(userId)
                )
            );
            this.restResponse.getMeta().setDataType("object");
        }
        catch(Exception ex) {
            this.restResponse.getMeta().setDataType("string");
            this.restResponse.setStatus(RestStatus.status400);
            this.restResponse.setData(ex.getMessage());
        }
    }
   

    
    private void authenticate(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Автентифікація за RFC 7617                         // Authorization: Basic QWxhZGRpbjpvcGVuIHNlc2FtZQ==
        String authHeader = req.getHeader("Authorization");   // Basic QWxhZGRpbjpvcGVuIHNlc2FtZQ==
        if(authHeader == null || "".equals(authHeader)) {            
            this.restResponse.setStatus(RestStatus.status401);
            this.restResponse.setData("Missing 'Authorization' header");
            return;
        }
        String authScheme = "Basic ";
        if( ! authHeader.startsWith(authScheme) ) {
            this.restResponse.setStatus(RestStatus.status401);
            this.restResponse.setData("Invalid Authorization scheme. Must be " + authScheme);
            return;
        }
        String credentials = authHeader.substring( authScheme.length() );  // QWxhZGRpbjpvcGVuIHNlc2FtZQ==
        String userPass;
        try { 
            userPass = new String( 
                Base64.getDecoder().decode(credentials));   // Aladdin:open sesame
        }
        catch(IllegalArgumentException ex) {
            this.restResponse.setStatus(RestStatus.status401);
            this.restResponse.setData("Invalid credentials. Base64 decode error " + ex.getMessage());
            return;
        }
        String[] parts = userPass.split(":", 2);   // [0]Aladdin  [1]open sesame
        if(parts.length != 2) {
            this.restResponse.setStatus(RestStatus.status401);
            this.restResponse.setData("Invalid user-pass. Missing ':' ");
            return;
        }
        
        AuthCredential ua = dataAccessor.getUserAccessByCredentials(parts[0], parts[1]);
        if(ua == null) {
            this.restResponse.setStatus(RestStatus.status401);
            this.restResponse.setData("Credentials rejected. Access denied");
            return;
        }
        AccessToken at =  dataAccessor.createAccessToken(
                        ua,
                        req.getHeader("User-Agent"),
                        req.getRemoteAddr()
                );
        JwtToken jwt = JwtToken.fromAccessToken(at);
        jwt.setSignature(
            Base64.getEncoder().encodeToString(
                signatureService.getSignatureBytes(jwt.getBody(), "secret")
        ));

        this.restResponse.setData( jwt.toString() );
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        
        resp.getWriter().print(
                gson.toJson("POST works. JWT: " + req.getAttribute("JWT") + 
                        ", status: " + req.getAttribute("JwtStatus"))
        );        
    }    
    
}

  