package learning.itstep.javaweb222.data.dto;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

public class ProfileView {
    private UUID id;
    private UUID profileOwnerId;
    private UUID viewerUserId;
    private String viewerIp;
    private String viewerUserAgent;
    private String source;
    private Date viewedAt;

    public static ProfileView fromResultSet(ResultSet rs) throws SQLException {
        ProfileView pv = new ProfileView();
        pv.setId(UUID.fromString(rs.getString("pv_id")));
        pv.setProfileOwnerId(UUID.fromString(rs.getString("profile_owner_id")));

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

    public ProfileView setId(UUID id) {
        this.id = id;
        return this;
    }

    public UUID getProfileOwnerId() {
        return profileOwnerId;
    }

    public ProfileView setProfileOwnerId(UUID profileOwnerId) {
        this.profileOwnerId = profileOwnerId;
        return this;
    }

    public UUID getViewerUserId() {
        return viewerUserId;
    }

    public ProfileView setViewerUserId(UUID viewerUserId) {
        this.viewerUserId = viewerUserId;
        return this;
    }

    public String getViewerIp() {
        return viewerIp;
    }

    public ProfileView setViewerIp(String viewerIp) {
        this.viewerIp = viewerIp;
        return this;
    }

    public String getViewerUserAgent() {
        return viewerUserAgent;
    }

    public ProfileView setViewerUserAgent(String viewerUserAgent) {
        this.viewerUserAgent = viewerUserAgent;
        return this;
    }

    public String getSource() {
        return source;
    }

    public ProfileView setSource(String source) {
        this.source = source;
        return this;
    }

    public Date getViewedAt() {
        return viewedAt;
    }

    public ProfileView setViewedAt(Date viewedAt) {
        this.viewedAt = viewedAt;
        return this;
    }

  
}
