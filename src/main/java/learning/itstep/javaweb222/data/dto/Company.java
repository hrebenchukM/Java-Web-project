package learning.itstep.javaweb222.data.dto;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

public class Company {
    private UUID id;
    private UUID ownerUserId;
    private String name;
    private String logoUrl;
    private String industry;
    private String location;
    private String websiteUrl;
    private String description;
    private Date createdAt;
    private Date updatedAt;
    private Date deletedAt;

    public static Company fromResultSet(ResultSet rs) throws SQLException {
        Company c = new Company();
        c.setId(UUID.fromString(rs.getString("company_id")));

        String ownerId = rs.getString("owner_user_id");
        if (ownerId != null) {
            c.setOwnerUserId(UUID.fromString(ownerId));
        }

        c.setName(rs.getString("name"));
        c.setLogoUrl(rs.getString("logo_url"));
        c.setIndustry(rs.getString("industry"));
        c.setLocation(rs.getString("location"));
        c.setWebsiteUrl(rs.getString("website_url"));
        c.setDescription(rs.getString("description"));

        Timestamp ts;
        ts = rs.getTimestamp("created_at");
        c.setCreatedAt(new Date(ts.getTime()));

        ts = rs.getTimestamp("updated_at");
        if (ts != null) c.setUpdatedAt(new Date(ts.getTime()));

        ts = rs.getTimestamp("deleted_at");
        if (ts != null) c.setDeletedAt(new Date(ts.getTime()));

        return c;
    }

    public UUID getId() {
        return id;
    }

    public Company setId(UUID id) {
        this.id = id;
        return this;
    }

    public UUID getOwnerUserId() {
        return ownerUserId;
    }

    public Company setOwnerUserId(UUID ownerUserId) {
        this.ownerUserId = ownerUserId;
        return this;
    }

    public String getName() {
        return name;
    }

    public Company setName(String name) {
        this.name = name;
        return this;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public Company setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
        return this;
    }

    public String getIndustry() {
        return industry;
    }

    public Company setIndustry(String industry) {
        this.industry = industry;
        return this;
    }

    public String getLocation() {
        return location;
    }

    public Company setLocation(String location) {
        this.location = location;
        return this;
    }

    public String getWebsiteUrl() {
        return websiteUrl;
    }

    public Company setWebsiteUrl(String websiteUrl) {
        this.websiteUrl = websiteUrl;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Company setDescription(String description) {
        this.description = description;
        return this;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Company setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public Company setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    public Date getDeletedAt() {
        return deletedAt;
    }

    public Company setDeletedAt(Date deletedAt) {
        this.deletedAt = deletedAt;
        return this;
    }

    
    
    
}
