package learning.itstep.javaweb222.data.dto;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

public class Reaction {
    private UUID id;
    private UUID userId;
    private UUID postId;
    private String reactionType;
    private Date createdAt;

    private User user;
    private Post post;

    public static Reaction fromResultSet(ResultSet rs) throws SQLException {
        Reaction r = new Reaction();
        r.setId(UUID.fromString(rs.getString("reaction_id")));
        r.setUserId(UUID.fromString(rs.getString("user_id")));
        r.setPostId(UUID.fromString(rs.getString("post_id")));
        r.setReactionType(rs.getString("reaction_type"));

        Timestamp ts = rs.getTimestamp("created_at");
        r.setCreatedAt(new Date(ts.getTime()));

        try { r.setUser(User.fromResultSet(rs)); } catch (Exception ignore) {}
        try { r.setPost(Post.fromResultSet(rs)); } catch (Exception ignore) {}

        return r;
    }

    // getters / setters
        // getters / setters

    public UUID getId() {
        return id;
    }

    public Reaction setId(UUID id) {
        this.id = id;
        return this;
    }

    public UUID getUserId() {
        return userId;
    }

    public Reaction setUserId(UUID userId) {
        this.userId = userId;
        return this;
    }

    public UUID getPostId() {
        return postId;
    }

    public Reaction setPostId(UUID postId) {
        this.postId = postId;
        return this;
    }

    public String getReactionType() {
        return reactionType;
    }

    public Reaction setReactionType(String reactionType) {
        this.reactionType = reactionType;
        return this;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Reaction setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public User getUser() {
        return user;
    }

    public Reaction setUser(User user) {
        this.user = user;
        return this;
    }

    public Post getPost() {
        return post;
    }

    public Reaction setPost(Post post) {
        this.post = post;
        return this;
    }

}
