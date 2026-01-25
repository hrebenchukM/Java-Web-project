package learning.itstep.javaweb222.data.dto;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

public class EventSpeaker {

    private UUID id;
    private String name;
    private String title;
    private String avatarUrl;
    private Date createdAt;

    public static EventSpeaker fromResultSet(ResultSet rs) throws SQLException {
        EventSpeaker s = new EventSpeaker();

        s.setId(UUID.fromString(rs.getString("speaker_id")));
        s.setName(rs.getString("name"));
        s.setTitle(rs.getString("title"));
        s.setAvatarUrl(rs.getString("avatar_url"));

        Timestamp ts = rs.getTimestamp("created_at");
        s.setCreatedAt(new Date(ts.getTime()));

        return s;
    }

    // ===== getters / setters =====

    public UUID getId() {
        return id;
    }

    public EventSpeaker setId(UUID id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public EventSpeaker setName(String name) {
        this.name = name;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public EventSpeaker setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public EventSpeaker setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
        return this;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public EventSpeaker setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
        return this;
    }
}
