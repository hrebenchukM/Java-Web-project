package learning.itstep.javaweb222.data.dto;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserRole {
    private String id;
    private String description;
    private byte canCreate;
    private byte canRead;
    private byte canUpdate;
    private byte canDelete;

    
    public static UserRole fromResultSet(ResultSet rs) throws SQLException {
    UserRole item = new UserRole();
    item.setId( rs.getString("role_id") );
    item.setDescription( rs.getString("description") );
    item.setCanCreate( rs.getByte("can_create") );
    item.setCanRead( rs.getByte("can_read") );
    item.setCanUpdate( rs.getByte("can_update") );
    item.setCanDelete( rs.getByte("can_delete") );
    return item;
    }

    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public byte getCanCreate() {
        return canCreate;
    }

    public void setCanCreate(byte canCreate) {
        this.canCreate = canCreate;
    }

    public byte getCanRead() {
        return canRead;
    }

    public void setCanRead(byte canRead) {
        this.canRead = canRead;
    }

    public byte getCanUpdate() {
        return canUpdate;
    }

    public void setCanUpdate(byte canUpdate) {
        this.canUpdate = canUpdate;
    }

    public byte getCanDelete() {
        return canDelete;
    }

    public void setCanDelete(byte canDelete) {
        this.canDelete = canDelete;
    }
    
}
