package learning.itstep.javaweb222.data.dto;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

public class PostHashtag {
    private UUID id;
    private UUID postId;
    private UUID hashtagId;
    private Date createdAt;

    private Post post;
    private Hashtag hashtag;

    public static PostHashtag fromResultSet(ResultSet rs) throws SQLException {
        PostHashtag ph = new PostHashtag();
        ph.setId(UUID.fromString(rs.getString("post_hashtag_id")));
        ph.setPostId(UUID.fromString(rs.getString("post_id")));
        ph.setHashtagId(UUID.fromString(rs.getString("hashtag_id")));

        Timestamp ts = rs.getTimestamp("created_at");
        ph.setCreatedAt(new Date(ts.getTime()));

        try { ph.setPost(Post.fromResultSet(rs)); } catch (Exception ignore) {}
        try { ph.setHashtag(Hashtag.fromResultSet(rs)); } catch (Exception ignore) {}

        return ph;
    }
    // getters / setters

    public UUID getId() {
        return id;
    }

    public PostHashtag setId(UUID id) {
        this.id = id;
        return this;
    }

    public UUID getPostId() {
        return postId;
    }

    public PostHashtag setPostId(UUID postId) {
        this.postId = postId;
        return this;
    }

    public UUID getHashtagId() {
        return hashtagId;
    }

    public PostHashtag setHashtagId(UUID hashtagId) {
        this.hashtagId = hashtagId;
        return this;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public PostHashtag setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public Post getPost() {
        return post;
    }

    public PostHashtag setPost(Post post) {
        this.post = post;
        return this;
    }

    public Hashtag getHashtag() {
        return hashtag;
    }

    public PostHashtag setHashtag(Hashtag hashtag) {
        this.hashtag = hashtag;
        return this;
    }

    
}
