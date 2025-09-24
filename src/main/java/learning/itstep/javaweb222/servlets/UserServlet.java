package learning.itstep.javaweb222.servlets;
 
import com.google.gson.Gson;
import com.google.inject.Singleton;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Base64;
import java.util.UUID;
import learning.itstep.javaweb222.data.dto.User;
 
//@WebServlet("/user")
@Singleton
public class UserServlet extends HttpServlet {
 
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    // Автентифікація за RFC 7617
        Gson gson = new Gson();
        String authHeader = req.getHeader("Authorization");
        if(authHeader == null || "".equals(authHeader)) {
            resp.setStatus(401);
            resp.getWriter().print(
                gson.toJson("Missing 'Authorization' header")
            );
            return;
        }
        String authScheme = "Basic";
          if(!authHeader.startsWith(authScheme)) {
            resp.setStatus(401);
            resp.getWriter().print(
                gson.toJson("Invalid authorization scheme.Must be"+authScheme)
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
        
            String [] parts= userPass.split(":",2);
            if(parts.length!=2){
              resp.setStatus(401);
            resp.getWriter().print(
                gson.toJson("Invalid user pass.Missing ':'")
            );
            }
        User user = new User()
            .setId(UUID.randomUUID())
            .setName("Hrebenchuk")
            .setEmail("marya101204@gmail.com");
   
    resp.setHeader("Content-Type","application/json");
        resp.getWriter().print(
                gson.toJson(user)
        );
//     System.out.println("UserServlet::doGet");  // вивід до out сервера (Apache)
//       // атрибут, що буде у req протягом подальшої обробки (у т.ч. на JSP)
//        req.setAttribute("UserServlet", "Hello from UserServlet");
//        
//        // return View()
//        req.getRequestDispatcher("user.jsp").forward(req, resp);
//     
    }
}