package learning.itstep.javaweb222.data.dto;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

public class MessageRead {
    private UUID id;
    private UUID messageId;
    private UUID userId;
    private Date readAt;

    private User user;
    private Message message;

    public static MessageRead fromResultSet(ResultSet rs) throws SQLException {
        MessageRead mr = new MessageRead();
        mr.setId(UUID.fromString(rs.getString("message_read_id")));
        mr.setMessageId(UUID.fromString(rs.getString("message_id")));
        mr.setUserId(UUID.fromString(rs.getString("user_id")));

        Timestamp ts = rs.getTimestamp("read_at");
        mr.setReadAt(new Date(ts.getTime()));

        try { mr.setUser(User.fromResultSet(rs)); } catch (Exception ignore) {}
        try { mr.setMessage(Message.fromResultSet(rs)); } catch (Exception ignore) {}

        return mr;
    }

     // getters / setters

    public UUID getId() {
        return id;
    }

    public MessageRead setId(UUID id) {
        this.id = id;
        return this;
    }

    public UUID getMessageId() {
        return messageId;
    }

    public MessageRead setMessageId(UUID messageId) {
        this.messageId = messageId;
        return this;
    }

    public UUID getUserId() {
        return userId;
    }

    public MessageRead setUserId(UUID userId) {
        this.userId = userId;
        return this;
    }

    public Date getReadAt() {
        return readAt;
    }

    public MessageRead setReadAt(Date readAt) {
        this.readAt = readAt;
        return this;
    }

    public User getUser() {
        return user;
    }

    public MessageRead setUser(User user) {
        this.user = user;
        return this;
    }

    public Message getMessage() {
        return message;
    }

    public MessageRead setMessage(Message message) {
        this.message = message;
        return this;
    }

    
}
