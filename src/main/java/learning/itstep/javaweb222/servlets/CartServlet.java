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
import learning.itstep.javaweb222.data.dto.Cart;
import learning.itstep.javaweb222.data.dto.CartItem;
import learning.itstep.javaweb222.data.dto.Product;
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
    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
       String cartItemId = req.getParameter("cart-item-id");
       String inc = req.getParameter("inc");
       
        if(cartItemId == null||cartItemId.isBlank()){
            this.restResponse.setStatus(RestStatus.status400);
            this.restResponse.setData("Missing query parametr:cart-item-id");  
            return;
        } 
        if(inc == null||inc.isBlank()){
            this.restResponse.setStatus(RestStatus.status400);
            this.restResponse.setData("Missing query parametr:inc"); 
            return;
        } 
        int d;
        try{d=Integer.parseInt(inc);}
        catch(NumberFormatException ignore)
        {
            this.restResponse.setStatus(RestStatus.status400);
            this.restResponse.setData("Query parametr inc must be decimal number"); 
            return;
        }
        if(d==0){
            this.restResponse.setStatus(RestStatus.status400);
            this.restResponse.setData("Query parametr inc is zero"); 
            return;
        }
        try{
          dataAccessor.modifyCartItem(cartItemId, d);
          this.restResponse.getMeta().setDataType("null"); 
        }
       catch (Exception ex) {
            this.restResponse.getMeta().setDataType("string");
            this.restResponse.setStatus(RestStatus.status400);
            this.restResponse.setData(ex.getMessage());
        }
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
                 Map.entry("add-to-cart", "POST /cart?product-id={id}"),
                    Map.entry("modify-cart", "PATCH /cart?cart-item-id={id}&inc={n}"),
                    Map.entry("repeat-cart", "LINK /cart/{old-cart-id}"),
                    Map.entry("active-cart", "GET /cart"),
                    Map.entry("history-cart", "GET /cart/{old-cart-id}"),
                    Map.entry("checkout-cart", "PUT /cart"),
                    Map.entry("remove-cart-item", "DELETE /cart?cart-item-id={id}")
                ) )
        );
        
         jwtToken = (JwtToken) req.getAttribute("JWT");
            if (jwtToken == null) {
             this.restResponse.setStatus(RestStatus.status401);
             resp.setContentType("application/json");
             resp.getWriter().print(gson.toJson(restResponse));
             return;
           }
        else {
            if(req.getMethod().equals("LINK")) {
                this.doLink(req, resp);
            }
            else super.service(req, resp);
        }


        
        resp.setContentType("application/json");
        resp.getWriter().print(
                gson.toJson(restResponse)
        );
    }

    private void doLink(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String cartId = req.getPathInfo();
        if(cartId == null || cartId.isBlank() || "/".equals(cartId)) {
             this.restResponse.setStatus(RestStatus.status400);
             this.restResponse.setData("Missing path parameter: cart-id");
             return;
        }

        // прибираємо перший "/"
        cartId = cartId.substring(1);
        String userId = jwtToken.getPayload().getSub();
        this.restResponse.getMeta().getParams().put("cartId", cartId);
        this.restResponse.getMeta().getParams().put("userId", userId);
    
        try {
            dataAccessor.repeatCart(userId, cartId);
            this.restResponse.getMeta().setDataType("null");
        }
        catch(Exception ex) {
            this.restResponse.getMeta().setDataType("string");
            this.restResponse.setStatus(RestStatus.status400);
            this.restResponse.setData(ex.getMessage());
        }

    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String cartId = req.getPathInfo();
        if(cartId == null || cartId.isBlank() || "/".equals(cartId)) {
            this.getActiveCart(req);
        }
        else {
            this.getHistoryCart(req, cartId.substring(1));
        }

    }
    private void getHistoryCart(HttpServletRequest req, String cartId) {
       this.restResponse.getMeta().getParams().put("cartId", cartId);
       this.restResponse.getMeta().getParams().put("userId", jwtToken.getPayload().getSub());
    
        try {
            Cart activeCart = dataAccessor.getHistoryCart(
                    jwtToken.getPayload().getSub(),
                    cartId);
            if(activeCart != null) {
                for(CartItem ci : activeCart.getCartItems()) {
                    Product p = ci.getProduct();
                    if(p != null) {
                        p.correctImageUrl(req);
                    }
                }
                this.restResponse.getMeta().setDataType("object");
            }
            else {
                this.restResponse.getMeta().setDataType("null");
            }
            this.restResponse.setData(activeCart);
        }
        catch(Exception ex) {
            this.restResponse.getMeta().setDataType("string");
            this.restResponse.setStatus(RestStatus.status400);
            this.restResponse.setData(ex.getMessage());
        }
    }

    private void getActiveCart(HttpServletRequest req) {
         try {
            Cart activeCart = dataAccessor.getActiveCart(
                    jwtToken.getPayload().getSub());
            if(activeCart != null) {
                for(CartItem ci : activeCart.getCartItems()) {
                    Product p = ci.getProduct();
                    if(p != null) {
                        p.correctImageUrl(req);
                    }
                }
                this.restResponse.getMeta().setDataType("object");
            }
            else {
                this.restResponse.getMeta().setDataType("null");
            }
            this.restResponse.setData(activeCart);
        }
        catch(Exception ex) {
            this.restResponse.getMeta().setDataType("string");
            this.restResponse.setStatus(RestStatus.status400);
            this.restResponse.setData(ex.getMessage());
        }
    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String productId = req.getParameter("product-id");
        this.restResponse.getMeta().setDataType("string");
        if(productId == null || productId.isBlank()) {
            this.restResponse.setStatus(RestStatus.status400);
            this.restResponse.setData("Missing query parameter: product-id");
            return;
        }
        try {
            dataAccessor.addToCart(productId, jwtToken.getPayload().getSub());          
            this.restResponse.setData("Add OK");
        }
        catch(Exception ex) {
            this.restResponse.setStatus(RestStatus.status400);
            this.restResponse.setData(ex.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String cartItemId = req.getParameter("cart-item-id");
        if(cartItemId == null || cartItemId.isBlank()) {
            this.restResponse.setStatus(RestStatus.status400);
            this.restResponse.setData("Missing query parameter: cart-item-id");
            return;
        }
        
        try {
            dataAccessor.deleteCartItem(cartItemId);
            this.restResponse.getMeta().setDataType("null");
        }
        catch(Exception ex) {
            this.restResponse.getMeta().setDataType("string");
            this.restResponse.setStatus(RestStatus.status400);
            this.restResponse.setData(ex.getMessage());
        }        
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Checkout cart - імітація здійснення покупки
        try {
            dataAccessor.checkoutActiveCart(jwtToken.getPayload().getSub());          
            this.restResponse.getMeta().setDataType("null");
        }
        catch(Exception ex) {
            this.restResponse.setStatus(RestStatus.status400);
            this.restResponse.setData(ex.getMessage());
        }
    }
    
    
    
    
}
