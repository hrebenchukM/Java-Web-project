package learning.itstep.javaweb222.data.dto;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

public class Group {
    private UUID id;
    private UUID ownerId;
    private String name;
    private String description;
    private Date createdAt;
    private Date updatedAt;
    private Date deletedAt;

    private User owner;

    public static Group fromResultSet(ResultSet rs) throws SQLException {
        Group g = new Group();
        g.setId(UUID.fromString(rs.getString("group_id")));
        g.setOwnerId(UUID.fromString(rs.getString("owner_id")));
        g.setName(rs.getString("name"));
        g.setDescription(rs.getString("description"));

        Timestamp ts;
        ts = rs.getTimestamp("created_at");
        g.setCreatedAt(new Date(ts.getTime()));

        ts = rs.getTimestamp("updated_at");
        if (ts != null) g.setUpdatedAt(new Date(ts.getTime()));

        ts = rs.getTimestamp("deleted_at");
        if (ts != null) g.setDeletedAt(new Date(ts.getTime()));

        try { g.setOwner(User.fromResultSet(rs)); } catch (Exception ignore) {}

        return g;
    }

    
    // getters / setters

    public UUID getId() {
        return id;
    }

    public Group setId(UUID id) {
        this.id = id;
        return this;
    }

    public UUID getOwnerId() {
        return ownerId;
    }

    public Group setOwnerId(UUID ownerId) {
        this.ownerId = ownerId;
        return this;
    }

    public String getName() {
        return name;
    }

    public Group setName(String name) {
        this.name = name;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Group setDescription(String description) {
        this.description = description;
        return this;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Group setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public Group setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    public Date getDeletedAt() {
        return deletedAt;
    }

    public Group setDeletedAt(Date deletedAt) {
        this.deletedAt = deletedAt;
        return this;
    }

    public User getOwner() {
        return owner;
    }

    public Group setOwner(User owner) {
        this.owner = owner;
        return this;
    }

}
