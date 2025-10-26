package learning.itstep.javaweb222.servlets;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import learning.itstep.javaweb222.data.DataAccessor;
import learning.itstep.javaweb222.data.dto.Product;
import learning.itstep.javaweb222.data.dto.ProductGroup;
import learning.itstep.javaweb222.rest.RestMeta;
import learning.itstep.javaweb222.rest.RestResponse;
import learning.itstep.javaweb222.rest.RestStatus;

@Singleton
public class GroupsServlet extends HttpServlet {
    private final DataAccessor dataAccessor;
    private final Gson gson = new GsonBuilder().serializeNulls().create();
    private  RestResponse restResponse ;
    @Inject
    public GroupsServlet(DataAccessor dataAccessor) {
        this.dataAccessor = dataAccessor;
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.restResponse = new  RestResponse();
        
        restResponse.setMeta(
                new RestMeta()
                .setServiceName("Shop API 'Product groups'")
                .setCacheSeconds(1000)
                .setManipulations(new String[] {"GET"})
                .setLinks(Map.ofEntries(
                    Map.entry("groups", "/groups"),
                    Map.entry("group", "/groups/{id}")
                ) )
        );
        String pathInfo = req.getPathInfo(); 
        if (pathInfo != null && !pathInfo.isEmpty()) {
          String[] pathParts = pathInfo.substring(1).split("/"); 
          restResponse.getMeta().setPathParams(pathParts);
        } else {
          restResponse.getMeta().setPathParams(new String[0]);
        }
        super.service(req, resp); 
        
        resp.setContentType("application/json");
        resp.getWriter().print(
                gson.toJson(restResponse)
        );
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
       String path = req.getPathInfo() ;
       if(path ==null){
          getAllGroups(req,resp);
       }
       else{
           getGroup(req,resp);
       }
    }   
    
    
     private void getGroup(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getPathInfo().substring(1) ;// /
        ProductGroup pg = dataAccessor.getProductGroupBySlug(path);
        if(pg!=null){
            String fileUrl = getFileUrl(req);
            pg.setImageUrl(fileUrl + pg.getImageUrl());
            List<Product> products = pg.getProducts();
            if(products!=null){
              for(Product p : products){
                  p.setImageUrl(fileUrl+ p.getImageUrl());
              }
            }
            restResponse.getMeta().setDataType("json.object");
   
        }
        else{
             restResponse.setStatus(RestStatus.status404);
             restResponse.getMeta().setDataType("null");
        }
        this.restResponse.setData(pg);
     }
    private void getAllGroups(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
               
           
        List<ProductGroup> groups = dataAccessor.getProductGroups();
        String fileUrl = getFileUrl(req);
        for(ProductGroup group : groups) {
            group.setImageUrl( fileUrl + group.getImageUrl() );
        }
        restResponse.getMeta().setDataType("json.array");
       
        restResponse.setData(groups);
       
    } 
    
    
    private String getFileUrl(HttpServletRequest req){
        return String.format("%s://%s:%d%s/file/", 
                req.getScheme(),
                req.getServerName(),
                req.getServerPort(),
                req.getContextPath());
    }
    // req.getServletPath()  /groups
    // req.getContextPath()  /JavaWeb222
    // req.getRequestURI()   /JavaWeb222/groups
    // req.getServerName()   localhost
    // req.getScheme()       http
    // req.getServerPort()   8080
}