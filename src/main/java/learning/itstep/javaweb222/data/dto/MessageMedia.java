package learning.itstep.javaweb222.data.dto;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

public class MessageMedia {
    private UUID id;
    private UUID messageId;
    private UUID mediaId;
    private Date createdAt;

    private Message message;
    private Media media;

    public static MessageMedia fromResultSet(ResultSet rs) throws SQLException {
        MessageMedia mm = new MessageMedia();
        mm.setId(UUID.fromString(rs.getString("message_media_id")));
        mm.setMessageId(UUID.fromString(rs.getString("message_id")));
        mm.setMediaId(UUID.fromString(rs.getString("media_id")));

        Timestamp ts = rs.getTimestamp("created_at");
        mm.setCreatedAt(new Date(ts.getTime()));

        try { mm.setMessage(Message.fromResultSet(rs)); } catch (Exception ignore) {}
        try { mm.setMedia(Media.fromResultSet(rs)); } catch (Exception ignore) {}

        return mm;
    }

    // getters / setters

    public UUID getId() {
        return id;
    }

    public MessageMedia setId(UUID id) {
        this.id = id;
        return this;
    }

    public UUID getMessageId() {
        return messageId;
    }

    public MessageMedia setMessageId(UUID messageId) {
        this.messageId = messageId;
        return this;
    }

    public UUID getMediaId() {
        return mediaId;
    }

    public MessageMedia setMediaId(UUID mediaId) {
        this.mediaId = mediaId;
        return this;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public MessageMedia setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public Message getMessage() {
        return message;
    }

    public MessageMedia setMessage(Message message) {
        this.message = message;
        return this;
    }

    public Media getMedia() {
        return media;
    }

    public MessageMedia setMedia(Media media) {
        this.media = media;
        return this;
    }

}
