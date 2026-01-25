package learning.itstep.javaweb222.data.dto;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

public class EventSchedule {

    private UUID id;
    private UUID eventId;

    private String timeLabel;
    private String title;
    private String speakerName;
    private int orderIndex;

    private Date createdAt;

    public static EventSchedule fromResultSet(ResultSet rs) throws SQLException {
        EventSchedule es = new EventSchedule();

        es.setId(UUID.fromString(rs.getString("schedule_id")));
        es.setEventId(UUID.fromString(rs.getString("event_id")));
        es.setTimeLabel(rs.getString("time_label"));
        es.setTitle(rs.getString("title"));
        es.setSpeakerName(rs.getString("speaker_name"));
        es.setOrderIndex(rs.getInt("order_index"));

        Timestamp ts = rs.getTimestamp("created_at");
        es.setCreatedAt(new Date(ts.getTime()));

        return es;
    }

    // ===== getters / setters =====

    public UUID getId() {
        return id;
    }

    public EventSchedule setId(UUID id) {
        this.id = id;
        return this;
    }

    public UUID getEventId() {
        return eventId;
    }

    public EventSchedule setEventId(UUID eventId) {
        this.eventId = eventId;
        return this;
    }

    public String getTimeLabel() {
        return timeLabel;
    }

    public EventSchedule setTimeLabel(String timeLabel) {
        this.timeLabel = timeLabel;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public EventSchedule setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getSpeakerName() {
        return speakerName;
    }

    public EventSchedule setSpeakerName(String speakerName) {
        this.speakerName = speakerName;
        return this;
    }

    public int getOrderIndex() {
        return orderIndex;
    }

    public EventSchedule setOrderIndex(int orderIndex) {
        this.orderIndex = orderIndex;
        return this;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public EventSchedule setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
        return this;
    }
}
