package learning.itstep.javaweb222.models.user;

import java.util.List;
import learning.itstep.javaweb222.data.dto.Cart;
import learning.itstep.javaweb222.data.dto.User;
import learning.itstep.javaweb222.data.dto.UserRole;


public class UserProfileModel {
    private String login;
    private User user;
    private UserRole role;
    private List<Cart> carts;

    public String getLogin() {
        return login;
    }

    public UserProfileModel setLogin(String login) {
        this.login = login;
        return this;
    }

    public User getUser() {
        return user;
    }

    public UserProfileModel setUser(User user) {
        this.user = user;
        return this;
    }

    public UserRole getRole() {
        return role;
    }

    public UserProfileModel setRole(UserRole role) {
        this.role = role;
        return this;
    }

    public List<Cart> getCarts() {
        return carts;
    }

    public UserProfileModel setCarts(List<Cart> carts) {
        this.carts = carts;
        return this;
    }
    
}
