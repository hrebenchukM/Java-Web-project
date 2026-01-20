package learning.itstep.javaweb222.data.chat;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import learning.itstep.javaweb222.data.core.DbProvider;
import learning.itstep.javaweb222.data.dto.Chat;

@Singleton
public class ChatDao {

    private final DbProvider db;
    private final Logger logger;

    @Inject
    public ChatDao(DbProvider db, Logger logger) {
        this.db = db;
        this.logger = logger;
    }

    // ================== GET ==================

    public Chat getChatById(String chatId) {
        String sql = "SELECT * FROM chats c "
                + "LEFT JOIN users u ON c.created_by = u.user_id "
                + "WHERE c.chat_id = ? AND c.deleted_at IS NULL";

        try (PreparedStatement prep = db.getConnection().prepareStatement(sql)) {
            prep.setString(1, chatId);
            ResultSet rs = prep.executeQuery();
            if (rs.next()) {
                return Chat.fromResultSet(rs);
            }
        }
        catch (SQLException ex) {
            logger.log(Level.WARNING, "ChatDao::getChatById {0}",
                    ex.getMessage() + " | " + sql);
        }
        return null;
    }

    public List<Chat> getUserChats(String userId) {
        String sql = "SELECT * FROM chats c "
                + "JOIN chat_members cm ON cm.chat_id = c.chat_id "
                + "LEFT JOIN users u ON c.created_by = u.user_id "
                + "WHERE cm.user_id = ? "
                + "AND cm.left_at IS NULL "
                + "AND c.deleted_at IS NULL "
                + "ORDER BY c.created_at DESC";

        List<Chat> chats = new ArrayList<>();

        try (PreparedStatement prep = db.getConnection().prepareStatement(sql)) {
            prep.setString(1, userId);
            ResultSet rs = prep.executeQuery();
            while (rs.next()) {
                chats.add(Chat.fromResultSet(rs));
            }
        }
        catch (SQLException ex) {
            logger.log(Level.WARNING, "ChatDao::getUserChats {0}",
                    ex.getMessage() + " | " + sql);
        }
        return chats;
    }

    // ================== CREATE ==================

    public Chat createChat(String creatorUserId) {
        UUID chatId = db.getDbIdentity();

        String sql = "INSERT INTO chats(chat_id, created_by) VALUES(?, ?)";

        try (PreparedStatement prep = db.getConnection().prepareStatement(sql)) {
            prep.setString(1, chatId.toString());
            prep.setString(2, creatorUserId);
            prep.executeUpdate();
        }
        catch (SQLException ex) {
            logger.log(Level.WARNING, "ChatDao::createChat {0}",
                    ex.getMessage() + " | " + sql);
            return null;
        }

        // creator автоматически становится участником
        addMember(chatId.toString(), creatorUserId);

        return getChatById(chatId.toString());
    }

    // ================== MEMBERS ==================

    public void addMember(String chatId, String userId) {
        String sql = "INSERT INTO chat_members "
                + "(chat_member_id, chat_id, user_id) "
                + "VALUES(UUID(), ?, ?)";

        try (PreparedStatement prep = db.getConnection().prepareStatement(sql)) {
            prep.setString(1, chatId);
            prep.setString(2, userId);
            prep.executeUpdate();
        }
        catch (SQLException ex) {
            logger.log(Level.WARNING, "ChatDao::addMember {0}",
                    ex.getMessage() + " | " + sql);
        }
    }

    public void leaveChat(String chatId, String userId) {
        String sql = "UPDATE chat_members "
                + "SET left_at = CURRENT_TIMESTAMP "
                + "WHERE chat_id = ? AND user_id = ? AND left_at IS NULL";

        try (PreparedStatement prep = db.getConnection().prepareStatement(sql)) {
            prep.setString(1, chatId);
            prep.setString(2, userId);
            prep.executeUpdate();
        }
        catch (SQLException ex) {
            logger.log(Level.WARNING, "ChatDao::leaveChat {0}",
                    ex.getMessage() + " | " + sql);
        }
    }

    // ================== DELETE ==================

    public void deleteChat(String chatId) {
        String sql = "UPDATE chats SET deleted_at = CURRENT_TIMESTAMP WHERE chat_id = ?";

        try (PreparedStatement prep = db.getConnection().prepareStatement(sql)) {
            prep.setString(1, chatId);
            prep.executeUpdate();
        }
        catch (SQLException ex) {
            logger.log(Level.WARNING, "ChatDao::deleteChat {0}",
                    ex.getMessage() + " | " + sql);
        }
    }
}
