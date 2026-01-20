package learning.itstep.javaweb222.data.dto;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

public class Message {
    private UUID id;
    private UUID chatId;
    private UUID senderId;
    private String content;
    private Date sentAt;
    private Date editedAt;
    private Date deletedAt;
    private byte isDraft;

    private User sender;
    private Chat chat;


    public static Message fromResultSet(ResultSet rs) throws SQLException {
        Message m = new Message();
        m.setId(UUID.fromString(rs.getString("message_id")));
        m.setChatId(UUID.fromString(rs.getString("chat_id")));
        m.setSenderId(UUID.fromString(rs.getString("sender_id")));
        m.setContent(rs.getString("content"));
        m.setIsDraft(rs.getByte("is_draft"));

        Timestamp ts;
        ts = rs.getTimestamp("sent_at");
        m.setSentAt(new Date(ts.getTime()));

        ts = rs.getTimestamp("edited_at");
        if (ts != null) m.setEditedAt(new Date(ts.getTime()));

        ts = rs.getTimestamp("deleted_at");
        if (ts != null) m.setDeletedAt(new Date(ts.getTime()));

        try { m.setSender(User.fromResultSet(rs)); } catch (Exception ignore) {}
        try { m.setChat(Chat.fromResultSet(rs)); } catch (Exception ignore) {}

        return m;
    }

 
     // getters / setters

    public UUID getId() {
        return id;
    }

    public Message setId(UUID id) {
        this.id = id;
        return this;
    }

    public UUID getChatId() {
        return chatId;
    }

    public Message setChatId(UUID chatId) {
        this.chatId = chatId;
        return this;
    }

    public UUID getSenderId() {
        return senderId;
    }

    public Message setSenderId(UUID senderId) {
        this.senderId = senderId;
        return this;
    }

    public String getContent() {
        return content;
    }

    public Message setContent(String content) {
        this.content = content;
        return this;
    }

    public Date getSentAt() {
        return sentAt;
    }

    public Message setSentAt(Date sentAt) {
        this.sentAt = sentAt;
        return this;
    }

    public Date getEditedAt() {
        return editedAt;
    }

    public Message setEditedAt(Date editedAt) {
        this.editedAt = editedAt;
        return this;
    }

    public Date getDeletedAt() {
        return deletedAt;
    }

    public Message setDeletedAt(Date deletedAt) {
        this.deletedAt = deletedAt;
        return this;
    }

    public byte getIsDraft() {
        return isDraft;
    }

    public Message setIsDraft(byte isDraft) {
        this.isDraft = isDraft;
        return this;
    }

    public User getSender() {
        return sender;
    }

    public Message setSender(User sender) {
        this.sender = sender;
        return this;
    }

    public Chat getChat() {
        return chat;
    }

    public Message setChat(Chat chat) {
        this.chat = chat;
        return this;
    }

}
