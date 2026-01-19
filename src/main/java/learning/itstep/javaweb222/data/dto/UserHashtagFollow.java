package learning.itstep.javaweb222.data.dto;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

public class UserHashtagFollow {
    private UUID id;
    private UUID userId;
    private UUID hashtagId;
    private Date followedAt;
    private Date unfollowedAt;

    private User user;
    private Hashtag hashtag;

    public static UserHashtagFollow fromResultSet(ResultSet rs) throws SQLException {
        UserHashtagFollow uh = new UserHashtagFollow();
        uh.setId(UUID.fromString(rs.getString("user_hashtag_follow_id")));
        uh.setUserId(UUID.fromString(rs.getString("user_id")));
        uh.setHashtagId(UUID.fromString(rs.getString("hashtag_id")));

        Timestamp ts;
        ts = rs.getTimestamp("followed_at");
        uh.setFollowedAt(new Date(ts.getTime()));

        ts = rs.getTimestamp("unfollowed_at");
        if (ts != null) uh.setUnfollowedAt(new Date(ts.getTime()));

        try { uh.setUser(User.fromResultSet(rs)); } catch (Exception ignore) {}
        try { uh.setHashtag(Hashtag.fromResultSet(rs)); } catch (Exception ignore) {}

        return uh;
    }

      // getters / setters

    public UUID getId() {
        return id;
    }

    public UserHashtagFollow setId(UUID id) {
        this.id = id;
        return this;
    }

    public UUID getUserId() {
        return userId;
    }

    public UserHashtagFollow setUserId(UUID userId) {
        this.userId = userId;
        return this;
    }

    public UUID getHashtagId() {
        return hashtagId;
    }

    public UserHashtagFollow setHashtagId(UUID hashtagId) {
        this.hashtagId = hashtagId;
        return this;
    }

    public Date getFollowedAt() {
        return followedAt;
    }

    public UserHashtagFollow setFollowedAt(Date followedAt) {
        this.followedAt = followedAt;
        return this;
    }

    public Date getUnfollowedAt() {
        return unfollowedAt;
    }

    public UserHashtagFollow setUnfollowedAt(Date unfollowedAt) {
        this.unfollowedAt = unfollowedAt;
        return this;
    }

    public User getUser() {
        return user;
    }

    public UserHashtagFollow setUser(User user) {
        this.user = user;
        return this;
    }

    public Hashtag getHashtag() {
        return hashtag;
    }

    public UserHashtagFollow setHashtag(Hashtag hashtag) {
        this.hashtag = hashtag;
        return this;
    }

}
