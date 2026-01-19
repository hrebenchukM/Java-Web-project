package learning.itstep.javaweb222.data.dto;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserRole {
    private String id;              // role_id
    private String description;

    private byte canCreate;
    private byte canRead;
    private byte canUpdate;
    private byte canDelete;

    
    // ---------- factory ----------

    public static UserRole fromResultSet(ResultSet rs) throws SQLException {
        UserRole role = new UserRole();

        role.setId(rs.getString("role_id"));
        role.setDescription(rs.getString("description"));
        role.setCanCreate(rs.getByte("can_create"));
        role.setCanRead(rs.getByte("can_read"));
        role.setCanUpdate(rs.getByte("can_update"));
        role.setCanDelete(rs.getByte("can_delete"));

        return role;
    }

    public String getId() {
        return id;
    }

    public UserRole setId(String id) {
        this.id = id;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public UserRole setDescription(String description) {
        this.description = description;
        return this;
    }

    public byte getCanCreate() {
        return canCreate;
    }

    public UserRole setCanCreate(byte canCreate) {
        this.canCreate = canCreate;
        return this;
    }

    public byte getCanRead() {
        return canRead;
    }

    public UserRole setCanRead(byte canRead) {
        this.canRead = canRead;
        return this;
    }

    public byte getCanUpdate() {
        return canUpdate;
    }

    public UserRole setCanUpdate(byte canUpdate) {
        this.canUpdate = canUpdate;
        return this;
    }

    public byte getCanDelete() {
        return canDelete;
    }

    public UserRole setCanDelete(byte canDelete) {
        this.canDelete = canDelete;
        return this;
    }

    
  
}
