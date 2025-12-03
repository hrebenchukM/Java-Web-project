
package learning.itstep.javaweb222.data.dto;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

public class Rate {
    private UUID id;
    private UUID userId;
    private UUID cartItemId;
    private UUID itemId;
    private byte rateStars;
    private String text;
    private Date createdAt;
    private Date updatedAt;
    private Date deletedAt;

    private User user;
    
    
    public static Rate fromResultSet(ResultSet rs) throws Exception {
        Rate item = new Rate();
        item.setId(     UUID.fromString( rs.getString("rate_id") ) );
        item.setUserId( UUID.fromString( rs.getString("user_id") ) );        
        item.setItemId( UUID.fromString( rs.getString("item_id") ) );
        String ciId = rs.getString("ci_id");
        if(ciId != null) {
            item.setCartItemId( UUID.fromString( ciId ) );
        }        
        item.setRateStars( rs.getByte("rate_stars") );
        item.setText( rs.getString("rate_text") );
        
        Timestamp timestamp;
        timestamp = rs.getTimestamp("rate_created_at");
        item.setCreatedAt( new Date(timestamp.getTime()) );
       
        timestamp = rs.getTimestamp("rate_updated_at");
        if(timestamp != null) {
            item.setUpdatedAt( new Date( timestamp.getTime() ) );
        }
        
        timestamp = rs.getTimestamp("rate_deleted_at");
        if(timestamp != null) {
            item.setDeletedAt( new Date( timestamp.getTime() ) );
        }
        try {
            item.setUser( User.fromResultSet(rs) );
        }
        catch(Exception ignore) { }

        return item;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

    public UUID getCartItemId() {
        return cartItemId;
    }

    public void setCartItemId(UUID cartItemId) {
        this.cartItemId = cartItemId;
    }

    public UUID getItemId() {
        return itemId;
    }

    public void setItemId(UUID itemId) {
        this.itemId = itemId;
    }

    public byte getRateStars() {
        return rateStars;
    }

    public void setRateStars(byte rateStars) {
        this.rateStars = rateStars;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Date getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Date deletedAt) {
        this.deletedAt = deletedAt;
    }
    
    
    
    
}

