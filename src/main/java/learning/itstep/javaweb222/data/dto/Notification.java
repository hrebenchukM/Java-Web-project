package learning.itstep.javaweb222.data.dto;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

public class Notification {
    private UUID id;
    private UUID userId;
    private String type;
    private String title;
    private String body;
    private String entityType;
    private UUID entityId;
    private byte isRead;
    private Date createdAt;
    private Date updatedAt;
    private Date deletedAt;

    private User user;

    public static Notification fromResultSet(ResultSet rs) throws SQLException {
        Notification n = new Notification();
        n.setId(UUID.fromString(rs.getString("notification_id")));
        n.setUserId(UUID.fromString(rs.getString("user_id")));
        n.setType(rs.getString("type"));
        n.setTitle(rs.getString("title"));
        n.setBody(rs.getString("body"));
        n.setEntityType(rs.getString("entity_type"));

        String eid = rs.getString("entity_id");
        if (eid != null) {
            n.setEntityId(UUID.fromString(eid));
        }

        n.setIsRead(rs.getByte("is_read"));

        Timestamp ts;
        ts = rs.getTimestamp("created_at");
        n.setCreatedAt(new Date(ts.getTime()));

        ts = rs.getTimestamp("updated_at");
        if (ts != null) n.setUpdatedAt(new Date(ts.getTime()));

        ts = rs.getTimestamp("deleted_at");
        if (ts != null) n.setDeletedAt(new Date(ts.getTime()));

        try { n.setUser(User.fromResultSet(rs)); } catch (Exception ignore) {}

        return n;
    }

    // getters / setters

    public UUID getId() {
        return id;
    }

    public Notification setId(UUID id) {
        this.id = id;
        return this;
    }

    public UUID getUserId() {
        return userId;
    }

    public Notification setUserId(UUID userId) {
        this.userId = userId;
        return this;
    }

    public String getType() {
        return type;
    }

    public Notification setType(String type) {
        this.type = type;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public Notification setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getBody() {
        return body;
    }

    public Notification setBody(String body) {
        this.body = body;
        return this;
    }

    public String getEntityType() {
        return entityType;
    }

    public Notification setEntityType(String entityType) {
        this.entityType = entityType;
        return this;
    }

    public UUID getEntityId() {
        return entityId;
    }

    public Notification setEntityId(UUID entityId) {
        this.entityId = entityId;
        return this;
    }

    public byte getIsRead() {
        return isRead;
    }

    public Notification setIsRead(byte isRead) {
        this.isRead = isRead;
        return this;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Notification setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public Notification setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    public Date getDeletedAt() {
        return deletedAt;
    }

    public Notification setDeletedAt(Date deletedAt) {
        this.deletedAt = deletedAt;
        return this;
    }

    public User getUser() {
        return user;
    }

    public Notification setUser(User user) {
        this.user = user;
        return this;
    }

}
