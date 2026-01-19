package learning.itstep.javaweb222.data.dto;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

public class Chat {
    private UUID id;
    private UUID createdBy;
    private Date createdAt;
    private Date deletedAt;

    private User creator;

    public static Chat fromResultSet(ResultSet rs) throws SQLException {
        Chat c = new Chat();
        c.setId(UUID.fromString(rs.getString("chat_id")));
        c.setCreatedBy(UUID.fromString(rs.getString("created_by")));

        Timestamp ts;
        ts = rs.getTimestamp("created_at");
        c.setCreatedAt(new Date(ts.getTime()));

        ts = rs.getTimestamp("deleted_at");
        if (ts != null) c.setDeletedAt(new Date(ts.getTime()));

        try { c.setCreator(User.fromResultSet(rs)); } catch (Exception ignore) {}

        return c;
    }

      // getters / setters

    public UUID getId() {
        return id;
    }

    public Chat setId(UUID id) {
        this.id = id;
        return this;
    }

    public UUID getCreatedBy() {
        return createdBy;
    }

    public Chat setCreatedBy(UUID createdBy) {
        this.createdBy = createdBy;
        return this;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Chat setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public Date getDeletedAt() {
        return deletedAt;
    }

    public Chat setDeletedAt(Date deletedAt) {
        this.deletedAt = deletedAt;
        return this;
    }

    public User getCreator() {
        return creator;
    }

    public Chat setCreator(User creator) {
        this.creator = creator;
        return this;
    }

}
