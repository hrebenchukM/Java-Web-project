package learning.itstep.javaweb222.data.dto;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

public class Recommendation {
    private UUID id;
    private UUID authorId;
    private UUID userId;
    private String text;
    private Date createdAt;
    private Date updatedAt;
    private Date deletedAt;

    public static Recommendation fromResultSet(ResultSet rs) throws SQLException {
        Recommendation r = new Recommendation();
        r.setId(UUID.fromString(rs.getString("recommendation_id")));
        r.setAuthorId(UUID.fromString(rs.getString("author_id")));
        r.setUserId(UUID.fromString(rs.getString("user_id")));
        r.setText(rs.getString("text"));

        Timestamp ts;
        ts = rs.getTimestamp("created_at");
        r.setCreatedAt(new Date(ts.getTime()));

        ts = rs.getTimestamp("updated_at");
        if (ts != null) r.setUpdatedAt(new Date(ts.getTime()));

        ts = rs.getTimestamp("deleted_at");
        if (ts != null) r.setDeletedAt(new Date(ts.getTime()));

        return r;
    }

        // getters / setters

    public UUID getId() {
        return id;
    }

    public Recommendation setId(UUID id) {
        this.id = id;
        return this;
    }

    public UUID getAuthorId() {
        return authorId;
    }

    public Recommendation setAuthorId(UUID authorId) {
        this.authorId = authorId;
        return this;
    }

    public UUID getUserId() {
        return userId;
    }

    public Recommendation setUserId(UUID userId) {
        this.userId = userId;
        return this;
    }

    public String getText() {
        return text;
    }

    public Recommendation setText(String text) {
        this.text = text;
        return this;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Recommendation setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public Recommendation setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    public Date getDeletedAt() {
        return deletedAt;
    }

    public Recommendation setDeletedAt(Date deletedAt) {
        this.deletedAt = deletedAt;
        return this;
    }

}
