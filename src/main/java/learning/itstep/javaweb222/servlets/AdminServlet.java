
package learning.itstep.javaweb222.servlets;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import learning.itstep.javaweb222.data.DataAccessor;
import learning.itstep.javaweb222.data.jwt.JwtToken;

@Singleton
public class AdminServlet extends HttpServlet{
   private final DataAccessor  dataAccessor;
   private final Gson gson = new Gson();
   
   @Inject
    public AdminServlet(DataAccessor dataAccessor) {
        this.dataAccessor = dataAccessor;
    }

  @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JwtToken jwtToken = (JwtToken) req.getAttribute("JWT");
        if(jwtToken == null)
        {
            resp.setStatus(401);
            resp.setContentType("text/plain");
            resp.getWriter().print(req.getAttribute("JwtStatus"));
            return;
        }
        String slug = req.getPathInfo();
        switch(req.getPathInfo())
        {
            case "/group":this.postGroup(req,resp);
                break;
            default :
            resp.setStatus(401);
            resp.setContentType("text/plain");
            resp.getWriter().print("Slug"+slug+"not found");
            return;
                
        }
      
    }
   
private void postGroup(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {
    resp.setContentType("application/json");
    resp.getWriter().print(//Перенести до GET методу
            gson.toJson(dataAccessor.adminGetProductGroups())
    );
}
 
}
