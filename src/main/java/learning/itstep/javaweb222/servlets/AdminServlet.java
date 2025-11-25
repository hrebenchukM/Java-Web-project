package learning.itstep.javaweb222.servlets;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import learning.itstep.javaweb222.data.DataAccessor;
import learning.itstep.javaweb222.data.dto.Product;
import learning.itstep.javaweb222.data.dto.ProductGroup;
import learning.itstep.javaweb222.data.jwt.JwtToken;
import learning.itstep.javaweb222.rest.RestMeta;
import learning.itstep.javaweb222.rest.RestResponse;
import learning.itstep.javaweb222.rest.RestStatus;
import learning.itstep.javaweb222.services.form.FormParseException;
import learning.itstep.javaweb222.services.form.FormParseResult;
import learning.itstep.javaweb222.services.form.FormParseService;
import learning.itstep.javaweb222.services.storage.StorageService;
import org.apache.commons.fileupload2.core.FileItem;

@Singleton
public class AdminServlet extends HttpServlet {
    private final Gson gson = new Gson();    
    private final DataAccessor dataAccessor;
    private final FormParseService formParseService;
    private final StorageService storageService;
    private RestResponse restResponse;
    private JwtToken jwtToken;

    @Inject
    public AdminServlet(
            DataAccessor dataAccessor, 
            FormParseService formParseService,
            StorageService storageService
    ) {
        this.dataAccessor = dataAccessor;
        this.formParseService = formParseService;
        this.storageService = storageService;
    }
    
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.restResponse = new RestResponse();
        restResponse.setMeta(
                new RestMeta()
                .setServiceName("Admin API")
                .setCacheSeconds(1000)
                .setManipulations(new String[] {"GET", "POST"})
                .setLinks(Map.ofEntries(
                    Map.entry("add-to-cart", "POST /cart?product-id={id}")
                ) )
        );
        jwtToken = (JwtToken) req.getAttribute("JWT");
        if(jwtToken == null) {
            this.restResponse.setStatus(RestStatus.status401);
        }
        else {
            /* if(req.getMethod().equals("LINK")) {
                this.doLink(req, resp);
            }
            else */ super.service(req, resp); 
        } 
        
        resp.setContentType("application/json; charset=utf-8");
        resp.getWriter().print(
                gson.toJson(restResponse)
        );
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String slug = req.getPathInfo() ;
        switch(slug) {
            case "/groups": this.getGroups(req, resp); break;
            default: 
                this.restResponse.setStatus(RestStatus.status404);
                this.restResponse.setData("Slug " + slug + " not found");
        }
    }
    
    

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String slug = req.getPathInfo() ;
        switch(slug) {
            case "/group": this.postGroup(req, resp); break;
            case "/product": this.postProduct(req, resp); break;
            default: 
                this.restResponse.setStatus(RestStatus.status404);
                this.restResponse.setData("Slug " + slug + " not found");
        }
    }
    
    private void getGroups(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.restResponse.setData( 
                dataAccessor.adminGetProductGroups()
        );
    }
    
    private void postProduct(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            FormParseResult res = formParseService.parse(req);
            Collection<FileItem> files = res.getFiles().values();            
            Map<String, String> fields = res.getFields();
            Product product = new Product();
            product.setName(fields.get("product-name"));
            product.setDescription(fields.get("product-description"));
            product.setSlug(fields.get("product-slug"));
            
            product.setPrice(Double.parseDouble(fields.get("product-price")));
            product.setStock(Integer.parseInt(fields.get("product-stock")));
            
            String groupId = fields.get("product-group-id");
            if(groupId != null && !groupId.isBlank()) {
                product.setGroupId(UUID.fromString(groupId));
            }
            else {
                throw new FormParseException("product-group-id required");
            }
            if(!files.isEmpty()) {
                product.setImageUrl(
                    storageService.save(files.stream().findFirst().get()));
            }
            
            dataAccessor.addProduct(product);
            this.restResponse.setData("Ok");
        }
        catch(FormParseException ex) {
            this.restResponse.getMeta().setDataType("string");
            this.restResponse.setStatus(RestStatus.status400);
            this.restResponse.setData(ex.getMessage());
        }
    }
    
    private void postGroup(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        try {
            FormParseResult res = formParseService.parse(req);
            Collection<FileItem> files = res.getFiles().values();
            if(files.isEmpty()) {
                throw new FormParseException("Image file required");
            }
            Map<String, String> fields = res.getFields();
            ProductGroup productGroup = new ProductGroup();
            productGroup.setName(fields.get("pg-name"));
            productGroup.setDescription(fields.get("pg-description"));
            productGroup.setSlug(fields.get("pg-slug"));
            String parentId = fields.get("pg-parent-id");
            if(parentId != null && !parentId.isBlank()) {
                productGroup.setParentId(UUID.fromString(parentId));
            }
            productGroup.setImageUrl(
                    storageService.save(files.stream().findFirst().get()));
            dataAccessor.addProductGroup(productGroup);
            this.restResponse.setData("Ok");
        }
        catch(FormParseException ex) {
            this.restResponse.getMeta().setDataType("string");
            this.restResponse.setStatus(RestStatus.status400);
            this.restResponse.setData(ex.getMessage());
        }
    }
}
/*
Д.З. Реалізувати валідацію даних на створення нового товару.
За умов приходу неправильних даних надсилати відповідні 
статуси та повідомлення від бекенду
* додати відповідні елементи на фронтенді
*/