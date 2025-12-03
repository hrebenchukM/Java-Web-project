package learning.itstep.javaweb222.data;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import learning.itstep.javaweb222.data.dto.AccessToken;
import learning.itstep.javaweb222.data.dto.Cart;
import learning.itstep.javaweb222.data.dto.CartItem;
import learning.itstep.javaweb222.data.dto.Product;
import learning.itstep.javaweb222.data.dto.ProductGroup;
import learning.itstep.javaweb222.data.dto.User;
import learning.itstep.javaweb222.data.dto.UserAccess;
import learning.itstep.javaweb222.models.rate.RateFormModel;
import learning.itstep.javaweb222.services.config.ConfigService;
import learning.itstep.javaweb222.services.kdf.KdfService;

@Singleton
public class DataAccessor {
    private final ConfigService configService;
    private final Logger logger;
    private final KdfService kdfService;
    private Connection connection;
    private Driver mysqlDriver;

    @Inject
    public DataAccessor(ConfigService configService, Logger logger, KdfService kdfService) {
        this.configService = configService;
        this.logger = logger;
        this.kdfService = kdfService;
    }
    
    public void addRate(RateFormModel rateFormModel) throws Exception {
        // Перевіряємо дані на валідні UUID
        UUID.fromString( rateFormModel.getCartItemId() );
        UUID.fromString( rateFormModel.getProductId()  );
        
        //  Перевіряємо, чи існують ІД сутностей, а також 
        //  - що у переданому кошику є продукт, що коментується
        //  - немає попереднього коментаря на нього.
        String sql = "SELECT * FROM cart_items ci "
                + "JOIN products p ON ci.ci_product_id = p.product_id "
                + "LEFT JOIN rates r ON ci.ci_id = r.ci_id "
                + "WHERE ci.ci_id = ? AND p.product_id = ?";
        try( PreparedStatement prep = getConnection().prepareStatement(sql) ) {
            prep.setString( 1, rateFormModel.getCartItemId() );
            prep.setString( 2, rateFormModel.getProductId()  );
            ResultSet rs = prep.executeQuery();
            if( ! rs.next() ) {
                throw new Exception("CartItemId or ProductId not found");
            }
            if( rs.getString("rate_id") != null ) {
                throw new Exception("Rate exists already");
            }            
        }
        catch(SQLException ex) {
            logger.log(Level.WARNING, "DataAccessor::addRate {0} ",
                    ex.getMessage() + " | " + sql);
            throw ex;
        }
        
        sql = "INSERT INTO rates(rate_id, user_id, ci_id, item_id, "
                + "rate_text, rate_stars ) VALUES( UUID(), ?, ?, ?, ?, ? )";
        try( PreparedStatement prep = getConnection().prepareStatement(sql) ) {
            prep.setString( 1, rateFormModel.getUserId() );
            prep.setString( 2, rateFormModel.getCartItemId() );
            prep.setString( 3, rateFormModel.getProductId() );
            prep.setString( 4, rateFormModel.getComment() );
            prep.setInt(    5, rateFormModel.getRate() );
            prep.executeUpdate();
        }
        catch(SQLException ex) {
            logger.log(Level.WARNING, "DataAccessor::addRate {0} ",
                    ex.getMessage() + " | " + sql);
            throw ex;
        }
    }
    
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
        /*
        Варіанти: 
        1. У користувача немає відкритого кошику (це перше додавання
            товару) -- відкриваємо (створюємо) новий кошик, створюємо новий запис
            (cart_item) і додаємо його до кошику.
        2. У користувача є відкритий кошик, але немає такого товару --
            створюємо новий запис (cart_item) і додаємо його до кошику.
        3. Є відкритий кошик і в ньому є такий товар --
            збільшуємо кількість на 1 (з комерційних міркувань, з наукових - 
            це виняткова ситуація)
        */
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
    
    private void updateDiscount(String cartId) throws Exception {
        /*
        Проходимо по всіх позиціях у кошику, додаємо відомості про базові
        ціни товарів, а також акції, що діють на них.
        Аналізуємо, чи є товар в акції, якщо ні, то додаємо повну ціну,
        якщо є - враховуємо акцію (або відсоток, або інші алгоритми)
        */
        // етап І. розраховуємо ціни зі знижками, зберігаємо їх в об'єкті (cart)
        Cart cart = new Cart();
        String sql = "SELECT * FROM cart_items ci "
                + "JOIN products p ON ci.ci_product_id = p.product_id "
                + "WHERE ci.ci_deleted_at IS NULL AND ci.ci_cart_id = ?" ;

        try( PreparedStatement prep = getConnection().prepareStatement(sql)) {
            prep.setString(1, cartId);
            logger.log(Level.INFO, "DataAccessor::updateDiscount {0} ", sql);
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
            logger.log(Level.WARNING, "DataAccessor::updateDiscount {0} ",
                    ex.getMessage() + " | " + sql);
            throw ex;
        }
        
        // етап ІІ. переносимо розраховані дані до БД
        sql = "UPDATE cart_items SET ci_price = ? WHERE ci_id = ?";
        try( PreparedStatement prep = getConnection().prepareStatement(sql)) {
            for(CartItem ci : cart.getCartItems()) {
                prep.setDouble(1, ci.getPrice());                
                prep.setString(2, ci.getId().toString());
                prep.executeUpdate();
            } 
        }
        catch(SQLException ex) {
            logger.log(Level.WARNING, "DataAccessor::updateDiscount {0} ",
                    ex.getMessage() + " | " + sql);
            throw ex;
        }
        sql = "UPDATE carts SET cart_price = ? WHERE cart_id = ?";
        try( PreparedStatement prep = getConnection().prepareStatement(sql)) {            
            prep.setDouble(1, cart.getPrice());                
            prep.setString(2, cartId);
            prep.executeUpdate();
        }
        catch(SQLException ex) {
            logger.log(Level.WARNING, "DataAccessor::updateDiscount {0} ",
                    ex.getMessage() + " | " + sql);
            throw ex;
        }
        
    }
    
    private void updateDiscount(Cart cart) throws Exception {
        this.updateDiscount(cart.getId().toString());
    }
    
    private void incCartItem(CartItem cartItem) throws SQLException {
        incCartItem(cartItem, 1);
    }
    
    private void incCartItem(CartItem cartItem, int inc) throws SQLException {
        String sql = String.format(
            "UPDATE cart_items SET ci_quantity = ci_quantity + %d WHERE ci_id = ? ",
            inc);
        try( PreparedStatement prep = getConnection().prepareStatement(sql)) {
            prep.setString(1, cartItem.getId().toString());
            logger.log(Level.INFO, "DataAccessor::incCartItem {0} ", sql);
            prep.executeUpdate();
        }
        catch(SQLException ex) {
            logger.log(Level.WARNING, "DataAccessor::incCartItem {0} ",
                    ex.getMessage() + " | " + sql);
            throw ex;
        }
    }
    
    private void createCartItem(Cart activeCart, UUID productGuid) throws SQLException {
        String sql = "INSERT INTO cart_items(ci_id, ci_cart_id, ci_product_id, ci_price) "
                + "VALUES(UUID(), ?, ?, -1)";
        try( PreparedStatement prep = getConnection().prepareStatement(sql)) {
            prep.setString(1, activeCart.getId().toString());
            prep.setString(2, productGuid.toString());
            prep.executeUpdate();
        }
        catch(SQLException ex) {
            logger.log(Level.WARNING, "DataAccessor::createCartItem {0} ",
                    ex.getMessage() + " | " + sql);
            throw ex;
        }
    }
    
    private void createCartItem(Cart activeCart, CartItem baseCartItem) throws SQLException {
        String sql = "INSERT INTO cart_items(ci_id, ci_cart_id, ci_product_id, ci_price, ci_quantity) "
                + "VALUES(UUID(), ?, ?, -1, ?)";
        try( PreparedStatement prep = getConnection().prepareStatement(sql)) {
            prep.setString(1, activeCart.getId().toString());
            prep.setString(2, baseCartItem.getProductId().toString());
            prep.setInt(3, baseCartItem.getQuantity());
            prep.executeUpdate();
        }
        catch(SQLException ex) {
            logger.log(Level.WARNING, "DataAccessor::createCartItem {0} ",
                    ex.getMessage() + " | " + sql);
            throw ex;
        }
    }
    
    public Cart createCart(String userId) {
        Cart cart = new Cart();
        cart.setId( this.getDbIdentity() );
        cart.setUserId( UUID.fromString(userId) );
        cart.setCartItems( new ArrayList<>() );
        cart.setCreatedAt( new Date() );
        
        String sql = "INSERT INTO carts(cart_id, cart_user_id, cart_price) VALUES(?, ?, -1)";
        try( PreparedStatement prep = getConnection().prepareStatement(sql)) {
            prep.setString(1, cart.getId().toString());
            prep.setString(2, userId);
            prep.executeUpdate();
            return cart;
        }
        catch(SQLException ex) {
            logger.log(Level.WARNING, "DataAccessor::createCart {0} ",
                    ex.getMessage() + " | " + sql);
            return null;
        }
    }
    
    public Cart getActiveCart(String userId) {
        String sql = "SELECT * FROM carts c "
                + "LEFT JOIN cart_items ci ON ci.ci_cart_id = c.cart_id "
                + "LEFT JOIN products p ON ci.ci_product_id = p.product_id "
                + "WHERE c.cart_user_id = ? AND c.cart_paid_at IS NULL "
                + "AND c.cart_deleted_at IS NULL AND ci.ci_deleted_at IS NULL";
        try( PreparedStatement prep = getConnection().prepareStatement(sql)) {
            prep.setString(1, userId);
            ResultSet rs = prep.executeQuery();
            if(rs.next()) {
                return Cart.fromResultSet( rs );
            }
            else return null;
        }
        catch(SQLException ex) {
            logger.log(Level.WARNING, "DataAccessor::getActiveCart {0} ",
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
        try( PreparedStatement prep = getConnection().prepareStatement(sql)) {
            prep.setString(1, userId);            
            prep.setString(2, cartId);
            ResultSet rs = prep.executeQuery();
            if(rs.next()) {
                return Cart.fromResultSet( rs );
            }
            else return null;
        }
        catch(SQLException ex) {
            logger.log(Level.WARNING, "DataAccessor::getHistoryCart {0} ",
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
        try( PreparedStatement prep = getConnection().prepareStatement(sql)) {
            prep.setString(1, cartId);
            ResultSet rs = prep.executeQuery();
            if(rs.next()) {
                return Cart.fromResultSet( rs );
            }
            else return null;
        }
        catch(SQLException ex) {
            logger.log(Level.WARNING, "DataAccessor::getCartById {0} ",
                    ex.getMessage() + " | " + sql);
            return null;
        }   
    }
    
    public void repeatCart(String userId, String cartId) throws Exception {
        // Перевіряємо, що userId, cartId відповідають реальним сутностям
        Cart cart = getCartById(cartId);
        if(cart == null) {
            throw new Exception("Cart not found for id: " + cartId);
        }
        User user = getUserById(userId);
        if(user == null) {
            throw new Exception("User not found for id: " + userId);
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
    
    public List<Cart> getUserCarts(String userId) {
        String sql = "SELECT * FROM carts c "
                + "LEFT JOIN cart_items ci ON ci.ci_cart_id = c.cart_id "
                + "LEFT JOIN products p ON ci.ci_product_id = p.product_id "
                + "WHERE c.cart_user_id = ? AND ci.ci_deleted_at IS NULL "
                + "ORDER BY c.cart_created_at DESC";
        try( PreparedStatement prep = getConnection().prepareStatement(sql)) {
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
            logger.log(Level.WARNING, "DataAccessor::getUserCarts {0} ",
                    ex.getMessage() + " | " + sql);
            return null;
        }    
    }
    
    public void checkoutActiveCart(String userId) throws Exception {
        Cart activeCart = this.getActiveCart(userId);
        if(activeCart == null) {
            throw new Exception("User has no active cart");
        }
        String sql = "UPDATE carts SET cart_paid_at = CURRENT_TIMESTAMP WHERE cart_id = ?";
        try( PreparedStatement prep = getConnection().prepareStatement(sql) ) {
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
        try( PreparedStatement prep = getConnection().prepareStatement(sql)) {
            prep.setString(1, cartItemId);
            ResultSet rs = prep.executeQuery();
            if(rs.next()) {
                cartItem = CartItem.fromResultSet( rs );
            }
        }
        catch(SQLException ex) {
            logger.log(Level.WARNING, "DataAccessor::deleteCartItem {0} ",
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
        try( PreparedStatement prep = getConnection().prepareStatement(sql) ) {
            prep.setString(1, cartItemId);
            prep.executeUpdate();
        }
        catch(SQLException ex) {
            logger.log(Level.WARNING, "DataAccessor::deleteCartItem {0} ",
                    ex.getMessage() + " | " + sql);
            throw ex;
        }
        this.updateDiscount( cartItem.getCartId().toString() );
    }
    
    public void modifyCartItem(String cartItemId, int increment) throws Exception {
        String sql = "SELECT * FROM cart_items ci WHERE ci_id = ?";
        CartItem cartItem = null;
        try( PreparedStatement prep = getConnection().prepareStatement(sql)) {
            prep.setString(1, cartItemId);
            ResultSet rs = prep.executeQuery();
            if(rs.next()) {
                cartItem = CartItem.fromResultSet( rs );
            }
        }
        catch(SQLException ex) {
            logger.log(Level.WARNING, "DataAccessor::modifyCartItem {0} ",
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
        /*
        Д.З. До методу modifyCartItem додати перевірку того, що нова кількість
        замовлення не перевищує складські залишки товару (stock)
        * на фронтенді блокувати кнопку "+" при досягненні граничної кількості
        */
        if(newQuantity == 0) {
            // delete
        }
        else {
            sql = String.format(
                    Locale.ROOT,
                    "UPDATE cart_items SET ci_quantity = %d WHERE ci_id = '%s'",
                    newQuantity,
                    cartItemId);
            
            try( Statement statement = getConnection().createStatement() ) {
                statement.executeUpdate(sql);
            }
            catch(SQLException ex) {
                logger.log(Level.WARNING, "DataAccessor::modifyCartItem {0} ",
                        ex.getMessage() + " | " + sql);
                throw ex;
            }
            this.updateDiscount( cartItem.getCartId().toString() );
        }
    }
    
    public Product getProductBySlugOrId(String slug) {
        String sql = "SELECT * FROM products p "
        + "JOIN product_groups pg ON p.product_group_id = pg.pg_id "
        + "LEFT JOIN rates r ON p.product_id = r.item_id "
        + "LEFT JOIN users u ON r.user_id = u.user_id "
        + "WHERE r.rate_deleted_at IS NULL AND (p.product_slug = ? OR p.product_id = ?)";

        try( PreparedStatement prep = getConnection().prepareStatement(sql)) {
            prep.setString(1, slug);
            prep.setString(2, slug);
            ResultSet rs = prep.executeQuery();
            if(rs.next()) {
                return Product.fromResultSet( rs,true );
            }
            else return null;
        }
        catch(Exception ex) {
            logger.log(Level.WARNING, "DataAccessor::getProductBySlugOrId {0} ",
                    ex.getMessage() + " | " + sql);
            return null;
        }    
    }
    
    public List<ProductGroup> getProductGroups() {
        String sql = "SELECT * FROM product_groups WHERE pg_deleted_at IS NULL";
        List<ProductGroup> ret = new ArrayList<>();
        try(Statement statement = getConnection().createStatement();                
            ResultSet rs = statement.executeQuery(sql)
        ) {
            while(rs.next()) {
                ret.add(ProductGroup.fromResultSet(rs));
            }
        }
        catch(SQLException ex) {
            logger.log(Level.WARNING, "DataAccessor::getProductGroups {0}" ,
                    ex.getMessage() + " | " + sql);
        }
        return ret;
    }
    
    public ProductGroup getProductGroupBySlug(String slug) {
//        String sql = "SELECT * FROM product_groups pg "
//                + "LEFT JOIN products p ON p.product_group_id = pg.pg_id "
//                + "WHERE pg.pg_slug = ?";
        String sql = "SELECT " +
                       " (SELECT COUNT(*) FROM rates r WHERE r.item_id = p.product_id AND  r.rate_deleted_at IS NULL) AS rates_count, " +
                       " (SELECT AVG(r.rate_stars) FROM rates r WHERE r.item_id = p.product_id AND r.rate_stars > 0 AND  r.rate_deleted_at IS NULL) AS rate_avg, " +
                       " pg.*, p.* " +
                       "FROM product_groups pg " +
                       "LEFT JOIN products p ON p.product_group_id = pg.pg_id " +
                       "WHERE pg.pg_slug = ?";
        try( PreparedStatement prep = getConnection().prepareStatement(sql)) {
            prep.setString(1, slug);
            ResultSet rs = prep.executeQuery();
            if(rs.next()) {
                return ProductGroup.fromResultSet( rs );
            }
            else return null;
        }
        catch(SQLException ex) {
            logger.log(Level.WARNING, "DataAccessor::getProductGroupBySlug {0}", 
                    ex.getMessage() + " | " + sql);
            return null;
        }    
    }
       
    public AccessToken getTokenByUserAccess(UserAccess ua) {
        AccessToken at = new AccessToken();
        at.setTokenId(UUID.randomUUID());
        at.setIssuedAt(new Date());
        at.setExpiredAt( new Date( at.getIssuedAt().getTime() + 1000 * 60 * 10 ) );
        at.setUserAccessId(ua.getId());
        at.setUserAccess(ua);
        /*
        Д.З. DataAccessor::getTokenByUserAccess
        модифікувати алгоритм формування токена доступу:
        якщо у користувача є активний токен (ще не прострочений), то оновити
        у БД його термін дії (+10 хв від поточного часу), новий токен не створювати.
        Якщо активного токена немає або він вже вичерпав термін, то створювати
        новий.
        (технічними словами, запит SQL буде або UPDATE або INSERT)
        */
        String sql = """
                INSERT INTO tokens(token_id,user_access_id,issued_at,expired_at)
                VALUES(?,?,?,?)
        """;
        try(PreparedStatement prep = this.getConnection().prepareStatement(sql)) {
            prep.setString(1, at.getTokenId().toString());
            prep.setString(2, at.getUserAccessId().toString());
            prep.setTimestamp(3, new Timestamp(at.getIssuedAt().getTime()));
            prep.setTimestamp(4, new Timestamp(at.getExpiredAt().getTime()));
            prep.executeUpdate();
        }
        catch(SQLException ex) {
            logger.log(Level.WARNING, "DataAccessor::getTokenByUserAccess {0}", 
                    ex.getMessage() + " | " + sql);
        }
        return at;
    }
    
    public User getUserById(String userId) {
        String sql = "SELECT * FROM users u WHERE u.user_id = ?";
        try(PreparedStatement prep = this.getConnection().prepareStatement(sql)) {
            prep.setString(1, userId);
            ResultSet rs = prep.executeQuery();
            if(rs.next()) {
                return User.fromResultSet(rs);                
            }
        }
        catch(Exception ex) {
            logger.log(Level.WARNING, "DataAccessor::getUserById {0}", 
                    ex.getMessage() + " | " + sql);
        }
        return null;
    }
    
    public UserAccess getUserAccessByCredentials(String login, String password) {
        String sql = """
            SELECT 
               *
            FROM 
               user_accesses ua 
               JOIN users u ON ua.user_id = u.user_id 
            WHERE ua.login = ?""";
        try(PreparedStatement prep = this.getConnection().prepareStatement(sql)) {
            prep.setString(1, login);
            ResultSet rs = prep.executeQuery();
            if(rs.next()) {
                UserAccess userAccess = UserAccess.fromResultSet(rs);
                if(kdfService.dk(password, userAccess.getSalt())
                        .equals(userAccess.getDk())) {
                    return userAccess;
                }
            }
        }
        catch(SQLException ex) {
            logger.log(Level.WARNING, "DataAccessor::getUserByCredentials {0}", 
                    ex.getMessage() + " | " + sql);
        }
        return null;
    }
    
    public UserAccess getUserAccess(String userId, String roleId) {
        // перевіряємо чи є UserAccess із зазначеними даними
        String sql = "SELECT * FROM user_accesses ua "
                + "JOIN users u ON ua.user_id = u.user_id "
                + "JOIN user_roles ur ON ua.role_id = ur.role_id "
                + "WHERE u.user_id = ? AND ur.role_id = ?";
        try (PreparedStatement ps = this.getConnection().prepareStatement(sql)) {
            ps.setString(1, userId);
            ps.setString(2, roleId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return UserAccess.fromResultSet(rs);
            }
        } catch (SQLException ex) {
            logger.log(Level.WARNING, "DataAccessor::getUserAccess {0}",
                    ex.getMessage() + " | " + sql);
        }
        return null;
    }
    
    public Connection getConnection() {
        if(this.connection == null) {
            String connectionString;
            try {  
                connectionString = 
                    configService.get("connectionStrings.mainDb");
            }
            catch(NoSuchFieldError err) {
                throw new RuntimeException(
                        "DataAccessor::getConnection Connection String not found 'connectionStrings.mainDb' " 
                        + err.getMessage());
            }
            try {
                mysqlDriver = new com.mysql.cj.jdbc.Driver();
                DriverManager.registerDriver(mysqlDriver);
                connection = DriverManager.getConnection(connectionString);
            }
            catch(SQLException ex) {
                throw new RuntimeException(
                        "DataAccessor::getConnection Connection not opened " 
                        + ex.getMessage());
            }
        }
        return this.connection;
    }
    
    public UUID getDbIdentity() {
        String sql = "SELECT UUID()";
        try(Statement statement = this.getConnection().createStatement()) {
            ResultSet rs = statement.executeQuery(sql);
            rs.next();
            return UUID.fromString( rs.getString(1) );
        }
        catch(SQLException ex) {
            logger.log(Level.WARNING, "DataAccessor::getDbIdentity " 
                    + ex.getMessage() + " | " + sql);
        }
        return null;
    }
    
    public boolean install() {
        String sql = "CREATE TABLE  IF NOT EXISTS  users("
                + "user_id       CHAR(36)     PRIMARY KEY,"
                + "name          VARCHAR(64)  NOT NULL,"
                + "email         VARCHAR(128) NOT NULL,"
                + "birthdate     DATETIME     NULL,"
                + "registered_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,"
                + "deleted_at    DATETIME     NULL"
                + ")ENGINE = INNODB, "
                + " DEFAULT CHARSET = utf8mb4, "
                + " COLLATE utf8mb4_unicode_ci";
        try(Statement statement = this.getConnection().createStatement()) {
            statement.executeUpdate(sql);
        }
        catch(SQLException ex) {
            logger.log(Level.WARNING, "DataAccessor::install " 
                    + ex.getMessage() + " | " + sql);
            return false;
        }
        
        sql = "CREATE TABLE  IF NOT EXISTS  user_accesses("
                + "ua_id   CHAR(36)    PRIMARY KEY,"
                + "user_id CHAR(36)    NOT NULL,"
                + "role_id VARCHAR(16) NOT NULL,"
                + "login   VARCHAR(32) NOT NULL,"
                + "salt    CHAR(16)    NOT NULL,"
                + "dk      CHAR(32)    NOT NULL,"
                + "UNIQUE(login)"
                + ")ENGINE = INNODB, "
                + " DEFAULT CHARSET = utf8mb4, "
                + " COLLATE utf8mb4_unicode_ci";
        try(Statement statement = this.getConnection().createStatement()) {
            statement.executeUpdate(sql);
        }
        catch(SQLException ex) {
            logger.log(Level.WARNING, "DataAccessor::install " 
                    + ex.getMessage() + " | " + sql);
            return false;
        }
        
        
        sql = "CREATE TABLE  IF NOT EXISTS  user_roles("
                + "role_id     VARCHAR(16)  PRIMARY KEY,"
                + "description VARCHAR(256) NOT NULL,"
                + "can_create  TINYINT      NOT NULL DEFAULT 0,"
                + "can_read    TINYINT      NOT NULL DEFAULT 0,"
                + "can_update  TINYINT      NOT NULL DEFAULT 0,"
                + "can_delete  TINYINT      NOT NULL DEFAULT 0"
                + ")ENGINE = INNODB, "
                + " DEFAULT CHARSET = utf8mb4, "
                + " COLLATE utf8mb4_unicode_ci";
        try(Statement statement = this.getConnection().createStatement()) {
            statement.executeUpdate(sql);
        }
        catch(SQLException ex) {
            logger.log(Level.WARNING, "DataAccessor::install " 
                    + ex.getMessage() + " | " + sql);
            return false;
        }
        
        sql = "CREATE TABLE  IF NOT EXISTS  tokens("
                + "token_id       CHAR(36) PRIMARY KEY,"
                + "user_access_id CHAR(36) NOT NULL,"
                + "issued_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,"
                + "expired_at     DATETIME NULL"
                + ")ENGINE = INNODB, "
                + " DEFAULT CHARSET = utf8mb4, "
                + " COLLATE utf8mb4_unicode_ci";
        try(Statement statement = this.getConnection().createStatement()) {
            statement.executeUpdate(sql);
        }
        catch(SQLException ex) {
            logger.log(Level.WARNING, "DataAccessor::install " 
                    + ex.getMessage() + " | " + sql);
            return false;
        }
        // ------------------ Added 2025-10-07 ----------------
        sql = "CREATE TABLE  IF NOT EXISTS  product_groups("
                + "pg_id          CHAR(36)     PRIMARY KEY,"
                + "pg_parent_id   CHAR(36)         NULL,"
                + "pg_name        VARCHAR(64)  NOT NULL,"
                + "pg_description TEXT         NOT NULL,"
                + "pg_slug        VARCHAR(64)  NOT NULL,"
                + "pg_image_url   VARCHAR(256) NOT NULL,"
                + "pg_deleted_at  DATETIME         NULL,"
                + "UNIQUE(pg_slug)"
                + ")ENGINE = INNODB, "
                + " DEFAULT CHARSET = utf8mb4, "
                + " COLLATE utf8mb4_unicode_ci";
        try(Statement statement = this.getConnection().createStatement()) {
            statement.executeUpdate(sql);
        }
        catch(SQLException ex) {
            logger.log(Level.WARNING, "DataAccessor::install " 
                    + ex.getMessage() + " | " + sql);
            return false;
        }
        
        // ------------------ Added 2025-10-20 ----------------
        sql = "CREATE TABLE  IF NOT EXISTS  products("
                + "product_id          CHAR(36)      PRIMARY KEY,"
                + "product_group_id    CHAR(36)      NOT NULL,"
                + "product_name        VARCHAR(64)   NOT NULL,"
                + "product_description TEXT              NULL,"
                + "product_slug        VARCHAR(64)       NULL,"
                + "product_image_url   VARCHAR(256)      NULL,"
                + "product_price       DECIMAL(12,2) NOT NULL,"
                + "product_stock       INT           NOT NULL,"
                + "product_deleted_at  DATETIME          NULL,"
                + "UNIQUE(product_slug)"
                + ")ENGINE = INNODB, "
                + " DEFAULT CHARSET = utf8mb4, "
                + " COLLATE utf8mb4_unicode_ci";
        try(Statement statement = this.getConnection().createStatement()) {
            statement.executeUpdate(sql);
        }
        catch(SQLException ex) {
            logger.log(Level.WARNING, "DataAccessor::install " 
                    + ex.getMessage() + " | " + sql);
            return false;
        }
        
        
        // ------------------ Added 2025-10-28 ----------------
        sql = "CREATE TABLE  IF NOT EXISTS  cart_items("
                + "ci_id         CHAR(36)      PRIMARY KEY,"
                + "ci_cart_id    CHAR(36)      NOT NULL,"
                + "ci_product_id CHAR(36)      NOT NULL,"
                + "ci_di_id      CHAR(36)          NULL  COMMENT 'ref to discount_items table',"
                + "ci_quantity   INT           NOT NULL  DEFAULT 1,"
                + "ci_price      DECIMAL(14,2) NOT NULL,"
                + "ci_deleted_at DATETIME          NULL"
                + ")ENGINE = INNODB, "
                + " DEFAULT CHARSET = utf8mb4, "
                + " COLLATE utf8mb4_unicode_ci";
        try(Statement statement = this.getConnection().createStatement()) {
            statement.executeUpdate(sql);
        }
        catch(SQLException ex) {
            logger.log(Level.WARNING, "DataAccessor::install {0}",
                    ex.getMessage() + " | " + sql);
            return false;
        }
        sql = "CREATE TABLE  IF NOT EXISTS  carts("
                + "cart_id         CHAR(36)      PRIMARY KEY,"
                + "cart_user_id    CHAR(36)      NOT NULL,"
                + "cart_di_id      CHAR(36)          NULL  COMMENT 'ref to discount_items table',"
                + "cart_price      DECIMAL(15,2) NOT NULL,"
                + "cart_created_at DATETIME      NOT NULL  DEFAULT CURRENT_TIMESTAMP,"
                + "cart_paid_at    DATETIME          NULL,"
                + "cart_deleted_at DATETIME          NULL"
                + ")ENGINE = INNODB, "
                + " DEFAULT CHARSET = utf8mb4, "
                + " COLLATE utf8mb4_unicode_ci";
        try(Statement statement = this.getConnection().createStatement()) {
            statement.executeUpdate(sql);
        }
        catch(SQLException ex) {
            logger.log(Level.WARNING, "DataAccessor::install {0}",
                    ex.getMessage() + " | " + sql);
            return false;
        }
        
        // додано 2025-11-26
        sql = "CREATE TABLE  IF NOT EXISTS  rates ("
                + "rate_id          CHAR(36)  PRIMARY KEY,"
                + "user_id          CHAR(36)  NOT NULL,"  
                + "ci_id            CHAR(36)      NULL  COMMENT 'cart item id',"  
                + "item_id          CHAR(36)  NOT NULL  COMMENT 'potentially linked to any table',"
                + "rate_stars       TINYINT   NOT NULL  COMMENT 'rate in stars from 1 to 5',"
                + "rate_text        TEXT          NULL,"
                + "rate_created_at  DATETIME  NOT NULL  DEFAULT CURRENT_TIMESTAMP,"
                + "rate_updated_at  DATETIME      NULL,"
                + "rate_deleted_at  DATETIME      NULL"
                + ")ENGINE = INNODB, "
                + " DEFAULT CHARSET = utf8mb4, "
                + " COLLATE utf8mb4_unicode_ci";
        try(Statement statement = this.getConnection().createStatement()) {
            statement.executeUpdate(sql);
        }
        catch(SQLException ex) {
            logger.log(Level.WARNING, "DataAccessor::install {0}",
                    ex.getMessage() + " | " + sql);
            return false;
        }
        
        return true;
    }
    
    public boolean seed() {
        String sql = "INSERT INTO user_roles(role_id, description, can_create, "
                + "can_read, can_update, can_delete)"
                + "VALUES('admin', 'Root Administrator', 1, 1, 1, 1) "
                + "ON DUPLICATE KEY UPDATE "
                + "description = VALUES(description),"
                + "can_create  = VALUES(can_create),"
                + "can_read    = VALUES(can_read),"
                + "can_update  = VALUES(can_update),"
                + "can_delete  = VALUES(can_delete)";
        try(Statement statement = this.getConnection().createStatement()) {
            statement.executeUpdate(sql);
        }
        catch(SQLException ex) {
            logger.log(Level.WARNING, "DataAccessor::seed " 
                    + ex.getMessage() + " | " + sql);
            return false;
        }
        sql = "INSERT INTO user_roles(role_id, description, can_create, "
                + "can_read, can_update, can_delete)"
                + "VALUES('guest', 'Self Registered User', 0, 0, 0, 0) "
                + "ON DUPLICATE KEY UPDATE "
                + "description = VALUES(description),"
                + "can_create  = VALUES(can_create),"
                + "can_read    = VALUES(can_read),"
                + "can_update  = VALUES(can_update),"
                + "can_delete  = VALUES(can_delete)";
        try(Statement statement = this.getConnection().createStatement()) {
            statement.executeUpdate(sql);
        }
        catch(SQLException ex) {
            logger.log(Level.WARNING, "DataAccessor::seed " 
                    + ex.getMessage() + " | " + sql);
            return false;
        }
        
        sql = "INSERT INTO users(user_id, name, email)"
                + "VALUES('69231c55-9851-11f0-b1b7-62517600596c', "
                + "'Default Administrator', 'admin@localhost') "
                + "ON DUPLICATE KEY UPDATE "
                + "name  = VALUES(name),"
                + "email = VALUES(email)";
        try(Statement statement = this.getConnection().createStatement()) {
            statement.executeUpdate(sql);
        }
        catch(SQLException ex) {
            logger.log(Level.WARNING, "DataAccessor::seed " 
                    + ex.getMessage() + " | " + sql);
            return false;
        }
        
        sql = "INSERT INTO user_accesses(ua_id, user_id, role_id, login, salt, dk)"
                + "VALUES('35326873-9852-11f0-b1b7-62517600596c', "
                + "'69231c55-9851-11f0-b1b7-62517600596c', "
                + "'admin', "
                + "'admin',"
                + "'admin', '" + kdfService.dk("admin", "admin")+ "'"
                + ") ON DUPLICATE KEY UPDATE "
                + "user_id  = VALUES(user_id),"
                + "role_id  = VALUES(role_id),"
                + "login  = VALUES(login),"
                + "salt  = VALUES(salt),"
                + "dk = VALUES(dk)";
        try(Statement statement = this.getConnection().createStatement()) {
            statement.executeUpdate(sql);
        }
        catch(SQLException ex) {
            logger.log(Level.WARNING, "DataAccessor::seed " 
                    + ex.getMessage() + " | " + sql);
            return false;
        }
        
        return true;
    }
    
    // ------------------- Admin ------------------------------
    public List<ProductGroup> adminGetProductGroups() {
        String sql = "SELECT * FROM product_groups";
        List<ProductGroup> ret = new ArrayList<>();
        try(Statement statement = getConnection().createStatement();                
            ResultSet rs = statement.executeQuery(sql)
        ) {
            while(rs.next()) {
                ret.add(ProductGroup.fromResultSet(rs));
            }
        }
        catch(SQLException ex) {
            logger.log(Level.WARNING, "DataAccessor::adminGetProductGroups " 
                    + ex.getMessage() + " | " + sql);
        }
        return ret;
    }
    
    public void adminAddProductGroup() {
        
    }
        
    public void addProduct(Product product) {
        if(product.getId() == null) {
            product.setId( getDbIdentity() );
        }
        String sql = "INSERT INTO products(product_id, product_group_id, product_name,"
                + "product_description, product_slug, product_image_url, product_price, "
                + "product_stock) VALUES(?,?,?,?,?,?,?,?)";
        try( PreparedStatement prep = getConnection().prepareStatement(sql)) {
            prep.setString(1, product.getId().toString());
            prep.setString(2, product.getGroupId().toString());
            prep.setString(3, product.getName());
            prep.setString(4, product.getDescription());
            prep.setString(5, product.getSlug());
            prep.setString(6, product.getImageUrl());
            prep.setDouble(7, product.getPrice());
            prep.setInt(8, product.getStock());
            prep.executeUpdate();
        }
        catch(SQLException ex) {
            logger.log(Level.WARNING, "DataAccessor::addProduct " 
                    + ex.getMessage() + " | " + sql);
            throw new RuntimeException(ex.getMessage());
        }
    }
    
    public void addProductGroup(ProductGroup productGroup) {
        if(productGroup.getId() == null) {
            productGroup.setId( getDbIdentity() );
        }
        String sql = "INSERT INTO product_groups(pg_id, pg_parent_id, pg_name,"
                + "pg_description, pg_slug, pg_image_url) VALUES(?,?,?,?,?,?)";
        try( PreparedStatement prep = getConnection().prepareStatement(sql)) {
            prep.setString(1, productGroup.getId().toString());
            UUID parentId = productGroup.getParentId();
            prep.setString(2, parentId == null ? null : parentId.toString());
            prep.setString(3, productGroup.getName());
            prep.setString(4, productGroup.getDescription());
            prep.setString(5, productGroup.getSlug());
            prep.setString(6, productGroup.getImageUrl());
            prep.executeUpdate();
        }
        catch(SQLException ex) {
            logger.log(Level.WARNING, "DataAccessor::addProductGroup " 
                    + ex.getMessage() + " | " + sql);
            throw new RuntimeException(ex.getMessage());
        }
    }
    
}
