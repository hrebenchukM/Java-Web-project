package learning.itstep.javaweb222.data.dto;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

public class RecommendedJobQuery {
    private UUID id;
    private String query;
    private Date createdAt;

    public static RecommendedJobQuery fromResultSet(ResultSet rs) throws SQLException {
        RecommendedJobQuery q = new RecommendedJobQuery();
        q.setId(UUID.fromString(rs.getString("recommended_job_query_id")));
        q.setQuery(rs.getString("query"));

        Timestamp ts = rs.getTimestamp("created_at");
        q.setCreatedAt(new Date(ts.getTime()));

        return q;
    }
    // getters / setters

    public UUID getId() {
        return id;
    }

    public RecommendedJobQuery setId(UUID id) {
        this.id = id;
        return this;
    }

    public String getQuery() {
        return query;
    }

    public RecommendedJobQuery setQuery(String query) {
        this.query = query;
        return this;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public RecommendedJobQuery setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    
}
