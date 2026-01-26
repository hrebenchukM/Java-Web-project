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
import learning.itstep.javaweb222.data.dto.User;
import learning.itstep.javaweb222.models.chat.ChatListItem;

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
        try {
            UUID.fromString(chatId);
        }
        catch (Exception ex) {
            return null;
        }

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
            String sql =
                "INSERT INTO chat_members (chat_member_id, chat_id, user_id) " +
                "SELECT UUID(), ?, ? " +
                "WHERE NOT EXISTS ( " +
                "    SELECT 1 FROM chat_members " +
                "    WHERE chat_id = ? AND user_id = ? AND left_at IS NULL " +
                ")";

            try (PreparedStatement prep = db.getConnection().prepareStatement(sql)) {
                prep.setString(1, chatId);
                prep.setString(2, userId);
                prep.setString(3, chatId);
                prep.setString(4, userId);

                prep.executeUpdate();
            }
            catch (SQLException ex) {
                logger.log(
                    Level.WARNING,
                    "ChatDao::addMember {0}",
                    ex.getMessage() + " | " + sql
                );
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
    public boolean isMember(String chatId, String userId) {
        String sql =
            "SELECT 1 FROM chat_members " +
            "WHERE chat_id = ? AND user_id = ? " +
            "AND left_at IS NULL";

        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, chatId);
            ps.setString(2, userId);
            return ps.executeQuery().next();
        }
        catch (SQLException ex) {
            logger.log(Level.WARNING, "ChatDao::isMember {0}", ex.getMessage());
            return false;
        }
    }
   public List<ChatListItem> getUserChatList(String userId) {

    String sql = """
        SELECT
            c.chat_id,
            c.created_at,

            lm.content AS last_message,
            lm.sent_at AS last_message_at,

            cm.has_unread,

            (
                SELECT COUNT(*)
                FROM chat_members m
                WHERE m.chat_id = c.chat_id
                  AND m.left_at IS NULL
            ) AS members_count,

            -- 🔥 COMPANION USER
            u.user_id        AS companion_id,
            u.email          AS companion_email,
            u.first_name     AS companion_first_name,
            u.second_name    AS companion_second_name,
            u.avatar_url     AS companion_avatar_url,
            u.profile_title  AS companion_profile_title,
            u.location       AS companion_location

        FROM chats c

        -- текущий пользователь
        JOIN chat_members cm
            ON cm.chat_id = c.chat_id
           AND cm.user_id = ?
           AND cm.left_at IS NULL

        -- второй участник (НЕ current user)
        JOIN chat_members cm2
            ON cm2.chat_id = c.chat_id
           AND cm2.user_id != ?
           AND cm2.left_at IS NULL

        JOIN users u
            ON u.user_id = cm2.user_id

        LEFT JOIN messages lm
            ON lm.message_id = (
                SELECT m2.message_id
                FROM messages m2
                WHERE m2.chat_id = c.chat_id
                  AND m2.deleted_at IS NULL
                ORDER BY m2.sent_at DESC
                LIMIT 1
            )

        WHERE c.deleted_at IS NULL

        ORDER BY
            lm.sent_at DESC,
            c.created_at DESC
    """;

    List<ChatListItem> list = new ArrayList<>();

    try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
        ps.setString(1, userId);
        ps.setString(2, userId);

        ResultSet rs = ps.executeQuery();

        while (rs.next()) {

            ChatListItem item = new ChatListItem()
                .setChatId(UUID.fromString(rs.getString("chat_id")))
                .setCreatedAt(rs.getTimestamp("created_at"))
                .setLastMessage(rs.getString("last_message"))
                .setLastMessageAt(rs.getTimestamp("last_message_at"))
                .setHasUnread(rs.getByte("has_unread") == 1)
                .setMembersCount(rs.getInt("members_count"));

            // 🔥 СОБЕСЕДНИК
            try {
                User companion = new User()
                    .setId(UUID.fromString(rs.getString("companion_id")))
                    .setEmail(rs.getString("companion_email"))
                    .setFirstName(rs.getString("companion_first_name"))
                    .setSecondName(rs.getString("companion_second_name"))
                    .setAvatarUrl(rs.getString("companion_avatar_url"))
                    .setProfileTitle(rs.getString("companion_profile_title"))
                    .setLocation(rs.getString("companion_location"));

                item.setCompanion(companion);
            }
            catch (Exception ignore) {}

            list.add(item);
        }
    }
    catch (SQLException ex) {
        logger.log(Level.WARNING, "ChatDao::getUserChatList {0}", ex.getMessage());
    }

    return list;
}

    public void clearUnread(String chatId, String userId) {

        String sql = """
            UPDATE chat_members
            SET has_unread = 0
            WHERE chat_id = ?
            AND user_id = ?
            AND left_at IS NULL
        """;

        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, chatId);
            ps.setString(2, userId);
            ps.executeUpdate();
        }
        catch (SQLException ex) {
            logger.log(Level.WARNING, "ChatDao::clearUnread {0}", ex.getMessage());
        }
    }

}
