package learning.itstep.javaweb222.data.dto;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

public class PageAdmin {
    private UUID id;
    private UUID pageId;
    private UUID userId;
    private String role;
    private Date assignedAt;
    private Date revokedAt;

    private Page page;
    private User user;

    public static PageAdmin fromResultSet(ResultSet rs) throws SQLException {
        PageAdmin pa = new PageAdmin();
        pa.setId(UUID.fromString(rs.getString("page_admin_id")));
        pa.setPageId(UUID.fromString(rs.getString("page_id")));
        pa.setUserId(UUID.fromString(rs.getString("user_id")));
        pa.setRole(rs.getString("role"));

        Timestamp ts;
        ts = rs.getTimestamp("assigned_at");
        pa.setAssignedAt(new Date(ts.getTime()));

        ts = rs.getTimestamp("revoked_at");
        if (ts != null) pa.setRevokedAt(new Date(ts.getTime()));

        try { pa.setPage(Page.fromResultSet(rs)); } catch (Exception ignore) {}
        try { pa.setUser(User.fromResultSet(rs)); } catch (Exception ignore) {}

        return pa;
    }

     // getters / setters

    public UUID getId() {
        return id;
    }

    public PageAdmin setId(UUID id) {
        this.id = id;
        return this;
    }

    public UUID getPageId() {
        return pageId;
    }

    public PageAdmin setPageId(UUID pageId) {
        this.pageId = pageId;
        return this;
    }

    public UUID getUserId() {
        return userId;
    }

    public PageAdmin setUserId(UUID userId) {
        this.userId = userId;
        return this;
    }

    public String getRole() {
        return role;
    }

    public PageAdmin setRole(String role) {
        this.role = role;
        return this;
    }

    public Date getAssignedAt() {
        return assignedAt;
    }

    public PageAdmin setAssignedAt(Date assignedAt) {
        this.assignedAt = assignedAt;
        return this;
    }

    public Date getRevokedAt() {
        return revokedAt;
    }

    public PageAdmin setRevokedAt(Date revokedAt) {
        this.revokedAt = revokedAt;
        return this;
    }

    public Page getPage() {
        return page;
    }

    public PageAdmin setPage(Page page) {
        this.page = page;
        return this;
    }

    public User getUser() {
        return user;
    }

    public PageAdmin setUser(User user) {
        this.user = user;
        return this;
    }

}
