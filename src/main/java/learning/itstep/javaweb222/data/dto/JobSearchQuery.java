package learning.itstep.javaweb222.data.dto;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

public class JobSearchQuery {
    private UUID id;
    private UUID userId;
    private String query;
    private String location;
    private Integer radius;
    private Date createdAt;
    private Date updatedAt;
    private Date deletedAt;

    public static JobSearchQuery fromResultSet(ResultSet rs) throws SQLException {
        JobSearchQuery q = new JobSearchQuery();
        q.setId(UUID.fromString(rs.getString("job_search_id")));
        q.setUserId(UUID.fromString(rs.getString("user_id")));
        q.setQuery(rs.getString("query"));
        q.setLocation(rs.getString("location"));
        q.setRadius((Integer) rs.getObject("radius"));

        Timestamp ts;
        ts = rs.getTimestamp("created_at");
        q.setCreatedAt(new Date(ts.getTime()));

        ts = rs.getTimestamp("updated_at");
        if (ts != null) q.setUpdatedAt(new Date(ts.getTime()));

        ts = rs.getTimestamp("deleted_at");
        if (ts != null) q.setDeletedAt(new Date(ts.getTime()));

        return q;
    }
    // getters / setters

    public UUID getId() {
        return id;
    }

    public JobSearchQuery setId(UUID id) {
        this.id = id;
        return this;
    }

    public UUID getUserId() {
        return userId;
    }

    public JobSearchQuery setUserId(UUID userId) {
        this.userId = userId;
        return this;
    }

    public String getQuery() {
        return query;
    }

    public JobSearchQuery setQuery(String query) {
        this.query = query;
        return this;
    }

    public String getLocation() {
        return location;
    }

    public JobSearchQuery setLocation(String location) {
        this.location = location;
        return this;
    }

    public Integer getRadius() {
        return radius;
    }

    public JobSearchQuery setRadius(Integer radius) {
        this.radius = radius;
        return this;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public JobSearchQuery setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public JobSearchQuery setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    public Date getDeletedAt() {
        return deletedAt;
    }

    public JobSearchQuery setDeletedAt(Date deletedAt) {
        this.deletedAt = deletedAt;
        return this;
    }

    
}
