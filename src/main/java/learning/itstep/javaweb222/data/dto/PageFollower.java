package learning.itstep.javaweb222.data.dto;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

public class PageFollower {
    private UUID id;
    private UUID pageId;
    private UUID userId;
    private Date followedAt;
    private Date unfollowedAt;

    private Page page;
    private User user;

    public static PageFollower fromResultSet(ResultSet rs) throws SQLException {
        PageFollower pf = new PageFollower();
        pf.setId(UUID.fromString(rs.getString("page_follower_id")));
        pf.setPageId(UUID.fromString(rs.getString("page_id")));
        pf.setUserId(UUID.fromString(rs.getString("user_id")));

        Timestamp ts;
        ts = rs.getTimestamp("followed_at");
        pf.setFollowedAt(new Date(ts.getTime()));

        ts = rs.getTimestamp("unfollowed_at");
        if (ts != null) pf.setUnfollowedAt(new Date(ts.getTime()));

        try { pf.setPage(Page.fromResultSet(rs)); } catch (Exception ignore) {}
        try { pf.setUser(User.fromResultSet(rs)); } catch (Exception ignore) {}

        return pf;
    }

       // getters / setters

    public UUID getId() {
        return id;
    }

    public PageFollower setId(UUID id) {
        this.id = id;
        return this;
    }

    public UUID getPageId() {
        return pageId;
    }

    public PageFollower setPageId(UUID pageId) {
        this.pageId = pageId;
        return this;
    }

    public UUID getUserId() {
        return userId;
    }

    public PageFollower setUserId(UUID userId) {
        this.userId = userId;
        return this;
    }

    public Date getFollowedAt() {
        return followedAt;
    }

    public PageFollower setFollowedAt(Date followedAt) {
        this.followedAt = followedAt;
        return this;
    }

    public Date getUnfollowedAt() {
        return unfollowedAt;
    }

    public PageFollower setUnfollowedAt(Date unfollowedAt) {
        this.unfollowedAt = unfollowedAt;
        return this;
    }

    public Page getPage() {
        return page;
    }

    public PageFollower setPage(Page page) {
        this.page = page;
        return this;
    }

    public User getUser() {
        return user;
    }

    public PageFollower setUser(User user) {
        this.user = user;
        return this;
    }

}
