package learning.itstep.javaweb222.data.dto;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

public class Academy {
    private UUID id;
    private String name;
    private String logoUrl;
    private String websiteUrl;
    private Date createdAt;
    private Date updatedAt;

    public static Academy fromResultSet(ResultSet rs) throws SQLException {
        Academy a = new Academy();
        a.setId(UUID.fromString(rs.getString("academy_id")));
        a.setName(rs.getString("name"));
        a.setLogoUrl(rs.getString("logo_url"));
        a.setWebsiteUrl(rs.getString("website_url"));

        Timestamp ts;
        ts = rs.getTimestamp("created_at");
        a.setCreatedAt(new Date(ts.getTime()));

        ts = rs.getTimestamp("updated_at");
        if (ts != null) a.setUpdatedAt(new Date(ts.getTime()));

        return a;
    }

    // getters / setters

    public UUID getId() {
        return id;
    }

    public Academy setId(UUID id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public Academy setName(String name) {
        this.name = name;
        return this;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public Academy setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
        return this;
    }

    public String getWebsiteUrl() {
        return websiteUrl;
    }

    public Academy setWebsiteUrl(String websiteUrl) {
        this.websiteUrl = websiteUrl;
        return this;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Academy setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public Academy setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }
    
}
