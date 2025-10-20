package learning.itstep.javaweb222.servlets;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import jakarta.servlet.ServletException;
// import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Base64;
import learning.itstep.javaweb222.data.DataAccessor;
import learning.itstep.javaweb222.data.dto.AccessToken;
import learning.itstep.javaweb222.data.dto.UserAccess;
import learning.itstep.javaweb222.data.jwt.JwtToken;
import learning.itstep.javaweb222.services.Signature.SignatureService;

@Singleton
public class UserServlet extends HttpServlet {
    private final DataAccessor dataAccessor;
    private final SignatureService signatureService;
    private final Gson gson = new Gson();
        
    @Inject
    public UserServlet(DataAccessor dataAccessor, SignatureService signatureService) {
        this.dataAccessor = dataAccessor;
        this.signatureService = signatureService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Автентифікація за RFC 7617                         // Authorization: Basic QWxhZGRpbjpvcGVuIHNlc2FtZQ==
        String authHeader = req.getHeader("Authorization");   // Basic QWxhZGRpbjpvcGVuIHNlc2FtZQ==
        if(authHeader == null || "".equals(authHeader)) {
            resp.setStatus(401);
            resp.getWriter().print(
                gson.toJson("Missing 'Authorization' header")
            );
            return;
        }
        String authScheme = "Basic ";
        if( ! authHeader.startsWith(authScheme) ) {
            resp.setStatus(401);
            resp.getWriter().print(
                gson.toJson("Invalid Authorization scheme. Must be " + authScheme)
            );
            return;
        }
        String credentials = authHeader.substring( authScheme.length() );  // QWxhZGRpbjpvcGVuIHNlc2FtZQ==
        String userPass;
        try { 
            userPass = new String( 
                Base64.getDecoder().decode(credentials));   // Aladdin:open sesame
        }
        catch(IllegalArgumentException ex) {
            resp.setStatus(401);
            resp.getWriter().print(
                gson.toJson("Invalid credentials. Base64 decode error " + ex.getMessage())
            );
            return;
        }
        String[] parts = userPass.split(":", 2);   // [0]Aladdin  [1]open sesame
        if(parts.length != 2) {
            resp.setStatus(401);
            resp.getWriter().print(
                gson.toJson("Invalid user-pass. Missing ':' ")
            );
            return;
        }
        
        UserAccess ua = dataAccessor.getUserAccessByCredentials(parts[0], parts[1]);
        if(ua == null) {
            resp.setStatus(401);
            resp.getWriter().print(
                gson.toJson("Credentials rejected. Access denied")
            );
            return;
        }
        AccessToken at = dataAccessor.getTokenByUserAccess(ua);
        JwtToken jwt = JwtToken.fromAccessToken(at);
        jwt.setSignature(
            Base64.getEncoder().encodeToString(
                    signatureService.getSignatureBytes(jwt.getBody(), "secret")
        ));
        resp.setHeader("Content-Type", "application/json");
        resp.getWriter().print( jwt.toString() );
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        
        resp.getWriter().print(
                gson.toJson("POST works. JWT: " + req.getAttribute("JWT") + 
                        ", status: " + req.getAttribute("JwtStatus"))
        );        
    }    
    
}

/*
Д.З. Реалізувати перевірку усіх стандартних НТТР-методів запиту
від фронтенда до бекенда, пересвідчитись у задоволенні вимог CORS
*/       