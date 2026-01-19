package learning.itstep.javaweb222.data.dto;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

public class MessageSettings {
    private UUID id;
    private UUID userId;
    private byte officeAbsenceEnabled;
    private String officeAbsenceMessage;
    private byte notificationsEnabled;
    private Date createdAt;
    private Date updatedAt;

    public static MessageSettings fromResultSet(ResultSet rs) throws SQLException {
        MessageSettings ms = new MessageSettings();
        ms.setId(UUID.fromString(rs.getString("ms_id")));
        ms.setUserId(UUID.fromString(rs.getString("user_id")));
        ms.setOfficeAbsenceEnabled(rs.getByte("office_absence_enabled"));
        ms.setOfficeAbsenceMessage(rs.getString("office_absence_message"));
        ms.setNotificationsEnabled(rs.getByte("notifications_enabled"));

        Timestamp ts;
        ts = rs.getTimestamp("created_at");
        ms.setCreatedAt(new Date(ts.getTime()));

        ts = rs.getTimestamp("updated_at");
        if (ts != null) {
            ms.setUpdatedAt(new Date(ts.getTime()));
        }
        return ms;
    }

    public UUID getId() {
        return id;
    }

    public MessageSettings setId(UUID id) {
        this.id = id;
        return this;
    }

    public UUID getUserId() {
        return userId;
    }

    public MessageSettings setUserId(UUID userId) {
        this.userId = userId;
        return this;
    }

    public byte getOfficeAbsenceEnabled() {
        return officeAbsenceEnabled;
    }

    public MessageSettings setOfficeAbsenceEnabled(byte officeAbsenceEnabled) {
        this.officeAbsenceEnabled = officeAbsenceEnabled;
        return this;
    }

    public String getOfficeAbsenceMessage() {
        return officeAbsenceMessage;
    }

    public MessageSettings setOfficeAbsenceMessage(String officeAbsenceMessage) {
        this.officeAbsenceMessage = officeAbsenceMessage;
        return this;
    }

    public byte getNotificationsEnabled() {
        return notificationsEnabled;
    }

    public MessageSettings setNotificationsEnabled(byte notificationsEnabled) {
        this.notificationsEnabled = notificationsEnabled;
        return this;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public MessageSettings setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public MessageSettings setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }


}
