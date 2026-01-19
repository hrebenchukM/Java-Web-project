
package learning.itstep.javaweb222.data.cart;

import java.util.Locale;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import learning.itstep.javaweb222.data.core.DbProvider;
import learning.itstep.javaweb222.data.dto.Cart;
import learning.itstep.javaweb222.data.dto.CartItem;
import learning.itstep.javaweb222.data.dto.Product;

@Singleton
public class CartDao {
    
    
    
    private final DbProvider db;
    private final Logger logger;

    @Inject
    public CartDao(DbProvider db, Logger logger) {
        this.db = db;
        this.logger = logger;
    }
    
     // ================== ADD TO CART ==================

    
    public void addToCart(String productId, String userId) throws Exception {
        UUID productGuid;
        try {
            productGuid = UUID.fromString(productId);
        }
        catch(Exception ignore) {
            throw new Exception("Invalid product id format. UUID expected");
        }
        try {
            UUID.fromString(userId);
        }
        catch(Exception ignore) {
            throw new Exception("Invalid user id format. UUID expected");
        }

        Cart activeCart = this.getActiveCart(userId);
        if(activeCart == null) {
            activeCart = this.createCart(userId);
        }
        CartItem cartItem = activeCart
                .getCartItems()
                .stream()
                .filter((ci) -> ci.getProductId().equals(productGuid))
                .findFirst()
                .orElse(null);
        if(cartItem == null) {
            this.createCartItem(activeCart, productGuid);
        }
        else {
            this.incCartItem(cartItem);
        }
        this.updateDiscount(activeCart);
    }
    
    
    
    
    // ================== CART CRUD ==================
    public Cart getActiveCart(String userId) {
        String sql = "SELECT * FROM carts c "
                + "LEFT JOIN cart_items ci ON ci.ci_cart_id = c.cart_id "
                + "LEFT JOIN products p ON ci.ci_product_id = p.product_id "
                + "WHERE c.cart_user_id = ? AND c.cart_paid_at IS NULL "
                + "AND c.cart_deleted_at IS NULL AND ci.ci_deleted_at IS NULL";
        try( PreparedStatement prep = db.getConnection().prepareStatement(sql)) {
            prep.setString(1, userId);
            ResultSet rs = prep.executeQuery();
            if(rs.next()) {
                return Cart.fromResultSet( rs );
            }
            else return null;
        }
        catch(SQLException ex) {
           logger.log(Level.WARNING, "CartDao::getActiveCart {0}", ex.getMessage());
            return null;
        }    
    }
    
    
    
    
    
       public Cart createCart(String userId) {
        Cart cart = new Cart();
        cart.setId( db.getDbIdentity() );
        cart.setUserId( UUID.fromString(userId) );
        cart.setCartItems( new ArrayList<>() );
        cart.setCreatedAt( new Date() );
        
        String sql = "INSERT INTO carts(cart_id, cart_user_id, cart_price) VALUES(?, ?, -1)";
        try( PreparedStatement prep = db.getConnection().prepareStatement(sql)) {
            prep.setString(1, cart.getId().toString());
            prep.setString(2, userId);
            prep.executeUpdate();
            return cart;
        }
        catch(SQLException ex) {
            logger.log(Level.WARNING, "CartDao::createCart {0} ",
                    ex.getMessage() + " | " + sql);
            return null;
        }
    }
    
    public Cart getHistoryCart(String userId, String cartId) {
        String sql = "SELECT * FROM carts c "
                + "LEFT JOIN cart_items ci ON ci.ci_cart_id = c.cart_id "
                + "LEFT JOIN products p ON ci.ci_product_id = p.product_id "
                + "LEFT JOIN rates r ON p.product_id = r.item_id AND ci.ci_id = r.ci_id "
                + "WHERE c.cart_user_id = ? "
                + " AND c.cart_id = ? "
                + " AND ci.ci_deleted_at IS NULL";
        try( PreparedStatement prep = db.getConnection().prepareStatement(sql)) {
            prep.setString(1, userId);            
            prep.setString(2, cartId);
            ResultSet rs = prep.executeQuery();
            if(rs.next()) {
                return Cart.fromResultSet( rs );
            }
            else return null;
        }
        catch(SQLException ex) {
            logger.log(Level.WARNING, "CartDao::getHistoryCart {0} ",
                    ex.getMessage() + " | " + sql);
            return null;
        }   
    }
    
    public Cart getCartById(String cartId) {
        String sql = "SELECT * FROM carts c "
                + "LEFT JOIN cart_items ci ON ci.ci_cart_id = c.cart_id "
                + "LEFT JOIN products p ON ci.ci_product_id = p.product_id "
                + "WHERE c.cart_id = ? "
                + " AND ci.ci_deleted_at IS NULL";
        try( PreparedStatement prep = db.getConnection().prepareStatement(sql)) {
            prep.setString(1, cartId);
            ResultSet rs = prep.executeQuery();
            if(rs.next()) {
                return Cart.fromResultSet( rs );
            }
            else return null;
        }
        catch(SQLException ex) {
            logger.log(Level.WARNING, "CartDao::getCartById {0} ",
                    ex.getMessage() + " | " + sql);
            return null;
        }   
    }
        public List<Cart> getUserCarts(String userId) {
        String sql = "SELECT * FROM carts c "
                + "LEFT JOIN cart_items ci ON ci.ci_cart_id = c.cart_id "
                + "LEFT JOIN products p ON ci.ci_product_id = p.product_id "
                + "WHERE c.cart_user_id = ? AND ci.ci_deleted_at IS NULL "
                + "ORDER BY c.cart_created_at DESC";
        try( PreparedStatement prep = db.getConnection().prepareStatement(sql)) {
            prep.setString(1, userId);
            ResultSet rs = prep.executeQuery();
            List<Cart> carts = new ArrayList<>();
            boolean hasNext = rs.next();
            while(hasNext) {
                Cart cart = Cart.fromResultSet( rs, false ) ;
                cart.setCartItems(new ArrayList<>());
                do {
                    try {
                        CartItem ci = CartItem.fromResultSet(rs);
                        cart.getCartItems().add(ci);
                    }
                    catch(Exception ignore){ break; }
                    hasNext = rs.next();
                } while(hasNext && 
                        rs.getString("cart_id").equals(cart.getId().toString()));
                carts.add(cart);
            }
            return carts;
        }
        catch(SQLException ex) {
            logger.log(Level.WARNING, "CartDao::getUserCarts {0} ",
                    ex.getMessage() + " | " + sql);
            return null;
        }    
    }
      
      
       
           // ================== DISCOUNT ==================
       
    private void updateDiscount(String cartId) throws Exception {
    
        
        Cart cart = new Cart();
        String sql = "SELECT * FROM cart_items ci "
                + "JOIN products p ON ci.ci_product_id = p.product_id "
                + "WHERE ci.ci_deleted_at IS NULL AND ci.ci_cart_id = ?" ;

        try( PreparedStatement prep = db.getConnection().prepareStatement(sql)) {
            prep.setString(1, cartId);
            logger.log(Level.INFO, "CartDao::updateDiscount {0} ", sql);
            ResultSet rs = prep.executeQuery();
            while(rs.next()) {
                CartItem cartItem = CartItem.fromResultSet(rs);
                Product product = Product.fromResultSet(rs);
                if(cartItem.getDiscountItemId()== null) {
                    // без акцій - звичайний розрахунок ціни
                    cartItem.setPrice( product.getPrice() * cartItem.getQuantity() );
                }
                else {
                    // врахування акції
                    cartItem.setPrice( product.getPrice() * cartItem.getQuantity()
                        * 0.9 /* -10% */
                    );
                }
                cart.getCartItems().add(cartItem);
                cart.setPrice( cart.getPrice() + cartItem.getPrice() );
            }
            if(cart.getDiscountId() != null) {   // акції на покупку (не на товар)
                cart.setPrice( cart.getPrice() * 0.9 ) ;
            }
        }
        catch(SQLException ex) {
            logger.log(Level.WARNING, "CartDao::updateDiscount {0} ",
                    ex.getMessage() + " | " + sql);
            throw ex;
        }
        
        // етап ІІ. переносимо розраховані дані до БД
        sql = "UPDATE cart_items SET ci_price = ? WHERE ci_id = ?";
        try( PreparedStatement prep = db.getConnection().prepareStatement(sql)) {
            for(CartItem ci : cart.getCartItems()) {
                prep.setDouble(1, ci.getPrice());                
                prep.setString(2, ci.getId().toString());
                prep.executeUpdate();
            } 
        }
        catch(SQLException ex) {
            logger.log(Level.WARNING, "CartDao::updateDiscount {0} ",
                    ex.getMessage() + " | " + sql);
            throw ex;
        }
        sql = "UPDATE carts SET cart_price = ? WHERE cart_id = ?";
        try( PreparedStatement prep = db.getConnection().prepareStatement(sql)) {            
            prep.setDouble(1, cart.getPrice());                
            prep.setString(2, cartId);
            prep.executeUpdate();
        }
        catch(SQLException ex) {
            logger.log(Level.WARNING, "CartDao::updateDiscount {0} ",
                    ex.getMessage() + " | " + sql);
            throw ex;
        }
        
    }
    
    private void updateDiscount(Cart cart) throws Exception {
        this.updateDiscount(cart.getId().toString());
    }
    
    
    // ================== CART ITEMS ==================
    
    private void incCartItem(CartItem cartItem) throws SQLException {
        incCartItem(cartItem, 1);
    }
    
    private void incCartItem(CartItem cartItem, int inc) throws SQLException {
        String sql = String.format(
            "UPDATE cart_items SET ci_quantity = ci_quantity + %d WHERE ci_id = ? ",
            inc);
        try( PreparedStatement prep = db.getConnection().prepareStatement(sql)) {
            prep.setString(1, cartItem.getId().toString());
            logger.log(Level.INFO, "CartDao::incCartItem {0} ", sql);
            prep.executeUpdate();
        }
        catch(SQLException ex) {
            logger.log(Level.WARNING, "CartDao::incCartItem {0} ",
                    ex.getMessage() + " | " + sql);
            throw ex;
        }
    }
    
    
    
    private void createCartItem(Cart activeCart, CartItem baseCartItem) throws SQLException {
        String sql = "INSERT INTO cart_items(ci_id, ci_cart_id, ci_product_id, ci_price, ci_quantity) "
                + "VALUES(UUID(), ?, ?, -1, ?)";
        try( PreparedStatement prep = db.getConnection().prepareStatement(sql)) {
            prep.setString(1, activeCart.getId().toString());
            prep.setString(2, baseCartItem.getProductId().toString());
            prep.setInt(3, baseCartItem.getQuantity());
            prep.executeUpdate();
        }
        catch(SQLException ex) {
            logger.log(Level.WARNING, "CartDao::createCartItem {0} ",
                    ex.getMessage() + " | " + sql);
            throw ex;
        }
    }
    private void createCartItem(Cart activeCart, UUID productGuid) throws SQLException {
        String sql = "INSERT INTO cart_items(ci_id, ci_cart_id, ci_product_id, ci_price) "
                + "VALUES(UUID(), ?, ?, -1)";
        try( PreparedStatement prep = db.getConnection().prepareStatement(sql)) {
            prep.setString(1, activeCart.getId().toString());
            prep.setString(2, productGuid.toString());
            prep.executeUpdate();
        }
        catch(SQLException ex) {
            logger.log(Level.WARNING, "CartDao::createCartItem {0} ",
                    ex.getMessage() + " | " + sql);
            throw ex;
        }
    }
    
  
      // ================== CART ACTIONS ==================

     public void modifyCartItem(String cartItemId, int increment) throws Exception {
        String sql = "SELECT * FROM cart_items ci WHERE ci_id = ?";
        CartItem cartItem = null;
        try( PreparedStatement prep = db.getConnection().prepareStatement(sql)) {
            prep.setString(1, cartItemId);
            ResultSet rs = prep.executeQuery();
            if(rs.next()) {
                cartItem = CartItem.fromResultSet( rs );
            }
        }
        catch(SQLException ex) {
            logger.log(Level.WARNING, "CartDao::modifyCartItem {0} ",
                    ex.getMessage() + " | " + sql);
            throw ex;
        }
        if(cartItem == null) {
            throw new Exception("No cart item with id given");
        }
        int newQuantity = cartItem.getQuantity() + increment;
        if(newQuantity < 0) {
            throw new Exception("Resulted quantity could not be negative");
        }
      
        if(newQuantity == 0) {
            // delete
        }
        else {
            sql = String.format(
                    Locale.ROOT,
                    "UPDATE cart_items SET ci_quantity = %d WHERE ci_id = '%s'",
                    newQuantity,
                    cartItemId);
            
            try( Statement statement = db.getConnection().createStatement() ) {
                statement.executeUpdate(sql);
            }
            catch(SQLException ex) {
                logger.log(Level.WARNING, "CartDao::modifyCartItem {0} ",
                        ex.getMessage() + " | " + sql);
                throw ex;
            }
            this.updateDiscount( cartItem.getCartId().toString() );
        }
    }
     
     
     
      public void repeatCart(String userId, String cartId) throws Exception {
        // Перевіряємо, що userId, cartId відповідають реальним сутностям
        Cart cart = getCartById(cartId);
        if(cart == null) {
            throw new Exception("Cart not found for id: " + cartId);
        }
       
        // Знаходимо активний кошик для користувача
        Cart activeCart = getActiveCart(userId);
        // Якщо немає, то створюємо новий
        if(activeCart == null) {
            activeCart = createCart(userId);
        }
        
        // проходимо по всіх елементах базового кошику (який повторюємо)
        // і переносимо їх до активного кошику, попередньо перевіряючи, чи
        // є вже в активному кошику відповідні елементи (якщо є, то додаємо,
        // якщо ні - то створюємо)
        for(CartItem baseCartItem : cart.getCartItems()) {
            CartItem activeCartItem = activeCart   // шукаємо в активному кошику
                .getCartItems()                    // елемент з тим самим
                .stream()                          // ProductId
                .filter((ci) -> ci.getProductId().equals(
                        baseCartItem.getProductId()))
                .findFirst()
                .orElse(null);
            
            if(activeCartItem == null) {
                this.createCartItem(activeCart, baseCartItem);
            }
            else {
                this.incCartItem(    // ? контролювати макс. кількість по залишкам товару
                        activeCartItem, 
                        baseCartItem.getQuantity());        
            }
        }
        updateDiscount(activeCart);
    }
    
      
      public void checkoutActiveCart(String userId) throws Exception {
        Cart activeCart = this.getActiveCart(userId);
        if(activeCart == null) {
            throw new Exception("User has no active cart");
        }
        String sql = "UPDATE carts SET cart_paid_at = CURRENT_TIMESTAMP WHERE cart_id = ?";
        try( PreparedStatement prep = db.getConnection().prepareStatement(sql) ) {
            prep.setString( 1, activeCart.getId().toString() );
            prep.executeUpdate();
        }
        catch(SQLException ex) {
            logger.log(Level.WARNING, "DataAccessor::checkoutActiveCart {0} ",
                    ex.getMessage() + " | " + sql);
            throw ex;
        }
    }
      
        public void deleteCartItem(String cartItemId) throws Exception {
        String sql = "SELECT * FROM cart_items ci WHERE ci_id = ?";
        CartItem cartItem = null;
        try( PreparedStatement prep = db.getConnection().prepareStatement(sql)) {
            prep.setString(1, cartItemId);
            ResultSet rs = prep.executeQuery();
            if(rs.next()) {
                cartItem = CartItem.fromResultSet( rs );
            }
        }
        catch(SQLException ex) {
            logger.log(Level.WARNING, "CartDao::deleteCartItem {0} ",
                    ex.getMessage() + " | " + sql);
            throw ex;
        }
        if(cartItem == null) {
            throw new Exception("No cart item with id given");
        }
        if(cartItem.getDeletedAt() != null) {
            throw new Exception("Cart item has been deleted previously");
        }
        
        sql = "UPDATE cart_items SET ci_deleted_at = CURRENT_TIMESTAMP WHERE ci_id = ?";
        try( PreparedStatement prep = db.getConnection().prepareStatement(sql) ) {
            prep.setString(1, cartItemId);
            prep.executeUpdate();
        }
        catch(SQLException ex) {
            logger.log(Level.WARNING, "CartDao::deleteCartItem {0} ",
                    ex.getMessage() + " | " + sql);
            throw ex;
        }
        this.updateDiscount( cartItem.getCartId().toString() );
    }
    
   
    
    
    
    
 
    
    
    
   
  
    
}
