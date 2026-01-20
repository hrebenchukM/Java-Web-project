package learning.itstep.javaweb222.models.user;

import java.util.List;
import learning.itstep.javaweb222.data.dto.Experience;
import learning.itstep.javaweb222.data.dto.User;

public class UserProfileModel {

    private User user;
    private String role;
    private String login;

    private int profileViews;
    private int postViews;
   

    
    public int getProfileViews() {
        return profileViews;
    }
    
    public UserProfileModel setProfileViews(int v) {
        this.profileViews = v;
        return this;
    }

    public int getPostViews() { 
        return postViews;
    }
    
    public UserProfileModel setPostViews(int v) {
        this.postViews = v;
        return this;
    }


    public User getUser() {
        return user;
    }

    public UserProfileModel setUser(User user) {
        this.user = user;
        return this;
    }

    public String getRole() {
        return role;
    }

    public UserProfileModel setRole(String role) {
        this.role = role;
        return this;
    }

    public String getLogin() {
        return login;
    }

    public UserProfileModel setLogin(String login) {
        this.login = login;
        return this;
    }
}