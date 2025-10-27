
package learning.itstep.javaweb222.data.dto;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class Product {
     private UUID   id;
    private UUID   groupId;
    private String name;
    private String description;
    private String slug;
    private String imageUrl;
    private double price;
    private int stock;
    private Date   deletedAt;
    
    private ProductGroup group;
    
    
    private List<Product> relativeProducts;

    public List<Product> getRelativeProducts() {
        return relativeProducts;
    }

    public void setRelativeProducts(List<Product> relativeProducts) {
        this.relativeProducts = relativeProducts;
    }
    
    
    
    public static Product fromResultSet(ResultSet rs) throws SQLException {
        Product p = new Product();
        p.setId( UUID.fromString( rs.getString("product_id") ) );
        p.setGroupId( UUID.fromString( rs.getString("product_group_id"))) ;
        p.setName( rs.getString("product_name") );
        p.setDescription( rs.getString("product_description") );
        p.setSlug( rs.getString("product_slug") );
        p.setImageUrl( rs.getString("product_image_url") );
        p.setPrice( rs.getDouble("product_price") );
        p.setStock( rs.getInt("product_stock") );
        Timestamp timestamp;
        timestamp = rs.getTimestamp("product_deleted_at");
        if(timestamp != null) {
            p.setDeletedAt( new Date( timestamp.getTime() ) );
        }
        try{
           p.group = ProductGroup.fromResultSet(rs,false);
        }
        catch(SQLException ignore){}
        
        return p;
    }

    public ProductGroup getGroup() {
        return group;
    }

    public void setGroup(ProductGroup group) {
        this.group = group;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getGroupId() {
        return groupId;
    }

    public void setGroupId(UUID groupId) {
        this.groupId = groupId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public Date getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Date deletedAt) {
        this.deletedAt = deletedAt;
    }

    
    
    
    
}
