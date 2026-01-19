package learning.itstep.javaweb222.data.dto;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

public class Contact {
    private UUID id;
    private UUID requesterId;
    private UUID receiverId;
    private String status;
    private Date requestedAt;
    private Date respondedAt;
    private Date statusChangedAt;

    private User requester;
    private User receiver;

    public static Contact fromResultSet(ResultSet rs) throws SQLException {
        Contact c = new Contact();
        c.setId(UUID.fromString(rs.getString("contact_id")));
        c.setRequesterId(UUID.fromString(rs.getString("requester_id")));
        c.setReceiverId(UUID.fromString(rs.getString("receiver_id")));
        c.setStatus(rs.getString("status"));

        Timestamp ts;
        ts = rs.getTimestamp("requested_at");
        c.setRequestedAt(new Date(ts.getTime()));

        ts = rs.getTimestamp("responded_at");
        if (ts != null) c.setRespondedAt(new Date(ts.getTime()));

        ts = rs.getTimestamp("status_changed_at");
        if (ts != null) c.setStatusChangedAt(new Date(ts.getTime()));

        try { c.setRequester(User.fromResultSet(rs)); } catch (Exception ignore) {}
        try { c.setReceiver(User.fromResultSet(rs)); } catch (Exception ignore) {}

        return c;
    }

       // getters / setters

    public UUID getId() {
        return id;
    }

    public Contact setId(UUID id) {
        this.id = id;
        return this;
    }

    public UUID getRequesterId() {
        return requesterId;
    }

    public Contact setRequesterId(UUID requesterId) {
        this.requesterId = requesterId;
        return this;
    }

    public UUID getReceiverId() {
        return receiverId;
    }

    public Contact setReceiverId(UUID receiverId) {
        this.receiverId = receiverId;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public Contact setStatus(String status) {
        this.status = status;
        return this;
    }

    public Date getRequestedAt() {
        return requestedAt;
    }

    public Contact setRequestedAt(Date requestedAt) {
        this.requestedAt = requestedAt;
        return this;
    }

    public Date getRespondedAt() {
        return respondedAt;
    }

    public Contact setRespondedAt(Date respondedAt) {
        this.respondedAt = respondedAt;
        return this;
    }

    public Date getStatusChangedAt() {
        return statusChangedAt;
    }

    public Contact setStatusChangedAt(Date statusChangedAt) {
        this.statusChangedAt = statusChangedAt;
        return this;
    }

    public User getRequester() {
        return requester;
    }

    public Contact setRequester(User requester) {
        this.requester = requester;
        return this;
    }

    public User getReceiver() {
        return receiver;
    }

    public Contact setReceiver(User receiver) {
        this.receiver = receiver;
        return this;
    }

    
}
