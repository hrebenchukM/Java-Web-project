
package learning.itstep.javaweb222.data.product;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import learning.itstep.javaweb222.data.core.DbProvider;
import learning.itstep.javaweb222.data.dto.Product;
import learning.itstep.javaweb222.data.dto.ProductGroup;

@Singleton
public class ProductDao {
    private final DbProvider db;
    private final Logger logger;

    @Inject
    public ProductDao(DbProvider db, Logger logger) {
        this.db = db;
        this.logger = logger;
    }
      public Product getProductBySlugOrId(String slug) {
        String sql = "SELECT * FROM products p "
        + "JOIN product_groups pg ON p.product_group_id = pg.pg_id "
        + "LEFT JOIN rates r ON p.product_id = r.item_id "
        + "LEFT JOIN users u ON r.user_id = u.user_id "
        + "WHERE r.rate_deleted_at IS NULL AND (p.product_slug = ? OR p.product_id = ?) "
        + "ORDER BY r.rate_created_at LIMIT 2 ";

        try( PreparedStatement prep = db.getConnection().prepareStatement(sql)) {
            prep.setString(1, slug);
            prep.setString(2, slug);
            ResultSet rs = prep.executeQuery();
            if(rs.next()) {
                return Product.fromResultSet( rs,true );
            }
            else return null;
        }
        catch(Exception ex) {
            logger.log(Level.WARNING, "ProductDao::getProductBySlugOrId {0} ",
                    ex.getMessage() + " | " + sql);
            return null;
        }    
    }
    
    public List<ProductGroup> getProductGroups() {
        String sql = "SELECT * FROM product_groups WHERE pg_deleted_at IS NULL";
        List<ProductGroup> ret = new ArrayList<>();
        try(Statement statement = db.getConnection().createStatement();                
            ResultSet rs = statement.executeQuery(sql)
        ) {
            while(rs.next()) {
                ret.add(ProductGroup.fromResultSet(rs));
            }
        }
        catch(SQLException ex) {
            logger.log(Level.WARNING, "ProductDao::getProductGroups {0}" ,
                    ex.getMessage() + " | " + sql);
        }
        return ret;
    }
    
   


    public int getProductsCountByGroupSlug(String slug) {
        String sql = "SELECT " +
        " COUNT(*) " +
        "FROM product_groups pg " +
        "LEFT JOIN products p ON p.product_group_id = pg.pg_id " +
        "WHERE pg.pg_slug = ? ";
        try( PreparedStatement prep = db.getConnection().prepareStatement(sql)) {
            prep.setString(1, slug);
            ResultSet rs = prep.executeQuery();
            rs.next();
            return rs.getInt(1);
        }
        catch(SQLException ex) {
            logger.log(Level.WARNING, "ProductDao::getProductsCountByGroupSlug {0}", 
                    ex.getMessage() + " | " + sql);
            return 0;
        }    
    }
    public ProductGroup getProductGroupBySlug(String slug, int page, int perPage) {
 
        int skip = (page - 1) * perPage;
        String sql = "SELECT " +
        " (SELECT COUNT(*) FROM rates r WHERE r.item_id = p.product_id AND r.rate_deleted_at IS NULL) AS rates_count, " +
        " (SELECT AVG(r.rate_stars) FROM rates r WHERE r.item_id = p.product_id AND r.rate_stars > 0 AND r.rate_deleted_at IS NULL) AS rate_avg, " +
        " pg.*, p.* " +
        "FROM product_groups pg " +
        "LEFT JOIN products p ON p.product_group_id = pg.pg_id " +
        "WHERE pg.pg_slug = ? " +
        "ORDER BY p.product_id " + 
        String.format("LIMIT %d, %d", skip, perPage);
        try( PreparedStatement prep = db.getConnection().prepareStatement(sql)) {
            prep.setString(1, slug);
            ResultSet rs = prep.executeQuery();
            if(rs.next()) {
                return ProductGroup.fromResultSet( rs );
            }
            else return null;
        }
        catch(SQLException ex) {
            logger.log(Level.WARNING, "ProductDao::getProductGroupBySlug {0}", 
                    ex.getMessage() + " | " + sql);
            return null;
        }    
    }
    
     public void addProduct(Product product) {
        if(product.getId() == null) {
            product.setId( db.getDbIdentity() );
        }
        String sql = "INSERT INTO products(product_id, product_group_id, product_name,"
                + "product_description, product_slug, product_image_url, product_price, "
                + "product_stock) VALUES(?,?,?,?,?,?,?,?)";
        try( PreparedStatement prep = db.getConnection().prepareStatement(sql)) {
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
            logger.log(Level.WARNING, "ProductDao::addProduct " 
                    + ex.getMessage() + " | " + sql);
            throw new RuntimeException(ex.getMessage());
        }
    }
    
    public void addProductGroup(ProductGroup productGroup) {
        if(productGroup.getId() == null) {
            productGroup.setId( db.getDbIdentity() );
        }
        String sql = "INSERT INTO product_groups(pg_id, pg_parent_id, pg_name,"
                + "pg_description, pg_slug, pg_image_url) VALUES(?,?,?,?,?,?)";
        try( PreparedStatement prep = db.getConnection().prepareStatement(sql)) {
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
            logger.log(Level.WARNING, "ProductDao::addProductGroup " 
                    + ex.getMessage() + " | " + sql);
            throw new RuntimeException(ex.getMessage());
        }
    }
     public List<ProductGroup> adminGetProductGroups() {
        String sql = "SELECT * FROM product_groups";
        List<ProductGroup> ret = new ArrayList<>();
        try(Statement statement = db.getConnection().createStatement();                
            ResultSet rs = statement.executeQuery(sql)
        ) {
            while(rs.next()) {
                ret.add(ProductGroup.fromResultSet(rs));
            }
        }
        catch(SQLException ex) {
            logger.log(Level.WARNING, "ProductDao::adminGetProductGroups " 
                    + ex.getMessage() + " | " + sql);
        }
        return ret;
    }
}
