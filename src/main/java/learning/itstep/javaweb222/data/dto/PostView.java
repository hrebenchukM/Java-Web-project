package learning.itstep.javaweb222.data.dto;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

public class PostView {
    private UUID id;
    private UUID postId;
    private UUID viewerUserId;
    private String viewerIp;
    private String viewerUserAgent;
    private String source;
    private Date viewedAt;

    public static PostView fromResultSet(ResultSet rs) throws SQLException {
        PostView pv = new PostView();
        pv.setId(UUID.fromString(rs.getString("post_view_id")));
        pv.setPostId(UUID.fromString(rs.getString("post_id")));

        String viewerId = rs.getString("viewer_user_id");
        if (viewerId != null) {
            pv.setViewerUserId(UUID.fromString(viewerId));
        }

        pv.setViewerIp(rs.getString("viewer_ip"));
        pv.setViewerUserAgent(rs.getString("viewer_user_agent"));
        pv.setSource(rs.getString("source"));

        Timestamp ts = rs.getTimestamp("viewed_at");
        pv.setViewedAt(new Date(ts.getTime()));

        return pv;
    }

    public UUID getId() {
        return id;
    }

    public PostView setId(UUID id) {
        this.id = id;
         return this;
    }

    public UUID getPostId() {
        return postId;
    }

    public PostView setPostId(UUID postId) {
        this.postId = postId;
         return this;
    }

    public UUID getViewerUserId() {
        return viewerUserId;
    }

    public PostView setViewerUserId(UUID viewerUserId) {
        this.viewerUserId = viewerUserId;
         return this;
    }

    public String getViewerIp() {
        return viewerIp;
    }

    public PostView setViewerIp(String viewerIp) {
        this.viewerIp = viewerIp;
         return this;
    }

    public String getViewerUserAgent() {
        return viewerUserAgent;
    }

    public PostView setViewerUserAgent(String viewerUserAgent) {
        this.viewerUserAgent = viewerUserAgent; 
        return this;
    }

    public String getSource() {
        return source;
    }

    public PostView setSource(String source) {
        this.source = source;
         return this;
    }

    public Date getViewedAt() {
        return viewedAt;
    }

    public PostView setViewedAt(Date viewedAt) {
        this.viewedAt = viewedAt;
        return this;
    }

 
}
