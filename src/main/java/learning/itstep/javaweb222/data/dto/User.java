
package learning.itstep.javaweb222.data.dto;

import java.util.UUID;

public class User {
    private UUID id;
    private String name;
    private String email;

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
