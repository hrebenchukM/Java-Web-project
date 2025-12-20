package learning.itstep.javaweb222.data.dto;

import jakarta.servlet.http.HttpServletRequest;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import learning.itstep.javaweb222.servlets.FileServlet;


public class Product {
    private UUID   id;
    private UUID   groupId;
    private String name;
    private String description;
    private String slug;
    private String imageUrl;
    private double price;
    private int    stock;
    private Date   deletedAt;
    
    private ProductGroup group;
    private Rate rate;
    private double avgRate;
    private int ratesCount;
    private List<Rate> rates;
    
    
     public static Product fromResultSet(ResultSet rs) throws Exception {
        return fromResultSet(rs,false);
     }
    
    public static Product fromResultSet(ResultSet rs,boolean withRates) throws Exception {
        Product p = new Product();
        p.setId( UUID.fromString( rs.getString("product_id") ) );
        p.setGroupId( UUID.fromString( rs.getString("product_group_id") ) );
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
        try {
            p.group = ProductGroup.fromResultSet(rs, false);
        }        
        catch(Exception ignore) { }
        


        if(withRates) {
            // int i = 0;
             p.rates = new ArrayList<>();
             try {
                 do {
                     p.rates.add( Rate.fromResultSet(rs) );
                    // i += 1;
                     //if( i >= 2 ) break;
                 } while(rs.next());
             }
             catch(Exception ignore) { }
         }

        else{
            try {
                p.rate = Rate.fromResultSet(rs);
            }        
            catch(Exception ignore) { }

            try {
                p.avgRate = rs.getDouble("rate_avg");
                p.ratesCount = rs.getInt("rates_count");
            }
            catch(Exception ignore) { }
        }
        return p;
    }

    public List<Rate> getRates() {
        return rates;
    }

    public void setRates(List<Rate> rates) {
        this.rates = rates;
    }
    

    public double getAvgRate() {
        return avgRate;
    }

    public void setAvgRate(double avgRate) {
        this.avgRate = avgRate;
    }

    public int getRatesCount() {
        return ratesCount;
    }

    public void setRatesCount(int ratesCount) {
        this.ratesCount = ratesCount;
    }

 
    
    public void correctImageUrl(HttpServletRequest req) {
        if(imageUrl != null) {
            imageUrl = FileServlet.getFileUrl(req, imageUrl);
        }
    }

    public Rate getRate() {
        return rate;
    }

    public void setRate(Rate rate) {
        this.rate = rate;
    }

    public ProductGroup getGroup() {
        return group;
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