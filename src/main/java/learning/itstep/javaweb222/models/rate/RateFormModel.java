package learning.itstep.javaweb222.models.rate;

import com.google.gson.annotations.SerializedName;

/**
 * Form model - для передачі відгука (Rate) від фронтенда до сервера
 */
public class RateFormModel {
    @SerializedName("ciId")
    private String cartItemId;
    private String productId;
    private String userId;
    private String comment;
    private int rate;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCartItemId() {
        return cartItemId;
    }

    public void setCartItemId(String cartItemId) {
        this.cartItemId = cartItemId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }
    
    
}