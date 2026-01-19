package learning.itstep.javaweb222.data.dto;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

public class Follow {
    private UUID id;
    private UUID followerId;
    private UUID followingId;
    private Date followedAt;
    private Date unfollowedAt;

    private User follower;
    private User following;

    public static Follow fromResultSet(ResultSet rs) throws SQLException {
        Follow f = new Follow();
        f.setId(UUID.fromString(rs.getString("follow_id")));
        f.setFollowerId(UUID.fromString(rs.getString("follower_id")));
        f.setFollowingId(UUID.fromString(rs.getString("following_id")));

        Timestamp ts;
        ts = rs.getTimestamp("followed_at");
        f.setFollowedAt(new Date(ts.getTime()));

        ts = rs.getTimestamp("unfollowed_at");
        if (ts != null) f.setUnfollowedAt(new Date(ts.getTime()));

        try { f.setFollower(User.fromResultSet(rs)); } catch (Exception ignore) {}
        try { f.setFollowing(User.fromResultSet(rs)); } catch (Exception ignore) {}

        return f;
    }
    // getters / setters

    public UUID getId() {
        return id;
    }

    public Follow setId(UUID id) {
        this.id = id;
        return this;
    }

    public UUID getFollowerId() {
        return followerId;
    }

    public Follow setFollowerId(UUID followerId) {
        this.followerId = followerId;
        return this;
    }

    public UUID getFollowingId() {
        return followingId;
    }

    public Follow setFollowingId(UUID followingId) {
        this.followingId = followingId;
        return this;
    }

    public Date getFollowedAt() {
        return followedAt;
    }

    public Follow setFollowedAt(Date followedAt) {
        this.followedAt = followedAt;
        return this;
    }

    public Date getUnfollowedAt() {
        return unfollowedAt;
    }

    public Follow setUnfollowedAt(Date unfollowedAt) {
        this.unfollowedAt = unfollowedAt;
        return this;
    }

    public User getFollower() {
        return follower;
    }

    public Follow setFollower(User follower) {
        this.follower = follower;
        return this;
    }

    public User getFollowing() {
        return following;
    }

    public Follow setFollowing(User following) {
        this.following = following;
        return this;
    }

    
}
