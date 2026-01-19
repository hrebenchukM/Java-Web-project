package learning.itstep.javaweb222.data.dto;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

public class GroupMember {
    private UUID id;
    private UUID groupId;
    private UUID userId;
    private String role;
    private Date createdAt;
    private Date updatedAt;
    private Date deletedAt;

    private Group group;
    private User user;

    public static GroupMember fromResultSet(ResultSet rs) throws SQLException {
        GroupMember gm = new GroupMember();
        gm.setId(UUID.fromString(rs.getString("group_member_id")));
        gm.setGroupId(UUID.fromString(rs.getString("group_id")));
        gm.setUserId(UUID.fromString(rs.getString("user_id")));
        gm.setRole(rs.getString("role"));

        Timestamp ts;
        ts = rs.getTimestamp("created_at");
        gm.setCreatedAt(new Date(ts.getTime()));

        ts = rs.getTimestamp("updated_at");
        if (ts != null) gm.setUpdatedAt(new Date(ts.getTime()));

        ts = rs.getTimestamp("deleted_at");
        if (ts != null) gm.setDeletedAt(new Date(ts.getTime()));

        try { gm.setGroup(Group.fromResultSet(rs)); } catch (Exception ignore) {}
        try { gm.setUser(User.fromResultSet(rs)); } catch (Exception ignore) {}

        return gm;
    }

        // getters / setters

    public UUID getId() {
        return id;
    }

    public GroupMember setId(UUID id) {
        this.id = id;
        return this;
    }

    public UUID getGroupId() {
        return groupId;
    }

    public GroupMember setGroupId(UUID groupId) {
        this.groupId = groupId;
        return this;
    }

    public UUID getUserId() {
        return userId;
    }

    public GroupMember setUserId(UUID userId) {
        this.userId = userId;
        return this;
    }

    public String getRole() {
        return role;
    }

    public GroupMember setRole(String role) {
        this.role = role;
        return this;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public GroupMember setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public GroupMember setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    public Date getDeletedAt() {
        return deletedAt;
    }

    public GroupMember setDeletedAt(Date deletedAt) {
        this.deletedAt = deletedAt;
        return this;
    }

    public Group getGroup() {
        return group;
    }

    public GroupMember setGroup(Group group) {
        this.group = group;
        return this;
    }

    public User getUser() {
        return user;
    }

    public GroupMember setUser(User user) {
        this.user = user;
        return this;
    }

}
