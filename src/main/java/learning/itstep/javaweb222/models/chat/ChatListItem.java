package learning.itstep.javaweb222.models.chat;

import java.util.Date;
import java.util.UUID;
import learning.itstep.javaweb222.data.dto.User;

public class ChatListItem {

    private UUID chatId;
    private Date createdAt;

    private String lastMessage;
    private Date lastMessageAt;

    private boolean hasUnread;
    private int membersCount;
    private User companion;
    // getters / setters
    public User getCompanion() {
        return companion;
    }

    public ChatListItem setCompanion(User companion) {
        this.companion = companion;
        return this;
    }
    public UUID getChatId() {
        return chatId;
    }

    public ChatListItem setChatId(UUID chatId) {
        this.chatId = chatId;
        return this;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public ChatListItem setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public ChatListItem setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
        return this;
    }

    public Date getLastMessageAt() {
        return lastMessageAt;
    }

    public ChatListItem setLastMessageAt(Date lastMessageAt) {
        this.lastMessageAt = lastMessageAt;
        return this;
    }

    public boolean isHasUnread() {
        return hasUnread;
    }

    public ChatListItem setHasUnread(boolean hasUnread) {
        this.hasUnread = hasUnread;
        return this;
    }

    public int getMembersCount() {
        return membersCount;
    }

    public ChatListItem setMembersCount(int membersCount) {
        this.membersCount = membersCount;
        return this;
    }
}
