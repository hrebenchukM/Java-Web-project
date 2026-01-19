package learning.itstep.javaweb222.data.dto;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

public class Repost {
    private UUID id;
    private UUID userId;
    private UUID originalPostId;
    private Date repostedAt;
    private Date removedAt;

    private User user;
    private Post originalPost;

    public static Repost fromResultSet(ResultSet rs) throws SQLException {
        Repost r = new Repost();
        r.setId(UUID.fromString(rs.getString("repost_id")));
        r.setUserId(UUID.fromString(rs.getString("user_id")));
        r.setOriginalPostId(UUID.fromString(rs.getString("original_post_id")));

        Timestamp ts;
        ts = rs.getTimestamp("reposted_at");
        r.setRepostedAt(new Date(ts.getTime()));

        ts = rs.getTimestamp("removed_at");
        if (ts != null) r.setRemovedAt(new Date(ts.getTime()));

        try { r.setUser(User.fromResultSet(rs)); } catch (Exception ignore) {}
        try { r.setOriginalPost(Post.fromResultSet(rs)); } catch (Exception ignore) {}

        return r;
    }

       // getters / setters

    public UUID getId() {
        return id;
    }

    public Repost setId(UUID id) {
        this.id = id;
        return this;
    }

    public UUID getUserId() {
        return userId;
    }

    public Repost setUserId(UUID userId) {
        this.userId = userId;
        return this;
    }

    public UUID getOriginalPostId() {
        return originalPostId;
    }

    public Repost setOriginalPostId(UUID originalPostId) {
        this.originalPostId = originalPostId;
        return this;
    }

    public Date getRepostedAt() {
        return repostedAt;
    }

    public Repost setRepostedAt(Date repostedAt) {
        this.repostedAt = repostedAt;
        return this;
    }

    public Date getRemovedAt() {
        return removedAt;
    }

    public Repost setRemovedAt(Date removedAt) {
        this.removedAt = removedAt;
        return this;
    }

    public User getUser() {
        return user;
    }

    public Repost setUser(User user) {
        this.user = user;
        return this;
    }

    public Post getOriginalPost() {
        return originalPost;
    }

    public Repost setOriginalPost(Post originalPost) {
        this.originalPost = originalPost;
        return this;
    }

}
