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
import learning.itstep.javaweb222.data.dto.Product;
import learning.itstep.javaweb222.data.dto.ProductGroup;
import learning.itstep.javaweb222.rest.RestMeta;
import learning.itstep.javaweb222.rest.RestResponse;
import learning.itstep.javaweb222.rest.RestStatus;

@Singleton
public class ProductServlet extends HttpServlet {
    private final DataAccessor dataAccessor;
    private final Gson gson = new GsonBuilder().serializeNulls().create();
    private RestResponse restResponse;
    
    @Inject
    public ProductServlet(DataAccessor dataAccessor) {
        this.dataAccessor = dataAccessor;
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.restResponse = new RestResponse();
        restResponse.setMeta(
                new RestMeta()
                .setServiceName("Shop API 'Product'")
                .setCacheSeconds(1000)
                .setManipulations(new String[] {"GET"})
                .setLinks(Map.ofEntries(
                    Map.entry("product", "/product/{id-or-slug}")
                ) )
        );
        
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
            this.restResponse.setStatus(RestStatus.status400);
            this.restResponse.setData("Missing path parameter: id-or-slug");
            this.restResponse.getMeta().setDataType("string");
        }
        else {
            path = path.substring(1);
            Product product = dataAccessor.getProductBySlugOrId(path);
            if(product == null) {                
                this.restResponse.setStatus(RestStatus.status404);
                this.restResponse.setData("Product not found for id: " + path);
                this.restResponse.getMeta().setDataType("string");
            }
            else {
//                String imgUrl = product.getImageUrl();
//                if(imgUrl != null) {
//                    product.setImageUrl(FileServlet.getFileUrl(req, imgUrl));
//                }
                product.correctImageUrl(req);
                ProductGroup grp = product.getGroup();
                if(grp != null) {
                    grp.setImageUrl(FileServlet.getFileUrl(req, grp.getImageUrl()));
                }
                this.restResponse.getMeta().setDataType("object");
                this.restResponse.setData(product);                
            }
        }
    }
    
    
}