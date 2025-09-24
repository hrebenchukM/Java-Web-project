
package learning.itstep.javaweb222.data.dto;

import java.sql.ResultSet;
import java.util.UUID;

public class UserAccesss {
    private UUID id;
    private UUID userId;
    private String roleId;
    private String login; 
    private String salt;
    private String dk;
   
      public static UserAccesss fromResultSet(ResultSet rs){
        return null;
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

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getDk() {
        return dk;
    }

    public void setDk(String dk) {
        this.dk = dk;
    }
}
