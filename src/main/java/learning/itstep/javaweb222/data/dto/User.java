
package learning.itstep.javaweb222.data.dto;

import java.sql.ResultSet;
import java.util.Date;
import java.util.UUID;

public class User {
    private UUID id;
    private String name;
    private String email;
    private Date birthdate;
    private Date registeredAt;
    private Date deletedAt;
    
   public static User fromResultSet(ResultSet rs){
        return null;
     }
 
    public Date getBirthdate() {
        return birthdate;
    }

    public Date getRegisteredAt() {
        return registeredAt;
    }

    public Date getDeletedAt() {
        return deletedAt;
    }

    public User setBirthdate(Date birthdate) {
        this.birthdate = birthdate;
        return this;
    }

    public User setRegisteredAt(Date registeredAt) {
        this.registeredAt = registeredAt;
        return this;
    }

    public User setDeletedAt(Date deletedAt) {
        this.deletedAt = deletedAt;
        return this;
    }
    
    
    
    
    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public User setId(UUID id) {
        this.id = id;
         return this;
    }

    public User setName(String name) {
        this.name = name;
        return this;
    }

    public User setEmail(String email) {
        this.email = email;
        return this;
    }
    
}
