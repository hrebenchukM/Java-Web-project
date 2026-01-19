package learning.itstep.javaweb222.data.dto;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

public class ChatMember {
    private UUID id;
    private UUID chatId;
    private UUID userId;
    private String folder;
    private String status;
    private byte isFavorite;
    private byte hasUnread;
    private Date joinedAt;
    private Date updatedAt;
    private Date leftAt;

    private User user;
    private Chat chat;

    public static ChatMember fromResultSet(ResultSet rs) throws SQLException {
        ChatMember cm = new ChatMember();
        cm.setId(UUID.fromString(rs.getString("chat_member_id")));
        cm.setChatId(UUID.fromString(rs.getString("chat_id")));
        cm.setUserId(UUID.fromString(rs.getString("user_id")));
        cm.setFolder(rs.getString("folder"));
        cm.setStatus(rs.getString("status"));
        cm.setIsFavorite(rs.getByte("is_favorite"));
        cm.setHasUnread(rs.getByte("has_unread"));

        Timestamp ts;
        ts = rs.getTimestamp("joined_at");
        cm.setJoinedAt(new Date(ts.getTime()));

        ts = rs.getTimestamp("updated_at");
        if (ts != null) cm.setUpdatedAt(new Date(ts.getTime()));

        ts = rs.getTimestamp("left_at");
        if (ts != null) cm.setLeftAt(new Date(ts.getTime()));

        try { cm.setUser(User.fromResultSet(rs)); } catch (Exception ignore) {}
        try { cm.setChat(Chat.fromResultSet(rs)); } catch (Exception ignore) {}

        return cm;
    }

     // getters / setters

    public UUID getId() {
        return id;
    }

    public ChatMember setId(UUID id) {
        this.id = id;
        return this;
    }

    public UUID getChatId() {
        return chatId;
    }

    public ChatMember setChatId(UUID chatId) {
        this.chatId = chatId;
        return this;
    }

    public UUID getUserId() {
        return userId;
    }

    public ChatMember setUserId(UUID userId) {
        this.userId = userId;
        return this;
    }

    public String getFolder() {
        return folder;
    }

    public ChatMember setFolder(String folder) {
        this.folder = folder;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public ChatMember setStatus(String status) {
        this.status = status;
        return this;
    }

    public byte getIsFavorite() {
        return isFavorite;
    }

    public ChatMember setIsFavorite(byte isFavorite) {
        this.isFavorite = isFavorite;
        return this;
    }

    public byte getHasUnread() {
        return hasUnread;
    }

    public ChatMember setHasUnread(byte hasUnread) {
        this.hasUnread = hasUnread;
        return this;
    }

    public Date getJoinedAt() {
        return joinedAt;
    }

    public ChatMember setJoinedAt(Date joinedAt) {
        this.joinedAt = joinedAt;
        return this;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public ChatMember setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    public Date getLeftAt() {
        return leftAt;
    }

    public ChatMember setLeftAt(Date leftAt) {
        this.leftAt = leftAt;
        return this;
    }

    public User getUser() {
        return user;
    }

    public ChatMember setUser(User user) {
        this.user = user;
        return this;
    }

    public Chat getChat() {
        return chat;
    }

    public ChatMember setChat(Chat chat) {
        this.chat = chat;
        return this;
    }

}
