package learning.itstep.javaweb222.data.dto;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

public class SavedPost {
    private UUID id;
    private UUID userId;
    private UUID postId;
    private Date savedAt;
    private Date unsavedAt;

    private User user;
    private Post post;

    public static SavedPost fromResultSet(ResultSet rs) throws SQLException {
        SavedPost sp = new SavedPost();
        sp.setId(UUID.fromString(rs.getString("saved_post_id")));
        sp.setUserId(UUID.fromString(rs.getString("user_id")));
        sp.setPostId(UUID.fromString(rs.getString("post_id")));

        Timestamp ts;
        ts = rs.getTimestamp("saved_at");
        sp.setSavedAt(new Date(ts.getTime()));

        ts = rs.getTimestamp("unsaved_at");
        if (ts != null) sp.setUnsavedAt(new Date(ts.getTime()));

        try { sp.setUser(User.fromResultSet(rs)); } catch (Exception ignore) {}
        try { sp.setPost(Post.fromResultSet(rs)); } catch (Exception ignore) {}

        return sp;
    }

      // getters / setters

    public UUID getId() {
        return id;
    }

    public SavedPost setId(UUID id) {
        this.id = id;
        return this;
    }

    public UUID getUserId() {
        return userId;
    }

    public SavedPost setUserId(UUID userId) {
        this.userId = userId;
        return this;
    }

    public UUID getPostId() {
        return postId;
    }

    public SavedPost setPostId(UUID postId) {
        this.postId = postId;
        return this;
    }

    public Date getSavedAt() {
        return savedAt;
    }

    public SavedPost setSavedAt(Date savedAt) {
        this.savedAt = savedAt;
        return this;
    }

    public Date getUnsavedAt() {
        return unsavedAt;
    }

    public SavedPost setUnsavedAt(Date unsavedAt) {
        this.unsavedAt = unsavedAt;
        return this;
    }

    public User getUser() {
        return user;
    }

    public SavedPost setUser(User user) {
        this.user = user;
        return this;
    }

    public Post getPost() {
        return post;
    }

    public SavedPost setPost(Post post) {
        this.post = post;
        return this;
    }

    
}
