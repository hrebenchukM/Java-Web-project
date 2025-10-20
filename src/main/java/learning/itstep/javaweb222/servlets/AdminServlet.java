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
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JwtToken jwtToken = (JwtToken) req.getAttribute("JWT");
        if(jwtToken == null) {
            resp.setStatus(401);
            resp.setContentType("text/plain");
            resp.getWriter().print( req.getAttribute("JwtStatus") );
            return;
        }        
        String slug = req.getPathInfo() ;
        switch(slug) {
            case "/groups": this.getGroups(req, resp); break;
            default: 
                resp.setStatus(404);
                resp.setContentType("text/plain");
                resp.getWriter().print( "Slug " + slug + " not found" );
        }
    }
    
    

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JwtToken jwtToken = (JwtToken) req.getAttribute("JWT");
        if(jwtToken == null) {
            resp.setStatus(401);
            resp.setContentType("text/plain");
            resp.getWriter().print( req.getAttribute("JwtStatus") );
            return;
        }
        String slug = req.getPathInfo() ;
        switch(slug) {
            case "/group": this.postGroup(req, resp); break;
            default: 
                resp.setStatus(404);
                resp.setContentType("text/plain");
                resp.getWriter().print( "Slug " + slug + " not found" );
                case "/product": this.postProduct(req, resp); break;
            
        }
    }
    
    private void getGroups(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.getWriter().print( 
                gson.toJson(dataAccessor.adminGetProductGroups())
        );
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
            
            
            
            if(fields.get("pg-name") == null || fields.get("pg-name").isBlank()) {
            resp.setStatus(400);
            resp.getWriter().print(gson.toJson("Field 'Name' is required"));
            return;
            }
            if(fields.get("pg-slug") == null || fields.get("pg-slug").isBlank()) {
            resp.setStatus(400);
            resp.getWriter().print(gson.toJson("Field 'Slug' is required"));
            return;
            }
            
            if(fields.get("pg-description") == null || fields.get("pg-description").isBlank()) {
            resp.setStatus(400);
            resp.getWriter().print(gson.toJson("Field 'Description' is required"));
            return;
            }
            
            boolean slugExists = dataAccessor.adminGetProductGroups()
                                .stream()
                                .anyMatch(g -> g.getSlug().equals(fields.get("pg-slug")));
            if(slugExists) {
            resp.setStatus(400);
            resp.getWriter().print(gson.toJson("Group with such slug exists"));
            return;
            }
            
            
            
            
            
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
            resp.getWriter().print(
                    gson.toJson("Ok")
            );
        }
        catch(FormParseException ex) {
            resp.setStatus(400);
            resp.getWriter().print(
                    gson.toJson(ex.getMessage()));
        }
        catch(Exception ex) {
        resp.setStatus(500);
        resp.getWriter().print(gson.toJson("Помилка сервера"));
        }
    }
    
    
    
    
     private void postProduct(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        try {
            FormParseResult res = formParseService.parse(req);
            Collection<FileItem> files = res.getFiles().values();
            Map<String, String> fields = res.getFields();
            
            
              
            if(fields.get("product-name") == null || fields.get("product-name").isBlank()) {
            resp.setStatus(400);
            resp.getWriter().print(gson.toJson("Field 'Name' is required"));
            return;
            }
            if(fields.get("product-price") == null || fields.get("product-price").isBlank()) {
            resp.setStatus(400);
            resp.getWriter().print(gson.toJson("Field 'Price' is required"));
            return;
            }
        
            if(fields.get("product-stock") == null || fields.get("product-stock").isBlank()) {
            resp.setStatus(400);
            resp.getWriter().print(gson.toJson("Field 'Stock' is required"));
            return;
            } 
            
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
            else{
                throw new FormParseException("product-group-id reqiered");
            }
            
            if(!files.isEmpty()) {
            product.setImageUrl(
                    storageService.save(files.stream().findFirst().get()));
          
            }
            dataAccessor.addProduct(product);
            resp.getWriter().print(
                    gson.toJson("Ok")
            );
        }
        catch(FormParseException ex) {
            resp.setStatus(400);
            resp.getWriter().print(
                    gson.toJson(ex.getMessage()));
        }
        catch(Exception ex) {
        resp.setStatus(500);
        resp.getWriter().print(gson.toJson("Помилка сервера"));
        }
    }
    
    
    
    
    
}
/*
Д.З. Реалізувати валідацію даних на створення нової групи.
За умов приходу неправильних даних надсилати відповідні 
статуси та повідомлення від бекенду
* додати відповідні елементи на фронтенді
*/