package learning.itstep.javaweb222.data.message;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import learning.itstep.javaweb222.data.core.DbProvider;
import learning.itstep.javaweb222.data.dto.Message;
import learning.itstep.javaweb222.data.dto.MessageMedia;
import learning.itstep.javaweb222.data.dto.MessageRead;

@Singleton
public class MessageDao {

    private final DbProvider db;
    private final Logger logger;

    @Inject
    public MessageDao(DbProvider db, Logger logger) {
        this.db = db;
        this.logger = logger;
    }

    // ================== GET ==================

    public List<Message> getChatMessages(String chatId) {
        String sql = "SELECT * FROM messages m "
                + "LEFT JOIN users u ON m.sender_id = u.user_id "
                + "LEFT JOIN chats c ON m.chat_id = c.chat_id "
                + "WHERE m.chat_id = ? AND m.deleted_at IS NULL "
                + "ORDER BY m.sent_at";

        List<Message> messages = new ArrayList<>();

        try (PreparedStatement prep = db.getConnection().prepareStatement(sql)) {
            prep.setString(1, chatId);
            ResultSet rs = prep.executeQuery();
            while (rs.next()) {
                messages.add(Message.fromResultSet(rs));
            }
        }
        catch (SQLException ex) {
            logger.log(Level.WARNING, "MessageDao::getChatMessages {0}",
                    ex.getMessage() + " | " + sql);
        }
        return messages;
    }

    public Message getMessageById(String messageId) {
        String sql = "SELECT * FROM messages m "
                + "LEFT JOIN users u ON m.sender_id = u.user_id "
                + "LEFT JOIN chats c ON m.chat_id = c.chat_id "
                + "WHERE m.message_id = ?";

        try (PreparedStatement prep = db.getConnection().prepareStatement(sql)) {
            prep.setString(1, messageId);
            ResultSet rs = prep.executeQuery();
            if (rs.next()) {
                return Message.fromResultSet(rs);
            }
        }
        catch (SQLException ex) {
            logger.log(Level.WARNING, "MessageDao::getMessageById {0}",
                    ex.getMessage() + " | " + sql);
        }
        return null;
    }

    // ================== CREATE ==================

    public void addMessage(Message message) throws Exception {
        UUID.fromString(message.getChatId().toString());
        UUID.fromString(message.getSenderId().toString());

        String sql = "INSERT INTO messages "
                + "(message_id, chat_id, sender_id, content, is_draft) "
                + "VALUES(UUID(), ?, ?, ?, ?)";

        try (PreparedStatement prep = db.getConnection().prepareStatement(sql)) {
            prep.setString(1, message.getChatId().toString());
            prep.setString(2, message.getSenderId().toString());
            prep.setString(3, message.getContent());
            prep.setInt(4, message.getIsDraft());
            prep.executeUpdate();
        }
        catch (SQLException ex) {
            logger.log(Level.WARNING, "MessageDao::addMessage {0}",
                    ex.getMessage() + " | " + sql);
            throw ex;
        }
    }

    // ================== UPDATE ==================

    public void editMessage(String messageId, String content) {
        String sql = "UPDATE messages "
                + "SET content = ?, edited_at = CURRENT_TIMESTAMP "
                + "WHERE message_id = ? AND deleted_at IS NULL";

        try (PreparedStatement prep = db.getConnection().prepareStatement(sql)) {
            prep.setString(1, content);
            prep.setString(2, messageId);
            prep.executeUpdate();
        }
        catch (SQLException ex) {
            logger.log(Level.WARNING, "MessageDao::editMessage {0}",
                    ex.getMessage() + " | " + sql);
        }
    }

    public void deleteMessage(String messageId) {
        String sql = "UPDATE messages SET deleted_at = CURRENT_TIMESTAMP WHERE message_id = ?";

        try (PreparedStatement prep = db.getConnection().prepareStatement(sql)) {
            prep.setString(1, messageId);
            prep.executeUpdate();
        }
        catch (SQLException ex) {
            logger.log(Level.WARNING, "MessageDao::deleteMessage {0}",
                    ex.getMessage() + " | " + sql);
        }
    }

    // ================== MEDIA ==================

    public void addMessageMedia(String messageId, String mediaId) {
        String sql = "INSERT INTO message_media "
                + "(message_media_id, message_id, media_id) "
                + "VALUES(UUID(), ?, ?)";

        try (PreparedStatement prep = db.getConnection().prepareStatement(sql)) {
            prep.setString(1, messageId);
            prep.setString(2, mediaId);
            prep.executeUpdate();
        }
        catch (SQLException ex) {
            logger.log(Level.WARNING, "MessageDao::addMessageMedia {0}",
                    ex.getMessage() + " | " + sql);
        }
    }

    public List<MessageMedia> getMessageMedia(String messageId) {
        String sql = "SELECT * FROM message_media mm "
                + "LEFT JOIN media m ON mm.media_id = m.media_id "
                + "WHERE mm.message_id = ?";

        List<MessageMedia> list = new ArrayList<>();

        try (PreparedStatement prep = db.getConnection().prepareStatement(sql)) {
            prep.setString(1, messageId);
            ResultSet rs = prep.executeQuery();
            while (rs.next()) {
                list.add(MessageMedia.fromResultSet(rs));
            }
        }
        catch (SQLException ex) {
            logger.log(Level.WARNING, "MessageDao::getMessageMedia {0}",
                    ex.getMessage() + " | " + sql);
        }
        return list;
    }

    // ================== READ STATUS ==================

    public void markAsRead(String messageId, String userId) {
        String sql = "INSERT INTO message_reads "
                + "(message_read_id, message_id, user_id) "
                + "VALUES(UUID(), ?, ?)";

        try (PreparedStatement prep = db.getConnection().prepareStatement(sql)) {
            prep.setString(1, messageId);
            prep.setString(2, userId);
            prep.executeUpdate();
        }
        catch (SQLException ex) {
            logger.log(Level.WARNING, "MessageDao::markAsRead {0}",
                    ex.getMessage() + " | " + sql);
        }
    }

    public List<MessageRead> getMessageReads(String messageId) {
        String sql = "SELECT * FROM message_reads mr "
                + "LEFT JOIN users u ON mr.user_id = u.user_id "
                + "WHERE mr.message_id = ?";

        List<MessageRead> reads = new ArrayList<>();

        try (PreparedStatement prep = db.getConnection().prepareStatement(sql)) {
            prep.setString(1, messageId);
            ResultSet rs = prep.executeQuery();
            while (rs.next()) {
                reads.add(MessageRead.fromResultSet(rs));
            }
        }
        catch (SQLException ex) {
            logger.log(Level.WARNING, "MessageDao::getMessageReads {0}",
                    ex.getMessage() + " | " + sql);
        }
        return reads;
    }
}
