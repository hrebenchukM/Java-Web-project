package learning.itstep.javaweb222.data.dto;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

public class UserActivity {
    private UUID id;
    private UUID userId;
    private String action;
    private String entityType;
    private UUID entityId;
    private String meta;
    private Date createdAt;

    private User user;

    public static UserActivity fromResultSet(ResultSet rs) throws SQLException {
        UserActivity a = new UserActivity();
        a.setId(UUID.fromString(rs.getString("activity_id")));
        a.setUserId(UUID.fromString(rs.getString("user_id")));
        a.setAction(rs.getString("action"));
        a.setEntityType(rs.getString("entity_type"));

        String eid = rs.getString("entity_id");
        if (eid != null) {
            a.setEntityId(UUID.fromString(eid));
        }

        a.setMeta(rs.getString("meta"));

        Timestamp ts = rs.getTimestamp("created_at");
        a.setCreatedAt(new Date(ts.getTime()));

        try { a.setUser(User.fromResultSet(rs)); } catch (Exception ignore) {}

        return a;
    }

    // getters / setters

    public UUID getId() {
        return id;
    }

    public UserActivity setId(UUID id) {
        this.id = id;
        return this;
    }

    public UUID getUserId() {
        return userId;
    }

    public UserActivity setUserId(UUID userId) {
        this.userId = userId;
        return this;
    }

    public String getAction() {
        return action;
    }

    public UserActivity setAction(String action) {
        this.action = action;
        return this;
    }

    public String getEntityType() {
        return entityType;
    }

    public UserActivity setEntityType(String entityType) {
        this.entityType = entityType;
        return this;
    }

    public UUID getEntityId() {
        return entityId;
    }

    public UserActivity setEntityId(UUID entityId) {
        this.entityId = entityId;
        return this;
    }

    public String getMeta() {
        return meta;
    }

    public UserActivity setMeta(String meta) {
        this.meta = meta;
        return this;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public UserActivity setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public User getUser() {
        return user;
    }

    public UserActivity setUser(User user) {
        this.user = user;
        return this;
    }

}
