package learning.itstep.javaweb222.data.dto;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class EventSpeakerMap {

    private UUID id;
    private UUID eventId;
    private UUID speakerId;
    private Integer orderIndex;

    public static EventSpeakerMap fromResultSet(ResultSet rs) throws SQLException {
        EventSpeakerMap m = new EventSpeakerMap();

        m.setId(UUID.fromString(rs.getString("event_speaker_map_id")));
        m.setEventId(UUID.fromString(rs.getString("event_id")));
        m.setSpeakerId(UUID.fromString(rs.getString("speaker_id")));
        m.setOrderIndex(rs.getInt("order_index"));

        return m;
    }

    // ===== getters / setters =====

    public UUID getId() {
        return id;
    }

    public EventSpeakerMap setId(UUID id) {
        this.id = id;
        return this;
    }

    public UUID getEventId() {
        return eventId;
    }

    public EventSpeakerMap setEventId(UUID eventId) {
        this.eventId = eventId;
        return this;
    }

    public UUID getSpeakerId() {
        return speakerId;
    }

    public EventSpeakerMap setSpeakerId(UUID speakerId) {
        this.speakerId = speakerId;
        return this;
    }

    public Integer getOrderIndex() {
        return orderIndex;
    }

    public EventSpeakerMap setOrderIndex(Integer orderIndex) {
        this.orderIndex = orderIndex;
        return this;
    }
}
