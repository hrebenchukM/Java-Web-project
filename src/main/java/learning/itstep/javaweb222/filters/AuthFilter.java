package learning.itstep.javaweb222.filters;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Base64;
import learning.itstep.javaweb222.data.jwt.JwtToken;
import learning.itstep.javaweb222.services.Signature.SignatureService;

@Singleton
public class AuthFilter implements Filter {

    private final SignatureService signatureService;

    @Inject
    public AuthFilter(SignatureService signatureService) {
        this.signatureService = signatureService;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        // узгодження типів для роботи з НТТР-протоколом
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        String authKey = "JwtStatus";
        String authHeader = req.getHeader("Authorization");   // Basic QWxhZGRpbjpvcGVuIHNlc2FtZQ==
        if (authHeader == null || "".equals(authHeader)) {
            req.setAttribute(authKey, "Missing'Authorization' header");

        }
        else{
       String authScheme = "Bearer ";
            if( ! authHeader.startsWith(authScheme) ) {
                req.setAttribute(authKey, "Invalid Authorization scheme. Must be " + authScheme);
            }
            else
            {
            
            
            String jwt = authHeader.substring(authScheme.length());
            String [] parts = jwt.split("\\.");
            if(parts.length !=3)
            {
              req.setAttribute(authKey, "Invalid JWT structure");
            }
            else
            {
                String jwtBody = parts[0] + "." + parts[1];
                String signature = Base64.getEncoder().encodeToString(

                   signatureService.getSignatureBytes(jwtBody, "secret")
             );
             if(parts[2].equals(signature))
             {
                 req.setAttribute("JWT", JwtToken.fromParts(parts));
                  req.setAttribute(authKey, "Ok");
             }
             else
             {
                     req.setAttribute(authKey, "Invalid signature");
             }
            }
            }
        }
       chain.doFilter(req, resp);
    }

    @Override
    public void destroy() {

    }

}
