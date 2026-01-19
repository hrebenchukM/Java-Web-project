package learning.itstep.javaweb222.data.dto;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

public class Page {
    private UUID id;
    private UUID ownerId;
    private String name;
    private String description;
    private String logoUrl;
    private Date createdAt;
    private Date updatedAt;
    private Date deletedAt;

    private User owner;

    public static Page fromResultSet(ResultSet rs) throws SQLException {
        Page p = new Page();
        p.setId(UUID.fromString(rs.getString("page_id")));
        p.setOwnerId(UUID.fromString(rs.getString("owner_id")));
        p.setName(rs.getString("name"));
        p.setDescription(rs.getString("description"));
        p.setLogoUrl(rs.getString("logo_url"));

        Timestamp ts;
        ts = rs.getTimestamp("created_at");
        p.setCreatedAt(new Date(ts.getTime()));

        ts = rs.getTimestamp("updated_at");
        if (ts != null) p.setUpdatedAt(new Date(ts.getTime()));

        ts = rs.getTimestamp("deleted_at");
        if (ts != null) p.setDeletedAt(new Date(ts.getTime()));

        try { p.setOwner(User.fromResultSet(rs)); } catch (Exception ignore) {}

        return p;
    }

        // getters / setters

    public UUID getId() {
        return id;
    }

    public Page setId(UUID id) {
        this.id = id;
        return this;
    }

    public UUID getOwnerId() {
        return ownerId;
    }

    public Page setOwnerId(UUID ownerId) {
        this.ownerId = ownerId;
        return this;
    }

    public String getName() {
        return name;
    }

    public Page setName(String name) {
        this.name = name;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Page setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public Page setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
        return this;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Page setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public Page setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    public Date getDeletedAt() {
        return deletedAt;
    }

    public Page setDeletedAt(Date deletedAt) {
        this.deletedAt = deletedAt;
        return this;
    }

    public User getOwner() {
        return owner;
    }

    public Page setOwner(User owner) {
        this.owner = owner;
        return this;
    }

    
}
