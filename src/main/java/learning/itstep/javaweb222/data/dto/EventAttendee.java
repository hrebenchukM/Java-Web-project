package learning.itstep.javaweb222.data.dto;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

public class EventAttendee {
    private UUID id;
    private UUID eventId;
    private UUID userId;
    private String status;
    private Date joinedAt;
    private Date updatedAt;
    private Date deletedAt;

    private User user;
    private Event event;

    public static EventAttendee fromResultSet(ResultSet rs) throws SQLException {
        EventAttendee ea = new EventAttendee();
        ea.setId(UUID.fromString(rs.getString("event_attendee_id")));
        ea.setEventId(UUID.fromString(rs.getString("event_id")));
        ea.setUserId(UUID.fromString(rs.getString("user_id")));
        ea.setStatus(rs.getString("status"));

        Timestamp ts;
        ts = rs.getTimestamp("joined_at");
        ea.setJoinedAt(new Date(ts.getTime()));

        ts = rs.getTimestamp("updated_at");
        if (ts != null) ea.setUpdatedAt(new Date(ts.getTime()));

        ts = rs.getTimestamp("deleted_at");
        if (ts != null) ea.setDeletedAt(new Date(ts.getTime()));

        try { ea.setUser(User.fromResultSet(rs)); } catch (Exception ignore) {}
        try { ea.setEvent(Event.fromResultSet(rs)); } catch (Exception ignore) {}

        return ea;
    }

      // getters / setters

    public UUID getId() {
        return id;
    }

    public EventAttendee setId(UUID id) {
        this.id = id;
        return this;
    }

    public UUID getEventId() {
        return eventId;
    }

    public EventAttendee setEventId(UUID eventId) {
        this.eventId = eventId;
        return this;
    }

    public UUID getUserId() {
        return userId;
    }

    public EventAttendee setUserId(UUID userId) {
        this.userId = userId;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public EventAttendee setStatus(String status) {
        this.status = status;
        return this;
    }

    public Date getJoinedAt() {
        return joinedAt;
    }

    public EventAttendee setJoinedAt(Date joinedAt) {
        this.joinedAt = joinedAt;
        return this;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public EventAttendee setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    public Date getDeletedAt() {
        return deletedAt;
    }

    public EventAttendee setDeletedAt(Date deletedAt) {
        this.deletedAt = deletedAt;
        return this;
    }

    public User getUser() {
        return user;
    }

    public EventAttendee setUser(User user) {
        this.user = user;
        return this;
    }

    public Event getEvent() {
        return event;
    }

    public EventAttendee setEvent(Event event) {
        this.event = event;
        return this;
    }

    
}
