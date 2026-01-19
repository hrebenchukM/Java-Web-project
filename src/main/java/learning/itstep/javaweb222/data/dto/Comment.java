package learning.itstep.javaweb222.data.dto;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

public class Comment {
    private UUID id;
    private UUID postId;
    private UUID userId;
    private UUID parentCommentId;
    private String content;
    private Date createdAt;
    private Date updatedAt;
    private Date deletedAt;

    private User user;
    private Post post;

    public static Comment fromResultSet(ResultSet rs) throws SQLException {
        Comment c = new Comment();
        c.setId(UUID.fromString(rs.getString("comment_id")));
        c.setPostId(UUID.fromString(rs.getString("post_id")));
        c.setUserId(UUID.fromString(rs.getString("user_id")));

        String parentId = rs.getString("parent_comment_id");
        if (parentId != null) c.setParentCommentId(UUID.fromString(parentId));

        c.setContent(rs.getString("content"));

        Timestamp ts;
        ts = rs.getTimestamp("created_at");
        c.setCreatedAt(new Date(ts.getTime()));

        ts = rs.getTimestamp("updated_at");
        if (ts != null) c.setUpdatedAt(new Date(ts.getTime()));

        ts = rs.getTimestamp("deleted_at");
        if (ts != null) c.setDeletedAt(new Date(ts.getTime()));

        try { c.setUser(User.fromResultSet(rs)); } catch (Exception ignore) {}
        try { c.setPost(Post.fromResultSet(rs)); } catch (Exception ignore) {}

        return c;
    }

        // getters / setters

    public UUID getId() {
        return id;
    }

    public Comment setId(UUID id) {
        this.id = id;
        return this;
    }

    public UUID getPostId() {
        return postId;
    }

    public Comment setPostId(UUID postId) {
        this.postId = postId;
        return this;
    }

    public UUID getUserId() {
        return userId;
    }

    public Comment setUserId(UUID userId) {
        this.userId = userId;
        return this;
    }

    public UUID getParentCommentId() {
        return parentCommentId;
    }

    public Comment setParentCommentId(UUID parentCommentId) {
        this.parentCommentId = parentCommentId;
        return this;
    }

    public String getContent() {
        return content;
    }

    public Comment setContent(String content) {
        this.content = content;
        return this;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Comment setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public Comment setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    public Date getDeletedAt() {
        return deletedAt;
    }

    public Comment setDeletedAt(Date deletedAt) {
        this.deletedAt = deletedAt;
        return this;
    }

    public User getUser() {
        return user;
    }

    public Comment setUser(User user) {
        this.user = user;
        return this;
    }

    public Post getPost() {
        return post;
    }

    public Comment setPost(Post post) {
        this.post = post;
        return this;
    }

}
