package learning.itstep.javaweb222.data.dto;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

public class PostMedia {
    private UUID id;
    private UUID postId;
    private UUID mediaId;
    private Date createdAt;

    private Post post;
    private Media media;

    public static PostMedia fromResultSet(ResultSet rs) throws SQLException {
        PostMedia pm = new PostMedia();
        pm.setId(UUID.fromString(rs.getString("post_media_id")));
        pm.setPostId(UUID.fromString(rs.getString("post_id")));
        pm.setMediaId(UUID.fromString(rs.getString("media_id")));

        Timestamp ts = rs.getTimestamp("created_at");
        pm.setCreatedAt(new Date(ts.getTime()));

        try { pm.setPost(Post.fromResultSet(rs)); } catch (Exception ignore) {}
        try { pm.setMedia(Media.fromResultSet(rs)); } catch (Exception ignore) {}

        return pm;
    }

        // getters / setters

    public UUID getId() {
        return id;
    }

    public PostMedia setId(UUID id) {
        this.id = id;
        return this;
    }

    public UUID getPostId() {
        return postId;
    }

    public PostMedia setPostId(UUID postId) {
        this.postId = postId;
        return this;
    }

    public UUID getMediaId() {
        return mediaId;
    }

    public PostMedia setMediaId(UUID mediaId) {
        this.mediaId = mediaId;
        return this;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public PostMedia setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public Post getPost() {
        return post;
    }

    public PostMedia setPost(Post post) {
        this.post = post;
        return this;
    }

    public Media getMedia() {
        return media;
    }

    public PostMedia setMedia(Media media) {
        this.media = media;
        return this;
    }

}
