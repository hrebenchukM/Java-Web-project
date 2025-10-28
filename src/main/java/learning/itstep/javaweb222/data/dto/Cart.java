package learning.itstep.javaweb222.data.dto;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;


public class Cart {
    private UUID   id;
    private UUID   userId;
    private UUID   discountId;
    private double price;
    private Date createdAt;
    private Date paidAt;
    private Date deletedAt;

    
    public static Cart fromResultSet(ResultSet rs) throws SQLException {
        Cart item = new Cart();
        item.setId( UUID.fromString( rs.getString("cart_id") ) );
        item.setUserId( UUID.fromString( rs.getString("cart_user_id") ) );
        String discountId = rs.getString("cart_di_id");
        if(discountId != null) {
            item.setDiscountId( UUID.fromString( discountId ) );
        }        
        item.setPrice( rs.getDouble("cart_price") );
        Timestamp timestamp;
        timestamp = rs.getTimestamp("cart_created_at");
        item.setCreatedAt( new Date(timestamp.getTime()) );
        timestamp = rs.getTimestamp("cart_paid_at");
        if(timestamp != null) {
            item.setPaidAt( new Date( timestamp.getTime() ) );
        }
        timestamp = rs.getTimestamp("cart_deleted_at");
        if(timestamp != null) {
            item.setDeletedAt( new Date( timestamp.getTime() ) );
        }
        return item;
    }
    
    
    
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public UUID getDiscountId() {
        return discountId;
    }

    public void setDiscountId(UUID discountId) {
        this.discountId = discountId;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getPaidAt() {
        return paidAt;
    }

    public void setPaidAt(Date paidAt) {
        this.paidAt = paidAt;
    }

    public Date getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Date deletedAt) {
        this.deletedAt = deletedAt;
    }
    
    
    
}
