package learning.itstep.javaweb222.data.dto;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

public class Mention {
    private UUID id;
    private UUID postId;
    private UUID mentionedUserId;
    private Date createdAt;
    private Date deletedAt;

    private User mentionedUser;
    private Post post;

    public static Mention fromResultSet(ResultSet rs) throws SQLException {
        Mention m = new Mention();
        m.setId(UUID.fromString(rs.getString("mention_id")));
        m.setPostId(UUID.fromString(rs.getString("post_id")));
        m.setMentionedUserId(UUID.fromString(rs.getString("mentioned_user_id")));

        Timestamp ts;
        ts = rs.getTimestamp("created_at");
        m.setCreatedAt(new Date(ts.getTime()));

        ts = rs.getTimestamp("deleted_at");
        if (ts != null) m.setDeletedAt(new Date(ts.getTime()));

        try { m.setMentionedUser(User.fromResultSet(rs)); } catch (Exception ignore) {}
        try { m.setPost(Post.fromResultSet(rs)); } catch (Exception ignore) {}

        return m;
    }

     // getters / setters

    public UUID getId() {
        return id;
    }

    public Mention setId(UUID id) {
        this.id = id;
        return this;
    }

    public UUID getPostId() {
        return postId;
    }

    public Mention setPostId(UUID postId) {
        this.postId = postId;
        return this;
    }

    public UUID getMentionedUserId() {
        return mentionedUserId;
    }

    public Mention setMentionedUserId(UUID mentionedUserId) {
        this.mentionedUserId = mentionedUserId;
        return this;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Mention setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public Date getDeletedAt() {
        return deletedAt;
    }

    public Mention setDeletedAt(Date deletedAt) {
        this.deletedAt = deletedAt;
        return this;
    }

    public User getMentionedUser() {
        return mentionedUser;
    }

    public Mention setMentionedUser(User mentionedUser) {
        this.mentionedUser = mentionedUser;
        return this;
    }

    public Post getPost() {
        return post;
    }

    public Mention setPost(Post post) {
        this.post = post;
        return this;
    }

    
}
