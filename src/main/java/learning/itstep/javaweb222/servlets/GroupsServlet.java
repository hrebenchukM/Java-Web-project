
package learning.itstep.javaweb222.servlets;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import learning.itstep.javaweb222.data.DataAccessor;
import learning.itstep.javaweb222.data.dto.ProductGroup;

@Singleton
public class GroupsServlet extends HttpServlet {
    private final DataAccessor dataAccessor;
    private final Gson gson = new Gson();
    
    @Inject
    public GroupsServlet(DataAccessor dataAccessor) {
        this.dataAccessor = dataAccessor;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<ProductGroup> groups = dataAccessor.getProductGroups();
        String fileUrl = String.format("%s://%s:%d%s/file/", 
                req.getScheme() ,
                req.getServerName() ,
                req.getServerPort(),
                req.getContextPath()
                );
        for(ProductGroup group : groups){
         group.setImageUrl(fileUrl + group.getImageUrl());
        }
      resp.setCharacterEncoding(StandardCharsets.UTF_8);
        resp.setContentType("application/json");
        resp.getWriter().print(
          gson.toJson(groups)
  );
    }
    // req.getServletPath()  /groups

    // req.getContextPath()  /JavaWeb222

    // req.getRequestURI()   /JavaWeb222/groups

    // req.getServerName()   localhost

    // req.getScheme()       http

    // req.getServerPort()   8080
 
}
