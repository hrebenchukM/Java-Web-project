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
import java.util.Map;
import learning.itstep.javaweb222.data.DataAccessor;
import learning.itstep.javaweb222.data.jwt.JwtToken;
import learning.itstep.javaweb222.rest.RestMeta;
import learning.itstep.javaweb222.rest.RestResponse;
import learning.itstep.javaweb222.rest.RestStatus;

@Singleton
public class CartServlet extends HttpServlet{
    private final DataAccessor dataAccessor;
    private final Gson gson = new GsonBuilder().serializeNulls().create();
    private  RestResponse restResponse ;
    private JwtToken jwtToken;
    
    @Inject
    public CartServlet(DataAccessor dataAccessor) {
        this.dataAccessor = dataAccessor;
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.restResponse = new  RestResponse();
        
        restResponse.setMeta(
                new RestMeta()
                .setServiceName("Shop API 'Cart'")
                .setCacheSeconds(1000)
                .setManipulations(new String[] {"GET","POST"})
                .setLinks(Map.ofEntries(
                    Map.entry("add-to-cart", "POST /cart?product-id={product-id}")
                ) )
        );
        
         jwtToken = (JwtToken) req.getAttribute("JWT");
         if(jwtToken ==null){
           this.restResponse.setStatus(RestStatus.status401);
          
         }
         else{
           super.service(req, resp);
         }
//        String pathInfo = req.getPathInfo(); 
//        if (pathInfo != null && !pathInfo.isEmpty()) {
//          String[] pathParts = pathInfo.substring(1).split("/"); 
//          restResponse.getMeta().setPathParams(pathParts);
//        } else {
//          restResponse.getMeta().setPathParams(new String[0]);
//        }
        super.service(req, resp); 
        
        resp.setContentType("application/json");
        resp.getWriter().print(
                gson.toJson(restResponse)
        );
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
       String productId = req.getParameter("product-id");
       this.restResponse.getMeta().setDataType("string");
       
       if(productId == null||productId.isBlank()){
            this.restResponse.setStatus(RestStatus.status400);
            this.restResponse.setData("Missing query parametr:product-id");    
        } 
        try{
          
          dataAccessor.addToCart(productId,jwtToken.getPayload().getSub());
          this.restResponse.setData("Add OK");
        }
        catch(Exception ex){
            this.restResponse.setStatus(RestStatus.status400);
            this.restResponse.setData(ex.getMessage());
        }
       
    }

    
    
    
}
