package learning.itstep.javaweb222.data;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import learning.itstep.javaweb222.data.cart.CartDao;
import learning.itstep.javaweb222.data.dto.AccessToken;
import learning.itstep.javaweb222.data.dto.Cart;
import learning.itstep.javaweb222.data.dto.Product;
import learning.itstep.javaweb222.data.dto.ProductGroup;
import learning.itstep.javaweb222.data.dto.Rate;
import learning.itstep.javaweb222.data.dto.User;
import learning.itstep.javaweb222.data.dto.UserAccess;
import learning.itstep.javaweb222.data.product.ProductDao;
import learning.itstep.javaweb222.data.rate.RateDao;
import learning.itstep.javaweb222.data.user.UserDao;
import learning.itstep.javaweb222.models.rate.RateFormModel;
import learning.itstep.javaweb222.rest.RestPagination;

import java.util.List;
import learning.itstep.javaweb222.data.core.DbInstaller;
import learning.itstep.javaweb222.data.core.DbSeeder;

@Singleton
public class DataAccessor {

    private final DbSeeder dbSeeder;
    private final DbInstaller dbInstaller;
    private final CartDao cartDao;
    private final ProductDao productDao;
    private final RateDao rateDao;
    private final UserDao userDao;

   @Inject
    public DataAccessor(
            DbSeeder dbSeeder,
            DbInstaller dbInstaller,
            CartDao cartDao,
            ProductDao productDao,
            RateDao rateDao,
            UserDao userDao
    ) {
        this.dbSeeder = dbSeeder;
        this.dbInstaller = dbInstaller;
        this.cartDao = cartDao;
        this.productDao = productDao;
        this.rateDao = rateDao;
        this.userDao = userDao;
    }

    // ===== INSTALL / SEED =====
    public boolean install() {
        return dbInstaller.install();
    }

    public boolean seed() {
        return dbSeeder.seed();
    }
   // ================= USER =================

    public UserAccess getUserAccess(String userId, String roleId) {
        return userDao.getUserAccess(userId, roleId);
    }

    public UserAccess getUserAccessByCredentials(String login, String password) {
        return userDao.getUserAccessByCredentials(login, password);
    }

    public AccessToken getTokenByUserAccess(UserAccess ua) {
        return userDao.getTokenByUserAccess(ua);
    }

    public User getUserById(String userId) {
        return userDao.getUserById(userId);
    }


    // ================= PRODUCTS =================

    public Product getProductBySlugOrId(String slugOrId) {
        return productDao.getProductBySlugOrId(slugOrId);
    }

    public List<ProductGroup> getProductGroups() {
        return productDao.getProductGroups();
    }

    public ProductGroup getProductGroupBySlug(String slug, int page, int perPage) {
        return productDao.getProductGroupBySlug(slug, page, perPage);
    }

    public int getProductsCountByGroupSlug(String slug) {
        return productDao.getProductsCountByGroupSlug(slug);
    }

    // ================= CART =================

    
        public List<Cart> getUserCarts(String userId) {
            return cartDao.getUserCarts(userId);
        }
     public void addToCart(String productId, String userId) throws Exception {
         cartDao.addToCart(productId, userId);
     }

     public Cart getActiveCart(String userId) {
         return cartDao.getActiveCart(userId);
     }

     public Cart getHistoryCart(String userId, String cartId) {
         return cartDao.getHistoryCart(userId, cartId);
     }

     public void modifyCartItem(String cartItemId, int increment) throws Exception {
         cartDao.modifyCartItem(cartItemId, increment);
     }

     public void deleteCartItem(String cartItemId) throws Exception {
         cartDao.deleteCartItem(cartItemId);
     }

     public void repeatCart(String userId, String cartId) throws Exception {
         cartDao.repeatCart(userId, cartId);
     }

     public void checkoutActiveCart(String userId) throws Exception {
         cartDao.checkoutActiveCart(userId);
     }


    // ================= RATES =================

     public void addRate(RateFormModel rateFormModel) throws Exception {
        rateDao.addRate(rateFormModel);
    }

    public int getRatesCountById(String id) {
        return rateDao.getRatesCountById(id);
    }

    public List<Rate> getRates(String id, RestPagination pagination) throws Exception {
        return rateDao.getRates(id, pagination);
    }
    // ================= ADMIN / PRODUCTS =================

    public List<ProductGroup> adminGetProductGroups() {
        return productDao.adminGetProductGroups();
    }

    public void addProduct(Product product) {
        productDao.addProduct(product);
    }

    public void addProductGroup(ProductGroup productGroup) {
        productDao.addProductGroup(productGroup);
    }

}
