package learning.itstep.javaweb222.models.message;

public class MessageFormModel {
    private String chatId;
    private String content;
    private byte isDraft;

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public byte getIsDraft() {
        return isDraft;
    }

    public void setIsDraft(byte isDraft) {
        this.isDraft = isDraft;
    }
}
