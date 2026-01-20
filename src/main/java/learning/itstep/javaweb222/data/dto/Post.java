package learning.itstep.javaweb222.data.dto;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

public class Post {
    private UUID id;
    private UUID userId;
    private String content;
    private String visibility;
    private int reactionCount;
    private int commentCount;
    private int repostCount;
    private Date createdAt;
    private Date editedAt;
    private Date deletedAt;

    private User user;

    

    
    public static Post fromResultSet(ResultSet rs) throws SQLException {
        Post p = new Post();
        p.setId(UUID.fromString(rs.getString("post_id")));
        p.setUserId(UUID.fromString(rs.getString("user_id")));
        p.setContent(rs.getString("content"));
        p.setVisibility(rs.getString("visibility"));

        
        p.setReactionCount(rs.getInt("reaction_count"));
        p.setCommentCount(rs.getInt("comment_count"));
        p.setRepostCount(rs.getInt("repost_count"));
        
        Timestamp ts;
        ts = rs.getTimestamp("created_at");
        p.setCreatedAt(new Date(ts.getTime()));

        ts = rs.getTimestamp("edited_at");
        if (ts != null) p.setEditedAt(new Date(ts.getTime()));

        ts = rs.getTimestamp("deleted_at");
        if (ts != null) p.setDeletedAt(new Date(ts.getTime()));

        try { p.setUser(User.fromResultSet(rs)); } catch (Exception ignore) {}

        return p;
    }
    // getters / setters

    public UUID getId() {
        return id;
    }

    public Post setId(UUID id) {
        this.id = id;
        return this;
    }

    public UUID getUserId() {
        return userId;
    }

    public Post setUserId(UUID userId) {
        this.userId = userId;
        return this;
    }

    public String getContent() {
        return content;
    }

    public Post setContent(String content) {
        this.content = content;
        return this;
    }

    public String getVisibility() {
        return visibility;
    }

    public Post setVisibility(String visibility) {
        this.visibility = visibility;
        return this;
    }

    public int getReactionCount() {
        return reactionCount;
    }

    public Post  setReactionCount(int reactionCount) {
        this.reactionCount = reactionCount;
         return this;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public Post  setCommentCount(int commentCount) {
        this.commentCount = commentCount;
         return this;
    }

    public int getRepostCount() {
        return repostCount;
    }

    public Post  setRepostCount(int repostCount) {
        this.repostCount = repostCount;
         return this;
    }

    
    public Date getCreatedAt() {
        return createdAt;
    }

    public Post setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public Date getEditedAt() {
        return editedAt;
    }

    public Post setEditedAt(Date editedAt) {
        this.editedAt = editedAt;
        return this;
    }

    public Date getDeletedAt() {
        return deletedAt;
    }

    public Post setDeletedAt(Date deletedAt) {
        this.deletedAt = deletedAt;
        return this;
    }

    public User getUser() {
        return user;
    }

    public Post setUser(User user) {
        this.user = user;
        return this;
    }

}
